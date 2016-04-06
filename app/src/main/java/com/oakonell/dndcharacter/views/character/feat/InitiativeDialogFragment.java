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
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ComponentSource;
import com.oakonell.dndcharacter.model.character.CustomAdjustmentType;
import com.oakonell.dndcharacter.model.character.feature.FeatureContextArgument;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.CustomNumericAdjustmentDialog;
import com.oakonell.dndcharacter.views.character.RollableDialogFragment;
import com.oakonell.dndcharacter.views.character.RowWithSourceAdapter;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Rob on 11/30/2015.
 */
public class InitiativeDialogFragment extends RollableDialogFragment {
    public static final String INITIATIVE_ADJUSTMENT_FRAG = "initiative_adjustment";
    private RecyclerView listView;
    private InitiativeSourcesAdapter adapter;
    View add_adjustment;
    TextView initiative_modifier;

    @NonNull
    public static InitiativeDialogFragment create() {
        return new InitiativeDialogFragment();
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.initiative_dialog, container);
        superCreateView(view, savedInstanceState);


        listView = (RecyclerView) view.findViewById(R.id.list);

        add_adjustment = view.findViewById(R.id.add_adjustment);
        initiative_modifier = (TextView) view.findViewById(R.id.initiative_modifier);

        add_adjustment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomNumericAdjustmentDialog dialog = CustomNumericAdjustmentDialog.createDialog(getString(R.string.add_initiative_adjustment), CustomAdjustmentType.INITIATIVE);
                dialog.show(getFragmentManager(), INITIATIVE_ADJUSTMENT_FRAG);
            }
        });

        return view;
    }


    @Override
    protected String getTitle() {
        return getString(R.string.initiative_title);
    }

    @NonNull
    @Override
    protected Set<FeatureContextArgument> getContextFilter() {
        Set<FeatureContextArgument> filter = new HashSet<>();
        filter.add(new FeatureContextArgument(FeatureContext.INITIATIVE));
        return filter;
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);
        RowWithSourceAdapter.ListRetriever<Character.InitiativeWithSource> listRetriever = new RowWithSourceAdapter.ListRetriever<Character.InitiativeWithSource>() {
            @NonNull
            @Override
            public List<Character.InitiativeWithSource> getList(@NonNull Character character) {
                return character.deriveInitiative();
            }
        };

        final int initiative = character.getInitiative();
        setModifier(initiative);
        initiative_modifier.setText(NumberUtils.formatNumber(initiative));

        adapter = new InitiativeSourcesAdapter(this, listRetriever);
        listView.setAdapter(adapter);

        listView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listView.setHasFixedSize(false);
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);
        adapter.reloadList(character);

        final int initiative = character.getInitiative();
        setModifier(initiative);
        initiative_modifier.setText(NumberUtils.formatNumber(initiative));
    }

    public static class InitiativeSourceViewHolder extends RowWithSourceAdapter.WithSourceViewHolder<Character.InitiativeWithSource> {

        public InitiativeSourceViewHolder(@NonNull View view) {
            super(view);
        }

        @Override
        public void bind(@NonNull CharacterActivity activity, @NonNull RowWithSourceAdapter<Character.InitiativeWithSource, RowWithSourceAdapter.WithSourceViewHolder<Character.InitiativeWithSource>> adapter, @NonNull Character.InitiativeWithSource item) {
            super.bind(activity, adapter, item);
            int initiative = item.getInitiative();

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

    public static class InitiativeSourcesAdapter extends RowWithSourceAdapter<Character.InitiativeWithSource, InitiativeSourceViewHolder> {
        InitiativeSourcesAdapter(@NonNull InitiativeDialogFragment fragment, @NonNull ListRetriever<Character.InitiativeWithSource> listRetriever) {
            super(fragment.getMainActivity(), listRetriever);
        }

        @Override
        protected int getLayoutResource() {
            return R.layout.stat_mod_row;
        }

        @NonNull
        @Override
        protected InitiativeSourceViewHolder newViewHolder(@NonNull View view) {
            return new InitiativeSourceViewHolder(view);
        }

        protected void launchNoSource(CharacterActivity activity, Character character) {
            // TODO launch base/dex stat
        }

        @Override
        protected CustomAdjustmentType getAdjustmentType() {
            return CustomAdjustmentType.INITIATIVE;
        }

    }

    protected boolean supportsCriticalRolls() {
        return false;
    }


}
