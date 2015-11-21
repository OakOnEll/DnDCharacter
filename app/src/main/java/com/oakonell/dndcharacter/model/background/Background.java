package com.oakonell.dndcharacter.model.background;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.model.CharacterBackground;

import org.w3c.dom.Document;

/**
 * Created by Rob on 11/9/2015.
 */
@Table(name = "background", id = BaseColumns._ID)
public class Background extends AbstractComponentModel {
    @Column
    public String name;
    @Column
    public String xml;

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

}
