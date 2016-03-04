package com.oakonell.dndcharacter.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.model.character.AbstractContextualComponent;
import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentType;
import com.oakonell.dndcharacter.model.character.FeatureExtensionType;
import com.oakonell.dndcharacter.model.character.Proficient;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.character.SpeedType;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
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
import com.oakonell.dndcharacter.model.feat.Feat;
import com.oakonell.dndcharacter.model.item.CreateCharacterArmorVisitor;
import com.oakonell.dndcharacter.model.item.CreateCharacterWeaponVisitor;
import com.oakonell.dndcharacter.model.item.ItemRow;
import com.oakonell.dndcharacter.model.item.ItemType;
import com.oakonell.dndcharacter.model.spell.Spell;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.character.md.CategoryChoicesMD;
import com.oakonell.dndcharacter.views.character.md.ChooseMD;

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
    private final Context context;
    private String currentChoiceName;

    private Feature feature;
    private String spellPrefix = "";

    protected ApplyChangesToGenericComponent(Context context, SavedChoices savedChoices, C component, @Nullable Character character) {
        this.context = context;
        this.component = component;
        this.savedChoices = savedChoices;
        this.character = character;
    }


    private static void deleteItems(@NonNull List<? extends CharacterItem> items, ComponentType componentType) {
        for (Iterator<? extends CharacterItem> iterator = items.iterator(); iterator.hasNext(); ) {
            CharacterItem item = iterator.next();
            if (item.getSource() == componentType) {
                iterator.remove();
            }
        }
    }

    public static <C extends BaseCharacterComponent> void applyToCharacter(@NonNull Context context, @NonNull Element element, SavedChoices savedChoices, @NonNull C component, @Nullable Character character, boolean deleteEquipment) {
        if (deleteEquipment && character != null) {
            // first clear any equipment from this type previous value
            ComponentType componentType = component.getType();
            deleteItems(character.getItems(), componentType);
            deleteItems(character.getArmor(), componentType);
            deleteItems(character.getWeapons(), componentType);
        }

        ApplyChangesToGenericComponent<C> newMe = new ApplyChangesToGenericComponent<>(context, savedChoices, component, character);
        newMe.visitChildren(element);
    }

    @Override
    protected void visitShortDescription(@NonNull Element element) {
        component.setDescription(element.getTextContent());
    }

    private void addFeat(String name) {
        Feat feat = new Select().from(Feat.class).where("upper(name) = ?", name.toUpperCase()).executeSingle();
        if (feat == null) {
            throw new RuntimeException("Error finding feat named '" + name + "'");
        }

        final Element root = XmlUtils.getDocument(feat.getXml()).getDocumentElement();
        visitFeature(root);
    }

    @Override
    protected void visitFeat(@NonNull Element element) {
        String name = element.getTextContent().toUpperCase();
        addFeat(name);
    }


    @Override
    protected void visitEffect(@NonNull Element effectElement) {
        final Feature.FeatureCharacterEffect effect = new Feature.FeatureCharacterEffect();
        AddEffectToCharacterVisitor.readEffect(context, effectElement, effect);
        int cost = readIntegerAttribute(effectElement, "uses", 1);
        effect.setCost(cost);
        String actionName = effectElement.getAttribute("actionName");
        if (actionName == null || actionName.trim().length() == 0) {
            actionName = effect.getName();
        }

        String actionExtensionTypeString = effectElement.getAttribute("extension");
        FeatureExtensionType actionExtensionType = null;
        if (actionExtensionTypeString != null && actionExtensionTypeString.trim().length() > 0) {
            actionExtensionType = EnumHelper.stringToEnum(actionExtensionTypeString, FeatureExtensionType.class);
            effect.setExtensionType(actionExtensionType);
        }

        final String contextsString = XmlUtils.getElementText(effectElement, "actionContext");
        if (contextsString != null) {
            String[] contexts = contextsString.split(",");
            for (String each : contexts) {
                String contextString = each.trim();
                if (contextString.length() > 0) {
                    final FeatureContextArgument featureContextArgument = AbstractContextualComponent.parseFeatureContext(contextString);
                    effect.addActionContext(featureContextArgument);
                }
            }
        }

        String actionDescription = XmlUtils.getElementText(effectElement, "actionDescription");
        effect.setActionDescription(actionDescription);
        effect.setAction(actionName);
        feature.addEffect(effect);

        // look for any variable/prompts
        List<Element> variables = XmlUtils.getChildElements(effectElement, "variable");
        for (Element variableElement : variables) {
            String varName = variableElement.getAttribute("name");
            String promptString = variableElement.getAttribute("prompt");
            String valuesString = variableElement.getTextContent();
            String[] values;
            if (valuesString == null || valuesString.trim().length() == 0) {
                values = new String[]{};
            } else {
                values = valuesString.split("\\s*,\\s*");
            }
            // clean up empty strings..

            Feature.FeatureEffectVariable variable = new Feature.FeatureEffectVariable(varName, promptString, values);
            effect.addVariable(variable);
        }


        effect.setSource(component.getSourceString(context.getResources()) + "[" + feature.getSourceString(context.getResources()) + "]");
    }

    @Override
    protected void visitAction(@NonNull Element actionElement) {
        int cost = readIntegerAttribute(actionElement, "uses", -1);
        String actionName = XmlUtils.getElementText(actionElement, "name");
        String shortDescription = XmlUtils.getElementText(actionElement, "shortDescription");
        Feature.FeatureAction action = new Feature.FeatureAction();

        String actionExtensionTypeString = actionElement.getAttribute("extension");
        FeatureExtensionType actionExtensionType = null;
        if (actionExtensionTypeString != null && actionExtensionTypeString.trim().length() > 0) {
            actionExtensionType = EnumHelper.stringToEnum(actionExtensionTypeString, FeatureExtensionType.class);
            action.setExtensionType(actionExtensionType);
        }

        final String contextsString = XmlUtils.getElementText(actionElement, "context");
        if (contextsString != null) {
            String[] contexts = contextsString.split(",");
            for (String each : contexts) {
                String contextString = each.trim();
                if (contextString.length() > 0) {
                    final FeatureContextArgument featureContextArgument = AbstractContextualComponent.parseFeatureContext(contextString);
                    action.addContext(featureContextArgument);
                }
            }
        }

        action.setName(actionName);
        action.setCost(cost);
        action.setDescription(shortDescription);
        feature.addAction(action);
    }

    @Override
    protected void visitFeature(@NonNull Element element) {
        String name = XmlUtils.getElementText(element, "name");

        String oldSpellPrefix = spellPrefix;
        spellPrefix = name;


        feature = new Feature();
        feature.setName(name);
        feature.setDescription(XmlUtils.getElementText(element, "shortDescription"));
        // TODO handle refreshes, and other data in XML

        final String contextsString = XmlUtils.getElementText(element, "context");
        if (contextsString != null) {
            String[] contexts = contextsString.split(",");
            for (String each : contexts) {
                String contextString = each.trim();
                if (contextString.length() > 0) {
                    final FeatureContextArgument featureContextArgument = AbstractContextualComponent.parseFeatureContext(contextString);
                    feature.addContext(featureContextArgument);
                }
            }
        }

        String extensionTypeString = element.getAttribute("extension");
        FeatureExtensionType extensionType = null;
        if (extensionTypeString != null && extensionTypeString.trim().length() > 0) {
            extensionType = EnumHelper.stringToEnum(extensionTypeString, FeatureExtensionType.class);
            feature.setExtensionType(extensionType);
        }


        String refreshString = XmlUtils.getElementText(element, "refreshes");
        RefreshType refreshType = null;
        if (refreshString != null) {
            refreshType = EnumHelper.stringToEnum(refreshString, RefreshType.class);
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

        super.visitFeature(element);

        if (useType != null) {
            feature.setUseType(useType);
            if (refreshType == null) {
                if (extensionType == null) {
                    throw new RuntimeException("Missing refreshes element on feature " + component.getName() + "." + name);
                }
            } else {
                feature.setRefreshesOn(refreshType);

                if (feature.getEffects().isEmpty() && feature.getActions().isEmpty()) {
                    Feature.FeatureAction simpleAction = new Feature.FeatureAction();
                    simpleAction.setCost(1);
                    simpleAction.setName("Use");
                    feature.addAction(simpleAction);
                }
            }
        }

        final String appliesFormula = element.getAttribute("applies");
        feature.setAppliesFormula(appliesFormula);


        final String ac = XmlUtils.getElementText(element, "ac");
        feature.setAcFormula(ac);

        final String hp = XmlUtils.getElementText(element, "hp");
        feature.setHpFormula(hp);

        final String condition = XmlUtils.getElementText(element, "condition");
        feature.setActiveFormula(condition);

        component.addFeature(feature);

        spellPrefix = oldSpellPrefix;

    }

    protected int readIntegerAttribute(@NonNull Element actionElement, String attributeName, int defaultValue) {
        String stringValue = actionElement.getAttribute(attributeName);
        if (stringValue != null && stringValue.trim().length() > 0) {
            return Integer.parseInt(stringValue);
        }
        return defaultValue;
    }

    @Override
    protected void visitProficiency(@NonNull Element element) {
        String skillName = element.getTextContent();
        String category = element.getAttribute("category");

        // TODO how to encode expert, or half prof- via attribute?
        String level = element.getAttribute("level");
        Proficient proficient = Proficient.PROFICIENT;
        if (level != null && level.trim().length() > 0) {
            proficient = EnumHelper.stringToEnum(level, Proficient.class);
        }


        if (state == AbstractComponentVisitor.VisitState.SKILLS) {
            SkillType type = EnumHelper.stringToEnum(skillName, SkillType.class);
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
            StatType type = EnumHelper.stringToEnum(skillName, StatType.class);
            // TODO handle error
            component.addSaveThrow(type, proficient);

        }
        super.visitProficiency(element);
    }

    @Override
    protected void visitIncrease(@NonNull Element element) {
        String statName = element.getAttribute("name");
        String amountStr = element.getTextContent();

        StatType type = EnumHelper.stringToEnum(statName, StatType.class);

        int amount = Integer.parseInt(amountStr);

        component.addModifier(type, amount);
        super.visitIncrease(element);
    }

    @Override
    protected void visitLanguage(@NonNull Element element) {
        String language = element.getTextContent();
        component.getLanguages().add(language);
        super.visitLanguage(element);
    }

    @Override
    protected void visitChoose(@NonNull Element element) {
        String oldChoiceName = currentChoiceName;

        currentChoiceName = element.getAttribute("name");

        List<Element> childOrElems = XmlUtils.getChildElements(element, "or");
        if (childOrElems.size() == 0) {
            // category, context sensitive choices ?
            categoryChoices(element);
        } else {
            super.visitChoose(element);
        }

        currentChoiceName = oldChoiceName;
    }

    @Override
    protected void visitSpeed(@NonNull Element element) {
        String speedTypString = element.getAttribute("type");
        SpeedType speedType = SpeedType.WALK;
        if (speedTypString != null && speedTypString.trim().length() > 0) {
            speedType = EnumHelper.stringToEnum(speedTypString, SpeedType.class);
        }
        String valueString = element.getTextContent();
        component.setSpeedFormula(speedType, valueString);
    }

    @Override
    protected void visitInitiative(@NonNull Element element) {
        String valueString = element.getTextContent();
        component.setInitiativeModFormula(valueString);
    }

    @Override
    protected void visitPassivePerception(@NonNull Element element) {
        String valueString = element.getTextContent();
        component.setPassivePerceptionModFormula(valueString);
    }

    @Override
    protected void visitSpells(@NonNull Element element) {
        if (savedChoices != null) {
            final List<String> spells = savedChoices.getChoicesFor(spellPrefix + "_spells");
            final List<String> addedSpells = savedChoices.getChoicesFor(spellPrefix + "_addedSpells");
            if (spells != null && (!spells.isEmpty() || !addedSpells.isEmpty())) {
                String statString = element.getAttribute("stat");
                String casterClass = element.getAttribute("casterClass");
                String knownString = element.getAttribute("known");
                boolean countsAsKnown = true;
                if (knownString != null && knownString.trim().length() > 0) {
                    countsAsKnown = "true".equals(knownString);
                }
                StatType stat = null;
                if (statString != null && statString.trim().length() > 0) {
                    stat = EnumHelper.stringToEnum(statString, StatType.class);
                }
                for (String each : spells) {
                    addSpell(each, stat, countsAsKnown);
                }
                for (String each : addedSpells) {
                    addSpell(each, stat, countsAsKnown);
                }
            }
            List<Element> spellList = XmlUtils.getChildElements(element, "spell");
            int addedSpellIndex = 0;
            if (spellList.size() > 0) {
                for (Element spellElement : spellList) {
                    final String spellName = spellElement.getTextContent();
                    if (spellName == null || spellName.trim().length() == 0) {

                        String knownString = spellElement.getAttribute("known");
                        boolean countsAsKnown = true;
                        if (knownString != null && knownString.trim().length() > 0) {
                            countsAsKnown = "true".equals(knownString);
                        }

                        List<String> specificAddedSpell = savedChoices.getChoicesFor(spellPrefix + "_addedSpell" + addedSpellIndex);
                        StatType stat = null;
                        for (String eachSavedSpell : specificAddedSpell) {
                            addSpell(eachSavedSpell, stat, countsAsKnown);
                        }
                    }
                    addedSpellIndex++;
                }
            }
        }

        super.visitSpells(element);
    }

    @Override
    protected void visitSpell(@NonNull Element element) {
        final String spellName = element.getTextContent();
        // if spell name is empty, presume it was a specificAddSpell in visitSpells above
        if (spellName == null || spellName.trim().length() == 0) return;

        String statString = element.getAttribute("stat");

        StatType stat = null;
        if (statString != null && statString.trim().length() > 0) {
            stat = EnumHelper.stringToEnum(statString, StatType.class);
        }
        boolean countsAsKnown = true;
        addSpell(spellName, stat, countsAsKnown);
    }

    private void addSpell(@NonNull String spellName, StatType stat, boolean countsAsKnown) {
        List<Spell> spells = new Select()
                .from(Spell.class).where("UPPER(name) = ?", spellName.toUpperCase()).execute();
        CharacterSpell characterSpell = null;
        if (!spells.isEmpty()) {
            if (spells.size() > 1) {
                throw new RuntimeException("Too many spells named " + spellName);
            }
            final Spell spell = spells.get(0);
            characterSpell = new CharacterSpell();
            final Element root = XmlUtils.getDocument(spell.getXml()).getDocumentElement();
            ApplyChangesToGenericComponent.applyToCharacter(context, root, null, characterSpell, null, false);
            characterSpell.setLevel(spell.getLevel());
        } else {
            characterSpell = new CharacterSpell();
            characterSpell.setLevel(1);
        }
        characterSpell.countsAsKnown(countsAsKnown);
        characterSpell.setName(spellName);
        characterSpell.setCastingStat(stat);
        // TODO need to store the caster class, if different from owner name?
        characterSpell.setOwnerName(component.getName());
        characterSpell.setSource(component.getType());
        component.getSpells().add(characterSpell);
    }

    @Override
    protected void visitCantrips(@NonNull Element element) {
        if (savedChoices != null) {
            final List<String> cantrips = savedChoices.getChoicesFor(spellPrefix + "_cantrips");
            final List<String> addedCantrips = savedChoices.getChoicesFor(spellPrefix + "_addedCantrips");
            if (cantrips != null && (!cantrips.isEmpty() || !addedCantrips.isEmpty())) {
                String casterClass = element.getAttribute("casterClass");

                String knownString = element.getAttribute("known");
                boolean countsAsKnown = true;
                if (knownString != null && knownString.trim().length() > 0) {
                    countsAsKnown = "true".equals(knownString);
                }

                String statString = element.getAttribute("stat");
                StatType stat = null;
                if (statString != null && statString.trim().length() > 0) {
                    stat = EnumHelper.stringToEnum(statString, StatType.class);
                }
                for (String each : cantrips) {
                    addCantrip(each, stat, countsAsKnown);
                }
                for (String each : addedCantrips) {
                    addCantrip(each, stat, countsAsKnown);
                }
            }
            List<Element> cantripList = XmlUtils.getChildElements(element, "cantrip");
            int addedCantripIndex = 0;
            if (cantripList.size() > 0) {
                for (Element cantripElement : cantripList) {
                    final String cantripName = cantripElement.getTextContent();
                    if (cantripName == null || cantripName.trim().length() == 0) {
                        String knownString = cantripElement.getAttribute("known");
                        boolean countsAsKnown = true;
                        if (knownString != null && knownString.trim().length() > 0) {
                            countsAsKnown = "true".equals(knownString);
                        }

                        List<String> specificAddedCantrip = savedChoices.getChoicesFor(spellPrefix + "_addedCantrip" + addedCantripIndex);
                        StatType stat = null;
                        for (String eachSavedCantrip : specificAddedCantrip) {
                            addCantrip(eachSavedCantrip, stat, countsAsKnown);
                        }
                    }
                    addedCantripIndex++;
                }
            }
        }
        super.visitCantrips(element);
    }

    @Override
    protected void visitCantrip(@NonNull Element element) {
        final String cantripName = element.getTextContent();
        // if cantrip name is empty, presume it was a specificAddCantrip in visitCantrips above
        if (cantripName == null || cantripName.trim().length() == 0) return;
        String statString = element.getAttribute("stat");

        StatType stat = null;
        if (statString != null && statString.trim().length() > 0) {
            stat = EnumHelper.stringToEnum(statString, StatType.class);
        }
        boolean countsAsKnown = true;
        addCantrip(cantripName, stat, countsAsKnown);
    }

    private void addCantrip(@NonNull String cantripName, StatType stat, boolean countsAsKnown) {
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
            ApplyChangesToGenericComponent.applyToCharacter(context, root, null, characterSpell, null, false);
        } else {
            characterSpell = new CharacterSpell();
        }
        characterSpell.setLevel(0);
        characterSpell.countsAsKnown(countsAsKnown);
        characterSpell.setName(cantripName);
        characterSpell.setCastingStat(stat);
        // TODO need to store the caster class, if different from owner name?
        characterSpell.setOwnerName(component.getName());
        characterSpell.setSource(component.getType());
        component.getCantrips().add(characterSpell);
    }

    @Override
    protected void visitItem(@NonNull Element element) {
        if (character == null) {
            return;
        }
        final String itemName = element.getTextContent();
        addItem(itemName);
    }

    private void addItem(@NonNull String itemName) {
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
                    CharacterArmor armor = CreateCharacterArmorVisitor.createArmor(context, itemRow, character);
                    armor.setName(itemName);
                    armor.setSource(component.getType());
                    break;
                case WEAPON:
                    CharacterWeapon weapon = CreateCharacterWeaponVisitor.createWeapon(context, itemRow, character);
                    weapon.setName(itemName);
                    weapon.setSource(component.getType());
                    break;
                case EQUIPMENT:
                    CharacterItem item = new CharacterItem();
                    item.setName(itemName);
                    item.setSource(component.getType());

                    ApplyChangesToGenericComponent.applyToCharacter(context, XmlUtils.getDocument(itemRow.getXml()).getDocumentElement(), null, item, character, false);

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

    private void categoryChoices(Element element) {
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
                case FEAT:
                    addFeat(selection);
                    break;
                case SKILLS:
                    String level = element.getAttribute("level");
                    Proficient proficient = Proficient.PROFICIENT;
                    if (level != null && level.trim().length() > 0) {
                        proficient = EnumHelper.stringToEnum(level, Proficient.class);
                    }

                    // TODO how to specify a different proficiency
                    SkillType type = EnumHelper.stringToEnum(selection, SkillType.class);
                    component.addSkill(type, proficient);
                    break;
            }
        }

    }


    @Override
    protected void visitOr(@NonNull Element element) {
        String optionName = element.getAttribute("name");
        List<String> selections = savedChoices.getChoicesFor(currentChoiceName);
        if (selections.contains(optionName)) {
            super.visitOr(element);
        }
    }

    public SavedChoices getSavedChoices() {
        return savedChoices;
    }

    public C getComponent() {
        return component;
    }
}
