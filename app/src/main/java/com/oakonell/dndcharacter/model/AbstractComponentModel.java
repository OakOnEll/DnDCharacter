package com.oakonell.dndcharacter.model;

import com.activeandroid.Model;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 11/20/2015.
 */
public abstract class AbstractComponentModel extends Model {
    abstract public String getName();

    abstract public void setName(String name);

    abstract public String getXml();

    abstract public void setXml(String xml);

    public void setDocument(Element doc) {
        if (doc != null) {
            setXml(XmlUtils.prettyPrint(doc));
            setName(XmlUtils.getElementText(doc, "name"));
        } else {
            setName("xml parse error- no name");
        }

    }
}
