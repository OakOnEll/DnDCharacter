package com.oakonell.dndcharacter.model.race;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.w3c.dom.Document;

/**
 * Created by Rob on 11/10/2015.
 */
@Table(name = "race", id = BaseColumns._ID)
public class Race extends Model {
    @Column
    public String name;
    @Column
    public String xml;
}
