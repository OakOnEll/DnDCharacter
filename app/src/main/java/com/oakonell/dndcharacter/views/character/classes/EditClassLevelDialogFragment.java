package com.oakonell.dndcharacter.views.character.classes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
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
    public static final String CLASS_INDEX = "classIndex";
    public static final String INCLUDE_HP = "includeHP";
    private CharacterClass characterClass;
    private int classIndex;
    private boolean includeHP;

    @NonNull
    public static EditClassLevelDialogFragment createDialog(int classIndex, boolean includeHP) {
        EditClassLevelDialogFragment newMe = new EditClassLevelDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CLASS_INDEX, classIndex);
        bundle.putBoolean(INCLUDE_HP, includeHP);

        newMe.setArguments(bundle);

        newMe.includeHP = includeHP;
        return newMe;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.edit_class);
    }

    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        classIndex = getArguments().getInt(CLASS_INDEX);
        includeHP = getArguments().getBoolean(INCLUDE_HP);
        return super.onCreateTheView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
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
        ApplyClassToCharacterVisitor.updateClassLevel(getActivity(), getModel(), savedChoices, customChoices, getSubClass(), getSubClassChoices(), getCharacter(), classIndex, characterClass.getLevel(), hp, false);
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
    protected int getCharacterLevel() {
        return classIndex + 1;
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
