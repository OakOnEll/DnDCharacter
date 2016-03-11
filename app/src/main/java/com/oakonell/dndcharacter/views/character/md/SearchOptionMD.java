package com.oakonell.dndcharacter.views.character.md;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.SavedChoices;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 2/1/2016.
 */
public class SearchOptionMD extends CategoryOptionMD {
    private final ImageView search;
    private final TextView text;
    private final ImageView delete;

    public SearchOptionMD(CategoryChoicesMD chooseMD, ImageView search, TextView text, ImageView delete) {
        super(chooseMD);
        this.search = search;
        this.text = text;
        this.delete = delete;
    }

    @Override
    public void saveChoice(ViewGroup dynamicView, @NonNull List<String> list, Map<String, String> customChoices, SavedChoices savedChoices) {
        if (!isPopulated()) return;
        String selection = text.getText().toString();
        list.add(selection);
    }

    @Override
    void setEnabled(boolean enabled) {
        search.setEnabled(enabled);
        text.setEnabled(enabled);
        delete.setEnabled(enabled);
    }

    public void setSelected(String selected) {
        text.setText(selected);
        search.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.VISIBLE);
        text.setOnClickListener(null);
        text.setError(null);
    }

    public void resetSelection(View.OnClickListener onSearchClickListener) {
        text.setText("");
        search.setVisibility(View.VISIBLE);
        text.setOnClickListener(onSearchClickListener);
        delete.setVisibility(View.GONE);
        text.setError(null);
    }

    @Override
    public boolean isPopulated() {
        return text.getText().length() > 0;
    }

    @Override
    public void showRequiredError(@NonNull ViewGroup dynamicView) {
        text.setError(dynamicView.getContext().getString(R.string.choose_one_error));
        Animation shake = AnimationUtils.loadAnimation(dynamicView.getContext(), R.anim.shake);
        search.startAnimation(shake);
        text.startAnimation(shake);
    }

    @NonNull
    public String getText() {
        return text.getText().toString();
    }
}
