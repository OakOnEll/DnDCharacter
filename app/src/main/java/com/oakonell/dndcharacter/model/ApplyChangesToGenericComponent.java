package com.oakonell.dndcharacter.model;

import android.support.annotation.Nullable;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentType;
import com.oakonell.dndcharacter.model.character.Proficient;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.character.item.CharacterArmor;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.model.character.item.CharacterWeapon;
import com.oakonell.dndcharacter.model.character.spell.CharacterSpell;
import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.model.components.UseType;
import com.oakonell.dndcharacter.model.effect.AddEffectToCharacterVisitor;
import com.oakonell.dndcharacter.model.item.CreateCharacterArmorVisitor;
import com.oakonell.dndcharacter.model.item.CreateCharacterWeaponVisitor;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.item.ItemType;
import com.oakonell.dndcharacter.model.spell.Spell;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import org.w3c.dom.Element;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Rob on 11/18/2015.
 */
public class ApplyChangesToGenericComponent<C extends BaseCharacterComponent> extends AbstractChoiceComponentVisitor {
    private final C component;
    private final SavedChoices savedChoices;
    @Nullable
    private final com.oakonell.dndcharacter.model.character.Character character;
    private String currentChoiceName;

    private ApplyChangesToGenericComponent(SavedChoices savedChoices, C component, @Nullable Character character) {
        this.component = component;
        this.savedChoices = savedChoices;
        this.character = character;
    }


    private static void deleteItems(List<? extends CharacterItem> items, ComponentType componentType) {
        for (Iterator<? extends CharacterItem> iterator = items.iterator(); iterator.hasNext(); ) {
            CharacterItem item = iterator.next();
            if (item.getSource() == componentType) {
                iterator.remove();
            }
        }
    }

    public static <C extends BaseCharacterComponent> void applyToCharacter(Element element, SavedChoices savedChoices, C component, @Nullable Character character, boolean deleteEquipment) {
        if (deleteEquipment && character != null) {
            // first clear any equipment from this type previous value
            ComponentType componentType = component.getType();
            deleteItems(character.getItems(), componentType);
            deleteItems(character.getArmor(), componentType);
            deleteItems(character.getWeapons(), componentType);
        }

        ApplyChangesToGenericComponent<C> newMe = new ApplyChangesToGenericComponent<>(savedChoices, component, character);
        newMe.visitChildren(element);
    }

    @Override
    protected void visitFeature(Element element) {
        Feature feature = new Feature();
        String name = XmlUtils.getElementText(element, "name");
        feature.setName(name);
        feature.setDescription(XmlUtils.getElementText(element, "shortDescription"));
        // TODO handle refreshes, and other data in XML

        final String contextsString = XmlUtils.getElementText(element, "context");
        if (contextsString != null) {
            String[] contexts = contextsString.split(",");
            for (String each : contexts) {
                String contextString = each.trim();
                if (contextString.length() > 0) {
                    FeatureContext context = FeatureContext.valueOf(contextString.toUpperCase());
                    feature.addContext(context);
                }
            }
        }

        String refreshString = XmlUtils.getElementText(element, "refreshes");
        RefreshType refreshType = null;
        if (refreshString != null) {
            refreshString = refreshString.toLowerCase();
            refreshString = refreshString.replaceAll(" ", "");
            switch (refreshString) {
                case "rest": // fall through
                case "shortrest":
                    refreshType = RefreshType.SHORT_REST;
                    break;
                case "longrest":
                    refreshType = RefreshType.LONG_REST;
                    break;
                default:
                    throw new RuntimeException("Unknown refresh string '" + refreshString + "' on feature " + component.getName() + "." + name);
            }
        }
        final String uses = XmlUtils.getElementText(element, "uses");
        final String pool = XmlUtils.getElementText(element, "pool");
        // TODO fail if both
        UseType useType = null;
        if (uses != null) {
            useType = UseType.PER_USE;
            feature.setFormula(uses);
        } else if (pool != null) {
            useType = UseType.POOL;
            feature.setFormula(pool);
        }
        if (useType != null) {
            feature.setUseType(useType);
            if (refreshType == null) {
                throw new RuntimeException("Missing refreshes element on feature " + component.getName() + "." + name);
            }
            feature.setRefreshesOn(refreshType);

            boolean hadAnyActionsOrEffects = false;
            final List<Element> effectElements = XmlUtils.getChildElements(element, "effect");
            for (Element effectElement : effectElements) {
                final Feature.FeatureCharacterEffect effect = new Feature.FeatureCharacterEffect();
                AddEffectToCharacterVisitor.readEffect(effectElement, effect);
                int cost = readIntegerAttribute(effectElement, "uses", 1);
                effect.setCost(cost);
                String actionName = effectElement.getAttribute("actionName");
                if (actionName == null || actionName.trim().length() == 0) {
                    actionName = effect.getName();
                }
                effect.setAction(actionName);
                feature.addEffect(effect);
                effect.setSource(component.getSourceString() + "[" + feature.getSourceString() + "]");
                hadAnyActionsOrEffects = true;
            }

            final List<Element> actionElements = XmlUtils.getChildElements(element, "action");
            for (Element actionElement : actionElements) {
                int cost = readIntegerAttribute(actionElement, "uses", -1);
                String actionName = XmlUtils.getElementText(actionElement, "name");
                String shortDescription = XmlUtils.getElementText(actionElement, "shortDescription");
                Feature.FeatureAction action = new Feature.FeatureAction();

                action.setName(actionName);
                action.setCost(cost);
                action.setDescription(shortDescription);
                feature.addAction(action);
                hadAnyActionsOrEffects = true;
            }

            if (!hadAnyActionsOrEffects) {
                Feature.FeatureAction simpleAction = new Feature.FeatureAction();
                simpleAction.setCost(1);
                simpleAction.setName("Use");
                feature.addAction(simpleAction);
            }
        }


        final String ac = XmlUtils.getElementText(element, "ac");
        feature.setAcFormula(ac);

        final String condition = XmlUtils.getElementText(element, "condition");
        feature.setActiveFormula(condition);

        component.addFeature(feature);
        super.visitFeature(element);
    }

    protected int readIntegerAttribute(Element actionElement, String attributeName, int defaultValue) {
        String stringValue = actionElement.getAttribute(attributeName);
        if (stringValue != null && stringValue.trim().length() > 0) {
            return Integer.parseInt(stringValue);
        }
        return defaultValue;
    }

    @Override
    protected void visitProficiency(Element element) {
        String skillName = element.getTextContent();
        String category = element.getAttribute("category");

        // TODO how to encode expert, or half prof- via attribute?
        String level = element.getAttribute("level");
        Proficient proficient = Proficient.PROFICIENT;
        if (level != null && level.trim().length() > 0) {
            proficient = Proficient.valueOf(level.toUpperCase());
        }


        if (state == AbstractComponentVisitor.VisitState.SKILLS) {
            skillName = skillName.replaceAll(" ", "_");
            skillName = skillName.toUpperCase();
            SkillType type = SkillType.valueOf(SkillType.class, skillName);
            // TODO handle error
            component.addSkill(type, proficient);
        } else if (state == VisitState.TOOLS || state == VisitState.ARMOR || state == VisitState.WEAPONS) {
            ProficiencyType type;
            if (state == VisitState.TOOLS) {
                type = ProficiencyType.TOOL;
            } else if (state == VisitState.ARMOR) {
                type = ProficiencyType.ARMOR;
            } else if (state == VisitState.WEAPONS) {
                type = ProficiencyType.WEAPON;
            } else {
                throw new RuntimeException("Unexpected state " + state);
            }

            // TODO handle error
            if (category != null && category.trim().length() > 0) {
                component.addToolCategoryProficiency(type, category, proficient);
            } else {
                component.addToolProficiency(type, skillName, proficient);
            }
        } else if (state == VisitState.SAVING_THROWS) {
            skillName = skillName.replaceAll(" ", "_");
            skillName = skillName.toUpperCase();
            StatType type = StatType.valueOf(StatType.class, skillName);
            // TODO handle error
            component.addSaveThrow(type, proficient);

        }
        super.visitProficiency(element);
    }

    @Override
    protected void visitIncrease(Element element) {
        String statName = element.getAttribute("name");
        String amountStr = element.getTextContent();

        statName = statName.replaceAll(" ", "_");
        statName = statName.toUpperCase();
        StatType type = StatType.valueOf(StatType.class, statName);

        int amount = Integer.parseInt(amountStr);

        component.addModifier(type, amount);
        super.visitIncrease(element);
    }

    @Override
    protected void visitLanguage(Element element) {
        String language = element.getTextContent();
        component.getLanguages().add(language);
        super.visitLanguage(element);
    }

    @Override
    protected void visitChoose(Element element) {
        String oldChoiceName = currentChoiceName;

        currentChoiceName = element.getAttribute("name");

        List<Element> childOrElems = XmlUtils.getChildElements(element, "or");
        if (childOrElems.size() == 0) {
            // category, context sensitive choices ?
            categoryChoices();
        } else {
            super.visitChoose(element);
        }

        currentChoiceName = oldChoiceName;
    }

    protected void visitCharacterLevel(Element element) {
        // TODO ...
        //visitGroup(element);
    }

    @Override
    protected void visitCantrip(Element element) {
        final String cantripName = element.getTextContent();
        String statString = element.getAttribute("stat");

        StatType stat = null;
        if (statString != null && statString.trim().length() > 0) {
            statString = statString.toUpperCase();
            stat = StatType.valueOf(StatType.class, statString);
        }
        addCantrip(cantripName, stat);
    }

    private void addCantrip(String cantripName, StatType stat) {
        List<Spell> spells = new Select()
                .from(Spell.class).where("UPPER(name) = ?", cantripName.toUpperCase()).execute();
        CharacterSpell characterSpell = null;
        if (!spells.isEmpty()) {
            if (spells.size() > 1) {
                throw new RuntimeException("Too many spells named " + cantripName);
            }
            final Spell spell = spells.get(0);
            characterSpell = new CharacterSpell();
            final Element root = XmlUtils.getDocument(spell.getXml()).getDocumentElement();
            ApplyChangesToGenericComponent.applyToCharacter(root, null, characterSpell, null, false);
        } else {
            characterSpell = new CharacterSpell();
        }
        characterSpell.setLevel(0);
        characterSpell.setName(cantripName);
        characterSpell.setCastingStat(stat);
        characterSpell.setSource(component.getType());
        component.getCantrips().add(characterSpell);
    }

    @Override
    protected void visitItem(Element element) {
        if (character == null) {
            return;
        }
        final String itemName = element.getTextContent();
        addItem(itemName);
    }

    private void addItem(String itemName) {
        // look up in items table for more information
        List<ItemRow> items = new Select()
                .from(ItemRow.class).where("UPPER(name) = ?", itemName.toUpperCase()).execute();
        if (!items.isEmpty()) {
            if (items.size() > 1) {
                throw new RuntimeException("Too many items named " + itemName);
            }
            final ItemRow itemRow = items.get(0);
            final ItemType itemType = itemRow.getItemType();
            switch (itemType) {
                case ARMOR:
                    CharacterArmor armor = CreateCharacterArmorVisitor.createArmor(itemRow, character);
                    armor.setName(itemName);
                    armor.setSource(component.getType());
                    break;
                case WEAPON:
                    CharacterWeapon weapon = CreateCharacterWeaponVisitor.createWeapon(itemRow, character);
                    weapon.setName(itemName);
                    weapon.setSource(component.getType());
                    break;
                case EQUIPMENT:
                    CharacterItem item = new CharacterItem();
                    item.setName(itemName);
                    item.setSource(component.getType());

                    ApplyChangesToGenericComponent.applyToCharacter(XmlUtils.getDocument(itemRow.getXml()).getDocumentElement(), null, item, character, false);

                    character.addItem(item);
                    break;
            }
        } else {
            CharacterItem item = new CharacterItem();
            item.setName(itemName);
            item.setSource(component.getType());
            character.addItem(item);
        }
    }

    private void categoryChoices() {
        List<String> selections = savedChoices.getChoicesFor(currentChoiceName);
        for (String selection : selections) {
            switch (state) {
                case LANGUAGES:
                    component.getLanguages().add(selection);
                    break;
                case TOOLS:
                    component.addToolProficiency(ProficiencyType.TOOL, selection, Proficient.PROFICIENT);
                    break;
                case ARMOR:
                    component.addToolProficiency(ProficiencyType.ARMOR, selection, Proficient.PROFICIENT);
                    break;
                case WEAPONS:
                    component.addToolCategoryProficiency(ProficiencyType.WEAPON, selection, Proficient.PROFICIENT);
                    break;
                case EQUIPMENT:
                    addItem(selection);
                    break;
            }
        }

    }

    @Override
    protected void visitOr(Element element) {
        String optionName = element.getAttribute("name");
        List<String> selections = savedChoices.getChoicesFor(currentChoiceName);
        if (selections.contains(optionName)) {
            super.visitOr(element);
        }
    }


}
