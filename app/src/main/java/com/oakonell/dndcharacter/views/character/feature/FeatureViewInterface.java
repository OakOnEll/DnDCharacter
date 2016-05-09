package com.oakonell.dndcharacter.views.character.feature;

import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Rob on 5/6/2016.
 */
public interface FeatureViewInterface {
    TextView getSourceTextView();

    ViewGroup getLimitedUsesGroupViewGroup();

    TextView getUsesLabelTextView();

    TextView getShortDescriptionTextView();

    TextView getUsesRemainingTextView();

    TextView getRefreshesLabelTextView();
}
