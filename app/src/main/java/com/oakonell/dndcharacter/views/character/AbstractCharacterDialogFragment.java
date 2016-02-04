package com.oakonell.dndcharacter.views.character;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.DndCharacterApp;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.squareup.leakcanary.RefWatcher;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.Collections;
import java.util.Set;

import hugo.weaving.DebugLog;

/**
 * Created by Rob on 12/24/2015.
 */
public abstract class AbstractCharacterDialogFragment extends AppCompatDialogFragment implements OnCharacterLoaded, CharacterChangedListener {
    private RecyclerView context_list;

    private Button done;
    private ContextualComponentAdapter contextualComponentAdapter;
    private ViewGroup context_group;

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = onCreateTheView(inflater, container, savedInstanceState);
        context_list = (RecyclerView) view.findViewById(R.id.context_list);
        context_group = (ViewGroup) view.findViewById(R.id.context_group);

        done = (Button) view.findViewById(R.id.done);
        if (done != null) {
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean valid = onDone();
                    if (!valid) return;

                    if (contextualComponentAdapter != null) {
                        contextualComponentAdapter.deletePendingEffects(getCharacter());
                    }

                    getMainActivity().updateViews();
                    getMainActivity().saveCharacter();
                    dismiss();
                }
            });
        }
        Button cancel = (Button) view.findViewById(R.id.cancel);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().dismiss();
                }
            });
        }
        setCancelable(isCancelable(cancel != null));


        if (getDialog() != null) {
            getDialog().setTitle(getTitle());
        }

        getMainActivity().addCharacterLoadLister(this);

        return view;
    }

    @Nullable
    protected abstract String getTitle();

    protected void hideKeyboardFrom(@NonNull TextView v) {
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected boolean isCancelable(boolean hasCancelButton) {
        return !hasCancelButton;
    }

    protected void enableDone(boolean enabled) {
        if (done != null) {
            done.setEnabled(enabled);
        }
    }


    @Nullable
    protected abstract View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);


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
    protected Set<FeatureContext> getContextFilter() {
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
