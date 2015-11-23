package com.oakonell.dndcharacter.model.item;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.model.AbstractComponentModel;

/**
 * Created by Rob on 11/22/2015.
 */
@Table(name = "item", id = BaseColumns._ID)
public class ItemRow extends AbstractComponentModel {
    @Column
    ItemType itemType;

    @Column
    public String name;

    @Column
    public String category;

    @Column
    public String xml;


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

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
