package com.oakonell.dndcharacter.views.character.feat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentSource;
import com.oakonell.dndcharacter.model.character.CustomAdjustmentType;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.CustomNumericAdjustmentDialog;
import com.oakonell.dndcharacter.views.character.RowWithSourceAdapter;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 11/30/2015.
 */
public class PassivePerceptionDialogFragment extends AbstractCharacterDialogFragment {
    public static final String ADJUSTMENT_FRAG = "stat_adjustment";
    private RecyclerView listView;
    private PassivePerceptionSourcesAdapter adapter;

    private View add_adjustment;
    TextView perception_total;

    @NonNull
    public static PassivePerceptionDialogFragment create(boolean isForCompanion) {
        final PassivePerceptionDialogFragment fragment = new PassivePerceptionDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(COMPANION_ARG, isForCompanion);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.passive_perception_dialog, container);

        listView = (RecyclerView) view.findViewById(R.id.list);

        perception_total = (TextView) view.findViewById(R.id.total);

        add_adjustment = view.findViewById(R.id.add_adjustment);
        add_adjustment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = getString(R.string.add_passive_perception_adjustment);
                CustomNumericAdjustmentDialog dialog = CustomNumericAdjustmentDialog.createDialog(title, CustomAdjustmentType.PASSIVE_PERCEPTION, isForCompanion());
                dialog.show(getFragmentManager(), ADJUSTMENT_FRAG);
            }
        });


        return view;
    }


    @Override
    protected String getTitle() {
        return getString(R.string.passive_perception_title);
    }

    @NonNull
    @Override
    public Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.PASSIVE_PERCEPTION));
        return filter;
    }

    @Override
    public FeatureContextArgument getNoteContext() {
        return new FeatureContextArgument(FeatureContext.PASSIVE_PERCEPTION);
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        RowWithSourceAdapter.ListRetriever<Character.PassivePerceptionWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.PassivePerceptionWithSource>() {
            @NonNull
            @Override
            public List<Character.PassivePerceptionWithSource> getList(@NonNull AbstractCharacter character) {
                return getDisplayedCharacter().derivePassivePerception();
            }
        };

        adapter = new PassivePerceptionSourcesAdapter(this, listRetriever, isForCompanion());
        listView.setAdapter(adapter);

        listView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);

        perception_total.setText(NumberUtils.formatNumber(character.getPassivePerception()));

    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);
        adapter.reloadList(getDisplayedCharacter());
        perception_total.setText(NumberUtils.formatNumber(getDisplayedCharacter().getPassivePerception()));
    }

    public static class PassivePerceptionSourceViewHolder extends RowWithSourceAdapter.WithSourceViewHolder<Character.PassivePerceptionWithSource> {

        public PassivePerceptionSourceViewHolder(@NonNull View view) {
            super(view);
        }

        @Override
        public void bind(@NonNull CharacterActivity activity, @NonNull RowWithSourceAdapter<Character.PassivePerceptionWithSource, RowWithSourceAdapter.WithSourceViewHolder<Character.PassivePerceptionWithSource>> adapter, @NonNull Character.PassivePerceptionWithSource item) {
            super.bind(activity, adapter, item);
            int initiative = item.getPassivePerception();

            this.value.setText(NumberUtils.formatNumber(initiative));
            final ComponentSource source = item.getSource();
            if (source == null) {
                // a base stat
                this.source.setText(R.string.dex_mod);
            } else {
                this.source.setText(source.getSourceString(activity.getResources()));
            }
        }
    }

    public static class PassivePerceptionSourcesAdapter extends RowWithSourceAdapter<Character.PassivePerceptionWithSource, PassivePerceptionSourceViewHolder> {
        PassivePerceptionSourcesAdapter(@NonNull PassivePerceptionDialogFragment fragment, @NonNull ListRetriever<Character.PassivePerceptionWithSource> listRetriever, boolean isForCompanion) {
            super(fragment.getMainActivity(), listRetriever, isForCompanion);
        }


        @Override
        protected int getLayoutResource() {
            return R.layout.stat_mod_row;
        }

        @NonNull
        @Override
        protected PassivePerceptionSourceViewHolder newViewHolder(@NonNull View view) {
            return new PassivePerceptionSourceViewHolder(view);
        }

        @Override
        protected CustomAdjustmentType getAdjustmentType() {
            return CustomAdjustmentType.PASSIVE_PERCEPTION;
        }
    }


}
