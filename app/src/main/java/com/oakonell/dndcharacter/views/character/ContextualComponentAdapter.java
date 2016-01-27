package com.oakonell.dndcharacter.views.character;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterEffect;
import com.oakonell.dndcharacter.model.character.FeatureInfo;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
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
public class ContextualComponentAdapter extends RecyclerView.Adapter<BindableComponentViewHolder<IContextualComponent, MainActivity,ContextualComponentAdapter>> {
    private static final int UNDO_DELAY = 5000;
    private final MainActivity context;
    private final Set<FeatureContext> filter;
    private List<IContextualComponent> list;
    private final Map<String, Long> deletedEffects = new HashMap<>();


    public ContextualComponentAdapter(AbstractCharacterDialogFragment context, Set<FeatureContext> filter) {
        this.context = context.getMainActivity();
        this.filter = filter;
        list = filterList(context.getCharacter());
    }

    public void reloadList(Character character) {
        if (filter == null) {
//                list = context.getCharacter().getFeatureInfos();
        } else {
            list = filterList(character);
        }
        notifyDataSetChanged();
    }

    public void deletePendingEffects(Character character) {
        for (Iterator<Map.Entry<String, Long>> iter = deletedEffects.entrySet().iterator(); iter.hasNext(); ) {
            final Map.Entry<String, Long> next = iter.next();
            iter.remove();
            CharacterEffect effect = character.getEffectNamed(next.getKey());
            if (effect != null) {
                character.removeEffect(effect);
            }
        }
    }

    private List<IContextualComponent> filterList(Character character) {
        List<IContextualComponent> result = new ArrayList<>();

        if (filter == null) {
            result.addAll(character.getFeatureInfos());
            result.addAll(character.getEffects());
            return result;
        }
        for (FeatureInfo each : character.getFeatureInfos()) {
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
        if (context.getCharacter() == null) return 0;
        return list.size();
    }


    public IContextualComponent getItem(int position) {
        if (context.getCharacter() == null) return null;
        return list.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        IContextualComponent item = getItem(position);
        // TODO extract the unique view type into the interface
        if (item instanceof FeatureInfo) {
            return 1;
        }
        if (item instanceof CharacterEffect) {
            if (deletedEffects.containsKey(((CharacterEffect) item).getName())) {
                return 2;
            }
            return 0;
        }
        throw new RuntimeException("Unknown component type " + item.getClass());
    }

    @Override
    public BindableComponentViewHolder<IContextualComponent, MainActivity,ContextualComponentAdapter> onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.feature_layout, parent, false);
            BindableComponentViewHolder holder = new FeatureViewHolder(view);
            return holder;
        }
        if (viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.effect_context_layout, parent, false);
            BindableComponentViewHolder holder = new EffectContextViewHolder(view);
            return holder;
        }
        if (viewType == 2) {
            View view = LayoutInflater.from(context).inflate(R.layout.effect_deleted_context_layout, parent, false);
            BindableComponentViewHolder holder = new DeletedEffectContextViewHolder(view);
            return holder;
        }

        throw new RuntimeException("Unknown view type " + viewType);
    }

    @Override
    public void onBindViewHolder(final BindableComponentViewHolder<IContextualComponent, MainActivity,ContextualComponentAdapter> viewHolder, final int position) {
        final IContextualComponent info = getItem(position);
        viewHolder.bind(context, this, info);
    }

    private static class DeletedEffectContextViewHolder extends BindableComponentViewHolder<CharacterEffect, MainActivity, ContextualComponentAdapter> {
        private final TextView name;

        private final Button undo;

        public DeletedEffectContextViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);

            undo = (Button) view.findViewById(R.id.undo);
        }

        @Override
        public void bind(final MainActivity context, final ContextualComponentAdapter componentAdapter, final CharacterEffect info) {
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

    private static class EffectContextViewHolder extends BindableComponentViewHolder<CharacterEffect, MainActivity, ContextualComponentAdapter> {
        private final TextView name;
        private final TextView source;
        private final TextView short_description;
        private final Button end_effect;

        public EffectContextViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            source = (TextView) view.findViewById(R.id.source);
            short_description = (TextView) view.findViewById(R.id.short_description);
            end_effect = (Button) view.findViewById(R.id.end_effect);
        }

        @Override
        public void bind(final MainActivity context, final ContextualComponentAdapter componentAdapter, final CharacterEffect info) {
            name.setText(info.getName());
            source.setText(info.getSource());
            short_description.setText(info.getDescription());
            end_effect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String name = info.getName();
                    if (componentAdapter.deletedEffects.containsKey(name)) {
                        // actually delete the record, now
                        componentAdapter.context.getCharacter().removeEffect(info);
                        componentAdapter.deletedEffects.remove(name);
                        componentAdapter.notifyItemRemoved(getAdapterPosition());
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
                                componentAdapter.context.getCharacter().removeEffect(info);
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

}
