package com.oakonell.dndcharacter.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 11/8/2015.
 */
public class ShortRestRequest extends AbstractRestRequest {


    private int healing;
    private Map<Integer, Integer> hitDieUses = new HashMap<>();

    public void addHitDiceUsed(int dieSides, int numUses) {
        Integer uses = hitDieUses.get(dieSides);
        if (uses == null) uses = 0;
        uses += numUses;
        hitDieUses.put(dieSides, uses);
    }

    public int getHealing() {
        return healing;
    }

    public void setHealing(int healing) {
        this.healing = healing;
    }

    public Map<Integer, Integer> getHitDieUses() {
        return hitDieUses;
    }

//    public int getHitDieUses(int die) {
//        Integer uses = hitDieUses.get(die);
//        if (uses == null) uses = 0;
//        return uses;
//    }
}
