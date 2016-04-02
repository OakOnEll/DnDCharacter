package com.oakonell.dndcharacter.views.character.feature;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CustomAdjustments;
import com.oakonell.dndcharacter.model.character.FeatureInfo;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.AbstractSheetFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.feat.InitiativeDialogFragment;
import com.oakonell.dndcharacter.views.character.feat.PassivePerceptionDialogFragment;
import com.oakonell.dndcharacter.views.character.item.ArmorClassDialogFragment;
import com.oakonell.dndcharacter.views.character.race.SpeedDialogFragment;
import com.oakonell.dndcharacter.views.character.stats.StatBlockDialogFragment;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 10/26/2015.
 */
public class FeaturesFragment extends AbstractSheetFragment {

    private static final int ADJUSTMENT_VIEW = 1001;
    private FeatureAdapter adapter;
    private RecyclerView gridView;

    @NonNull
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.feature_sheet, container, false);

        superCreateViews(rootView);

        gridView = (RecyclerView) rootView.findViewById(R.id.features);

        return rootView;
    }

    @Override
    protected void updateViews(View rootView) {
        super.updateViews(rootView);
        if (adapter == null) { // odd state
            return;
        }
        adapter.reloadList(getCharacter());
    }


    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);
        if (getActivity() == null) return;

        adapter = new FeatureAdapter((CharacterActivity) this.getActivity());
        gridView.setAdapter(adapter);
        // decide on 1 or 2 columns based on screen size
        int numColumns = getResources().getInteger(R.integer.feature_columns);
        gridView.setLayoutManager(new StaggeredGridLayoutManager(numColumns, StaggeredGridLayoutManager.VERTICAL));

        updateViews();
    }


    public class FeatureAdapter extends RecyclerView.Adapter<BindableComponentViewHolder<?, CharacterActivity, RecyclerView.Adapter<?>>> {
        @NonNull
        private final CharacterActivity context;
        private List<FeatureInfo> list;

        public FeatureAdapter(@NonNull CharacterActivity context) {
            this.context = context;
            list = new ArrayList<>(context.getCharacter().getFeatureInfos());
        }


        public void reloadList(@NonNull Character character) {
            list = new ArrayList<>(character.getFeatureInfos());
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (context.getCharacter() == null) return 0;
            int numFeatures = list.size();
            if (getCharacter().hasAdjustments()) return numFeatures + 1;
            return numFeatures;
        }


        @Nullable
        public FeatureInfo getItem(int position) {
            if (context.getCharacter() == null) return null;
            return list.get(position);
        }

        @NonNull
        @Override
        public BindableComponentViewHolder<?, CharacterActivity, RecyclerView.Adapter<?>> onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ADJUSTMENT_VIEW) {
                View view = LayoutInflater.from(context).inflate(R.layout.adjustments_layout, parent, false);
                AdjustmentsViewHolder holder = new AdjustmentsViewHolder(view);
                return holder;
            }

            View view = LayoutInflater.from(context).inflate(R.layout.feature_layout, parent, false);
            FeatureViewHolder holder = new FeatureViewHolder(view);
            return holder;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == list.size()) {
                if (getCharacter().hasAdjustments()) {
                    return ADJUSTMENT_VIEW;
                }
            }
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(@NonNull final BindableComponentViewHolder<?, CharacterActivity, RecyclerView.Adapter<?>> viewHolder, final int position) {
            if (position == list.size()) {
                if (getCharacter().hasAdjustments()) {
                    // slightly ugly...
                    BindableComponentViewHolder genericHolder = viewHolder;
                    AdjustmentsViewHolder featureViewHolder = (AdjustmentsViewHolder) genericHolder;
                    featureViewHolder.bind(context, this, getCharacter());
                }
                return;
            }


            final FeatureInfo info = getItem(position);
            // slightly ugly...
            BindableComponentViewHolder genericHolder = viewHolder;
            FeatureViewHolder featureViewHolder = (FeatureViewHolder) genericHolder;
            featureViewHolder.bind(context, this, info);
        }


    }

    public static class AdjustmentsViewHolder extends BindableComponentViewHolder<Character, CharacterActivity, RecyclerView.Adapter<?>> {
        RecyclerView list;
        AdjustmentsAdapter listAdapter;

        public AdjustmentsViewHolder(View view) {
            super(view);
            list = (RecyclerView) view.findViewById(R.id.list);
        }

        @Override
        public void bind(CharacterActivity context, RecyclerView.Adapter<?> adapter, Character character) {
            if (listAdapter == null) {
                listAdapter = new AdjustmentsAdapter(context, character);
                list.setAdapter(listAdapter);

                list.setHasFixedSize(false);
                list.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

                DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);
                list.addItemDecoration(itemDecoration);
            } else {
                listAdapter.setCharacter(character);
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    private static class AdjustmentsAdapter extends RecyclerView.Adapter<AdjustmentTypeViewHolder> {
        CharacterActivity context;
        private Character character;

        public AdjustmentsAdapter(CharacterActivity context, Character character) {
            this.context = context;
            this.character = character;
        }

        public void setCharacter(Character character) {
            this.character = character;
        }

        @Override
        public AdjustmentTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adjustment_type_row, parent, false);
            return new AdjustmentTypeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AdjustmentTypeViewHolder holder, int position) {
            CustomAdjustments adjustment = character.getCustomAdjustments().get(position);
            holder.bind(context, this, adjustment);
        }

        @Override
        public int getItemCount() {
            return character.getCustomAdjustments().size();
        }
    }

    private static final class AdjustmentTypeViewHolder extends BindableComponentViewHolder<CustomAdjustments, CharacterActivity, RecyclerView.Adapter<?>> {
        TextView type;
        RecyclerView list;
        AdjustmentTypeAdapter listAdapter;

        public AdjustmentTypeViewHolder(View view) {
            super(view);
            type = (TextView) view.findViewById(R.id.type);
            list = (RecyclerView) view.findViewById(R.id.list);
        }

        @Override
        public void bind(CharacterActivity context, RecyclerView.Adapter<?> adapter, CustomAdjustments info) {
            type.setText(info.getType().getStringResId());
            if (listAdapter == null) {
                listAdapter = new AdjustmentTypeAdapter(context, info);
                list.setAdapter(listAdapter);

                list.setHasFixedSize(false);
                list.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

                DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);
                list.addItemDecoration(itemDecoration);
            } else {
                listAdapter.setCustomAdjustments(info);
            }
        }
    }

    private static class AdjustmentTypeAdapter extends RecyclerView.Adapter<AdjustmentRowViewHolder> {
        CharacterActivity context;
        CustomAdjustments adjustments;

        public AdjustmentTypeAdapter(CharacterActivity context, CustomAdjustments adjustments) {
            this.context = context;
            this.adjustments = adjustments;
        }

        public void setCustomAdjustments(CustomAdjustments adjustments) {
            this.adjustments = adjustments;
            notifyDataSetChanged();
        }

        @Override
        public AdjustmentRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adjustment_row, parent, false);
            return new AdjustmentRowViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AdjustmentRowViewHolder holder, int position) {
            CustomAdjustments.Adjustment adjustment = adjustments.getAdjustments().get(position);
            holder.bind(context, this, adjustment);
        }

        @Override
        public int getItemCount() {
            return adjustments.getAdjustments().size();
        }
    }

    private static class AdjustmentRowViewHolder extends BindableComponentViewHolder<CustomAdjustments.Adjustment, CharacterActivity, AdjustmentTypeAdapter> {
        private CheckBox enable;
        private TextView value;
        private TextView comment;
        private View adjustment_group;
        private View delete;

        public AdjustmentRowViewHolder(View view) {
            super(view);
            enable = (CheckBox) view.findViewById(R.id.enable);
            value = (TextView) view.findViewById(R.id.value);
            comment = (TextView) view.findViewById(R.id.comment);
            adjustment_group = view.findViewById(R.id.adjustment_group);
            delete = view.findViewById(R.id.delete);
        }

        @Override
        public void bind(final CharacterActivity context, final AdjustmentTypeAdapter adapter, final CustomAdjustments.Adjustment info) {
            value.setText(NumberUtils.formatNumber(info.numValue));
            comment.setText(info.comment);
            enable.setOnCheckedChangeListener(null);
            enable.setChecked(info.applied);
            enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    info.applied = isChecked;
                    context.updateViews();
                    context.saveCharacter();
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.adjustments.delete(info);
                    // TODO support undo..
                    context.updateViews();
                    context.saveCharacter();
                }
            });
            adjustment_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (adapter.adjustments.getType()) {

                        case MODIFYING_ACS:
                        case ROOT_ACS:
                            ArmorClassDialogFragment acDialog = ArmorClassDialogFragment.createDialog();
                            acDialog.show(adapter.context.getSupportFragmentManager(), "ac");
                            break;

                        case PASSIVE_PERCEPTION:
                            PassivePerceptionDialogFragment percDialog = PassivePerceptionDialogFragment.create();
                            percDialog.show(adapter.context.getSupportFragmentManager(), "passivePerception");
                            break;
                        case INITIATIVE:
                            InitiativeDialogFragment initDialog = InitiativeDialogFragment.create();
                            initDialog.show(adapter.context.getSupportFragmentManager(), "initiative");
                            break;

                        case SPEED_BURROW:
                        case SPEED_CLIMB:
                        case SPEED_CRAWL:
                        case SPEED_FLY:
                        case SPEED_SWIM:
                        case SPEED_WALK:
                            SpeedDialogFragment speedDialog = SpeedDialogFragment.create();
                            speedDialog.show(adapter.context.getSupportFragmentManager(), "speed");
                            break;

                        case STAT_STRENGTH:
                            launchStatDialog(adapter, StatType.STRENGTH);
                            break;
                        case STAT_DEXTERITY:
                            launchStatDialog(adapter, StatType.DEXTERITY);
                            break;
                        case STAT_CONSTITUTION:
                            launchStatDialog(adapter, StatType.CONSTITUTION);
                            break;

                        case STAT_CHARISMA:
                            launchStatDialog(adapter, StatType.CHARISMA);
                            break;
                        case STAT_INTELLIGENCE:
                            launchStatDialog(adapter, StatType.INTELLIGENCE);
                            break;
                        case STAT_WISDOM:
                            launchStatDialog(adapter, StatType.WISDOM);
                            break;
                    }
                }
            });
        }

        protected void launchStatDialog(AdjustmentTypeAdapter adapter, StatType statType) {
            StatBlockDialogFragment statDialog = StatBlockDialogFragment.create(adapter.context.getCharacter().getStatBlock(statType));
            statDialog.show(adapter.context.getSupportFragmentManager(), statType.toString());
        }
    }

}