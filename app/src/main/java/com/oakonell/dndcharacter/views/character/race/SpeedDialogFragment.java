package com.oakonell.dndcharacter.views.character.race;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentSource;
import com.oakonell.dndcharacter.model.character.CustomAdjustmentType;
import com.oakonell.dndcharacter.model.character.SpeedType;
import com.oakonell.dndcharacter.model.character.companion.CharacterCompanion;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.CustomNumericAdjustmentDialog;
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
    public static SpeedDialogFragment create(boolean isForCompanion) {
        SpeedDialogFragment frag = new SpeedDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(COMPANION_ARG, isForCompanion);
        frag.setArguments(args);
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

        adapter = new SpeedTypeAdapter(this, getDisplayedCharacter().getSpeedType(), isForCompanion());
        listView.setAdapter(adapter);

        listView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);
    }

    @NonNull
    @Override
    public Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.SPEED));
        return filter;
    }

    @Override
    public FeatureContextArgument getNoteContext() {
        return new FeatureContextArgument(FeatureContext.SPEED);
    }

    @Override
    protected boolean onDone() {
        boolean isValid = super.onDone();
        //getCharacter().getSpeedType();
        getDisplayedCharacter().setSpeedType(adapter.selectedType);
        return isValid;
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        adapter.reloadList(getDisplayedCharacter());
    }

    private static class SpeedTypeListRetriever implements RowWithSourceAdapter.ListRetriever<Character.SpeedWithSource> {
        private final boolean isForCompanion;
        private SpeedType type;

        protected SpeedTypeListRetriever(SpeedType type, boolean isForCompanion) {
            this.type = type;
            this.isForCompanion = isForCompanion;
        }

        public void setType(SpeedType type) {
            this.type = type;
        }

        @NonNull
        @Override
        public List<Character.SpeedWithSource> getList(@NonNull AbstractCharacter character) {
            if (character instanceof CharacterCompanion) {
                Log.i("Speed", "Passed a companion");
            } else {
                Log.i("Speed", "Pass main character");
            }
            return character.deriveSpeeds(type);
        }
    }

    public static class SpeedTypeViewHolder extends BindableComponentViewHolder<SpeedType, CharacterActivity, SpeedTypeAdapter> {
        private static final String ADJUSTMENT_FRAG = "adjustment_frag";
        @NonNull
        private final CheckBox checkbox;
        @NonNull
        private final TextView name;
        @NonNull
        private final RecyclerView speed_sources;
        SpeedSourceAdapter sourceAdapter;
        private final View add_adjustment;


        public SpeedTypeViewHolder(@NonNull View view) {
            super(view);
            checkbox = (CheckBox) view.findViewById(R.id.checkBox);
            name = (TextView) view.findViewById(R.id.name);
            speed_sources = (RecyclerView) view.findViewById(R.id.speed_sources);
            add_adjustment = view.findViewById(R.id.add_adjustment);
        }

        @Override
        public void bind(@NonNull final CharacterActivity activity, @NonNull final SpeedTypeAdapter adapter, @NonNull final SpeedType type) {
            name.setText(type.getStringResId());

            add_adjustment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO
                    String title = activity.getString(R.string.add_speed_type_adjustment, activity.getString(type.getStringResId()));
                    CustomNumericAdjustmentDialog dialog = CustomNumericAdjustmentDialog.createDialog(title, type.getCustomType(), adapter.isForCompanion);
                    dialog.show(activity.getSupportFragmentManager(), ADJUSTMENT_FRAG);
                }
            });

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
                sourceAdapter = new SpeedSourceAdapter(adapter, type, adapter.isForCompanion);
                speed_sources.setAdapter(sourceAdapter);

                speed_sources.setLayoutManager(UIUtils.createLinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                speed_sources.setHasFixedSize(false);
            } else {
                sourceAdapter.setType(type);
                AbstractCharacter source = activity.getCharacter();
                if (adapter.isForCompanion) {
                    source = activity.getCharacter().getDisplayedCompanion();
                }
                sourceAdapter.reloadList(source);
            }
        }
    }

    public static class SpeedTypeAdapter extends RecyclerView.Adapter<SpeedTypeViewHolder> {
        @NonNull
        private final SpeedDialogFragment fragment;
        private final SpeedType[] speeds;
        private SpeedType selectedType;
        private boolean isForCompanion;

        SpeedTypeAdapter(@NonNull SpeedDialogFragment fragment, SpeedType selectedType, boolean isForCompanion) {
            this.fragment = fragment;
            speeds = SpeedType.values();
            this.selectedType = selectedType;
            this.isForCompanion = isForCompanion;
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

        public void reloadList(AbstractCharacter character) {
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
        private SpeedType type;

        SpeedSourceAdapter(@NonNull SpeedTypeAdapter adapter, SpeedType type, boolean isForCompanion) {
            super(adapter.fragment.getMainActivity(), new SpeedTypeListRetriever(type, isForCompanion), isForCompanion);
            this.type = type;
        }

        protected int getLayoutResource() {
            return R.layout.stat_mod_row;
        }

        @NonNull
        @Override
        protected SpeedViewHolder newViewHolder(@NonNull View view) {
            return new SpeedViewHolder(view);
        }


        public void setType(SpeedType type) {
            this.type = type;
            ((SpeedTypeListRetriever) getListRetriever()).setType(type);
        }

        @Override
        protected CustomAdjustmentType getAdjustmentType() {
            return type.getCustomType();
        }
    }

}
