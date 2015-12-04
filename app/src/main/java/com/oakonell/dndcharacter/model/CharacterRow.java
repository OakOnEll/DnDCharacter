package com.oakonell.dndcharacter.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by Rob on 11/2/2015.
 */
@Table(name = "character", id = BaseColumns._ID)
public class CharacterRow extends AbstractComponentModel {
    @Column
    public String name;
    @Column
    public String classesString;
    @Column()
    public String xml;
    @Column()
    public Date last_viewed;
    @Column()
    public Date last_updated;

    @Override
    public String getName() {
        return name;
    }

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
}
