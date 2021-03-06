package com.oakonell.dndcharacter.model.character;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.oakonell.dndcharacter.model.character.companion.CompanionTypeComponent;
import com.oakonell.dndcharacter.model.character.spell.CharacterSpell;
import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.components.Proficiency;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.expression.context.SimpleVariableContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 2/11/2016.
 */
public interface ICharacterComponent extends ComponentSource {
    void addExtraFormulaVariables(SimpleVariableContext extraVariables, @NonNull AbstractCharacter character);

    String getName();

    int getStatModifier(StatType type);

    @Nullable
    Proficient getSkillProficient(SkillType type);

    @Nullable
    Proficient getSaveProficient(StatType type);

    @NonNull
    List<String> getLanguages();

    @NonNull
    List<Proficiency> getToolProficiencies(ProficiencyType type);

    @Nullable
    String getAcFormula();

    @Nullable
    String getHpFormula();

    boolean isBaseArmor();

    @Nullable
    String getBaseAcFormula();

    @Nullable
    String getModifyingAcFormula();

    @NonNull
    List<CharacterSpell> getCantrips();

    @NonNull
    List<CharacterSpell> getSpells();


    int getSpeed(AbstractCharacter character, SpeedType type);

    Boolean isBaseSpeed(SpeedType type);

    int getInitiativeMod(AbstractCharacter character);

    int getPassivePerceptionMod(AbstractCharacter character);

    void addFeatureInfo(Map<String, FeatureInfo> map, AbstractCharacter character);

    Collection<CompanionTypeComponent> getCompanionTypes();
}
