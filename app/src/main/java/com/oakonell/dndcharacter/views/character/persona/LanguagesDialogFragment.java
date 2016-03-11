package com.oakonell.dndcharacter.views.character.persona;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentSource;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.RowWithSourceAdapter;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 11/30/2015.
 */
public class LanguagesDialogFragment extends AbstractCharacterDialogFragment {
    private RecyclerView listView;
    private LanguagesSourcesAdapter adapter;

    @NonNull
    public static LanguagesDialogFragment create() {
        return new LanguagesDialogFragment();
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.language_proficiency_dialog, container);

        listView = (RecyclerView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.languages_known_title);
    }

    @NonNull
    @Override
    protected Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.LANGUAGES));
        return filter;
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

        listView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);

    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);
        adapter.reloadList(character);
    }

    public static class LanguageSourceViewHolder extends RowWithSourceAdapter.WithSourceViewHolder<Character.LanguageWithSource> {

        public LanguageSourceViewHolder(@NonNull View view) {
            super(view);
        }

        @Override
        public void bind(@NonNull CharacterActivity activity, @NonNull RowWithSourceAdapter<Character.LanguageWithSource, RowWithSourceAdapter.WithSourceViewHolder<Character.LanguageWithSource>> adapter, @NonNull Character.LanguageWithSource item) {
            super.bind(activity, adapter, item);
            String language = item.getLanguage();

            this.value.setText(language);
            final ComponentSource source = item.getSource();
            if (source == null) {
                // a base stat
                this.source.setText(R.string.base_stat);
            } else {
                this.source.setText(source.getSourceString(activity.getResources()));
            }
        }
    }

    public static class LanguagesSourcesAdapter extends RowWithSourceAdapter<Character.LanguageWithSource, LanguageSourceViewHolder> {
        LanguagesSourcesAdapter(@NonNull LanguagesDialogFragment fragment, @NonNull ListRetriever<Character.LanguageWithSource> listRetriever) {
            super(fragment.getMainActivity(), listRetriever);
        }


        @NonNull
        @Override
        protected LanguageSourceViewHolder newViewHolder(@NonNull View view) {
            return new LanguageSourceViewHolder(view);
        }
    }


}
