package com.oakonell.dndcharacter.views.character.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.stats.StatBlock;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.oakonell.dndcharacter.views.character.MainActivity;
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
    private ListView listView;

    private StatSourceAdapter adapter;

    public static StatBlockDialogFragment create(StatBlock block) {
        StatBlockDialogFragment frag = new StatBlockDialogFragment();
        frag.setStatTypeArg(block);

        return frag;
    }


    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stat_dialog, container);
        superCreateView(view, savedInstanceState);

        total = (TextView) view.findViewById(R.id.total);
        modifier = (TextView) view.findViewById(R.id.modifier);
        listView = (ListView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    protected String getTitle() {
        return "Stat";
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

    }

    @Override
    protected Set<FeatureContext> getContextFilter() {
        Set<FeatureContext> filter = new HashSet<>();
        filter.add(FeatureContext.DICE_ROLL);
        filter.add(FeatureContext.SKILL_ROLL);
        return filter;
    }

    private void updateView(StatBlock statBlock) {
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


    public static class StatSourceAdapter extends RowWithSourceAdapter<Character.ModifierWithSource> {
        StatSourceAdapter(StatBlockDialogFragment fragment, ListRetriever<Character.ModifierWithSource> listRetriever) {
            super(fragment.getMainActivity(), listRetriever);
        }

        @Override
        protected int getLayoutResource() {
            return R.layout.stat_mod_row;
        }

        @Override
        protected void launchNoSource(MainActivity activity, Character character) {
            BaseStatsDialogFragment dialog = BaseStatsDialogFragment.createDialog();
            dialog.show(activity.getSupportFragmentManager(), "base_stats");
        }

        @Override
        protected void bindView(View view, WithSourceViewHolder<Character.ModifierWithSource> holder, Character.ModifierWithSource item) {
            int value = item.getModifier();
            final BaseCharacterComponent source = item.getSource();
            if (source == null) {
                // a base stat
                holder.source.setText(R.string.base_stat);
                holder.value.setText(NumberUtils.formatNumber(value));
            } else {
                holder.source.setText(source.getSourceString());
                holder.value.setText("+" + value);
            }
        }
    }

}
