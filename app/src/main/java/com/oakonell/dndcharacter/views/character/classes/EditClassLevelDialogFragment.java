package com.oakonell.dndcharacter.views.character.classes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterClass;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.model.classes.ApplyClassToCharacterVisitor;

import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class EditClassLevelDialogFragment extends AbstractClassLevelEditDialogFragment {
    private CharacterClass characterClass;
    private int classIndex;
    private boolean includeHP;

    public static EditClassLevelDialogFragment createDialog(int classIndex, boolean includeHP) {
        EditClassLevelDialogFragment newMe = new EditClassLevelDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("classIndex", classIndex);
        bundle.putBoolean("includeHP", includeHP);

        newMe.setArguments(bundle);

        newMe.includeHP = includeHP;
        return newMe;
    }

    @Override
    protected String getTitle() {
        return "Edit your Class";
    }

    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        classIndex = getArguments().getInt("classIndex");
        includeHP = getArguments().getBoolean("includeHP");
        return super.onCreateTheView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCharacterLoaded(Character character) {
        characterClass = character.getClasses().get(classIndex);
        AClass aClass = new Select().from(AClass.class).where("name = ?", characterClass.getName()).executeSingle();
        setModel(aClass);

        final String subclassName = character.getSubclassFor(aClass.getName());
        setSubClassName(subclassName, characterClass.getSubClassChoices());

        super.onCharacterLoaded(character);
    }

    @Override
    protected boolean includeHp() {
        return includeHP;
    }

    protected boolean allowMainComponentChange() {
        return false;
    }

    @Override
    protected void applyToCharacter(SavedChoices savedChoices, Map<String, String> customChoices) {
        ApplyClassToCharacterVisitor.updateClassLevel(getModel(), savedChoices, customChoices, getSubClass(), getSubClassChoices(), getCharacter(), classIndex, characterClass.getLevel(), hp);
    }


    protected SavedChoices getSavedChoicesFromCharacter(Character character) {
        return characterClass.getSavedChoices();
    }

    protected String getCurrentName() {
        return characterClass.getName();
    }


    public CharacterClass getCharacterClass() {
        return characterClass;
    }


    @Override
    protected int getCurrentHpRoll() {
        return characterClass.getHpRoll();
    }

    @Override
    protected boolean isFirstCharacterLevel() {
        return classIndex == 0;
    }

    @Override
    protected int getClassLevel() {
        return characterClass.getLevel();
    }

    protected boolean canModifySubclass() {
        return isLastClassLevel();
    }

    private boolean isLastClassLevel() {
        return getCharacter().getClassLevels().get(getCurrentName()) == getClassLevel();
    }

}
