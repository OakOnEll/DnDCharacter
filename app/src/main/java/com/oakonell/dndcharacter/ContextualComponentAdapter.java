package com.oakonell.dndcharacter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterEffect;
import com.oakonell.dndcharacter.model.FeatureInfo;
import com.oakonell.dndcharacter.views.FeatureContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 1/4/2016.
 */
public class ContextualComponentAdapter extends RecyclerView.Adapter<BindableComponentViewHolder<IContextualComponent, MainActivity>> {
    private MainActivity context;
    private Set<FeatureContext> filter;
    private List<IContextualComponent> list;

    public ContextualComponentAdapter(MainActivity context, Set<FeatureContext> filter) {
        this.context = context;
        this.filter = filter;
        list = filterList(context.getCharacter());
    }

    public void reloadList(com.oakonell.dndcharacter.model.Character character) {
        if (filter == null) {
//                list = context.getCharacter().getFeatureInfos();
        } else {
            list = filterList(character);
        }
        notifyDataSetChanged();
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
            return 0;
        }
        throw new RuntimeException("Unknown component type " + item.getClass());
    }

    @Override
    public BindableComponentViewHolder<IContextualComponent, MainActivity> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.feature_layout, parent, false);
            BindableComponentViewHolder holder = new FeaturesFragment.FeatureViewHolder(view);
            return holder;
        }
        if (viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.effect_context_layout, parent, false);
            BindableComponentViewHolder holder = new EffectContextViewHolder(view);
            return holder;
        }

        throw new RuntimeException("Unknown view type " + viewType);
    }

    @Override
    public void onBindViewHolder(final BindableComponentViewHolder<IContextualComponent, MainActivity> viewHolder, final int position) {
        final IContextualComponent info = getItem(position);
        viewHolder.bind(context, this, info);
    }

    private static class EffectContextViewHolder extends BindableComponentViewHolder<CharacterEffect, MainActivity> {
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
        public void bind(final MainActivity context, final RecyclerView.Adapter<?> adapter, final CharacterEffect info) {
            name.setText(info.getName());
            source.setText(info.getSourceString());
            short_description.setText(info.getDescription());
            end_effect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO add an undo ability here..
                    context.getCharacter().removeEffect(info);
                    context.updateViews();
                }
            });
        }
    }

}
