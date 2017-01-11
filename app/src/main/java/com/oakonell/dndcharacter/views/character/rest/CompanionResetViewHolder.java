package com.oakonell.dndcharacter.views.character.rest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.FeatureResetInfo;
import com.oakonell.dndcharacter.model.character.rest.CompanionRest;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;

import java.util.Collections;

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

    private final RecyclerView featureListView;
    private final View feature_resets_group;
    private FeatureResetsAdapter featureResetAdapter;

//    private FeatureContextHelper contextHelper = new FeatureContextHelper(this);

    public CompanionResetViewHolder(@NonNull View view, RestHealingViewHelper viewHelper) {
        super(view);
        name = (CheckBox) view.findViewById(R.id.name);
        description = (TextView) view.findViewById(R.id.description);
        healing_layout = view.findViewById(R.id.healing_layout);
        this.viewHelper = viewHelper;
        viewHelper.configureCommon(view);
        featureListView = (RecyclerView) view.findViewById(R.id.feature_list);
        feature_resets_group = view.findViewById(R.id.feature_resets);

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

        if (featureResetAdapter == null) {
            buildFeatureResets(context);
        }
        featureResetAdapter.setResets(row.getFeatureResets());

        feature_resets_group.setVisibility(featureResetAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);

        viewHelper.onCharacterLoaded(row);
    }

    protected void buildFeatureResets(final Context context) {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);

        featureResetAdapter = new FeatureResetsAdapter(context, Collections.<FeatureResetInfo>emptyList());
        featureListView.setAdapter(featureResetAdapter);

        featureListView.setHasFixedSize(false);
        featureListView.setLayoutManager(UIUtils.createLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        featureListView.addItemDecoration(itemDecoration);
    }

//    @Override
//    public FeatureContextArgument getNoteContext() {
//        return viewHelper.getNoteContext();
//    }
//
//    @Override
//    public Set<FeatureContextArgument> getContextFilter() {
//        return viewHelper.getContextFilter();
//    }
//
//    @Override
//    public AbstractCharacter getDisplayedCharacter() {
//        return null;
//    }
//
//    @Override
//    public boolean isForCompanion() {
//        return true;
//    }
//
//    @Override
//    public String getNoteTitle() {
//        return null;
//    }
}
