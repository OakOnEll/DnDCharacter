package com.oakonell.dndcharacter.views.character.race;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentSource;
import com.oakonell.dndcharacter.model.character.SpeedType;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.RowWithSourceAdapter;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 11/30/2015.
 */
public class SpeedDialogFragment extends AbstractCharacterDialogFragment {
    private RecyclerView listView;

    private SpeedTypeAdapter adapter;

    @NonNull
    public static SpeedDialogFragment create() {
        SpeedDialogFragment frag = new SpeedDialogFragment();
        return frag;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.speed_title);
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.speed_dialog, container);

        listView = (RecyclerView) view.findViewById(R.id.speed_type_list);

        return view;
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);

        adapter = new SpeedTypeAdapter(this, character.getSpeedType());
        listView.setAdapter(adapter);

        listView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);
    }

    @NonNull
    @Override
    protected Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.SPEED));
        return filter;
    }

    @Override
    protected boolean onDone() {
        boolean isValid = super.onDone();
        //getCharacter().getSpeedType();
        getCharacter().setSpeedType(adapter.selectedType);
        return isValid;
    }

    @Override
    public void onCharacterChanged(Character character) {
        adapter.reloadList(character);
    }

    private static class SpeedTypeListRetriever implements RowWithSourceAdapter.ListRetriever<Character.SpeedWithSource> {
        private SpeedType type;

        protected SpeedTypeListRetriever(SpeedType type) {
            this.type = type;
        }

        public void setType(SpeedType type) {
            this.type = type;
        }

        @NonNull
        @Override
        public List<Character.SpeedWithSource> getList(@NonNull Character character) {
            return character.deriveSpeeds(type);
        }
    }

    public static class SpeedTypeViewHolder extends BindableComponentViewHolder<SpeedType, CharacterActivity, SpeedTypeAdapter> {
        @NonNull
        private final CheckBox checkbox;
        @NonNull
        private final TextView name;
        @NonNull
        private final RecyclerView speed_sources;
        SpeedSourceAdapter sourceAdapter;

        public SpeedTypeViewHolder(@NonNull View view) {
            super(view);
            checkbox = (CheckBox) view.findViewById(R.id.checkBox);
            name = (TextView) view.findViewById(R.id.name);
            speed_sources = (RecyclerView) view.findViewById(R.id.speed_sources);
        }

        @Override
        public void bind(@NonNull CharacterActivity activity, @NonNull final SpeedTypeAdapter adapter, @NonNull final SpeedType type) {
            name.setText(type.getStringResId());
            boolean isChecked = (adapter.selectedType == type);
            checkbox.setOnCheckedChangeListener(null);
            checkbox.setChecked(isChecked);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        adapter.selectedType = type;
                    } else {
                        adapter.selectedType = SpeedType.WALK;
                    }
                    adapter.notifyDataSetChanged();
                }
            });
            if (sourceAdapter == null) {
                SpeedTypeListRetriever retriever = new SpeedTypeListRetriever(type);

                sourceAdapter = new SpeedSourceAdapter(adapter, retriever);
                speed_sources.setAdapter(sourceAdapter);

                speed_sources.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                speed_sources.setHasFixedSize(false);
            } else {
                ((SpeedTypeListRetriever) sourceAdapter.getListRetriever()).setType(type);
                sourceAdapter.reloadList(activity.getCharacter());
            }
        }
    }

    public static class SpeedTypeAdapter extends RecyclerView.Adapter<SpeedTypeViewHolder> {
        @NonNull
        private final SpeedDialogFragment fragment;
        private final SpeedType[] speeds;
        private SpeedType selectedType;

        SpeedTypeAdapter(@NonNull SpeedDialogFragment fragment, SpeedType selectedType) {
            this.fragment = fragment;
            speeds = SpeedType.values();
            this.selectedType = selectedType;
        }

        @NonNull
        @Override
        public SpeedTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.speed_type_item, parent, false);
            return new SpeedTypeViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull SpeedTypeViewHolder holder, int position) {
            final SpeedType speedType = speeds[position];
            holder.bind(fragment.getMainActivity(), this, speedType);
        }

        @Override
        public int getItemCount() {
            return speeds.length;
        }

        public void reloadList(Character character) {
            notifyDataSetChanged();
        }
    }


    public static class SpeedViewHolder extends RowWithSourceAdapter.WithSourceViewHolder<Character.SpeedWithSource> {
        public SpeedViewHolder(@NonNull View view) {
            super(view);
        }

        @Override
        public void bind(@NonNull CharacterActivity activity, @NonNull RowWithSourceAdapter<Character.SpeedWithSource, RowWithSourceAdapter.WithSourceViewHolder<Character.SpeedWithSource>> adapter, @NonNull Character.SpeedWithSource item) {
            super.bind(activity, adapter, item);
            int speed = item.getSpeed();

            this.value.setText(NumberUtils.formatNumber(speed));
            final ComponentSource source = item.getSource();
            this.source.setText(source.getSourceString(activity.getResources()));
        }
    }


    public static class SpeedSourceAdapter extends RowWithSourceAdapter<Character.SpeedWithSource, SpeedViewHolder> {
        SpeedSourceAdapter(@NonNull SpeedTypeAdapter adapter, @NonNull ListRetriever<Character.SpeedWithSource> listRetriever) {
            super(adapter.fragment.getMainActivity(), listRetriever);
        }

        protected int getLayoutResource() {
            return R.layout.speed_source_row;
        }

        @NonNull
        @Override
        protected SpeedViewHolder newViewHolder(@NonNull View view) {
            return new SpeedViewHolder(view);
        }
    }

}
