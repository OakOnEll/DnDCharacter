package com.oakonell.dndcharacter.views.character.spell;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.expression.context.SimpleVariableContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 1/24/2016.
 */
public class CasterInfoAdapter extends RecyclerView.Adapter<CasterInfoAdapter.CasterViewHolder> {
    private final SpellsFragment context;
    private List<Character.CastingClassInfo> list;

    CasterInfoAdapter(SpellsFragment context, Character character) {
        this.context = context;
        this.list = new ArrayList<>(character.getCasterClassInfo().values());
    }

    @Override
    public CasterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_caster_info_row, parent, false);
        return new CasterViewHolder(newView);

    }

    @Override
    public void onBindViewHolder(CasterViewHolder holder, int position) {
        Character.CastingClassInfo info = list.get(position);
        holder.bind(context, this, info);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void reloadList(Character character) {
        this.list = new ArrayList<>(character.getCasterClassInfo().values());
        notifyDataSetChanged();
    }


    public class CasterViewHolder extends BindableComponentViewHolder<Character.CastingClassInfo, SpellsFragment, CasterInfoAdapter> {

        private final TextView class_name;
        private final TextView casting_stat;
        private final TextView cantrips_known;
        private final TextView spells_known;
        private final TextView spells_prepared;
        private final TextView max_spell_level;

        public CasterViewHolder(View itemView) {
            super(itemView);
            class_name = (TextView) itemView.findViewById(R.id.class_name);
            casting_stat = (TextView) itemView.findViewById(R.id.casting_stat);
            cantrips_known = (TextView) itemView.findViewById(R.id.cantrips_known);
            spells_known = (TextView) itemView.findViewById(R.id.spells_known);
            spells_prepared = (TextView) itemView.findViewById(R.id.spells_prepared);
            max_spell_level = (TextView) itemView.findViewById(R.id.max_spell_level);
        }

        @Override
        public void bind(SpellsFragment context, CasterInfoAdapter adapter, Character.CastingClassInfo info) {
            class_name.setText(info.getClassName() + "(" + info.getClassLevel() + ")");
            casting_stat.setText(info.getCastingStat().toString() + "(" + context.getCharacter().getStatBlock(info.getCastingStat()).getModifier() + ")");

            int cantrips = context.getCharacter().getCantripsForClass(info.getClassName());
            int maxCantrips = context.getCharacter().evaluateFormula(info.getKnownCantrips(), null);
            if (maxCantrips > 0) {
                cantrips_known.setText(cantrips + "/" + maxCantrips);
            } else {
                cantrips_known.setText(cantrips + "");
            }
            if (cantrips > maxCantrips) {
                cantrips_known.setError("Too many cantrips for " + info.getClassName());
            } else {
                cantrips_known.setError(null);
            }

            int spellsKnown = context.getCharacter().getSpellsKnownForClass(info.getClassName());
            final String maxKnownSpellsFormula = info.getKnownSpells();
            if (maxKnownSpellsFormula != null && maxKnownSpellsFormula.length() > 0) {
                int maxKnown = context.getCharacter().evaluateFormula(maxKnownSpellsFormula, null);
                spells_known.setText(spellsKnown + "/" + maxKnown);
                if (spellsKnown > maxKnown) {
                    spells_known.setError("Too many known spells for " + info.getClass());
                } else {
                    spells_known.setError(null);
                }
            } else {
                spells_known.setText(spellsKnown + "");
            }


            final String maxPreparedSpellsFormula = info.getPreparedSpells();
            if (maxPreparedSpellsFormula != null && maxPreparedSpellsFormula.length() > 0) {
                spells_prepared.setVisibility(View.VISIBLE);
                int spellsPrepared = context.getCharacter().getSpellsPreparedForClass(info.getClassName());
                SimpleVariableContext variableContext = new SimpleVariableContext();
                variableContext.setNumber("classLevel", info.getClassLevel());
                int maxPrepared = context.getCharacter().evaluateFormula(maxPreparedSpellsFormula, variableContext);
                spells_prepared.setText(spellsPrepared + "/" + maxPrepared);

                if (spellsPrepared > maxPrepared) {
                    spells_prepared.setError("Too many prepared spells for " + info.getClass());
                } else {
                    spells_prepared.setError(null);
                }
            } else {
                spells_prepared.setVisibility(View.INVISIBLE);
            }


            max_spell_level.setText("" + info.getMaxSpellLevel());
        }
    }
}
