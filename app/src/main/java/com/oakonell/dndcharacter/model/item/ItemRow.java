package com.oakonell.dndcharacter.model.item;

import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/22/2015.
 */
@Table(name = "item", id = BaseColumns._ID)
public class ItemRow extends AbstractComponentModel {
    @Column
    private String name;
    @Column
    private String category;
    @Column
    private String xml;
    @Column
    private ItemType itemType;
    @Nullable
    @Column
    private String cost;

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

    @Override
    public void setDocument(@Nullable Element doc) {
        super.setDocument(doc);
        String category;
        ItemType type;
        String cost;
        if (doc == null) {
            category = "XmlParseError";
            type = null;
            cost = "?";
        } else {
            category = XmlUtils.getElementText(doc, "category");
            String itemTypeString = XmlUtils.getElementText(doc, "itemType");
            if (itemTypeString == null) {
                throw new RuntimeException("Item has no itemType set!");
            }
            cost = XmlUtils.getElementText(doc, "cost");
            type = EnumHelper.stringToEnum(itemTypeString, ItemType.class);
        }
        setCategory(category);
        setItemType(type);
        this.cost = cost;
    }
}
