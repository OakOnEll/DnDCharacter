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

import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.SkillBlock;
import com.oakonell.dndcharacter.model.StatBlock;

import java.util.List;

/**
 * Created by Rob on 11/7/2015.
 */
public class SaveThrowBlockDialogFragment extends DialogFragment {
    MainFragment mainFragment;
    private StatBlock statBlock;

    public static SaveThrowBlockDialogFragment create(MainFragment mainFragment, StatBlock block) {
        SaveThrowBlockDialogFragment frag = new SaveThrowBlockDialogFragment();
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
        View view = inflater.inflate(R.layout.save_throw_dialog, container);

        TextView statLabel = (TextView) view.findViewById(R.id.stat_label);
        TextView statModLabel = (TextView) view.findViewById(R.id.stat_mod_lbl);
        TextView statMod = (TextView) view.findViewById(R.id.stat_mod);
        TextView proficiency = (TextView) view.findViewById(R.id.proficiency);
        View proficiencyLayout = view.findViewById(R.id.proficiency_layout);


        Button done = (Button) view.findViewById(R.id.done);
        TextView total = (TextView) view.findViewById(R.id.modifier);
        ListView listView = (ListView) view.findViewById(R.id.list);

        List<Character.ProficientAndReason> proficiencies = statBlock.getSaveProficiencies();

        if (proficiencies.isEmpty()) {
            proficiencyLayout.setVisibility(View.GONE);
        } else {
            proficiencyLayout.setVisibility(View.VISIBLE);
            proficiency.setText(statBlock.getCharacter().getProficiency() + "");
        }

        statModLabel.setText(statBlock.getType().toString() + " modifier");
        statMod.setText(statBlock.getModifier() + "");
        statLabel.setText(statBlock.getType().toString());
        total.setText(statBlock.getSaveModifier() + "");


        ListAdapter adapter = new ArrayAdapter<Character.ProficientAndReason>(getActivity(), android.R.layout.simple_list_item_1, proficiencies);
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
