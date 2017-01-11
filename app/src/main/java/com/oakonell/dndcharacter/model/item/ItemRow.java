package com.oakonell.dndcharacter.model.item;

import android.content.Context;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.model.AbstractDescriptionComponentModel;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/22/2015.
 */
@Table(name = "item", id = BaseColumns._ID)
public class ItemRow extends AbstractDescriptionComponentModel {
    @Column
    private String name;
    @Column
    private String category;
    @Column
    private String xml;
    @Column
    private ItemType itemType;
    @Column
    private boolean canBeProficientIn;
    @Nullable
    @Column
    private String cost;

    @Column
    private String description;

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

    public boolean isCanBeProficientIn() {
        return canBeProficientIn;
    }

    public void setCanBeProficientIn(boolean canBeProficientIn) {
        this.canBeProficientIn = canBeProficientIn;
    }

    @Nullable
    public String getCost() {
        return cost;
    }

    public void setCost(@Nullable String cost) {
        this.cost = cost;
    }

    @Override
    public void setDocument(Context context, @Nullable Element doc) {
        super.setDocument(context, doc);
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
        if (type == ItemType.ARMOR || type == ItemType.ARMOR) {
            setCanBeProficientIn(true);
        } else {
            setCanBeProficientIn("true".equals(XmlUtils.getElementText(doc, "canBeProficient")));
        }
        this.cost = cost;
    }

    public static boolean isPack(String itemName) {
        // TODO better way to mark packs, to avoid hitting DB for each item encountered?
        return itemName.toUpperCase().contains(" PACK");
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
