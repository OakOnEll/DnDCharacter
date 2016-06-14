package com.oakonell.dndcharacter.model.feat;

import android.content.Context;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.spell.SpellSchool;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 1/3/2016.
 */
@Table(name = "feat", id = BaseColumns._ID)

public class Feat extends AbstractComponentModel {
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

    @Override
    public void setDocument(Context context, @Nullable Element doc) {
        super.setDocument(context, doc);

        if (doc != null) {
            String desc = XmlUtils.getElementText(doc, "shortDescription");
            description = desc;
        } else {
            description = "";
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
