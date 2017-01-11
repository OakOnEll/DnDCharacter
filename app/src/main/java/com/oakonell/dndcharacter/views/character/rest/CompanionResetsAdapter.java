package com.oakonell.dndcharacter.views.character.rest;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.model.character.rest.CompanionRest;

import java.util.List;

/**
 * Created by Rob on 1/10/2017.
 */
class CompanionResetsAdapter extends RecyclerView.Adapter<CompanionResetViewHolder> {
    private final List<CompanionRest> resets;
    private final AbstractRestDialogFragment fragment;

    public CompanionResetsAdapter(AbstractRestDialogFragment fragment, List<CompanionRest> resets) {
        this.fragment = fragment;
        this.resets = resets;
    }


    public CompanionRest getItem(int position) {
        return resets.get(position);
    }

    @NonNull
    @Override
    public CompanionResetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(fragment.getActivity(), fragment.getHealingViewHelper().getCompanionRestLayoutId(), null);
        CompanionResetViewHolder viewHolder = new CompanionResetViewHolder(view, fragment.createHealingViewHelper());

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CompanionResetViewHolder viewHolder, int position) {
        final CompanionRest row = getItem(position);
        viewHolder.bind(fragment.getActivity(), this, row);

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return resets.size();
    }


}
