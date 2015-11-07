package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.*;
import com.oakonell.dndcharacter.model.Character;

/**
 * Created by Rob on 11/7/2015.
 */
public class StatBlockDialogFragment extends DialogFragment {
    MainFragment mainFragment;
    private StatBlock statBlock;

    public static StatBlockDialogFragment create(MainFragment mainFragment, StatBlock block) {
        StatBlockDialogFragment frag = new StatBlockDialogFragment();
        frag.setMainFragment(mainFragment);
        frag.setStatBlock(block);
        return frag;
    }

    private void setStatBlock(StatBlock statBlock) {
        this.statBlock = statBlock;
    }

    private void setMainFragment(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stat_dialog, container);

        TextView statLabel = (TextView) view.findViewById(R.id.stat_label);
        statLabel.setText(statBlock.getType().toString());

        Button done = (Button) view.findViewById(R.id.done);
        TextView total = (TextView) view.findViewById(R.id.total);
        TextView modifier = (TextView) view.findViewById(R.id.modifier);
        ListView listView = (ListView) view.findViewById(R.id.list);

        total.setText(statBlock.getValue() + "");
        modifier.setText(statBlock.getModifier() + "");

        ListAdapter adapter = new ArrayAdapter<Character.ModifierAndReason>(getActivity(), android.R.layout.simple_list_item_1, statBlock.getModifiers());
        listView.setAdapter(adapter);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

}
