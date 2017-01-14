package com.oakonell.dndcharacter.views.character.stats;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentSource;
import com.oakonell.dndcharacter.model.character.CustomAdjustmentType;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.model.character.stats.StatBlock;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.CustomNumericAdjustmentDialog;
import com.oakonell.dndcharacter.views.character.RowWithSourceAdapter;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 11/7/2015.
 */
public class StatBlockDialogFragment extends AbstractStatBlockBasedDialog {
    public static final String STAT_ADJUSTMENT_FRAG = "stat_adjustment";
    private TextView total;
    private TextView modifier;
    private RecyclerView listView;

    private View add_adjustment;

    private StatSourceAdapter adapter;

    @NonNull
    public static StatBlockDialogFragment create(@NonNull StatBlock block, boolean isForCompanion) {
        StatBlockDialogFragment frag = new StatBlockDialogFragment();
        frag.setStatTypeArg(block,isForCompanion);
        return frag;
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stat_dialog, container);
        superCreateView(view, savedInstanceState);

        total = (TextView) view.findViewById(R.id.total);
        modifier = (TextView) view.findViewById(R.id.modifier);
        listView = (RecyclerView) view.findViewById(R.id.list);

        add_adjustment = view.findViewById(R.id.add_adjustment);
        add_adjustment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = getString(R.string.add_stat_adjustment, getString(getType().getStringResId()));
                CustomNumericAdjustmentDialog dialog = CustomNumericAdjustmentDialog.createDialog(title, getType().getCustomType(), isForCompanion());
                dialog.show(getFragmentManager(), STAT_ADJUSTMENT_FRAG);
            }
        });


        return view;
    }


    @Override
    protected String getTitle() {
        return getString(getType().getStringResId());
        //return getString(R.string.stat_title);
    }


    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);

        StatBlock statBlock = setStatBlock(getDisplayedCharacter());

        updateView(statBlock);

        RowWithSourceAdapter.ListRetriever<Character.ModifierWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.ModifierWithSource>() {
            @NonNull
            @Override
            public List<Character.ModifierWithSource> getList(AbstractCharacter character) {
                return getStatBlock().getModifiers();
            }
        };

        adapter = new StatSourceAdapter(this, listRetriever, isForCompanion());
        listView.setAdapter(adapter);

        listView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);

    }

    @NonNull
    @Override
    public Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.DICE_ROLL));
        filter.add(new FeatureContextArgument(FeatureContext.SKILL_ROLL, getType().name()));
        filter.add(new FeatureContextArgument(FeatureContext.STAT_BLOCK, getType().name()));
        return filter;
    }

    @Override
    public FeatureContextArgument getNoteContext() {
        return new FeatureContextArgument(FeatureContext.STAT_BLOCK, getType().name());
    }


    private void updateView(@NonNull StatBlock statBlock) {
        //getDialog().setTitle(statBlock.getType().getStringResId());

        setModifier(statBlock.getModifier());
        total.setText(NumberUtils.formatNumber(statBlock.getValue()));
        modifier.setText(NumberUtils.formatNumber(statBlock.getModifier()));
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);

        AbstractCharacter source = getDisplayedCharacter();
        StatBlock statBlock = setStatBlock(source);
        updateView(statBlock);

        adapter.reloadList(source);
    }

    public static class StatSourceViewHolder extends RowWithSourceAdapter.WithSourceViewHolder<Character.ModifierWithSource> {

        public StatSourceViewHolder(@NonNull View view) {
            super(view);
        }

        @Override
        public void bind(@NonNull CharacterActivity activity, @NonNull RowWithSourceAdapter<Character.ModifierWithSource, RowWithSourceAdapter.WithSourceViewHolder<Character.ModifierWithSource>> adapter, @NonNull Character.ModifierWithSource item) {
            super.bind(activity, adapter, item);
            final ComponentSource source = item.getSource();
            int value = item.getModifier();
            if (source == null) {
                // a base stat
                this.source.setText(R.string.base_stat);
                this.value.setText(NumberUtils.formatNumber(value));
            } else {
                this.source.setText(source.getSourceString(activity.getResources()));
                this.value.setText(NumberUtils.formatSignedNumber(value));
            }
        }
    }

    public static class StatSourceAdapter extends RowWithSourceAdapter<Character.ModifierWithSource, StatSourceViewHolder> {
        final private StatType type;

        StatSourceAdapter(@NonNull StatBlockDialogFragment fragment, @NonNull ListRetriever<Character.ModifierWithSource> listRetriever, boolean isForCompanion) {
            super(fragment.getMainActivity(), listRetriever, isForCompanion);
            type = fragment.getType();
        }

        @Override
        protected int getLayoutResource() {
            return R.layout.stat_mod_row;
        }

        @NonNull
        @Override
        protected StatSourceViewHolder newViewHolder(@NonNull View view) {
            return new StatSourceViewHolder(view);
        }

        @Override
        protected void launchNoSource(@NonNull CharacterActivity activity, AbstractCharacter character) {
            BaseStatsDialogFragment dialog = BaseStatsDialogFragment.createDialog();
            dialog.show(activity.getSupportFragmentManager(), "base_stats");
        }

        @Override
        protected CustomAdjustmentType getAdjustmentType() {
            return type.getCustomType();
        }
    }

}
