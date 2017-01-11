package com.oakonell.dndcharacter.views.character.rest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.FeatureResetInfo;

import java.util.List;

/**
 * Created by Rob on 1/10/2017.
 */
class FeatureResetsAdapter extends RecyclerView.Adapter<FeatureResetViewHolder> {
    private List<FeatureResetInfo> resets;
    private final Context context;

    public FeatureResetsAdapter(Context context, List<FeatureResetInfo> resets) {
        this.context = context;
        this.resets = resets;
    }


    public FeatureResetInfo getItem(int position) {
        return resets.get(position);
    }

    public void setResets(List<FeatureResetInfo> resets) {
        this.resets = resets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FeatureResetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.feature_reset_item, null);
        FeatureResetViewHolder viewHolder = new FeatureResetViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FeatureResetViewHolder viewHolder, int position) {
        final FeatureResetInfo row = getItem(position);
        viewHolder.bind(context, this, row);

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
