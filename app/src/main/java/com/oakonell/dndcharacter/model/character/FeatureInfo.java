package com.oakonell.dndcharacter.model.character;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.oakonell.dndcharacter.model.character.companion.CompanionTypeComponent;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.character.spell.CharacterSpell;
import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.components.Feature;
import com.oakonell.dndcharacter.model.components.IFeatureAction;
import com.oakonell.dndcharacter.model.components.Proficiency;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.model.components.UseType;
import com.oakonell.dndcharacter.views.character.IContextualComponent;
import com.oakonell.expression.context.SimpleVariableContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Rob on 10/26/2015.
 */
public class FeatureInfo implements IContextualComponent, ICharacterComponent {
    private final Feature feature;
    private final BaseCharacterComponent source;
    private final FeatureInfo extendedFeature;

    public FeatureInfo(Feature feature, BaseCharacterComponent baseCharacterComponent) {
        this(feature, baseCharacterComponent, null);
    }

    public FeatureInfo(Feature feature, BaseCharacterComponent baseCharacterComponent, FeatureInfo extendedFeature) {
        this.feature = feature;
        this.source = baseCharacterComponent;
        this.extendedFeature = extendedFeature;
    }

    public String getName() {
        return feature.getName();
    }


    @NonNull
    public String getSourceString(@NonNull Resources resources) {
        return source.getSourceString(resources);
    }

    @Override
    public String getAsSourceString(Resources resources) {
        return getSourceString(resources) + ":" + getName();
    }

    @NonNull
    @Override
    public ComponentType getType() {
        return ComponentType.FEATURE;
    }

    public BaseCharacterComponent getSource() {
        return source;
    }


    @Nullable
    @Override
    public String getActiveFormula() {
        if (getFeature().getActiveFormula() != null) {
            return getFeature().getActiveFormula();
        }
        if (extendedFeature != null) return extendedFeature.getActiveFormula();
        return null;
    }

    @Nullable
    public String getShortDescription() {
        if (getFeature().getDescription() != null) {
            return getFeature().getDescription();
        }
        if (extendedFeature != null) return extendedFeature.getShortDescription();
        return null;
    }


    public boolean hasLimitedUses() {
        if (getFeature().getUsesFormula() != null && getFeature().getUsesFormula().length() > 0 && !"0".equals(getFeature().getUsesFormula())) {
            return true;
        }
        if (extendedFeature != null) return extendedFeature.hasLimitedUses();
        return false;
    }


    @Nullable
    public RefreshType getRefreshesOn() {
        if (getFeature().getRefreshesOn() != null) {
            return getFeature().getRefreshesOn();
        }
        if (extendedFeature != null) return extendedFeature.getRefreshesOn();
        return null;
    }

    // TODO hide feature, to allow an "override"/replace cascade
    private Feature getFeature() {
        return feature;
    }

    public int evaluateMaxUses(@NonNull AbstractCharacter character) {
        if (feature.getUsesFormula() != null) {
            SimpleVariableContext variableContext = new SimpleVariableContext();
            source.addExtraFormulaVariables(variableContext, character);
            return character.evaluateFormula(feature.getUsesFormula(), variableContext);
        }
        if (extendedFeature != null) return extendedFeature.evaluateMaxUses(character);
        return 0;
    }


    @Nullable
    public UseType getUseType() {
        if (getFeature().getUseType() != null) {
            return getFeature().getUseType();
        }
        if (extendedFeature != null) return extendedFeature.getUseType();
        return null;
    }

    @Override
    public boolean isInContext(@NonNull FeatureContextArgument context) {
        if (feature.isInContext(context)) return true;
        if (extendedFeature != null) {
            if (extendedFeature.isInContext(context)) return true;
        }
        return false;
    }

    @Override
    public boolean isInContext(@NonNull Set<FeatureContextArgument> filter) {
        if (feature.isInContext(filter)) return true;
        if (extendedFeature != null) {
            if (extendedFeature.isInContext(filter)) return true;
        }
        return false;
    }


    public boolean isBaseArmor() {
        if (feature.isBaseArmor()) return true;
        if (extendedFeature != null) {
            if (extendedFeature.isBaseArmor()) return true;
        }
        return false;
    }

    public String getBaseAcFormula() {
        if (getFeature().getBaseAcFormula() != null && getFeature().getBaseAcFormula().trim().length() > 0) {
            return getFeature().getBaseAcFormula();
        }
        if (extendedFeature != null) return extendedFeature.getBaseAcFormula();
        return null;
    }

    @Nullable
    @Override
    public String getModifyingAcFormula() {
        if (getFeature().getModifyingAcFormula() != null) {
            return getFeature().getModifyingAcFormula();
        }
        if (extendedFeature != null) return extendedFeature.getModifyingAcFormula();
        return null;
    }


    @Override
    public int getSpeed(@NonNull AbstractCharacter character, SpeedType type) {
        int speed = getFeature().getSpeed(character, type);
        if (speed != 0) {
            return speed;
        }
        if (extendedFeature != null) return extendedFeature.getSpeed(character, type);
        return 0;
    }

    @Override
    public Boolean isBaseSpeed(SpeedType type) {
        Boolean isBase = getFeature().isBaseSpeed(type);
        if (isBase != null) {
            return isBase;
        }
        if (extendedFeature != null) return extendedFeature.isBaseSpeed(type);
        return null;
    }

    @Override
    public int getInitiativeMod(@NonNull AbstractCharacter character) {
        if (getFeature().getInitiativeModFormula() != null) {
            return getFeature().getInitiativeMod(character);
        }
        if (extendedFeature != null) return extendedFeature.getInitiativeMod(character);
        return 0;
    }

    @Override
    public int getPassivePerceptionMod(@NonNull AbstractCharacter character) {
        if (getFeature().getPassivePerceptionModFormula() != null) {
            return getFeature().getPassivePerceptionMod(character);
        }
        if (extendedFeature != null) return extendedFeature.getPassivePerceptionMod(character);
        return 0;
    }

    @Override
    public int getStatModifier(StatType type) {
        if (getFeature().getStatModifier(type) != 0) {
            return getFeature().getStatModifier(type);
        }
        if (extendedFeature != null) return extendedFeature.getStatModifier(type);
        return 0;
    }

    public boolean usesSpellSlot() {
        if (getFeature().usesSpellSlot()) {
            return true;
        }
        if (extendedFeature != null) return extendedFeature.usesSpellSlot();
        return false;
    }


    @Nullable
    @Override
    public Proficient getSkillProficient(SkillType type) {
        if (getFeature().getSkillProficient(type) != null) {
            return getFeature().getSkillProficient(type);
        }
        if (extendedFeature != null) return extendedFeature.getSkillProficient(type);
        return null;
    }

    @Nullable
    @Override
    public Proficient getSaveProficient(StatType type) {
        if (getFeature().getSaveProficient(type) != null) {
            return getFeature().getSaveProficient(type);
        }
        if (extendedFeature != null) return extendedFeature.getSaveProficient(type);
        return null;
    }


    @NonNull
    public Collection<IFeatureAction> getActionsAndEffects(@NonNull Character character) {
        Map<String, IFeatureAction> resultActionsAndEffects = new HashMap<>();
        for (IFeatureAction each : feature.getActionsAndEffects()) {

            if (!each.applies(character)) continue;

            resultActionsAndEffects.put(each.getName(), each);
        }
        if (extendedFeature != null) {
            final Collection<IFeatureAction> actionsAndEffects = extendedFeature.getActionsAndEffects(character);
            for (IFeatureAction each : actionsAndEffects) {
                IFeatureAction latest = resultActionsAndEffects.get(each.getName());
                if (latest != null) {
                    if (!latest.replacesPrevious()) {
                        // TODO report the error better
                        Log.e("Feature error", "Found a feature '" + feature.getName() + "' action '" + each.getName() + "', that doesn't specify replace");
                    }
                    continue;
                }
                resultActionsAndEffects.put(each.getName(), each);
            }

        }
        return resultActionsAndEffects.values();
    }

    @NonNull
    @Override
    public List<String> getLanguages() {
        List<String> languages = new ArrayList<>();
        languages.addAll(feature.getLanguages());
        if (extendedFeature != null) {
            languages.addAll(extendedFeature.getLanguages());
        }
        return languages;

    }

    @NonNull
    @Override
    public List<Proficiency> getToolProficiencies(ProficiencyType type) {
        List<Proficiency> toolProficiencies = new ArrayList<>();
        toolProficiencies.addAll(feature.getToolProficiencies(type));
        if (extendedFeature != null) {
            toolProficiencies.addAll(extendedFeature.getToolProficiencies(type));
        }
        return toolProficiencies;
    }

    @Nullable
    @Override
    public String getAcFormula() {
        if (getFeature().getAcFormula() != null) {
            return getFeature().getAcFormula();
        }
        if (extendedFeature != null) return extendedFeature.getAcFormula();
        return null;
    }

    @Nullable
    @Override
    public String getHpFormula() {
        if (getFeature().getHpFormula() != null) {
            return getFeature().getHpFormula();
        }
        if (extendedFeature != null) return extendedFeature.getHpFormula();
        return null;
    }

    @Override
    public void addExtraFormulaVariables(SimpleVariableContext extraVariables, @NonNull AbstractCharacter character) {
        getFeature().addExtraFormulaVariables(extraVariables, character);
        getSource().addExtraFormulaVariables(extraVariables, character);
        if (extendedFeature != null)
            extendedFeature.addExtraFormulaVariables(extraVariables, character);
    }


    @NonNull
    @Override
    public List<CharacterSpell> getCantrips() {
        List<CharacterSpell> cantrips = new ArrayList<>();
        cantrips.addAll(feature.getCantrips());
        if (extendedFeature != null) {
            cantrips.addAll(extendedFeature.getCantrips());
        }
        return cantrips;
    }

    @NonNull
    @Override
    public List<CharacterSpell> getSpells() {
        List<CharacterSpell> spells = new ArrayList<>();
        spells.addAll(feature.getSpells());
        if (extendedFeature != null) {
            spells.addAll(extendedFeature.getSpells());
        }
        return spells;
    }

    @Override
    public void addFeatureInfo(Map<String, FeatureInfo> map, AbstractCharacter character) {
        throw new RuntimeException("SHouldn't get here?");
    }

    @Override
    public boolean originatesFrom(ComponentSource currentComponent) {
        if (equals(currentComponent)) return true;
        return getSource().originatesFrom(currentComponent);
    }

    public IFeatureAction getActionNamed(@NonNull Character character, String actionName) {
        List<IFeatureAction> matching = new ArrayList<>();
        for (IFeatureAction action : getActionsAndEffects(character)) {
            if (action.getName().equals(actionName)) matching.add(action);
        }
        if (matching.isEmpty()) return null;
        if (matching.size() == 1) return matching.get(0);
        Log.e("FeatureInfo", "Found multiple actions named " + actionName);
        throw new RuntimeException("Found multiple actions named " + actionName);
    }

    @Override
    public List<CompanionTypeComponent> getCompanionTypes() {
        List<CompanionTypeComponent> result = new ArrayList<>();
        result.addAll(getFeature().getCompanionTypes());
        if (extendedFeature != null) {
            result.addAll(extendedFeature.getCompanionTypes());
        }
        return result;
    }

}
