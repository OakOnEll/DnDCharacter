package com.oakonell.dndcharacter.views.character.feat;

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
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.RollableDialogFragment;
import com.oakonell.dndcharacter.views.character.RowWithSourceAdapter;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 11/30/2015.
 */
public class InitiativeDialogFragment extends RollableDialogFragment {
    private RecyclerView listView;
    private InitiativeSourcesAdapter adapter;

    @NonNull
    public static InitiativeDialogFragment create() {
        return new InitiativeDialogFragment();
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.initiative_dialog, container);
        superCreateView(view, savedInstanceState);

        listView = (RecyclerView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.initiative_title);
    }

    @NonNull
    @Override
    protected Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.INITIATIVE));
        return filter;
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);
        RowWithSourceAdapter.ListRetriever<Character.InitiativeWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.InitiativeWithSource>() {
            @NonNull
            @Override
            public List<Character.InitiativeWithSource> getList(@NonNull Character character) {
                return character.deriveInitiative();
            }
        };

        setModifier(character.getInitiative());

        adapter = new InitiativeSourcesAdapter(this, listRetriever);
        listView.setAdapter(adapter);

        listView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);

    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);
        adapter.reloadList(character);
    }

    public static class InitiativeSourceViewHolder extends RowWithSourceAdapter.WithSourceViewHolder<Character.InitiativeWithSource> {

        public InitiativeSourceViewHolder(@NonNull View view) {
            super(view);
        }

        @Override
        public void bind(@NonNull CharacterActivity activity, @NonNull RowWithSourceAdapter<Character.InitiativeWithSource, RowWithSourceAdapter.WithSourceViewHolder<Character.InitiativeWithSource>> adapter, @NonNull Character.InitiativeWithSource item) {
            super.bind(activity, adapter, item);
            int initiative = item.getInitiative();

            this.value.setText(NumberUtils.formatNumber(initiative));
            final ComponentSource source = item.getSource();
            if (source == null) {
                // a base stat
                this.source.setText(R.string.dex_mod);
            } else {
                this.source.setText(source.getSourceString(activity.getResources()));
            }
        }
    }

    public static class InitiativeSourcesAdapter extends RowWithSourceAdapter<Character.InitiativeWithSource, InitiativeSourceViewHolder> {
        InitiativeSourcesAdapter(@NonNull InitiativeDialogFragment fragment, @NonNull ListRetriever<Character.InitiativeWithSource> listRetriever) {
            super(fragment.getMainActivity(), listRetriever);
        }


        @NonNull
        @Override
        protected InitiativeSourceViewHolder newViewHolder(@NonNull View view) {
            return new InitiativeSourceViewHolder(view);
        }
    }


}
