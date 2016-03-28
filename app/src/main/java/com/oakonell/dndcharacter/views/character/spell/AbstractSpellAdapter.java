package com.oakonell.dndcharacter.views.character.spell;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentType;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;

import java.util.List;

/**
 * Created by Rob on 1/24/2016.
 */
public abstract class AbstractSpellAdapter<V extends AbstractSpellAdapter.AbstractSpellViewHolder> extends RecyclerView.Adapter<AbstractSpellAdapter.AbstractSpellViewHolder> {
    private final SpellsFragment context;
    List<Character.CharacterSpellWithSource> spellInfos;

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
        Character.CharacterSpellWithSource spellInfo = spellInfos.get(position);
        holder.bind(context, this, spellInfo);
    }

    @Override
    public int getItemCount() {
        return spellInfos.size();
    }

    public static class AbstractSpellViewHolder extends BindableComponentViewHolder<Character.CharacterSpellWithSource, SpellsFragment, AbstractSpellAdapter<?>> {
        @NonNull
        private final TextView name;
        @NonNull
        private final TextView school;
        @NonNull
        private final TextView source;
        @NonNull
        private final View delete;

        public AbstractSpellViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            source = (TextView) itemView.findViewById(R.id.source);
            school = (TextView) itemView.findViewById(R.id.school);
            delete = itemView.findViewById(R.id.delete);
        }

        @Override
        public void bind(@NonNull final SpellsFragment context, @NonNull final AbstractSpellAdapter adapter, @NonNull final Character.CharacterSpellWithSource info) {
            name.setText(info.getSpell().getName());
            if (info.getSpell().getSchool() != null) {
                school.setText(context.getString(info.getSpell().getSchool().getStringResId()));
            } else {
                school.setText(R.string.unknown);
            }
            if (context.getResources().getBoolean(R.bool.large)) {
                source.setText(info.getSourceString(context.getResources()));
            } else {
                // TODO short
                source.setText(info.getShortSourceString(context.getResources()));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // launch spell cast dialog
                    SpellDialogFragment dialog = SpellDialogFragment.create(info.getSpell());
                    dialog.show(context.getFragmentManager(), "spell");
                }
            });
            if (info.getSource() == null) {
                delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO support undo
                        context.getCharacter().deleteSpell(info.getSpell());
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
