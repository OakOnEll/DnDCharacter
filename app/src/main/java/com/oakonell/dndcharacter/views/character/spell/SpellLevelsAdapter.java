package com.oakonell.dndcharacter.views.character.spell;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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

    public SpellLevelsAdapter(SpellsFragment spellsFragment, Character character) {
        this.context = spellsFragment;
        this.spellLevels = character.getSpellInfos();
    }

    public void reloadList(Character character) {
        this.spellLevels = character.getSpellInfos();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public AbstractSpellLevelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_char_cantrips, parent, false);
            return new CantripsViewHolder(newView);
        }
        View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_char_spells, parent, false);
        return new SpellsViewHolder(newView);
    }

    @Override
    public void onBindViewHolder(AbstractSpellLevelViewHolder holder, int position) {
        final Character.SpellLevelInfo levelInfo = spellLevels.get(position);
        holder.bind(context, this, levelInfo);
    }

    @Override
    public int getItemCount() {
        return spellLevels.size();
    }

    public abstract static class AbstractSpellLevelViewHolder extends BindableComponentViewHolder<Character.SpellLevelInfo, SpellsFragment, SpellLevelsAdapter> {
        private final RecyclerView list;
        private ImageButton add_spell;
        private AbstractSpellAdapter spellAdapter;

        public AbstractSpellLevelViewHolder(View itemView) {
            super(itemView);
            list = (RecyclerView) itemView.findViewById(R.id.spells);
            add_spell = (ImageButton) itemView.findViewById(R.id.add_spell);
        }

        @Override
        public void bind(final SpellsFragment context, final SpellLevelsAdapter adapter, final Character.SpellLevelInfo info) {
            add_spell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddSpellDialogFragment frag = AddSpellDialogFragment.createDialog(context.getCharacter().getCasterClassInfo().keySet(), cantripsOnly());
                    frag.show(context.getFragmentManager(), "add_spell_frag");
                }
            });
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

        protected boolean cantripsOnly() {
            return false;
        }

        protected abstract AbstractSpellAdapter newAdapter(SpellsFragment context, Character.SpellLevelInfo info);
    }
}