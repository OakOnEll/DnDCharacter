package com.oakonell.dndcharacter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.FeatureInfo;
import com.oakonell.dndcharacter.views.AbstractSheetFragment;

/**
 * Created by Rob on 10/26/2015.
 */
public class FeaturesFragment extends AbstractSheetFragment {

    private FeatureAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.feature_sheet, container, false);

        superCreateViews(rootView);

        adapter = new FeatureAdapter(this.getActivity(), character);
        RecyclerView gridView = (RecyclerView) rootView.findViewById(R.id.features);
        gridView.setAdapter(adapter);
        // decide on 1 or 2 columns based on screen size
        int numColumns = getResources().getInteger(R.integer.feature_columns);
        gridView.setLayoutManager(new StaggeredGridLayoutManager(numColumns, StaggeredGridLayoutManager.VERTICAL));


        updateViews(rootView);

        return rootView;
    }

    @Override
    protected void updateViews(View rootView) {
        super.updateViews(rootView);
        adapter.notifyDataSetChanged();
    }

    public void setCharacter(Character character) {
        super.setCharacter(character);
        if (adapter != null) {
            adapter.setCharacter(character);
        }
    }


    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView source;
        TextView shortDescription;

        TextView uses_label;
        Button usesButton;
        TextView refreshes_label;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class FeatureAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Character character;
        private Context context;

        public FeatureAdapter(Context context, Character character) {
            this.context = context;
            this.character = character;
        }

        @Override
        public int getItemCount() {
            if (character == null) return 0;
            return character.getFeatureInfos().size();
        }


        public FeatureInfo getItem(int position) {
            if (character == null) return null;
            return character.getFeatureInfos().get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.feature_layout, parent, false);
            ViewHolder holder = new ViewHolder(view);

            holder.name = (TextView) view.findViewById(R.id.name);
            holder.source = (TextView) view.findViewById(R.id.source);
            holder.uses_label = (TextView) view.findViewById(R.id.uses_label);
            holder.usesButton = (Button) view.findViewById(R.id.uses_button);
            holder.shortDescription = (TextView) view.findViewById(R.id.short_description);
            holder.refreshes_label = (TextView) view.findViewById(R.id.refreshes_label);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            final FeatureInfo info = getItem(position);
            viewHolder.name.setText(info.getName());
            viewHolder.source.setText(info.getSourceString());
            String formula = info.getUsesFormula();
            boolean hasFormula = !(formula == null || formula.length() == 0);
            if (!hasFormula) {
                viewHolder.uses_label.setVisibility(View.GONE);
                viewHolder.usesButton.setVisibility(View.GONE);
                viewHolder.refreshes_label.setVisibility(View.GONE);
            } else {
                viewHolder.uses_label.setVisibility(View.VISIBLE);
                viewHolder.usesButton.setVisibility(View.VISIBLE);
                viewHolder.refreshes_label.setVisibility(View.VISIBLE);
                int maxUses = character.evaluateMaxUses(info.getFeature());
                int usesRemaining = character.getUsesRemaining(info.getFeature());
                viewHolder.usesButton.setText(usesRemaining + " / " + maxUses);
                viewHolder.refreshes_label.setText("Refreshes on " + info.getFeature().getRefreshesOn().toString());
                viewHolder.usesButton.setEnabled(usesRemaining > 0);
                viewHolder.usesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        character.useFeature(info.getFeature());
                        notifyItemChanged(position);
//                        onBindViewHolder(viewHolder, position);
                    }
                });
            }
            viewHolder.shortDescription.setText(info.getShortDescription());
        }


        public void setCharacter(Character character) {
            if (this.character != character) {
                this.character = character;
                notifyDataSetChanged();
            }
        }
    }
}