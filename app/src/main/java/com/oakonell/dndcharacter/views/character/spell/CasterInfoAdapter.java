package com.oakonell.dndcharacter.views.character.spell;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.stats.StatBlock;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.utils.NumberUtils;
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

    CasterInfoAdapter(SpellsFragment context, @NonNull Character character) {
        this.context = context;
        this.list = new ArrayList<>(character.getCasterClassInfo());
    }

    @NonNull
    @Override
    public CasterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_caster_info_row, parent, false);
        return new CasterViewHolder(newView);

    }

    @Override
    public void onBindViewHolder(@NonNull CasterViewHolder holder, int position) {
        Character.CastingClassInfo info = list.get(position);
        holder.bind(context, this, info);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void reloadList(@NonNull Character character) {
        this.list = new ArrayList<>(character.getCasterClassInfo());
        notifyDataSetChanged();
    }


    public class CasterViewHolder extends BindableComponentViewHolder<Character.CastingClassInfo, SpellsFragment, CasterInfoAdapter> {

        @NonNull
        private final TextView class_name;
        @NonNull
        private final TextView casting_stat;
        @NonNull
        private final TextView cantrips_known;
        @NonNull
        private final TextView spells_known;
        @NonNull
        private final TextView spells_prepared;
        @NonNull
        private final TextView max_spell_level;

        public CasterViewHolder(@NonNull View itemView) {
            super(itemView);
            class_name = (TextView) itemView.findViewById(R.id.class_name);
            casting_stat = (TextView) itemView.findViewById(R.id.casting_stat);
            cantrips_known = (TextView) itemView.findViewById(R.id.cantrips_known);
            spells_known = (TextView) itemView.findViewById(R.id.spells_known);
            spells_prepared = (TextView) itemView.findViewById(R.id.spells_prepared);
            max_spell_level = (TextView) itemView.findViewById(R.id.max_spell_level);
        }

        @Override
        public void bind(@NonNull SpellsFragment context, CasterInfoAdapter adapter, @NonNull Character.CastingClassInfo info) {
            final String owningClassName = info.getOwningClassName();
            class_name.setText(context.getString(R.string.class_and_level, owningClassName, info.getClassLevel()));

            StatType castingStat = info.getCastingStat();
            String castingStatString = context.getString(castingStat.getStringResId());
            StatBlock castingStatBlock = context.getCharacter().getStatBlock(castingStat);
            final int dc = 8 + castingStatBlock.getModifier() + context.getCharacter().getProficiency();
            final int spellAttack = castingStatBlock.getModifier() + context.getCharacter().getProficiency();
            casting_stat.setText(context.getString(R.string.stat_and_mod, castingStatString, NumberUtils.formatSignedNumber(spellAttack), dc));

            int cantrips = context.getCharacter().getCantripsKnownForClass(owningClassName);
            int maxCantrips = context.getCharacter().evaluateFormula(info.getKnownCantrips(), null);
            if (maxCantrips > 0) {
                cantrips_known.setText(context.getString(R.string.fraction_d_slash_d, cantrips, maxCantrips));
            } else {
                cantrips_known.setText(NumberUtils.formatNumber(cantrips));
            }
            if (cantrips > maxCantrips) {
                cantrips_known.setError(context.getString(R.string.too_many_cantrips_for_class, owningClassName));
            } else {
                cantrips_known.setError(null);
            }

            int spellsKnown = context.getCharacter().getSpellsKnownForClass(owningClassName);
            final String maxKnownSpellsFormula = info.getKnownSpells();
            if (maxKnownSpellsFormula != null && maxKnownSpellsFormula.length() > 0) {
                int maxKnown = context.getCharacter().evaluateFormula(maxKnownSpellsFormula, null);
                spells_known.setText(context.getString(R.string.fraction_d_slash_d, spellsKnown, maxKnown));
                if (spellsKnown > maxKnown) {
                    spells_known.setError(context.getString(R.string.too_many_known_spells_for_class, owningClassName));
                } else {
                    spells_known.setError(null);
                }
            } else {
                spells_known.setText(NumberUtils.formatNumber(spellsKnown));
            }


            final String maxPreparedSpellsFormula = info.getPreparedSpells();
            if (maxPreparedSpellsFormula != null && maxPreparedSpellsFormula.length() > 0) {
                spells_prepared.setVisibility(View.VISIBLE);
                int spellsPrepared = context.getCharacter().getSpellsPreparedForClass(owningClassName);
                SimpleVariableContext variableContext = new SimpleVariableContext();
                variableContext.setNumber("classLevel", info.getClassLevel());
                int maxPrepared = context.getCharacter().evaluateFormula(maxPreparedSpellsFormula, variableContext);
                spells_prepared.setText(context.getString(R.string.fraction_d_slash_d, spellsPrepared, maxPrepared));

                if (spellsPrepared > maxPrepared) {
                    spells_prepared.setError(context.getString(R.string.too_many_prepared_spells_for_class, owningClassName));
                } else {
                    spells_prepared.setError(null);
                }
            } else {
                spells_prepared.setVisibility(View.INVISIBLE);
            }


            max_spell_level.setText(NumberUtils.formatNumber(info.getMaxSpellLevel()));
        }
    }
}
