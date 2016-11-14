package com.oakonell.dndcharacter.views.character.spell;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.spell.CharacterSpell;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 1/24/2016.
 */
public abstract class AbstractSpellAdapter<V extends AbstractSpellAdapter.AbstractSpellViewHolder> extends RecyclerView.Adapter<AbstractSpellAdapter.AbstractSpellViewHolder> {
    private final SpellsFragment context;
    List<Character.CharacterSpellWithSource> spellInfos;

    AbstractSpellAdapter(SpellsFragment context, @NonNull Character.SpellLevelInfo spellLevelInfo) {
        this.context = context;
        this.spellInfos = spellLevelInfo.getDisplaySpellInfos();
    }

    public void reloadList(@NonNull Character.SpellLevelInfo spellLevelInfo) {
        this.spellInfos = spellLevelInfo.getDisplaySpellInfos();
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
        @NonNull
        private final View expand;
        @NonNull
        private final TextView description;
        @NonNull
        private final View expanded;
        @NonNull
        private final TextView casting_time;
        @NonNull
        private final TextView range;
        @NonNull
        private final TextView duration;
        @NonNull
        private final TextView components;


        public AbstractSpellViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            source = (TextView) itemView.findViewById(R.id.source);
            school = (TextView) itemView.findViewById(R.id.school);
            delete = itemView.findViewById(R.id.delete);
            expand = itemView.findViewById(R.id.expand);
            description = (TextView) itemView.findViewById(R.id.description);
            expanded = itemView.findViewById(R.id.expanded);

            casting_time = (TextView) itemView.findViewById(R.id.casting_time);
            range = (TextView) itemView.findViewById(R.id.range);
            duration = (TextView) itemView.findViewById(R.id.duration);
            components = (TextView) itemView.findViewById(R.id.components);

        }

        @Override
        public void bind(@NonNull final SpellsFragment context, @NonNull final AbstractSpellAdapter adapter, @NonNull final Character.CharacterSpellWithSource info) {
            if (info.getSpell().isRitual()) {
                name.setText(context.getString(R.string.ritual_suffix, info.getSpell().getName()));
            } else {
                name.setText(info.getSpell().getName());
            }
            if (info.getSpell().getSchool() != null) {
                school.setText(context.getString(info.getSpell().getSchool().getStringResId()));
            } else {
                school.setText(R.string.unknown);
            }
            final Resources resources = context.getResources();
            if (resources.getBoolean(R.bool.large)) {
                source.setText(info.getSourceString(resources));
            } else {
                // TODO short
                source.setText(info.getShortSourceString(resources));
            }
            description.setText(info.getSpell().getDescription());
            CharacterSpell spell = info.getSpell();
            casting_time.setText(spell.getCastingTimeString(resources));
            range.setText(spell.getRangeString(resources));
            duration.setText(spell.getDurationString(resources));
            components.setText(spell.getComponentString());

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

            final int position = getAdapterPosition();
            boolean isExpanded = adapter.isExpanded(position);
            if (isExpanded) {
                expanded.setVisibility(View.VISIBLE);
                description.setEllipsize(null);
                description.setLines(5);
                description.setMaxLines(Integer.MAX_VALUE);
                expand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.setExpanded(position, false);
                        adapter.notifyDataSetChanged();
                    }
                });
            } else {
                expanded.setVisibility(View.GONE);
                description.setEllipsize(TextUtils.TruncateAt.END);
                description.setLines(1);
                description.setMaxLines(1);
                expand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.setExpanded(position, true);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    private void setExpanded(int position, boolean expanded) {
        if (expanded) {
            expandedSpellPositions.add(position);
        } else {
            expandedSpellPositions.remove(position);
        }
    }

    private Set<Integer> expandedSpellPositions = new HashSet<>();

    private boolean isExpanded(int position) {
        return expandedSpellPositions.contains(position);
    }
}
