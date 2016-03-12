package com.oakonell.dndcharacter.views.character;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.DndCharacterApp;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.squareup.leakcanary.RefWatcher;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.Collections;
import java.util.Set;

import hugo.weaving.DebugLog;

/**
 * Created by Rob on 12/24/2015.
 */
public abstract class AbstractCharacterDialogFragment extends AbstractDialogFragment implements OnCharacterLoaded, CharacterChangedListener {
    private RecyclerView context_list;

    private ContextualComponentAdapter contextualComponentAdapter;
    private ViewGroup context_group;

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        context_list = (RecyclerView) view.findViewById(R.id.context_list);
        context_group = (ViewGroup) view.findViewById(R.id.context_group);


        getMainActivity().addCharacterLoadLister(this);

        return view;
    }

    protected void extraDoneActions() {
        if (contextualComponentAdapter != null) {
            contextualComponentAdapter.deletePendingEffects(getCharacter());
        }

        getMainActivity().updateViews();
        getMainActivity().saveCharacter();
    }


    @NonNull
    public final CharacterActivity getMainActivity() {
        return (CharacterActivity) getActivity();
    }

    @Nullable
    public final com.oakonell.dndcharacter.model.character.Character getCharacter() {
        return getMainActivity().getCharacter();
    }

    @DebugLog
    public void onCharacterLoaded(Character character) {
        if (context_list != null) {
            contextualComponentAdapter = new ContextualComponentAdapter(this, getContextFilter());
            context_list.setAdapter(contextualComponentAdapter);
            // decide on 1 or 2 columns based on screen size
            //int numColumns = getResources().getInteger(R.integer.feature_columns);
            context_list.setLayoutManager(new LinearLayoutManager(getMainActivity(), android.support.v7.widget.LinearLayoutManager.VERTICAL, false));

            if (contextualComponentAdapter.getItemCount() == 0) {
                context_group.setVisibility(View.GONE);
            } else {
                context_group.setVisibility(View.VISIBLE);
            }
        }
    }

    @DebugLog
    public void onCharacterChanged(@NonNull Character character) {
        if (contextualComponentAdapter != null) {
            contextualComponentAdapter.reloadList(character);
            if (contextualComponentAdapter.getItemCount() == 0) {
                context_group.setVisibility(View.GONE);
            } else {
                context_group.setVisibility(View.VISIBLE);
            }
        }
    }

    @NonNull
    protected Set<FeatureContextArgument> getContextFilter() {
        return Collections.emptySet();
    }

    protected boolean onDone() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = DndCharacterApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

}
