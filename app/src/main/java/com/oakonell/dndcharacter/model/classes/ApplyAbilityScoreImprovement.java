package com.oakonell.dndcharacter.model.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by Rob on 2/2/2016.
 */
public class ApplyAbilityScoreImprovement extends ApplyChangesToGenericComponent<CharacterClass> {
    private ApplyAbilityScoreImprovement(Context context, SavedChoices savedChoices, CharacterClass component, @Nullable com.oakonell.dndcharacter.model.character.Character character) {
        super(context, savedChoices, component, character);
    }

    public static <C extends BaseCharacterComponent> void applyASIToCharacter(@NonNull Context context, @NonNull Element element, SavedChoices savedChoices, @NonNull CharacterClass component, @Nullable Character character) {
        ApplyAbilityScoreImprovement newMe = new ApplyAbilityScoreImprovement(context, savedChoices, component, character);
        newMe.visitChildren(element);
    }

    @Override
    protected void visitChoose(@NonNull Element element) {
        String choiceName = element.getAttribute("name");

        if (choiceName.equals("stat")) {
            final SavedChoices savedChoices = getSavedChoices();
            final List<String> statNames = savedChoices.getChoicesFor(choiceName);

            int increase = 2;
            if (statNames.size() == 2) {
                increase = 1;
            }
            for (String statName : statNames) {
                statName = statName.replaceAll(" ", "_");
                statName = statName.toUpperCase();
                StatType type = StatType.valueOf(StatType.class, statName);

                getComponent().addModifier(type, increase);
            }


            return;
        }

        super.visitChoose(element);
    }

}
