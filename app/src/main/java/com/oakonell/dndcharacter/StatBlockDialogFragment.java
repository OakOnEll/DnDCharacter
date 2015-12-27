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
import com.oakonell.dndcharacter.views.RowWithSourceAdapter;

import java.util.List;

/**
 * Created by Rob on 11/7/2015.
 */
public class StatBlockDialogFragment extends AbstractStatBlockBasedDialog {
    private TextView total;
    private TextView modifier;
    private ListView listView;
    private TextView statLabel;

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
        superCreateView(view);

        statLabel = (TextView) view.findViewById(R.id.stat_label);

        total = (TextView) view.findViewById(R.id.total);
        modifier = (TextView) view.findViewById(R.id.modifier);
        listView = (ListView) view.findViewById(R.id.list);

        return view;
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

    private void updateView(StatBlock statBlock) {
        statLabel.setText(statBlock.getType().toString());

        setModifier(statBlock.getModifier());
        total.setText(statBlock.getValue() + "");
        modifier.setText(statBlock.getModifier() + "");
    }

    @Override
    public void onCharacterChanged(Character character) {
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
                holder.source.setText("Base Stat");
                holder.value.setText(value + "");
            } else {
                holder.source.setText(source.getSourceString());
                holder.value.setText("+" + value);
            }
        }
    }

}
