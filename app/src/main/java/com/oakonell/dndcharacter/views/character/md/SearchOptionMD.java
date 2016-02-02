package com.oakonell.dndcharacter.views.character.md;

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
    public void saveChoice(ViewGroup dynamicView, List<String> list, Map<String, String> customChoices, SavedChoices savedChoices) {
        String selection = text.getText().toString();
        list.add(selection);
    }

    @Override
    public boolean validate(ViewGroup dynamicView) {
        if (text.getText().length() > 0) return true;

        text.setError(dynamicView.getContext().getString(R.string.choose_one_error));
        Animation shake = AnimationUtils.loadAnimation(dynamicView.getContext(), R.anim.shake);
        search.startAnimation(shake);
        text.startAnimation(shake);
        return false;
    }

    @Override
    void setEnabled(boolean enabled) {
        search.setEnabled(enabled);
        text.setEnabled(enabled);
        delete.setEnabled(enabled);
    }
}
