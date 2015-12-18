package com.oakonell.dndcharacter.views.classes;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterClass;
import com.oakonell.dndcharacter.model.SavedChoices;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.model.classes.ApplyClassToCharacterVisitor;
import com.oakonell.dndcharacter.views.ComponentLaunchHelper;

import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class EditClassLevelDialogFragment extends AbstractClassLevelEditDialogFragment {
    CharacterClass characterClass;
    private int classIndex;
    private boolean includeHP;

    public static EditClassLevelDialogFragment createDialog(Character character, CharacterClass characterClass, int classIndex, ComponentLaunchHelper.OnDialogDone onDone, boolean includeHP) {
        EditClassLevelDialogFragment newMe = new EditClassLevelDialogFragment();
        AClass aClass = new Select().from(Background.class).where("name = ?", characterClass.getName()).executeSingle();
        newMe.setModel(aClass);
        newMe.setClassIndex(classIndex);
        newMe.setCharacterClass(characterClass);
        newMe.setCharacter(character);
        newMe.setOnDone(onDone);
        newMe.includeHP = includeHP;
        return newMe;
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


    public void setCharacterClass(CharacterClass characterClass) {
        this.characterClass = characterClass;
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
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
