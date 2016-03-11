package com.oakonell.dndcharacter.views.character.feat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentSource;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.utils.NumberUtils;
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
public class PassivePerceptionDialogFragment extends AbstractCharacterDialogFragment {
    private RecyclerView listView;
    private PassivePerceptionSourcesAdapter adapter;

    @NonNull
    public static PassivePerceptionDialogFragment create() {
        return new PassivePerceptionDialogFragment();
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.initiative_dialog, container);

        listView = (RecyclerView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.passive_perception_title);
    }

    @NonNull
    @Override
    protected Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.PASSIVE_PERCEPTION));
        return filter;
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        RowWithSourceAdapter.ListRetriever<Character.PassivePerceptionWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.PassivePerceptionWithSource>() {
            @NonNull
            @Override
            public List<Character.PassivePerceptionWithSource> getList(@NonNull Character character) {
                return character.derivePassivePerception();
            }
        };

        adapter = new PassivePerceptionSourcesAdapter(this, listRetriever);
        listView.setAdapter(adapter);

        listView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);

    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);
        adapter.reloadList(character);
    }

    public static class PassivePerceptionSourceViewHolder extends RowWithSourceAdapter.WithSourceViewHolder<Character.PassivePerceptionWithSource> {

        public PassivePerceptionSourceViewHolder(@NonNull View view) {
            super(view);
        }

        @Override
        public void bind(@NonNull CharacterActivity activity, @NonNull RowWithSourceAdapter<Character.PassivePerceptionWithSource, RowWithSourceAdapter.WithSourceViewHolder<Character.PassivePerceptionWithSource>> adapter, @NonNull Character.PassivePerceptionWithSource item) {
            super.bind(activity, adapter, item);
            int initiative = item.getPassivePerception();

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

    public static class PassivePerceptionSourcesAdapter extends RowWithSourceAdapter<Character.PassivePerceptionWithSource, PassivePerceptionSourceViewHolder> {
        PassivePerceptionSourcesAdapter(@NonNull PassivePerceptionDialogFragment fragment, @NonNull ListRetriever<Character.PassivePerceptionWithSource> listRetriever) {
            super(fragment.getMainActivity(), listRetriever);
        }


        @NonNull
        @Override
        protected PassivePerceptionSourceViewHolder newViewHolder(@NonNull View view) {
            return new PassivePerceptionSourceViewHolder(view);
        }
    }


}
