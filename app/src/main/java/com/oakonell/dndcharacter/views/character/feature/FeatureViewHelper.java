package com.oakonell.dndcharacter.views.character.feature;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.FeatureInfo;
import com.oakonell.dndcharacter.model.components.UseType;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.ComponentLaunchHelper;

/**
 * Created by Rob on 5/6/2016.
 */
public class FeatureViewHelper {
    private final CharacterActivity context;
    final FeatureViewInterface view;

    @NonNull
    public final TextView source;
    @NonNull
    public final TextView shortDescription;

    @NonNull
    public final ViewGroup limited_uses_group;

    @NonNull
    public final TextView uses_label;
    @NonNull
    public final TextView uses_remaining;

    @NonNull
    public final TextView refreshes_label;


    FeatureViewHelper(CharacterActivity activity, @NonNull final FeatureViewInterface view) {
        this.context = activity;
        this.view = view;

        source = view.getSourceTextView();
        limited_uses_group = view.getLimitedUsesGroupViewGroup();
        uses_label = view.getUsesLabelTextView();
        shortDescription = view.getShortDescriptionTextView();

        uses_remaining = view.getUsesRemainingTextView();
        refreshes_label = view.getRefreshesLabelTextView();
    }

    public void bind(@NonNull final FeatureInfo info) {


        source.setText(info.getSourceString(context.getResources()));
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentLaunchHelper.editComponent(context, info.getSource(), false);
            }
        });

        boolean hasLimitedUses = info.hasLimitedUses();
        if (!hasLimitedUses) {
            limited_uses_group.setVisibility(View.GONE);
        } else {
            limited_uses_group.setVisibility(View.VISIBLE);
            bindLimitedUseViews(info);

            if (info.getUseType() == UseType.PER_USE) {
                uses_label.setText(R.string.uses);
            } else {
                uses_label.setText(R.string.pool);
            }
        }

        shortDescription.setText(info.getShortDescription());

    }

    protected void bindLimitedUseViews(@NonNull final FeatureInfo info) {
        int maxUses = info.evaluateMaxUses(context.getCharacter());
        final int usesRemaining = context.getCharacter().getUsesRemaining(info);

        uses_remaining.setText(context.getString(R.string.fraction_d_slash_d, usesRemaining, maxUses));
        refreshes_label.setText(context.getString(R.string.refreshes_on_s, context.getString(info.getRefreshesOn().getStringResId())));
    }
}
