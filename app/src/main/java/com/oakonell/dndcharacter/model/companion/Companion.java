package com.oakonell.dndcharacter.model.companion;

import android.content.Context;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.model.spell.SpellClass;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Rob on 11/10/2015.
 */
@Table(name = "companion", id = BaseColumns._ID)
public class Companion extends AbstractComponentModel {
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }


    @NonNull
    public List<String> getUsableByTypes() {
        List<SpellClass> spellClasses = new Select().from(CompanionType.class).where("companion = ?", getId()).execute();
        List<String> result = new ArrayList<>();
        for (SpellClass each : spellClasses) {
            result.add(each.getAClass());
        }
        return result;
    }

    public void setUsableByTypes(@NonNull List<String> types) {
        final From query = new Delete().from(SpellClass.class).where("companion = ?", getId());
//        int count = query.count();
//        Log.i("Spell", "Deleted " + count + " spellClass rows for spell = " + getName());
        query.execute();
//        count = query.count();
//        assert count ==0;

        for (String each : types) {
            CompanionType companionType = new CompanionType();
            companionType.setType(each);
            companionType.setCompanion(this);
            companionType.save();
        }
    }


    protected void addRelatedChildren(@Nullable Element doc) {
        if (doc != null) {
            Element classesElement = XmlUtils.getElement(doc, "types");
            if (classesElement != null) {
                List<String> classes = new ArrayList<>();
                for (Element each : XmlUtils.getChildElements(classesElement, "type")) {
                    classes.add(each.getTextContent());
                }
                setUsableByTypes(classes);
            } else {
                setUsableByTypes(Collections.<String>emptyList());
            }
        } else {
            setUsableByTypes(Collections.<String>emptyList());
        }
    }

}