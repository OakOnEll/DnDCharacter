package com.oakonell.dndcharacter.views.character;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.AdjustmentComponentSource;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentSource;
import com.oakonell.dndcharacter.model.character.CustomAdjustmentType;
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
    private final boolean isCompanion;
    private List<C> list;

    public RowWithSourceAdapter(@NonNull CharacterActivity activity, @NonNull ListRetriever<C> listRetriever, boolean isCompanion) {
        this.listRetriever = listRetriever;
        this.activity = activity;
        this.isCompanion = isCompanion;
        this.list = listRetriever.getList(getCharacter());
    }

    public void reloadList(AbstractCharacter character) {
        list = listRetriever.getList(character);
        notifyDataSetChanged();
    }

    protected AbstractCharacter getCharacter() {
        Character character = activity.getCharacter();
        if (isCompanion) {
            return character.getDisplayedCompanion();
        }
        return character;
    }

    @NonNull
    public ListRetriever<C> getListRetriever() {
        return listRetriever;
    }

    public C getItem(int position) {
        return list.get(position);
    }


    protected CustomAdjustmentType getAdjustmentType() {
        return null;
    }

    @NonNull
    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(getLayoutResource(), parent, false);
        V holder = newViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull V holder, int position) {
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

    protected void launchNoSource(CharacterActivity activity, AbstractCharacter character) {
    }

    @NonNull
    abstract protected V newViewHolder(View view);

//    @NonNull
//    protected V newViewHolder(View view) {
//        return (V) new WithSourceViewHolder<C>(view);
//    }

    public interface ListRetriever<C> {
        @NonNull
        List<C> getList(AbstractCharacter character);
    }

    public static class WithSourceViewHolder<C extends Character.WithSource> extends BindableComponentViewHolder<C, CharacterActivity, RowWithSourceAdapter<C, WithSourceViewHolder<C>>> {
        @Nullable
        private final View delete;
        @Nullable
        private final CheckBox enable;
        private final int originalTextColor;
        private final int originalSourceColor;
        @NonNull
        public TextView value;
        @NonNull
        public TextView source;

        public WithSourceViewHolder(@NonNull View view) {
            super(view);
            value = (TextView) view.findViewById(R.id.value);
            source = (TextView) view.findViewById(R.id.source);
            delete = view.findViewById(R.id.delete);
            enable = (CheckBox) view.findViewById(R.id.enable);
            originalTextColor = value.getCurrentTextColor();
            originalSourceColor = source.getCurrentTextColor();
        }

        @Override
        public void bind(@NonNull final CharacterActivity activity, @NonNull final RowWithSourceAdapter<C, WithSourceViewHolder<C>> adapter, final @NonNull C item) {
            final ComponentSource source = item.getSource();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AbstractCharacter character = adapter.getCharacter();
                    if (source == null) {
                        adapter.launchNoSource(activity, character);
                    } else {
                        ComponentLaunchHelper.editComponent(activity, source, false);
                    }
                }
            });
            if (enable != null) enable.setVisibility(View.GONE);
            if (delete != null) delete.setVisibility(View.GONE);
            if (!item.isActive()) {
                final int grey = activity.getResources().getColor(android.support.v7.appcompat.R.color.material_grey_300);
                value.setTextColor(grey);
                this.source.setTextColor(grey);
            } else {
                value.setTextColor(originalTextColor);
                this.source.setTextColor(originalTextColor);
            }
            if (item.isAdjustment()) {
                if (delete != null) {
                    delete.setVisibility(View.VISIBLE);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO
                            CustomAdjustmentType adjustmentType = adapter.getAdjustmentType();
                            if (adjustmentType != null) {
                                adapter.getCharacter().getCustomAdjustments(adjustmentType).delete(((AdjustmentComponentSource) item.getSource()).getAdjustment());
                                activity.updateViews();
                                activity.saveCharacter();
                            }
                        }
                    });
                }
                if (enable != null) {
                    enable.setVisibility(View.VISIBLE);
                    enable.setOnCheckedChangeListener(null);
                    enable.setChecked(item.isActive());
                    enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            item.setActive(isChecked);
                            activity.updateViews();
                            activity.saveCharacter();
                        }
                    });
                }
            }
        }


    }
}
