package com.oakonell.dndcharacter.views.character.spell;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;

import java.util.List;

/**
 * Created by Rob on 1/24/2016.
 */
public class SpellLevelsAdapter extends RecyclerView.Adapter<SpellLevelsAdapter.AbstractSpellLevelViewHolder> {
    private final SpellsFragment context;
    private List<Character.SpellLevelInfo> spellLevels;

    public SpellLevelsAdapter(SpellsFragment spellsFragment, @NonNull Character character) {
        this.context = spellsFragment;
        getSpellLevels(character);
    }

    protected void getSpellLevels(@NonNull Character character) {
        this.spellLevels = character.getSpellInfos();
        this.spellLevels = spellLevels.subList(1, spellLevels.size());
    }

    public void reloadList(@NonNull Character character) {
        getSpellLevels(character);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AbstractSpellLevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_char_spells, parent, false);
        return new SpellsViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractSpellLevelViewHolder holder, int position) {
        final Character.SpellLevelInfo levelInfo = spellLevels.get(position);
        holder.bind(context, this, levelInfo);
    }

    @Override
    public int getItemCount() {
        return spellLevels.size();
    }

    public abstract static class AbstractSpellLevelViewHolder extends BindableComponentViewHolder<Character.SpellLevelInfo, SpellsFragment, SpellLevelsAdapter> {
        @NonNull
        private final RecyclerView list;
        private AbstractSpellAdapter spellAdapter;

        public AbstractSpellLevelViewHolder(@NonNull View itemView) {
            super(itemView);
            list = (RecyclerView) itemView.findViewById(R.id.spells);
        }

        @Override
        public void bind(@NonNull final SpellsFragment context, final SpellLevelsAdapter adapter, @NonNull final Character.SpellLevelInfo info) {
            if (spellAdapter == null) {
                spellAdapter = newAdapter(context, info);
                list.setAdapter(spellAdapter);
                list.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(context.getActivity(), LinearLayoutManager.VERTICAL, false));
                list.setHasFixedSize(false);

                DividerItemDecoration itemDecoration = new DividerItemDecoration(context.getActivity(), DividerItemDecoration.VERTICAL_LIST);
                list.addItemDecoration(itemDecoration);
            } else {
                spellAdapter.reloadList(info);
            }
        }


        @NonNull
        protected abstract AbstractSpellAdapter newAdapter(SpellsFragment context, Character.SpellLevelInfo info);
    }
}