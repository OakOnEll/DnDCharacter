package com.oakonell.dndcharacter.model.character;

import com.oakonell.dndcharacter.model.character.item.CharacterArmor;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.model.character.item.CharacterWeapon;

import java.util.List;

/**
 * Created by Rob on 1/3/2016.
 */
public abstract class CharacterAbilityDeriver {
    boolean skipFeatures;

    public CharacterAbilityDeriver() {

    }

    public CharacterAbilityDeriver(boolean skipFeatures) {
        this.skipFeatures = skipFeatures;
    }

    //@DebugLog
    void derive(Character character, String comment) {
        CharacterBackground background = character.getBackground();
        if (background != null) {
            visitComponent(background);
        }
        CharacterRace race = character.getRace();
        if (race != null) {
            visitComponent(race);
        }
        List<CharacterClass> classes = character.getClasses();
        if (classes != null) {
            for (CharacterClass eachClass : classes) {
                visitComponent(eachClass);
            }
        }
        List<CharacterEffect> effects = character.getEffects();
        if (effects != null) {
            for (CharacterEffect each : effects) {
                visitComponent(each);
            }
        }
        List<CharacterItem> items = character.getItems();
        if (items != null) {
            for (CharacterItem each : items) {
                visitComponent(each);
            }
        }
        List<CharacterWeapon> weapons = character.getWeapons();
        if (weapons != null) {
            for (CharacterWeapon each : weapons) {
                visitComponent(each);
            }
        }
        List<CharacterArmor> armor = character.getArmor();
        if (armor != null) {
            for (CharacterArmor each : armor) {
                visitComponent(each);
            }
        }
        if (!skipFeatures) {
            List<FeatureInfo> features = character.getFeatureInfos();
            if (features != null) {
                for (FeatureInfo each : features) {
                    visitComponent(each.feature);
                }
            }
        }
    }

    abstract void visitComponent(BaseCharacterComponent component);
}
