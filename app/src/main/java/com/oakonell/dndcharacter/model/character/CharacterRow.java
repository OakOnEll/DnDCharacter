package com.oakonell.dndcharacter.model.character;

import android.content.Context;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractComponentModel;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
    public void setDocument(Context context, @Nullable Element doc) {
        super.setDocument(context, doc);
        if (doc != null) {
            last_updated = new Date();

            Serializer serializer = new Persister();
            InputStream input;
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Source xmlSource = new DOMSource(doc);
                Result outputTarget = new StreamResult(outputStream);
                TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
                InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

                Character character = serializer.read(Character.class, is);
                is.close();

                updateFromCharacter(context, character);
            } catch (Exception e) {
                classesString = "Had a serialization error...";
                race_display_name = "Will update after next save...";
                hp = "Will update after next save...";
                throw new RuntimeException("Error saving character from xml doc",e);
            }
        }
    }

    protected void updateFromCharacter(Context context, Character character) {
        classesString = character.getClassesString();
        race_display_name = character.getDisplayRaceName();
        hp = context.getString(R.string.fraction_d_slash_d, character.getHP(), character.getMaxHP());
    }

    public static class SaveResult {
        public String action;
        public long id;
    }

    @NonNull
    public static SaveResult saveCharacter(Context context, Serializer serializer, Character character, long id) {
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
        row.updateFromCharacter(context, character);
        row.name = character.getName();
        row.xml = xml;
        row.last_updated = new Date();

        SaveResult result = new SaveResult();
        result.id = row.save();
        result.action = action;
        return result;
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
