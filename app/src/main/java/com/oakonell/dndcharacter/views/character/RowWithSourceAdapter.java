package com.oakonell.dndcharacter.views.character;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;

import java.util.List;

/**
 * Created by Rob on 12/16/2015.
 */
public abstract class RowWithSourceAdapter<C extends Character.WithSource, V extends RowWithSourceAdapter.WithSourceViewHolder<C>> extends RecyclerView.Adapter<V> {
    @NonNull
    private final CharacterActivity activity;
    @NonNull
    private final ListRetriever<C> listRetriever;
    private List<C> list;

    public RowWithSourceAdapter(@NonNull CharacterActivity activity, @NonNull ListRetriever<C> listRetriever) {
        this.listRetriever = listRetriever;
        this.list = listRetriever.getList(activity.getCharacter());
        this.activity = activity;
    }

    public void reloadList(Character character) {
        list = listRetriever.getList(character);
        notifyDataSetChanged();
    }

    @NonNull
    public ListRetriever<C> getListRetriever() {
        return listRetriever;
    }

    public C getItem(int position) {
        return list.get(position);
    }

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(getLayoutResource(), parent, false);
        V holder = newViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(V holder, int position) {
        final C item = getItem(position);
        holder.bind(activity, (RowWithSourceAdapter<C, WithSourceViewHolder<C>>) this, item);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    protected int getLayoutResource() {
        return R.layout.skill_prof_row;
    }

    protected void launchNoSource(CharacterActivity activity, Character character) {
    }

    @NonNull
    abstract protected V newViewHolder(View view);

//    @NonNull
//    protected V newViewHolder(View view) {
//        return (V) new WithSourceViewHolder<C>(view);
//    }

    public interface ListRetriever<C> {
        List<C> getList(Character character);
    }

    public static class WithSourceViewHolder<C extends Character.WithSource> extends BindableComponentViewHolder<C, CharacterActivity, RowWithSourceAdapter<C, WithSourceViewHolder<C>>> {
        public TextView value;
        public TextView source;

        public WithSourceViewHolder(@NonNull View view) {
            super(view);
            value = (TextView) view.findViewById(R.id.value);
            source = (TextView) view.findViewById(R.id.source);
        }

        @Override
        public void bind(final CharacterActivity activity, final RowWithSourceAdapter<C, WithSourceViewHolder<C>> adapter, C item) {
            final BaseCharacterComponent source = item.getSource();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Character character = activity.getCharacter();
                    if (source == null) {
                        adapter.launchNoSource(activity, character);
                    } else {
                        ComponentLaunchHelper.editComponent(activity, source, false);
                    }
                }
            });
        }

    }
}
