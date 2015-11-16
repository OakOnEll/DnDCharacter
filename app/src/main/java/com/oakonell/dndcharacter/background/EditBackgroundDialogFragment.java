package com.oakonell.dndcharacter.background;

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
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Document;

/**
 * Created by Rob on 11/10/2015.
 */
public class EditBackgroundDialogFragment extends DialogFragment {
    private Background background;

    public static EditBackgroundDialogFragment create(Background background) {
        EditBackgroundDialogFragment frag = new EditBackgroundDialogFragment();
        frag.setBackground(background);
        return frag;
    }

    public void setBackground(Background background) {
        this.background = background;
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

        title.setText(background.name);
        xmltext.setText(background.getXml());

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Document doc = XmlUtils.getDocument(xmltext.getText().toString());

                background.xml = XmlUtils.prettyPrint(doc);
                background.name = XmlUtils.getElementText(doc.getDocumentElement(), "name");
                background.save();
                dismiss();
            }
        });
        return view;
    }
}
