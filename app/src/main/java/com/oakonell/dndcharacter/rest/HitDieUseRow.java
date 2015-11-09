package com.oakonell.dndcharacter.rest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 11/8/2015.
 */
public class HitDieUseRow {
    public int dieSides;

    public int numDiceRemaining;
    public int totalDice;
    public int numUses;

    public List<Integer> rolls = new ArrayList<>();

}
