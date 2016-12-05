package com.oakonell.dndcharacter.model.character;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Rob on 1/3/2016.
 */
public class CharacterAbilityDeriver extends AbstractCharacterAbilityDeriver<Character> {
    boolean skipFeatures;

    public CharacterAbilityDeriver(ComponentVisitor visitor) {
        super(visitor);
    }

    public CharacterAbilityDeriver(ComponentVisitor visitor, @SuppressWarnings("SameParameterValue") boolean skipFeatures) {
        super(visitor, skipFeatures);
    }

    //@DebugLog
    @Override
    protected void derive(@NonNull Character character, String comment) {
        CharacterBackground background = character.getBackground();
        if (background != null) {
            getVisitor().visitComponent(background);
        }
        CharacterRace race = character.getRace();
        if (race != null) {
            getVisitor().visitComponent(race);
        }
        List<CharacterClass> classes = character.getClasses();
        if (classes != null) {
            for (CharacterClass eachClass : classes) {
                getVisitor().visitComponent(eachClass);
            }
        }
        super.derive(character, comment);
    }


}
