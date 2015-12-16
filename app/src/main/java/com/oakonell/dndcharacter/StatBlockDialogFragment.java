package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.StatBlock;
import com.oakonell.dndcharacter.views.BaseStatsDialogFragment;
import com.oakonell.dndcharacter.views.ComponentLaunchHelper;
import com.oakonell.dndcharacter.views.RowWithSourceAdapter;

import java.util.List;

/**
 * Created by Rob on 11/7/2015.
 */
public class StatBlockDialogFragment extends RollableDialogFragment {
    private StatBlock statBlock;

    public static StatBlockDialogFragment create(MainActivity activity, StatBlock block) {
        StatBlockDialogFragment frag = new StatBlockDialogFragment();
        frag.setMainActivity(activity);
        frag.setStatBlock(block);
        return frag;
    }

    private void setStatBlock(StatBlock statBlock) {
        this.statBlock = statBlock;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stat_dialog, container);
        superCreateView(view);
        setModifier(statBlock.getModifier());

        TextView statLabel = (TextView) view.findViewById(R.id.stat_label);
        statLabel.setText(statBlock.getType().toString());

        TextView total = (TextView) view.findViewById(R.id.total);
        TextView modifier = (TextView) view.findViewById(R.id.modifier);
        ListView listView = (ListView) view.findViewById(R.id.list);

        total.setText(statBlock.getValue() + "");
        modifier.setText(statBlock.getModifier() + "");

        RowWithSourceAdapter.ListRetriever<Character.ModifierWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.ModifierWithSource>() {
            @Override
            public List<Character.ModifierWithSource> getList(Character character) {
                return statBlock.getModifiers();
            }
        };

        StatSourceAdapter adapter = new StatSourceAdapter(this, listRetriever);
        listView.setAdapter(adapter);

        return view;
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
        protected void launchNoSource(MainActivity activity, Character character, ComponentLaunchHelper.OnDialogDone onDone) {
            BaseStatsDialogFragment dialog = BaseStatsDialogFragment.createDialog(character);
            dialog.show(activity.getSupportFragmentManager(), "base_stats");
        }

        @Override
        protected void bindView(View view, WithSourceViewHolder<Character.ModifierWithSource> holder, Character.ModifierWithSource item) {
            int value = item.getModifier();
            final BaseCharacterComponent source = item.getSource();
            if (source == null) {
                // a base stat
                holder.source.setText("Base Stat");
                holder.value.setText(value + "");
            } else {
                holder.source.setText(source.getSourceString());
                holder.value.setText("+" + value);
            }
        }
    }

}
