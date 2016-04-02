package com.oakonell.dndcharacter.model.race;

import android.content.Context;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/10/2015.
 */
@Table(name = "race", id = BaseColumns._ID)
public class Race extends AbstractComponentModel {
    @Column
    public String name;
    @Column
    public String xml;
    @Column(name = "parentRace")
    private String parentRace;

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

    public String getParentRace() {
        return parentRace;
    }

    public void setParentRace(String parentRace) {
        this.parentRace = parentRace;
    }

    @Override
    public void setDocument(Context context, @Nullable Element doc) {
        super.setDocument(context, doc);
        if (doc == null) {
            setParentRace("XmlParseError");
        } else {
            setParentRace(XmlUtils.getElementText(doc, "parent"));
        }
    }
}
