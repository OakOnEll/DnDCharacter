package com.oakonell.dndcharacter.model.feat;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.model.AbstractDescriptionComponentModel;

/**
 * Created by Rob on 1/3/2016.
 */
@Table(name = "feat", id = BaseColumns._ID)

public class Feat extends AbstractDescriptionComponentModel {
    @Column
    private String name;
    @Column
    private String xml;

    @Column
    private String description;

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


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
