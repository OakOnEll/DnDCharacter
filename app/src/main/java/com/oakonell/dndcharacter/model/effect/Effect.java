package com.oakonell.dndcharacter.model.effect;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.model.AbstractComponentModel;

/**
 * Created by Rob on 1/3/2016.
 */
@Table(name = "effect", id = BaseColumns._ID)

public class Effect extends AbstractComponentModel {
    @Column
    private String name;
    @Column
    private String xml;

    @Override
    public String getXml() {
        return xml;
    }

    @Override
    public void setXml(String xml) {
        this.xml = xml;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
