package com.oakonell.dndcharacter.views.race;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.background.Background;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Document;

/**
 * Created by Rob on 11/19/2015.
 */
public class EditRaceDialogFragment extends DialogFragment {
    private Race race;

    public static EditRaceDialogFragment create(Race race) {
        EditRaceDialogFragment frag = new EditRaceDialogFragment();
        frag.setRace(race);
        return frag;
    }

    TextView title;
    EditText xmltext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xml_edit_dialog, container);

        title = (TextView) view.findViewById(R.id.title);
        xmltext = (EditText) view.findViewById(R.id.xml);
        Button done = (Button) view.findViewById(R.id.done);

        title.setText(race.name);
        xmltext.setText(race.getXml());

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Document doc = XmlUtils.getDocument(xmltext.getText().toString());

                race.xml = XmlUtils.prettyPrint(doc);
                race.name = XmlUtils.getElementText(doc.getDocumentElement(), "name");
                race.save();
                dismiss();
            }
        });
        return view;
    }

    public void setRace(Race race) {
        this.race = race;
    }
}
