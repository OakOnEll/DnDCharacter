package com.oakonell.dndcharacter.views.character.spell;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentType;
import com.oakonell.dndcharacter.model.character.spell.CharacterSpell;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;

import java.util.List;

/**
 * Created by Rob on 1/24/2016.
 */
public abstract class AbstractSpellAdapter<V extends AbstractSpellAdapter.AbstractSpellViewHolder> extends RecyclerView.Adapter<AbstractSpellAdapter.AbstractSpellViewHolder> {
    private final SpellsFragment context;
    List<CharacterSpell> spellInfos;

    AbstractSpellAdapter(SpellsFragment context, @NonNull Character.SpellLevelInfo spellLevelInfo) {
        this.context = context;
        this.spellInfos = spellLevelInfo.getSpellInfos();
    }

    public void reloadList(@NonNull Character.SpellLevelInfo spellLevelInfo) {
        this.spellInfos = spellLevelInfo.getSpellInfos();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractSpellViewHolder holder, int position) {
        CharacterSpell spellInfo = spellInfos.get(position);
        holder.bind(context, this, spellInfo);
    }

    @Override
    public int getItemCount() {
        return spellInfos.size();
    }

    public static class AbstractSpellViewHolder extends BindableComponentViewHolder<CharacterSpell, SpellsFragment, AbstractSpellAdapter<?>> {
        @NonNull
        private final TextView name;
        @NonNull
        private final TextView school;
        @NonNull
        private final TextView source;
        @NonNull
        private final ImageButton delete;

        public AbstractSpellViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            source = (TextView) itemView.findViewById(R.id.source);
            school = (TextView) itemView.findViewById(R.id.school);
            delete = (ImageButton) itemView.findViewById(R.id.delete);
        }

        @Override
        public void bind(@NonNull final SpellsFragment context, @NonNull final AbstractSpellAdapter adapter, @NonNull final CharacterSpell info) {
            name.setText(info.getName());
            school.setText(info.getSchool());
            source.setText(info.getOriginString());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // launch spell cast dialog
                    SpellDialogFragment dialog = SpellDialogFragment.create(info);
                    dialog.show(context.getFragmentManager(), "spell");
                }
            });
            if (info.getSource() == ComponentType.CLASS || info.getSource() == null) {
                delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.getCharacter().deleteSpell(info);
                        adapter.notifyDataSetChanged();
                        context.updateViews();
                        context.getMainActivity().saveCharacter();
                    }
                });
            } else {
                delete.setVisibility(View.GONE);
                delete.setOnClickListener(null);
            }
        }
    }
}
