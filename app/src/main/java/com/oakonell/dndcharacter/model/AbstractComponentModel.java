package com.oakonell.dndcharacter.model;

import com.activeandroid.Model;

/**
 * Created by Rob on 11/20/2015.
 */
public abstract class AbstractComponentModel extends Model{
    abstract public String getName();
    abstract public String getXml();
    abstract public void setXml(String xml);
    abstract public void setName(String name);
}
