package com.oakonell.dndcharacter.views.character.persona;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.character.RowWithSourceAdapter;

import java.util.List;

/**
 * Created by Rob on 11/30/2015.
 */
public class LanguagesDialogFragment extends AbstractCharacterDialogFragment {
    private ListView listView;
    private LanguagesSourcesAdapter adapter;

    @NonNull
    public static LanguagesDialogFragment create() {
        return new LanguagesDialogFragment();
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.language_proficiency_dialog, container);

        listView = (ListView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.languages_known_title);
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        RowWithSourceAdapter.ListRetriever<Character.LanguageWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.LanguageWithSource>() {
            @NonNull
            @Override
            public List<Character.LanguageWithSource> getList(@NonNull Character character) {
                return character.deriveLanguages();
            }
        };

        adapter = new LanguagesSourcesAdapter(this, listRetriever);
        listView.setAdapter(adapter);
    }

    @Override
    public void onCharacterChanged(Character character) {
        super.onCharacterChanged(character);
        adapter.reloadList(character);
    }

    public static class LanguagesSourcesAdapter extends RowWithSourceAdapter<Character.LanguageWithSource> {
        LanguagesSourcesAdapter(@NonNull LanguagesDialogFragment fragment, ListRetriever<Character.LanguageWithSource> listRetriever) {
            super(fragment.getMainActivity(), listRetriever);
        }

        @Override
        protected void bindView(View view, @NonNull WithSourceViewHolder<Character.LanguageWithSource> holder, @NonNull Character.LanguageWithSource item) {
            String language = item.getLanguage();

            holder.value.setText(language);
            final BaseCharacterComponent source = item.getSource();
            if (source == null) {
                // a base stat
                holder.source.setText(R.string.base_stat);
            } else {
                holder.source.setText(source.getSourceString());
            }
        }
    }


}
