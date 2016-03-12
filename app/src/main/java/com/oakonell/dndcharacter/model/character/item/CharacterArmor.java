package com.oakonell.dndcharacter.model.character.item;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.item.ItemType;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 12/8/2015.
 */
public class CharacterArmor extends CharacterItem {

    @Element(required = false)
    private boolean equipped;

    @NonNull
    public ItemType getItemType() {
        return ItemType.ARMOR;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(@NonNull Resources resources, @NonNull Character character, boolean equipped) {
        this.equipped = equipped;
        if (equipped) {
            if (!character.isProficientWith(this) && !hasNonproficientArmorEffect(character, resources)) {
                addNonproficientArmorEffect(character, resources);
            }
        } else if (hasNonproficientArmorEffect(character, resources)) {
            boolean hasAnyNonproficientArmor = false;
            for (CharacterArmor each : character.getArmor()) {
                if (each.isEquipped() && !character.isProficientWith(each)) {
                    hasAnyNonproficientArmor = true;
                }
            }
            if (!hasAnyNonproficientArmor) {
                removeNonproficientArmorEffect(character, resources);
            }
        }
    }

    private static void removeNonproficientArmorEffect(@NonNull Character character, @NonNull Resources resources) {
        CharacterEffect effect = character.getEffectNamed(resources.getString(R.string.nonproficient_armor_effect_name));
        character.removeEffect(effect);
    }

    private static boolean hasNonproficientArmorEffect(@NonNull Character character, @NonNull Resources resources) {
        CharacterEffect effect = character.getEffectNamed(resources.getString(R.string.nonproficient_armor_effect_name));
        return effect != null;
    }

    private static void addNonproficientArmorEffect(@NonNull Character character, @NonNull Resources resources) {
        CharacterEffect effect = new CharacterEffect();
        effect.setName(resources.getString(R.string.nonproficient_armor_effect_name));
        effect.setDescription(resources.getString(R.string.nonproficient_armor_effect_description));
        effect.addContext(new FeatureContextArgument(FeatureContext.SAVING_THROW, "strength"));
        effect.addContext(new FeatureContextArgument(FeatureContext.SAVING_THROW, "dexterity"));

        effect.addContext(new FeatureContextArgument(FeatureContext.WEAPON_ATTACK));
        effect.addContext(new FeatureContextArgument(FeatureContext.SPELL_CAST));

        effect.addContext(new FeatureContextArgument(FeatureContext.SKILL_ROLL, "strength"));
        effect.addContext(new FeatureContextArgument(FeatureContext.SKILL_ROLL, "dexterity"));

        character.addEffect(effect);
    }

    public boolean isShield() {
        // TODO perhaps shield should be a property, to allow different named shield items
        return getName().toUpperCase().contains("SHIELD");
    }
}
