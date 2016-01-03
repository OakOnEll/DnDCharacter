package com.oakonell.dndcharacter.views;

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

import com.oakonell.dndcharacter.FeaturesFragment;
import com.oakonell.dndcharacter.MainActivity;
import com.oakonell.dndcharacter.R;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.Collections;
import java.util.Set;

/**
 * Created by Rob on 12/24/2015.
 */
public abstract class AbstractCharacterDialogFragment extends AppCompatDialogFragment implements OnCharacterLoaded, CharacterChangedListener {

    private RecyclerView feature_context_list;

    private Button done;
    private FeaturesFragment.FeatureAdapter featureContextAdapter;
    private ViewGroup feature_context_group;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = onCreateTheView(inflater, container, savedInstanceState);
        feature_context_list = (RecyclerView) view.findViewById(R.id.feature_context_list);
        feature_context_group = (ViewGroup) view.findViewById(R.id.feature_context_group);

        done = (Button) view.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = onDone();
                if (!valid) return;
                getMainActivity().updateViews();
                getMainActivity().saveCharacter();
                dismiss();
            }
        });
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
        done.setEnabled(enabled);
    }


    protected abstract View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);


    protected final MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    protected final com.oakonell.dndcharacter.model.Character getCharacter() {
        return getMainActivity().getCharacter();
    }

    public void onCharacterLoaded(com.oakonell.dndcharacter.model.Character character) {
        if (feature_context_list != null) {
            featureContextAdapter = new FeaturesFragment.FeatureAdapter(this.getMainActivity(), getContextFilter());
            feature_context_list.setAdapter(featureContextAdapter);
            // decide on 1 or 2 columns based on screen size
            //int numColumns = getResources().getInteger(R.integer.feature_columns);
            feature_context_list.setLayoutManager(new LinearLayoutManager(getMainActivity(), android.support.v7.widget.LinearLayoutManager.VERTICAL, false));

            if (featureContextAdapter.getItemCount() == 0) {
                feature_context_group.setVisibility(View.GONE);
            } else {
                feature_context_group.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onCharacterChanged(com.oakonell.dndcharacter.model.Character character) {
        if (featureContextAdapter != null) {
            featureContextAdapter.reloadList(character);
            if (featureContextAdapter.getItemCount() == 0) {
                feature_context_group.setVisibility(View.GONE);
            } else {
                feature_context_group.setVisibility(View.VISIBLE);
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
