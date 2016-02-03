package com.oakonell.dndcharacter.model.character;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 10/28/2015.
 */
public enum DamageType {
    Acid(R.string.acid_damage), Bludgeoning(R.string.bludgeoning_damage), Cold(R.string.cold_damage),
    Fire(R.string.fire_damage), Force(R.string.force_damage), Lightning(R.string.lightning_damage),
    Necrotic(R.string.necrotic_damage), Piercing(R.string.piercing_damage), Poison(R.string.poison_damage),
    Psychic(R.string.psychic_damage), Radiant(R.string.radiant_damage), Slashing(R.string.slashing_damage),
    Thunder(R.string.thunder_damage);

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
