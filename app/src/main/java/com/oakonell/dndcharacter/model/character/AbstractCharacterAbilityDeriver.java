package com.oakonell.dndcharacter.model.character;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.item.CharacterArmor;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.model.character.item.CharacterWeapon;

import java.util.Collection;
import java.util.List;

/**
 * Created by Rob on 11/27/2016.
 */

public class AbstractCharacterAbilityDeriver<A extends AbstractCharacter> {
    private final ComponentVisitor visitor;
    boolean skipFeatures;

    public AbstractCharacterAbilityDeriver(ComponentVisitor visitor) {
        this.visitor = visitor;
    }

    public AbstractCharacterAbilityDeriver(ComponentVisitor visitor, @SuppressWarnings("SameParameterValue") boolean skipFeatures) {
        this.visitor = visitor;
        this.skipFeatures = skipFeatures;
    }

    //@DebugLog
    void derive(@NonNull A character, String comment) {
        List<CharacterEffect> effects = character.getEffects();
        if (effects != null) {
            for (CharacterEffect each : effects) {
                getVisitor().visitComponent(each);
            }
        }
        List<CharacterItem> items = character.getItems();
        if (items != null) {
            for (CharacterItem each : items) {
                getVisitor().visitComponent(each);
            }
        }
        List<CharacterWeapon> weapons = character.getWeapons();
        if (weapons != null) {
            for (CharacterWeapon each : weapons) {
                getVisitor().visitComponent(each);
            }
        }
        List<CharacterArmor> armor = character.getArmor();
        if (armor != null) {
            for (CharacterArmor each : armor) {
                getVisitor().visitComponent(each);
            }
        }
        if (!skipFeatures) {
            Collection<FeatureInfo> features = character.getFeatureInfos();
            if (features != null) {
                for (FeatureInfo each : features) {
                    getVisitor().visitComponent(each);
                }
            }
        }
    }

    public ComponentVisitor getVisitor() {
        return visitor;
    }
}
