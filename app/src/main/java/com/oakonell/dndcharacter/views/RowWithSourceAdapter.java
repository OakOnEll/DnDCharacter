package com.oakonell.dndcharacter.views;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oakonell.dndcharacter.MainActivity;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.Character;

import java.util.List;

/**
 * Created by Rob on 12/16/2015.
 */
public class RowWithSourceAdapter<C extends com.oakonell.dndcharacter.model.Character.WithSource> extends BaseAdapter {
    private final MainActivity activity;
    private final ListRetriever<C> listRetriever;
    private List<C> list;

    public interface ListRetriever<C> {
        List<C> getList(Character character);
    }

    public RowWithSourceAdapter(MainActivity activity, ListRetriever<C> listRetriever) {
        this.listRetriever = listRetriever;
        this.list = listRetriever.getList(activity.character);
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public C getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        WithSourceViewHolder<C> holder;
        if (view != null) {
            holder = (WithSourceViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(activity).inflate(getLayoutResource(), parent, false);
            holder = newViewHolder();
            holder.value = (TextView) view.findViewById(R.id.value);
            holder.source = (TextView) view.findViewById(R.id.source);
            view.setTag(holder);
        }

        C item = getItem(position);
        bindView(view, holder, item);

        final BaseCharacterComponent source = item.getSource();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Character character = activity.character;
                ComponentLaunchHelper.OnDialogDone onDone = new ComponentLaunchHelper.OnDialogDone() {
                    @Override
                    public void done(boolean changed) {
                        list = listRetriever.getList(activity.character);
                        notifyDataSetInvalidated();
                    }
                };
                if (source == null) {
                    launchNoSource(activity, character, onDone);
                } else {
                    ComponentLaunchHelper.editComponent(activity, character, source, onDone, false);
                }
            }
        });
        return view;
    }

    protected int getLayoutResource() {
        return R.layout.skill_prof_row;
    }

    protected void bindView(View view, WithSourceViewHolder<C> holder, C item) {

    }

    protected void launchNoSource(MainActivity activity, Character character, ComponentLaunchHelper.OnDialogDone onDone) {
    }

    @NonNull
    private WithSourceViewHolder newViewHolder() {
        return new WithSourceViewHolder();
    }

    public static class WithSourceViewHolder<C extends com.oakonell.dndcharacter.model.Character.WithSource> {
        public TextView value;
        public TextView source;
    }
}
