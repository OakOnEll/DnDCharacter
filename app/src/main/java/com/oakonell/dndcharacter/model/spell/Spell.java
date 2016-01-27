package com.oakonell.dndcharacter.model.spell;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Rob on 1/12/2016.
 */
@Table(name = "spell", id = BaseColumns._ID)
public class Spell extends AbstractComponentModel {
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
    private String school;


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

    public List<String> getUsableByClasses() {
        List<SpellClass> spellClasses = new Select().from(SpellClass.class).where("spell = ?", this).execute();
        List<String> result = new ArrayList<>();
        for (SpellClass each : spellClasses) {
            result.add(each.getAClass());
        }
        return result;
    }

    public void setUsableByClasses(List<String> classes) {
        new Delete().from(SpellClass.class).where("spell = ?", this).execute();
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

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }


    @Override
    public void setDocument(Element doc) {
        super.setDocument(doc);

        if (doc != null) {

            setSchool(XmlUtils.getElementText(doc, "school"));
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
            setSchool("");
            setConcentration(false);
            setRitual(false);
            setLevel(-1);
        }
    }

    protected void addRelatedChildren(Element doc) {
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


}
