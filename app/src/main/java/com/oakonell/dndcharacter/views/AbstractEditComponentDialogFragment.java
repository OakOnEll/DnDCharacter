package com.oakonell.dndcharacter.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Document;

/**
 * Created by Rob on 11/20/2015.
 */
public class AbstractEditComponentDialogFragment<M extends AbstractComponentModel> extends DialogFragment {
    TextView title;
    EditText xmltext;
    private M model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xml_edit_dialog, container);

        title = (TextView) view.findViewById(R.id.title);
        xmltext = (EditText) view.findViewById(R.id.xml);
        Button done = (Button) view.findViewById(R.id.done);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        title.setText(model.getName());
        xmltext.setText(model.getXml());

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Document doc = XmlUtils.getDocument(xmltext.getText().toString());
                model.setDocumentAndSave(AbstractEditComponentDialogFragment.this.getActivity(), doc.getDocumentElement());
                dismiss();
            }
        });
        return view;
    }


    protected final void updateModel(M model, Document doc) {

    }

    public void setModel(M model) {
        this.model = model;
    }
}
