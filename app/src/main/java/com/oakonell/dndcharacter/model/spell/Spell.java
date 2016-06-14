package com.oakonell.dndcharacter.model.spell;

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
import com.oakonell.dndcharacter.model.AbstractDescriptionComponentModel;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Rob on 1/12/2016.
 */
@Table(name = "spell", id = BaseColumns._ID)
public class Spell extends AbstractDescriptionComponentModel {
    @Column
    private String name;
    @Column
    private String xml;

    @Column
    private int level;
    @Column
    private boolean concentration;
    @Column
    private boolean ritual;
    @Column
    private SpellSchool school;

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
    public List<String> getUsableByClasses() {
        List<SpellClass> spellClasses = new Select().from(SpellClass.class).where("spell = ?", getId()).execute();
        List<String> result = new ArrayList<>();
        for (SpellClass each : spellClasses) {
            result.add(each.getAClass());
        }
        return result;
    }

    public void setUsableByClasses(@NonNull List<String> classes) {
        final From query = new Delete().from(SpellClass.class).where("spell = ?", getId());
//        int count = query.count();
//        Log.i("Spell", "Deleted " + count + " spellClass rows for spell = " + getName());
        query.execute();
//        count = query.count();
//        assert count ==0;

        for (String each : classes) {
            SpellClass spellClass = new SpellClass();
            spellClass.setAClass(each);
            spellClass.setSpell(this);
            spellClass.save();
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isConcentration() {
        return concentration;
    }

    public void setConcentration(boolean concentration) {
        this.concentration = concentration;
    }

    public boolean isRitual() {
        return ritual;
    }

    public void setRitual(boolean ritual) {
        this.ritual = ritual;
    }

    public SpellSchool getSchool() {
        return school;
    }

    public void setSchool(SpellSchool school) {
        this.school = school;
    }


    @Override
    public void setDocument(Context context, @Nullable Element doc) {
        super.setDocument(context, doc);

        if (doc != null) {

            String schoolName = XmlUtils.getElementText(doc, "school");
            schoolName = schoolName.toUpperCase();
            SpellSchool school = EnumHelper.stringToEnum(schoolName, SpellSchool.class);
            setSchool(school);

            setConcentration("true".equals(XmlUtils.getElementText(doc, "concentration")));
            setRitual("true".equals(XmlUtils.getElementText(doc, "ritual")));

            String levelString = XmlUtils.getElementText(doc, "level");
            if (levelString != null) {
                int level = Integer.parseInt(levelString);
                setLevel(level);
            } else {
                setLevel(-1);
            }
        } else {
            setSchool(null);
            setConcentration(false);
            setRitual(false);
            setLevel(-1);
        }
    }

    protected void addRelatedChildren(@Nullable Element doc) {
        if (doc != null) {
            Element classesElement = XmlUtils.getElement(doc, "classes");
            if (classesElement != null) {
                List<String> classes = new ArrayList<>();
                for (Element each : XmlUtils.getChildElements(classesElement, "class")) {
                    classes.add(each.getTextContent());
                }
                setUsableByClasses(classes);
            } else {
                setUsableByClasses(Collections.<String>emptyList());
            }
        } else {
            setUsableByClasses(Collections.<String>emptyList());
        }
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
