package com.oakonell.dndcharacter.model.spell;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Rob on 1/13/2016.
 */
@Table(name = "spell_class", id = BaseColumns._ID)
public class SpellClass extends Model {
    @Column
    private Spell spell;
    @Column
    private String aClass;

    public Spell getSpell() {
        return spell;
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
    }

    public String getAClass() {
        return aClass;
    }

    public void setAClass(String aClass) {
        this.aClass = aClass;
    }
}
