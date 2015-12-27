package com.oakonell.dndcharacter.views.classes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterClass;
import com.oakonell.dndcharacter.model.SavedChoices;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.model.classes.ApplyClassToCharacterVisitor;

import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class EditClassLevelDialogFragment extends AbstractClassLevelEditDialogFragment {
    CharacterClass characterClass;
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
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        classIndex = getArguments().getInt("classIndex");
        includeHP = getArguments().getBoolean("includeHP");
        return super.onCreateTheView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCharacterLoaded(Character character) {
        characterClass = character.getClasses().get(classIndex);
        AClass aClass = new Select().from(Background.class).where("name = ?", characterClass.getName()).executeSingle();
        setModel(aClass);


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
        ApplyClassToCharacterVisitor.updateClassLevel(getModel(), savedChoices, customChoices, getCharacter(), classIndex, characterClass.getLevel(), hp);
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
}
