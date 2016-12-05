package com.oakonell.dndcharacter.views.character;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.character.ContextNote;
import com.oakonell.dndcharacter.model.character.FeatureInfo;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.character.feature.FeatureViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Rob on 1/4/2016.
 */
public class ContextualComponentAdapter extends RecyclerView.Adapter<BindableComponentViewHolder<IContextualComponent, CharacterActivity, ContextualComponentAdapter>> {
    private static final int UNDO_DELAY = 5000;
    private static final int FEATURE = 0;
    private static final int DELETED_EFFECT = 2;
    private static final int EFFECT = 1;
    private static final int NOTE = 3;

    @NonNull
    //private final CharacterActivity context;
    AbstractCharacterDialogFragment fragment;
    private final Set<FeatureContextArgument> filter;
    private List<IContextualComponent> list;
    private final Map<String, Long> deletedEffects = new HashMap<>();


    public ContextualComponentAdapter(@NonNull AbstractCharacterDialogFragment context, Set<FeatureContextArgument> filter) {
        this.filter = filter;
        this.fragment = context;
        list = filterList(context.getDisplayedCharacter());
    }

    public AbstractCharacter getDisplayedCharacter() {
        return fragment.getDisplayedCharacter();
    }

    public void reloadList(@NonNull AbstractCharacter character) {
        if (filter == null) {
//                list = context.getCharacter().getFeatureInfos();
        } else {
            list = filterList(character);
        }
        notifyDataSetChanged();
    }

    public void deletePendingEffects(@NonNull AbstractCharacter character) {
        for (Iterator<Map.Entry<String, Long>> iter = deletedEffects.entrySet().iterator(); iter.hasNext(); ) {
            final Map.Entry<String, Long> next = iter.next();
            iter.remove();
            CharacterEffect effect = character.getEffectNamed(next.getKey());
            if (effect != null) {
                character.removeEffect(effect);
            }
        }
    }

    @NonNull
    private List<IContextualComponent> filterList(@NonNull AbstractCharacter character) {
        List<IContextualComponent> result = new ArrayList<>();

        if (filter == null) {
            result.addAll(character.getFeatureInfos());
            result.addAll(character.getEffects());
            // no context notes if no filter?
            return result;
        }
        for (FeatureInfo each : character.getFeatureInfos()) {
            if (each.isInContext(filter)) {
                result.add(each);
            }
        }
        for (ContextNote each : character.getContextNotes()) {
            if (each.isInContext(filter)) {
                result.add(each);
            }
        }
        for (CharacterEffect each : character.getEffects()) {
            if (each.isInContext(filter)) {
                result.add(each);
            }
        }
        return result;
    }

    @Override
    public int getItemCount() {
        if (fragment.getDisplayedCharacter() == null) return 0;
        return list.size();
    }


    @Nullable
    public IContextualComponent getItem(int position) {
        if (fragment.getDisplayedCharacter() == null) return null;
        return list.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        IContextualComponent item = getItem(position);
        // TODO extract the unique view type into the interface
        if (item instanceof ContextNote) {
            return NOTE;
        }
        if (item instanceof FeatureInfo) {
            return FEATURE;
        }
        if (item instanceof CharacterEffect) {
            if (deletedEffects.containsKey(((CharacterEffect) item).getName())) {
                return DELETED_EFFECT;
            }
            return EFFECT;
        }
        throw new RuntimeException("Unknown component type " + item.getClass());
    }

    @NonNull
    @Override
    public BindableComponentViewHolder<IContextualComponent, CharacterActivity, ContextualComponentAdapter> onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == FEATURE) {
            View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.feature_layout, parent, false);
            BindableComponentViewHolder holder = new FeatureViewHolder(view, filter);
            return holder;
        }
        if (viewType == EFFECT) {
            View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.effect_context_layout, parent, false);
            BindableComponentViewHolder holder = new EffectContextViewHolder(view);
            return holder;
        }
        if (viewType == DELETED_EFFECT) {
            View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.effect_deleted_context_layout, parent, false);
            BindableComponentViewHolder holder = new DeletedEffectContextViewHolder(view);
            return holder;
        }
        if (viewType == NOTE) {
            View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.context_note_layout, parent, false);
            BindableComponentViewHolder holder = new ContextNoteViewHolder(view);
            return holder;
        }

        throw new RuntimeException("Unknown view type " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final BindableComponentViewHolder<IContextualComponent, CharacterActivity, ContextualComponentAdapter> viewHolder, final int position) {
        final IContextualComponent info = getItem(position);
        viewHolder.bind(fragment.getMainActivity(), this, info);
    }

    private static class DeletedEffectContextViewHolder extends BindableComponentViewHolder<CharacterEffect, CharacterActivity, ContextualComponentAdapter> {
        @NonNull
        private final TextView name;

        @NonNull
        private final Button undo;

        public DeletedEffectContextViewHolder(@NonNull View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);

            undo = (Button) view.findViewById(R.id.undo);
        }

        @Override
        public void bind(final CharacterActivity context, @NonNull final ContextualComponentAdapter componentAdapter, @NonNull final CharacterEffect info) {
            final String nameString = info.getName();
            name.setText(nameString);

            undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    componentAdapter.deletedEffects.remove(nameString);
                    componentAdapter.notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }

    private static class EffectContextViewHolder extends BindableComponentViewHolder<CharacterEffect, CharacterActivity, ContextualComponentAdapter> {
        @NonNull
        private final TextView name;
        @NonNull
        private final TextView source;
        @NonNull
        private final TextView short_description;
        @NonNull
        private final Button end_effect;

        public EffectContextViewHolder(@NonNull View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            source = (TextView) view.findViewById(R.id.source);
            short_description = (TextView) view.findViewById(R.id.short_description);
            end_effect = (Button) view.findViewById(R.id.end_effect);
        }

        @Override
        public void bind(@NonNull final CharacterActivity context, @NonNull final ContextualComponentAdapter componentAdapter, @NonNull final CharacterEffect info) {
            name.setText(info.getName());
            source.setText(info.getSource());
            short_description.setText(info.getDescription());
            end_effect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String name = info.getName();
                    if (componentAdapter.deletedEffects.containsKey(name)) {
                        // actually delete the record, now
                        componentAdapter.getDisplayedCharacter().removeEffect(info);
                        componentAdapter.deletedEffects.remove(name);
                        componentAdapter.notifyItemRemoved(getAdapterPosition());
                        context.updateViews();
                        context.saveCharacter();
                    }

                    componentAdapter.deletedEffects.put(name, System.currentTimeMillis());
                    componentAdapter.notifyItemChanged(getAdapterPosition());

                    end_effect.postDelayed(new Runnable() {
                        public void run() {
                            // may have been deleted, undone, and then redeleted
                            Long deletedTime = componentAdapter.deletedEffects.get(name);
                            if (deletedTime == null) return;
                            if (System.currentTimeMillis() - deletedTime >= UNDO_DELAY) {
                                // actually delete the record, now
                                componentAdapter.getDisplayedCharacter().removeEffect(info);
                                componentAdapter.deletedEffects.remove(name);
                                context.updateViews();
                                context.saveCharacter();
                                //adapter.notifyItemRemoved(getAdapterPosition());
                            }
                        }
                    }, UNDO_DELAY);

                }
            });
        }
    }

    private static class ContextNoteViewHolder extends BindableComponentViewHolder<ContextNote, CharacterActivity, ContextualComponentAdapter> {
        private final TextView text;
        private final ImageView delete;
        private final View note_group;

        public ContextNoteViewHolder(@NonNull View view) {
            super(view);
            note_group = view.findViewById(R.id.note_group);
            text = (TextView) view.findViewById(R.id.text);
            delete = (ImageView) view.findViewById(R.id.delete);
        }

        @Override
        public void bind(@NonNull final CharacterActivity context, @NonNull final ContextualComponentAdapter componentAdapter, @NonNull final ContextNote info) {
            note_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO pop up an edit box, and change the text
                }
            });
            text.setText(info.getText());
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO support delayed deletion...
                    componentAdapter.getDisplayedCharacter().getContextNotes(info.getContext().getContext()).remove(info);
                    componentAdapter.reloadList(componentAdapter.getDisplayedCharacter());

//                    final String name = info.getName();
//                    if (componentAdapter.deletedEffects.containsKey(name)) {
//                        // actually delete the record, now
//                        componentAdapter.context.getCharacter().removeEffect(info);
//                        componentAdapter.deletedEffects.remove(name);
//                        componentAdapter.notifyItemRemoved(getAdapterPosition());
//                    }
//
//                    componentAdapter.deletedEffects.put(name, System.currentTimeMillis());
//                    componentAdapter.notifyItemChanged(getAdapterPosition());
//
//                    end_effect.postDelayed(new Runnable() {
//                        public void run() {
//                            // may have been deleted, undone, and then redeleted
//                            Long deletedTime = componentAdapter.deletedEffects.get(name);
//                            if (deletedTime == null) return;
//                            if (System.currentTimeMillis() - deletedTime >= UNDO_DELAY) {
//                                // actually delete the record, now
//                                componentAdapter.context.getCharacter().removeEffect(info);
//                                componentAdapter.deletedEffects.remove(name);
//                                context.updateViews();
//                                context.saveCharacter();
//                                //adapter.notifyItemRemoved(getAdapterPosition());
//                            }
//                        }
//                    }, UNDO_DELAY);

                }
            });
        }
    }

}
