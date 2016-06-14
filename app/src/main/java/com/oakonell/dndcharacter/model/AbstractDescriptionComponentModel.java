package com.oakonell.dndcharacter.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Created by Rob on 6/14/2016.
 */
public abstract class AbstractDescriptionComponentModel extends AbstractComponentModel {
    abstract public String getDescription();

    abstract public void setDescription(String description);

    @Override
    public void setDocument(Context context, @Nullable Element doc) {
        super.setDocument(context, doc);

        if (doc != null) {
            String desc = XmlUtils.getElementText(doc, "shortDescription");
            setDescription(desc);
        } else {
            setDescription("");
        }
    }

}
