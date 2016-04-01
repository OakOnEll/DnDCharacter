package com.oakonell.dndcharacter.model.character;

import android.content.Context;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.simpleframework.xml.Serializer;
import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * Created by Rob on 11/2/2015.
 */
@Table(name = "character", id = BaseColumns._ID)
public class CharacterRow extends AbstractComponentModel {
    @Column
    public String name;
    @Column
    public String classesString;
    @Nullable
    @Column
    public String race_display_name;
    @Column
    public String hp;

    @Column()
    public String xml;
    @Column()
    public Date last_viewed;
    @Column()
    public Date last_updated;

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

    // Used on an eg, import
    public void setDocument(@Nullable Element doc) {
        if (doc != null) {
            setXml(XmlUtils.prettyPrint(doc));
            setName(XmlUtils.getElementText(doc, "name"));

            // TODO
            classesString = "Will update after next save...";
            race_display_name = "Will update after next save...";
            hp = "Will update after next save...";
            //hp = context.getString(R.string.fraction_d_slash_d, character.getHP(), character.getMaxHP());
            last_updated = new Date();
        } else {
            setName("xml parse error- no name");
        }

    }

    @NonNull
    public static String saveCharacter(Context context, Serializer serializer, Character character, long id) {
        String xml = characterToXmlString(serializer, character);
        CharacterRow row;
        String action;
        if (id >= 0) {
            row = CharacterRow.load(CharacterRow.class, id);
            action = "Updated";
        } else {
            row = new CharacterRow();
            action = "Added";
        }
        row.classesString = character.getClassesString();
        row.race_display_name = character.getDisplayRaceName();
        row.hp = context.getString(R.string.fraction_d_slash_d, character.getHP(), character.getMaxHP());
        row.name = character.getName();
        row.xml = xml;
        row.last_updated = new Date();

        id = row.save();
        return action;
    }

    public static String characterToXmlString(Serializer serializer, Character character) {
        OutputStream out;
        try {
            out = new ByteArrayOutputStream();
            serializer.write(character, out);
            out.close();
        } catch (Exception e) {
            throw new RuntimeException("Error writing character xml", e);
        }
        return out.toString();
    }
}
