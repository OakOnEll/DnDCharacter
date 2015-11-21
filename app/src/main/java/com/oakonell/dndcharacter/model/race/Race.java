package com.oakonell.dndcharacter.model.race;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.model.AbstractComponentModel;

import org.w3c.dom.Document;

/**
 * Created by Rob on 11/10/2015.
 */
@Table(name = "race", id = BaseColumns._ID)
public class Race extends AbstractComponentModel {
    @Column
    public String name;
    @Column
    public String xml;
    @Column(name = "parent_race")
    private String parentRace;

    @Override
    public String getXml() {
        return xml;
    }

    @Override
    public void setXml(String xml) {
        this.xml = xml;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }


    public String getParentRace() {
        return parentRace;
    }

    public void setParentRace(String parentRace) {
        this.parentRace = parentRace;
    }
}
