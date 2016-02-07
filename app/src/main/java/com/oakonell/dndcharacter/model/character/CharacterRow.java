package com.oakonell.dndcharacter.model.character;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.model.AbstractComponentModel;

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
    @Column
    public String race_display_name;
    @Column
    public String hp;

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
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getXml() {
        return xml;
    }

    @Override
    public void setXml(String xml) {
        this.xml = xml;
    }
}
