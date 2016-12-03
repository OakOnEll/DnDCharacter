package com.oakonell.dndcharacter.views.character.companion;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.SpeedType;
import com.oakonell.dndcharacter.model.character.companion.CharacterCompanion;
import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.AbstractCharacterViewHelper;
import com.oakonell.dndcharacter.views.character.AbstractSheetFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.DeathSaveDialogFragment;
import com.oakonell.dndcharacter.views.character.HitPointDiaogFragment;
import com.oakonell.dndcharacter.views.character.MainFragment;
import com.oakonell.dndcharacter.views.character.feat.InitiativeDialogFragment;
import com.oakonell.dndcharacter.views.character.feat.PassivePerceptionDialogFragment;
import com.oakonell.dndcharacter.views.character.feature.FeatureContext;
import com.oakonell.dndcharacter.views.character.item.ArmorClassDialogFragment;
import com.oakonell.dndcharacter.views.character.race.SpeedDialogFragment;
import com.oakonell.dndcharacter.views.character.stats.SaveThrowBlockDialogFragment;
import com.oakonell.dndcharacter.views.character.stats.SavingThrowBlockView;
import com.oakonell.dndcharacter.views.character.stats.SkillBlockDialogFragment;
import com.oakonell.dndcharacter.views.character.stats.SkillBlockView;
import com.oakonell.dndcharacter.views.character.stats.StatBlockDialogFragment;
import com.oakonell.dndcharacter.views.character.stats.StatBlockView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 10/26/2015.
 */
public class CompanionsFragment extends AbstractSheetFragment {
    //    Button toXml;
    private View addCompanion;

    private CompanionsAdapter adapter;
    private RecyclerView companions_list;
    private View companion_layout;

    private TextView nameView;

    AbstractCharacterViewHelper characterViewHelper = new AbstractCharacterViewHelper(this);



    private static final int UNDO_DELAY = 5000;

    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.companions_sheet, container, false);

        addCompanion = rootView.findViewById(R.id.add_companion);
        companions_list = (RecyclerView) rootView.findViewById(R.id.companions_list);
        companion_layout = rootView.findViewById(R.id.companion_layout);
        nameView = (TextView) rootView.findViewById(R.id.name);

        characterViewHelper.onCreateView(rootView);

        superCreateViews(rootView);

        updateViews(rootView);

        addCompanion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<CharacterCompanion> companions = getCharacter().getCompanions();
                CharacterCompanion companion = new CharacterCompanion();
                companion.setName("Companion " + companions.size());
                companions.add(companion);

                updateViews(rootView);
            }
        });


        return rootView;
    }

    @Override
    protected void updateViews(View rootView) {
        super.updateViews(rootView);
        if (getCharacter() == null) {

        } else {
            final CharacterCompanion companion = getCharacter().getDisplayedCompanion();
            if (companion == null) {
                companion_layout.setVisibility(View.GONE);
            } else {
                companion_layout.setVisibility(View.VISIBLE);
                if (updateCompanionViews(rootView, companion)) return;
            }


            if (adapter != null) {
                adapter.reloadList(getCharacter());
            }
        }

    }

    private boolean updateCompanionViews(View rootView, final CharacterCompanion companion) {
        nameView.setText(companion.getName());

        characterViewHelper.updateViews(rootView, companion);


        return false;
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);
        if (getActivity() == null) return;


        adapter = new CompanionsAdapter((CharacterActivity) this.getActivity());
        companions_list.setAdapter(adapter);
        companions_list.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        companions_list.addItemDecoration(itemDecoration);

        companions_list.setHasFixedSize(false);

        updateViews();
    }


    public static class DeletingCompanionViewHolder extends BindableComponentViewHolder<CharacterCompanion, CharacterActivity, CompanionsFragment.CompanionsAdapter> {

        private final TextView name;
        @NonNull
        private final View undo;

        public DeletingCompanionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);

            undo = itemView.findViewById(R.id.undo);
        }

        @Override
        public void bind(final CharacterActivity context, final CompanionsAdapter adapter, final CharacterCompanion info) {
            name.setText(info.getName());

            undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info.setDeleting(false);
                    adapter.notifyDataSetChanged();
                    undo.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.saveCharacter();
                            context.updateViews();
                        }
                    }, 100);
                }
            });

        }
    }


    public static class CompanionViewHolder extends BindableComponentViewHolder<CharacterCompanion, CharacterActivity, CompanionsFragment.CompanionsAdapter> {

        private final TextView name;
        private final TextView race;
        private final TextView type;
        private final TextView description;
        @NonNull
        private final View delete;

        public CompanionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            type = (TextView) itemView.findViewById(R.id.type);
            race = (TextView) itemView.findViewById(R.id.race);
            description = (TextView) itemView.findViewById(R.id.description);

            delete = itemView.findViewById(R.id.delete);
        }

        @Override
        public void bind(final CharacterActivity context, final CompanionsAdapter adapter, final CharacterCompanion info) {
            name.setText(info.getName());
            race.setText(info.getRace());
            //type.setText(info.getType());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int index = getAdapterPosition();
                    if (index == context.getCharacter().getDisplayedCompanionIndex() || info.isDeleting()) {
                        context.getCharacter().setDisplayedCompanion(-1);
                    } else {
                        context.getCharacter().setDisplayedCompanion(index);
                    }
                    context.updateViews();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info.setDeleting(true);
                    final List<CharacterCompanion> companions = context.getCharacter().getCompanions();
                    int position = getAdapterPosition();
                    if (position == context.getCharacter().getDisplayedCompanionIndex()) {
                        context.getCharacter().setDisplayedCompanion(-1);
                    }

                    adapter.notifyDataSetChanged();
                    delete.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.saveCharacter();
                            context.updateViews();
                        }
                    }, 100);
                }
            });


            delete.postDelayed(new Runnable() {
                public void run() {
                    if (!info.isDeleting()) return;

                    // actually delete the record, now
                    // taking care to adjust the displayed companion index
                    final List<CharacterCompanion> companions = context.getCharacter().getCompanions();
                    int index = companions.indexOf(info);
                    if (context.getCharacter().getDisplayedCompanionIndex() > index) {
                        context.getCharacter().setDisplayedCompanion(context.getCharacter().getDisplayedCompanionIndex() - 1);
                    }
                    companions.remove(info);
                    //int position = getAdapterPosition();
                    //adapter.notifyItemRemoved(position);
                    adapter.notifyDataSetChanged();
                    delete.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.saveCharacter();
                            context.updateViews();
                        }
                    }, 100);
                }
            }, UNDO_DELAY);
        }
    }

    public static class CompanionsAdapter extends RecyclerView.Adapter<BindableComponentViewHolder<CharacterCompanion, CharacterActivity, CompanionsFragment.CompanionsAdapter>> {
        private static final int DELETING_VIEW_TYPE = -1;
        @NonNull
        private final CharacterActivity context;
        private List<CharacterCompanion> list;

        public CompanionsAdapter(@NonNull CharacterActivity context) {
            this.context = context;
            list = new ArrayList<>(context.getCharacter().getCompanions());
        }


        public void reloadList(@NonNull Character character) {
            list = new ArrayList<>(character.getCompanions());
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        @Nullable
        public CharacterCompanion getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getItemViewType(int position) {
            final CharacterCompanion companion = getItem(position);
            if (companion.isDeleting()) {
                return DELETING_VIEW_TYPE;
            }
            return 0;
        }

        @NonNull
        @Override
        public BindableComponentViewHolder<CharacterCompanion, CharacterActivity, CompanionsFragment.CompanionsAdapter> onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == DELETING_VIEW_TYPE) {
                View view = LayoutInflater.from(context).inflate(R.layout.deleted_companion_item_layout, parent, false);
                CompanionsFragment.DeletingCompanionViewHolder holder = new CompanionsFragment.DeletingCompanionViewHolder(view);
                return holder;
            }
            View view = LayoutInflater.from(context).inflate(R.layout.companion_item_layout, parent, false);
            CompanionsFragment.CompanionViewHolder holder = new CompanionsFragment.CompanionViewHolder(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(@NonNull final BindableComponentViewHolder<CharacterCompanion, CharacterActivity, CompanionsFragment.CompanionsAdapter> viewHolder, final int position) {
            final CharacterCompanion companion = getItem(position);
            viewHolder.bind(context, this, companion);
        }


    }


}
