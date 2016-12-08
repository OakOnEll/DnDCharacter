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
import com.oakonell.dndcharacter.model.AbstractDescriptionComponentModel;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Rob on 11/10/2015.
 */
@Table(name = "companion", id = BaseColumns._ID)
public class Companion extends AbstractDescriptionComponentModel {
    @Column
    public String name;
    @Column
    public String xml;

    @Column
    private String cr;

    @Column
    private double cr_value;

    @Column
    private String creature_size;

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


    @NonNull
    public List<String> getUsableByTypes() {
        List<CompanionType> spellClasses = new Select().from(CompanionType.class).where("companion = ?", getId()).execute();
        List<String> result = new ArrayList<>();
        for (CompanionType each : spellClasses) {
            result.add(each.getType());
        }
        return result;
    }

    public void setUsableByTypes(@NonNull List<String> types) {
        final From query = new Delete().from(CompanionType.class).where("companion = ?", getId());
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public void setDocument(Context context, @Nullable Element doc) {
        super.setDocument(context, doc);

        if (doc != null) {
            String cr = XmlUtils.getElementText(doc, "cr");
            setCr(cr);

            setCrValue(getCRRealValue(cr));

            String size = XmlUtils.getElementText(doc, "size");
            setSize(size);
        } else {
            setCr("");
            setSize("");
        }
    }

    public String getCr() {
        return cr;
    }

    public void setCr(String cr) {
        this.cr = cr;
    }


    public String getSize() {
        return creature_size;
    }

    public void setSize(String creature_size) {
        this.creature_size = creature_size;
    }

    public double getCrValue() {
        return cr_value;
    }

    public void setCrValue(double crValue) {
        this.cr_value = crValue;
    }

    public static double getCRRealValue(String cr) {
        if (cr.contains("/")) {
            String[] parts = cr.split("/");
            if (parts.length != 2) {
                throw new RuntimeException("Invalid CR text : " + cr);
            }
            int numerator = Integer.parseInt(parts[0]);
            int denom = Integer.parseInt(parts[1]);
            double value = ((double) numerator) / denom;
            return value;
        }
        return Integer.parseInt(cr);
    }
}