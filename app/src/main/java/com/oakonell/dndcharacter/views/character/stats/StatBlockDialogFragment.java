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
import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.stats.StatBlock;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.RowWithSourceAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 11/7/2015.
 */
public class StatBlockDialogFragment extends AbstractStatBlockBasedDialog {
    private TextView total;
    private TextView modifier;
    private RecyclerView listView;

    private StatSourceAdapter adapter;

    @NonNull
    public static StatBlockDialogFragment create(StatBlock block) {
        StatBlockDialogFragment frag = new StatBlockDialogFragment();
        frag.setStatTypeArg(block);

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

        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.stat_title);
    }


    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);

        StatBlock statBlock = setStatBlock(character);

        updateView(statBlock);

        RowWithSourceAdapter.ListRetriever<Character.ModifierWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.ModifierWithSource>() {
            @Override
            public List<Character.ModifierWithSource> getList(Character character) {
                return getStatBlock().getModifiers();
            }
        };

        adapter = new StatSourceAdapter(this, listRetriever);
        listView.setAdapter(adapter);

        listView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);

    }

    @NonNull
    @Override
    protected Set<FeatureContext> getContextFilter() {
        Set<FeatureContext> filter = new HashSet<>();
        filter.add(FeatureContext.DICE_ROLL);
        filter.add(FeatureContext.SKILL_ROLL);
        return filter;
    }

    private void updateView(@NonNull StatBlock statBlock) {
        getDialog().setTitle(statBlock.getType().toString());

        setModifier(statBlock.getModifier());
        total.setText(NumberUtils.formatNumber(statBlock.getValue()));
        modifier.setText(NumberUtils.formatNumber(statBlock.getModifier()));
    }

    @Override
    public void onCharacterChanged(Character character) {
        super.onCharacterChanged(character);

        StatBlock statBlock = setStatBlock(character);
        updateView(statBlock);

        adapter.reloadList(character);
    }

    public static class StatSourceViewHolder extends RowWithSourceAdapter.WithSourceViewHolder<Character.ModifierWithSource> {

        public StatSourceViewHolder(@NonNull View view) {
            super(view);
        }

        @Override
        public void bind(CharacterActivity activity, RowWithSourceAdapter<Character.ModifierWithSource, RowWithSourceAdapter.WithSourceViewHolder<Character.ModifierWithSource>> adapter, Character.ModifierWithSource item) {
            super.bind(activity, adapter, item);
            final BaseCharacterComponent source = item.getSource();
            int value = item.getModifier();
            if (source == null) {
                // a base stat
                this.source.setText(R.string.base_stat);
                this.value.setText(NumberUtils.formatNumber(value));
            } else {
                this.source.setText(source.getSourceString(activity.getResources()));
                this.value.setText("+" + value);
            }
        }
    }

    public static class StatSourceAdapter extends RowWithSourceAdapter<Character.ModifierWithSource, StatSourceViewHolder> {
        StatSourceAdapter(@NonNull StatBlockDialogFragment fragment, ListRetriever<Character.ModifierWithSource> listRetriever) {
            super(fragment.getMainActivity(), listRetriever);
        }

        @Override
        protected int getLayoutResource() {
            return R.layout.stat_mod_row;
        }

        @NonNull
        @Override
        protected StatSourceViewHolder newViewHolder(View view) {
            return new StatSourceViewHolder(view);
        }

        @Override
        protected void launchNoSource(@NonNull CharacterActivity activity, Character character) {
            BaseStatsDialogFragment dialog = BaseStatsDialogFragment.createDialog();
            dialog.show(activity.getSupportFragmentManager(), "base_stats");
        }
    }

}
