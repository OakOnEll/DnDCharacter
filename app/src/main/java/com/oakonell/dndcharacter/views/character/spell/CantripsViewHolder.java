package com.oakonell.dndcharacter.views.character.spell;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;

/**
 * Created by Rob on 1/24/2016.
 */
public class CantripsViewHolder extends SpellLevelsAdapter.AbstractSpellLevelViewHolder {
    public CantripsViewHolder(View itemView) {
        super(itemView);
    }

    @NonNull
    @Override
    protected CantripAdapter newAdapter(SpellsFragment context, com.oakonell.dndcharacter.model.character.Character.SpellLevelInfo info) {
        return new CantripAdapter(context, info);
    }

    protected boolean cantripsOnly() {
        return true;
    }

//        @Override
//        public void bind(SpellsFragment context, SpellLevelsAdapter adapter, Character.SpellLevelInfo info) {
//
//        }


    public static class CantripAdapter extends AbstractSpellAdapter<CantripViewHolder> {
        CantripAdapter(SpellsFragment context, Character.SpellLevelInfo spellLevelInfo) {
            super(context, spellLevelInfo);
        }

        @NonNull
        @Override
        public AbstractSpellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_char_cantrip_row, parent, false);
            return new CantripViewHolder(newView);
        }
    }

    public static class CantripViewHolder extends AbstractSpellAdapter.AbstractSpellViewHolder {
        public CantripViewHolder(View itemView) {
            super(itemView);
        }
    }
}
