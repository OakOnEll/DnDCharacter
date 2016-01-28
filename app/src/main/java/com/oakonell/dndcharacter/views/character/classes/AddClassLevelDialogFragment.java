package com.oakonell.dndcharacter.views.character.classes;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterClass;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.classes.ApplyClassToCharacterVisitor;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class AddClassLevelDialogFragment extends AbstractClassLevelEditDialogFragment {
    private int classLevel;

    @NonNull
    public static AddClassLevelDialogFragment createDialog() {
        return new AddClassLevelDialogFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.add_class_level);
    }

    @Override
    protected void modelChanged() {
        Integer currentLevel = getCharacter().getClassLevels().get(getModel().getName());
        if (currentLevel == null) currentLevel = 0;
        classLevel = currentLevel + 1;
    }

    @Override
    protected void applyToCharacter(SavedChoices savedChoices, Map<String, String> customChoices) {
        ApplyClassToCharacterVisitor.addClassLevel(getModel(), savedChoices, customChoices, getSubClass(), getSubClassChoices(), getCharacter(), getCharacter().getClasses().size() + 1, classLevel, hp);
    }


    @NonNull
    protected SavedChoices getSavedChoicesFromCharacter(Character character) {
        // a new class level won't have save choices
        return new SavedChoices();
    }

    protected String getCurrentName() {
        List<CharacterClass> classes = getCharacter().getClasses();
        if (classes.isEmpty()) return "";
        // continue with the last class
        return classes.get(classes.size() - 1).getName();
    }


    @Override
    protected int getCurrentHpRoll() {
        return 0;
    }

    @Override
    protected boolean isFirstCharacterLevel() {
        return getCharacter().getCharacterLevel() < 1;
    }

    @Override
    protected int getClassLevel() {
        return classLevel;
    }

    protected boolean canModifySubclass() {
        return true;
    }

}
