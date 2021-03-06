package com.oakonell.dndcharacter.model.classes;

import android.content.Context;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by Rob on 11/10/2015.
 */
@Table(name = "class", id = BaseColumns._ID)
public class AClass extends AbstractComponentModel {
    @Column
    private String name;
    @Column
    private String xml;
    @Column(name = "parentClass")
    private String parentClass;

    public static Element findLevelElement(@NonNull Element rootClassElement, int level) {
        // TODO change this to instance-based...
        List<Element> elements = XmlUtils.getChildElements(rootClassElement, "level");
        String levelString = level + "";
        for (Element each : elements) {
            String eachLevelString = each.getAttribute("value");

            if (levelString.equals(eachLevelString)) return each;
        }
        return null;
    }

    public static int getHitDieSides(@NonNull Element rootClassElement) {
        // TODO move this..
        String hitDieString = XmlUtils.getElementText(rootClassElement, "hitDice");
        // TODO correct the charClass hitdie
        if (hitDieString == null) {
            throw new RuntimeException("Class has no hit die!");
        }
        int hitDie = Integer.parseInt(hitDieString.substring(2));
        return hitDie;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getParentClass() {
        return parentClass;
    }

    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    @Override
    public void setDocument(Context context,@Nullable Element doc) {
        super.setDocument(context,doc);
        if (doc == null) {
            setParentClass("XmlParseError");
        } else {
            setParentClass(XmlUtils.getElementText(doc, "parent"));
        }
    }

}
