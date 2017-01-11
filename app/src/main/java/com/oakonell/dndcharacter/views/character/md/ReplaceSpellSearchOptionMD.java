package com.oakonell.dndcharacter.views.character.md;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterClass;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.character.spell.CharacterSpell;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 2/1/2016.
 */
public class ReplaceSpellSearchOptionMD extends SearchOptionMD {

    private int levelIndex;
    private int knownLevel;

    public ReplaceSpellSearchOptionMD(CategoryChoicesMD chooseMD, ImageView search, TextView text, ImageView delete) {
        super(chooseMD, search, text, delete);
    }

    @Override
    public void saveChoice(ViewGroup dynamicView, @NonNull List<String> list, Map<String, String> customChoices, SavedChoices savedChoices) {
        if (!isPopulated()) return;
        // store the data as a level,index string
        String selection = knownLevel + "," + levelIndex;
        list.add(selection);
    }

    public void setSelected(String selected, Character character, String className) {
        // the selected will be the level,index string
        String levelAndIndexStrings[] = selected.split(",");
        if (levelAndIndexStrings.length != 2) {
            throw new RuntimeException("Error on replace spell search MD");
        }
        knownLevel = Integer.parseInt(levelAndIndexStrings[0]);
        levelIndex = Integer.parseInt(levelAndIndexStrings[1]);
        CharacterClass theClass = null;
        int classLevel = 0;
        for (CharacterClass aClass : character.getClasses()) {
            if (aClass.getName().equals(className)) {
                classLevel++;
                if (classLevel == knownLevel) {
                    theClass = aClass;
                    break;
                }
            }
        }
        if (theClass == null) {
            throw new RuntimeException("Error on replace spell search MD");
        }
        int spellIndex = 0;
        CharacterSpell theSpell = null;
        for (CharacterSpell aSpell : theClass.getSpells()) {
            if (aSpell.getLevel() == 0) continue;
            if (spellIndex == levelIndex) {
                theSpell = aSpell;
                break;
            }
            spellIndex++;
        }
        if (theSpell == null) {
            throw new RuntimeException("Error on replace spell search MD");
        }
        String name = theSpell.getName();
        super.setSelected(name, character, null);
    }


    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public void setKnownLevel(int knownLevel) {
        this.knownLevel = knownLevel;
    }

    public int getKnownLevel() {
        return knownLevel;
    }
}
