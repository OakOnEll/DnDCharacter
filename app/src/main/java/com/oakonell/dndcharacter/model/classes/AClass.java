package com.oakonell.dndcharacter.model.classes;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.w3c.dom.Document;

/**
 * Created by Rob on 11/10/2015.
 */
@Table(name = "class", id = BaseColumns._ID)
public class AClass extends Model {
    @Column
    public String name;
    @Column
    public String xml;
}
