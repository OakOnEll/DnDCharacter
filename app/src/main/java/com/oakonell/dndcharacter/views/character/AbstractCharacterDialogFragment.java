package com.oakonell.dndcharacter.views.character;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.Collections;
import java.util.Set;

/**
 * Created by Rob on 12/24/2015.
 */
public abstract class AbstractCharacterDialogFragment extends AppCompatDialogFragment implements OnCharacterLoaded, CharacterChangedListener {
    private RecyclerView context_list;

    private Button done;
    private ContextualComponentAdapter contextualComponentAdapter;
    private ViewGroup context_group;

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


        getDialog().setTitle(getTitle());

        getMainActivity().addCharacterLoadLister(this);

        return view;
    }

    protected abstract String getTitle();

    protected void hideKeyboardFrom(TextView v) {
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


    protected abstract View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);


    public final MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    public final com.oakonell.dndcharacter.model.character.Character getCharacter() {
        return getMainActivity().getCharacter();
    }

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

    public void onCharacterChanged(Character character) {
        if (contextualComponentAdapter != null) {
            contextualComponentAdapter.reloadList(character);
            if (contextualComponentAdapter.getItemCount() == 0) {
                context_group.setVisibility(View.GONE);
            } else {
                context_group.setVisibility(View.VISIBLE);
            }
        }
    }

    protected Set<FeatureContext> getContextFilter() {
        return Collections.emptySet();
    }

    protected boolean onDone() {
        return true;
    }

}
