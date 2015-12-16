package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.oakonell.dndcharacter.model.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.views.RowWithSourceAdapter;

import java.util.List;

/**
 * Created by Rob on 11/30/2015.
 */
public class LanguagesDialogFragment extends DialogFragment {
    private MainActivity mainActivity;

    public static LanguagesDialogFragment create(MainActivity activity) {
        LanguagesDialogFragment frag = new LanguagesDialogFragment();
        frag.setMainActivity(activity);
        return frag;
    }

    private void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.language_proficiency_dialog, container);

        Button done = (Button) view.findViewById(R.id.done);
        ListView listView = (ListView) view.findViewById(R.id.list);

        final List<Character.LanguageWithSource> languageWithSources = mainActivity.character.deriveLanguages();


        ListAdapter adapter = new LanguagesSourcesAdapter(this, languageWithSources);
        listView.setAdapter(adapter);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    public static class LanguagesSourcesAdapter extends RowWithSourceAdapter<Character.LanguageWithSource> {
        LanguagesSourcesAdapter(LanguagesDialogFragment fragment, List<Character.LanguageWithSource> list) {
            super(fragment.mainActivity, list);
        }

        @Override
        protected void bindView(View view, WithSourceViewHolder<Character.LanguageWithSource> holder, Character.LanguageWithSource item) {
            String language = item.getLanguage();

            holder.value.setText(language);
            final BaseCharacterComponent source = item.getSource();
            if (source == null) {
                // a base stat
                holder.source.setText("Base Stat");
            } else {
                holder.source.setText(source.getSourceString());
            }
        }
    }


}
