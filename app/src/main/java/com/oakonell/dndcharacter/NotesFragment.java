package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.oakonell.dndcharacter.model.*;
import com.oakonell.dndcharacter.views.AbstractSheetFragment;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Created by Rob on 10/26/2015.
 */
public class NotesFragment extends AbstractSheetFragment {
    Button toXml;
    EditText notes;

    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notes_sheet, container, false);

        superCreateViews(rootView);
        toXml = (Button) rootView.findViewById(R.id.to_xml);
        notes = (EditText) rootView.findViewById(R.id.notes);

        toXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Serializer serializer = new Persister();
                OutputStream out = new ByteArrayOutputStream();
                try {
                    serializer.write(getCharacter(), out);
                    out.close();
                } catch (Exception e) {
                    notes.setText("Error converting to xml\n" + e.toString());

                    return;
                }
                notes.setText(out.toString());
            }
        });

        updateViews(rootView);

        // need to hook a notes text watcher, to update the model
        return rootView;
    }

    @Override
    protected void updateViews(View rootView) {
        super.updateViews(rootView);
        if (getCharacter() == null) {
            notes.setText("");
        } else {
            notes.setText(getCharacter().getNotes());
        }
    }


}
