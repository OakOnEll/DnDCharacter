package com.oakonell.dndcharacter.model.companion;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.oakonell.dndcharacter.model.spell.Spell;

/**
 * Created by Rob on 1/13/2016.
 */
@Table(name = "companion_type", id = BaseColumns._ID)
public class CompanionType extends Model {
    @Column
    private Companion companion;
    @Column
    private String type;

    public Companion getCompanion() {
        return companion;
    }

    public void setCompanion(Companion companion) {
        this.companion = companion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
