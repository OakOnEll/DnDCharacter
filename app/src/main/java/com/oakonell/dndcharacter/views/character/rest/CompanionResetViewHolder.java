package com.oakonell.dndcharacter.views.character.rest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.rest.CompanionRest;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;

/**
 * Created by Rob on 1/10/2017.
 */
class CompanionResetViewHolder extends BindableComponentViewHolder<CompanionRest, Context, CompanionResetsAdapter> {
    @NonNull
    final CheckBox name;
    @NonNull
    final TextView description;

    @NonNull
    private final RestHealingViewHelper viewHelper;
    private final View healing_layout;

    public CompanionResetViewHolder(@NonNull View view, RestHealingViewHelper viewHelper) {
        super(view);
        name = (CheckBox) view.findViewById(R.id.name);
        description = (TextView) view.findViewById(R.id.description);
        healing_layout = view.findViewById(R.id.healing_layout);
        this.viewHelper = viewHelper;
        viewHelper.configureCommon(view);
    }

    @Override
    public void bind(@NonNull final Context context, final CompanionResetsAdapter adapter, @NonNull final CompanionRest row) {
        name.setOnCheckedChangeListener(null);
        name.setText(row.getName());
        name.setChecked((row.shouldReset()));
        healing_layout.setVisibility(row.shouldReset() ? View.VISIBLE : View.GONE);
        description.setText(row.getDescription());
        name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                row.shouldReset(isChecked);
                healing_layout.setVisibility(row.shouldReset() ? View.VISIBLE : View.GONE);
            }
        });

        viewHelper.onCharacterLoaded(row);
    }
}
