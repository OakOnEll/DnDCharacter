package com.oakonell.dndcharacter.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.*;
import com.oakonell.dndcharacter.model.Character;

/**
 * Created by Rob on 10/26/2015.
 */
public class FeatureBlockView extends LinearLayout {
    Character character;
    FeatureInfo info;

    TextView name;
    TextView source;
    TextView shortDescription;

    TextView uses_label;
    Button usesButton;
    TextView refreshes_label;

    public FeatureBlockView(Context context) {
        super(context);
        init(context);
    }

    public FeatureBlockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FeatureBlockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.feature_layout, this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        this.name = (TextView) findViewById(R.id.name);
        this.source = (TextView) findViewById(R.id.source);
        this.uses_label = (TextView) findViewById(R.id.uses_label);
        this.usesButton = (Button) findViewById(R.id.uses_button);
        this.shortDescription = (TextView) findViewById(R.id.short_description);
        this.refreshes_label = (TextView) findViewById(R.id.refreshes_label);
    }

    public void setFeatureInfo(FeatureInfo info) {
        this.info = info;
        if (character != null) {
            updateViews();
        }
    }

    public void setCharacter(Character character) {
        this.character = character;
        if (info != null) {
            updateViews();
        }
    }

    private void updateViews() {
        name.setText(info.getName());
        source.setText(info.getSourceString());
        String formula = info.getUsesFormula();
        boolean hasFormula = !(formula == null || formula.length() == 0);
        if (!hasFormula) {
            uses_label.setVisibility(View.GONE);
            usesButton.setVisibility(View.GONE);
            refreshes_label.setVisibility(View.GONE);
        } else {
            uses_label.setVisibility(View.VISIBLE);
            usesButton.setVisibility(View.VISIBLE);
            refreshes_label.setVisibility(View.VISIBLE);
            int maxUses = character.evaluateMaxUses(info.getFeature());
            int usesRemaining = character.getUsesRemaining(info.getFeature());
            usesButton.setText(usesRemaining + " / " + maxUses);
            refreshes_label.setText("Refreshes on " + info.getFeature().getRefreshesOn().toString());
        }
        shortDescription.setText(info.getShortDescription());
    }
}
