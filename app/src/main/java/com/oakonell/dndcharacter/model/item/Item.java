package com.oakonell.dndcharacter.model.item;

import com.activeandroid.annotation.Column;
import com.oakonell.dndcharacter.model.DamageType;

import org.simpleframework.xml.Element;

import java.math.BigDecimal;

/**
 * Created by Rob on 11/22/2015.
 */
public class Item {
    @Element(required = false)
    public String name;

    @Element(required = false)
    ItemType itemType;

    @Element(required = false)
    public String category;


    @Element(required = false)
    public boolean isContainer;

    @Element(required = false)
    public String description;

    @Element(required = false)
    public BigDecimal costInGp;

    @Element(required = false)
    public BigDecimal weight;

    // for armor
    @Element(required = false)
    public String acFormula;

    @Element(required = false)
    public int strengthRequirement;

    @Element(required = false)
    public boolean stealthDisadvantage;

    // for weapons
    @Element(required = false)
    public String damage;

    @Element(required = false)
    public DamageType damageType;

    // TODO this needs more fleshing out, for range, and different damage amounts(versatile)
    @Column
    @Element(required = false)
    String properties;

}
