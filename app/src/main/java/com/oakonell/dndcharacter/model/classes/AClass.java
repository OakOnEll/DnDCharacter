package com.oakonell.dndcharacter.model.classes;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.model.AbstractComponentModel;

/**
 * Created by Rob on 11/10/2015.
 */
@Table(name = "class", id = BaseColumns._ID)
public class AClass extends AbstractComponentModel {
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
