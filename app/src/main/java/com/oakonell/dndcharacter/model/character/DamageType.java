package com.oakonell.dndcharacter.model.character;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 10/28/2015.
 */
public enum DamageType {
    ACID(R.string.acid_damage), BLUDGEONING(R.string.bludgeoning_damage), COLD(R.string.cold_damage),
    FIRE(R.string.fire_damage), FORCE(R.string.force_damage), Lightning(R.string.lightning_damage),
    NECROTIC(R.string.necrotic_damage), PIERCING(R.string.piercing_damage), POISON(R.string.poison_damage),
    PSYCHIC(R.string.psychic_damage), RADIANT(R.string.radiant_damage), SLASHING(R.string.slashing_damage),
    THUNDER(R.string.thunder_damage);

    private final int stringResId;

    DamageType(int stringResId) {
        this.stringResId = stringResId;
    }

    public int getStringResId() {
        return stringResId;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
