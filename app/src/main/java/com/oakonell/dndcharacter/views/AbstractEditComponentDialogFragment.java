package com.oakonell.dndcharacter.views;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Document;

/**
 * Created by Rob on 11/20/2015.
 */
public class AbstractEditComponentDialogFragment<M extends AbstractComponentModel> extends DialogFragment {
    private M model;

    TextView title;
    EditText xmltext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xml_edit_dialog, container);

        title = (TextView) view.findViewById(R.id.title);
        xmltext = (EditText) view.findViewById(R.id.xml);
        Button done = (Button) view.findViewById(R.id.done);

        title.setText(model.getName());
        xmltext.setText(model.getXml());

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Document doc = XmlUtils.getDocument(xmltext.getText().toString());

                model.setXml(XmlUtils.prettyPrint(doc));
                if (doc != null && doc.getDocumentElement() != null) {
                    model.setName(XmlUtils.getElementText(doc.getDocumentElement(), "name"));
                } else {
                    model.setName("xml parse error- no name");
                }
                updateModel(model, doc);
                model.save();
                dismiss();
            }
        });
        return view;
    }

    protected void updateModel(M model, Document doc) {

    }

    public void setModel(M model) {
        this.model = model;
    }
}
