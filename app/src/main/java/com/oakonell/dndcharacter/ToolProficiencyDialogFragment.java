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
import com.oakonell.dndcharacter.model.components.ProficiencyType;

import java.util.List;

/**
 * Created by Rob on 11/30/2015.
 */
public class ToolProficiencyDialogFragment extends DialogFragment {
    private MainActivity mainActivity;
    private ProficiencyType proficiencyType;


    public static ToolProficiencyDialogFragment create(MainActivity activity, ProficiencyType type) {
        ToolProficiencyDialogFragment frag = new ToolProficiencyDialogFragment();
        frag.setMainActivity(activity);
        frag.setProficiencyType(type);
        return frag;
    }

    private void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private void setProficiencyType(ProficiencyType proficiencyType) {
        this.proficiencyType = proficiencyType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tool_proficiency_dialog, container);

        TextView proficiency_label = (TextView) view.findViewById(R.id.proficiency_label);
        proficiency_label.setText(proficiencyType.toString());

        Button done = (Button) view.findViewById(R.id.done);
        ListView listView = (ListView) view.findViewById(R.id.list);

        final List<Character.ToolProficiencyAndReason> proficienciesAndReasons = mainActivity.character.deriveToolProficienciesReasons(proficiencyType);


        ListAdapter adapter = new ArrayAdapter<Character.ToolProficiencyAndReason>(getActivity(), android.R.layout.simple_list_item_1, proficienciesAndReasons);
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
