package com.oakonell.dndcharacter.model.background;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.model.CharacterBackground;

import org.w3c.dom.Document;

/**
 * Created by Rob on 11/9/2015.
 */
@Table(name = "background", id = BaseColumns._ID)
public class Background extends Model {
    @Column
    public String name;
    @Column
    public String xml;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXml() {
        return xml;
    }


}
