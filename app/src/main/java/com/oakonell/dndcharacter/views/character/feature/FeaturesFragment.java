package com.oakonell.dndcharacter.views.character.feature;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.FeatureInfo;
import com.oakonell.dndcharacter.views.character.AbstractSheetFragment;
import com.oakonell.dndcharacter.views.character.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 10/26/2015.
 */
public class FeaturesFragment extends AbstractSheetFragment {

    private FeatureAdapter adapter;
    private RecyclerView gridView;

    public View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.feature_sheet, container, false);

        superCreateViews(rootView);

        gridView = (RecyclerView) rootView.findViewById(R.id.features);

        return rootView;
    }

    @Override
    protected void updateViews(View rootView) {
        super.updateViews(rootView);
        if (adapter == null) { // odd state
            return;
        }
        adapter.reloadList(getCharacter());
    }


    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);

        adapter = new FeatureAdapter((MainActivity) this.getActivity());
        gridView.setAdapter(adapter);
        // decide on 1 or 2 columns based on screen size
        int numColumns = getResources().getInteger(R.integer.feature_columns);
        gridView.setLayoutManager(new StaggeredGridLayoutManager(numColumns, StaggeredGridLayoutManager.VERTICAL));

        updateViews();
    }


    public class FeatureAdapter extends RecyclerView.Adapter<FeatureViewHolder> {
        private final MainActivity context;
        private Set<FeatureContext> filter;
        private List<FeatureInfo> list;

        public FeatureAdapter(MainActivity context) {
            this.context = context;
            list = context.getCharacter().getFeatureInfos();
        }

        public FeatureAdapter(MainActivity context, Set<FeatureContext> filter) {
            this.context = context;
            this.filter = filter;
            list = filterList(context.getCharacter());
        }

        public void reloadList(Character character) {
            if (filter == null) {
                list = context.getCharacter().getFeatureInfos();
            } else {
                list = filterList(character);
            }
            notifyDataSetChanged();
        }

        private List<FeatureInfo> filterList(Character character) {
            if (filter == null) return character.getFeatureInfos();
            List<FeatureInfo> result = new ArrayList<>();
            for (FeatureInfo each : character.getFeatureInfos()) {
                if (each.getFeature().isInContext(filter)) {
                    result.add(each);
                }
            }
            return result;
        }

        @Override
        public int getItemCount() {
            if (context.getCharacter() == null) return 0;
            return list.size();
        }


        public FeatureInfo getItem(int position) {
            if (context.getCharacter() == null) return null;
            return list.get(position);
        }

        @Override
        public FeatureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.feature_layout, parent, false);
            FeatureViewHolder holder = new FeatureViewHolder(view);


            return holder;
        }

        @Override
        public void onBindViewHolder(final FeatureViewHolder viewHolder, final int position) {
            final FeatureInfo info = getItem(position);
            viewHolder.bind(context, this, info);
        }


    }
}