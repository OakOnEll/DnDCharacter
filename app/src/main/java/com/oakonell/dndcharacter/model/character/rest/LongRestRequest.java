package com.oakonell.dndcharacter.model.character.rest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 11/8/2015.
 */
public class LongRestRequest extends AbstractRestRequest {
    private int healing;
    private final Map<Integer, Integer> hitDieRestores = new HashMap<>();

    public int getHealing() {
        return healing;
    }

    public void setHealing(int healing) {
        this.healing = healing;
    }

    public void restoreHitDice(int dieSides, int numDiceToRestoreRequest) {
        Integer numToRestore = hitDieRestores.get(dieSides);
        if (numToRestore == null) numToRestore = 0;
        numToRestore += numDiceToRestoreRequest;
        hitDieRestores.put(dieSides, numToRestore);

    }

    public Map<Integer, Integer> getHitDiceToRestore() {
        return hitDieRestores;
    }
}
