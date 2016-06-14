package com.oakonell.dndcharacter.views.character.spell;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.spell.SpellSchool;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 5/26/2016.
 */
public class SelectKnownSpellDialogFragment extends AbstractCharacterDialogFragment {

    public static final String SPELLS = "spells";
    private RecyclerView listView;
    private SpellAdapter adapter;
    private ArrayList<KnownClassSpell> knownSpells;

    public static class KnownClassSpell implements Parcelable {
        public String spellName;
        public int spellLevel;
        public int levelKnown;
        public int levelIndex;
        public SpellSchool school;
        public boolean isRitual;

        public KnownClassSpell() {

        }

        public KnownClassSpell(Parcel in) {
            spellName = in.readString();
            spellLevel = in.readInt();
            levelKnown = in.readInt();
            levelIndex = in.readInt();
            school = EnumHelper.stringToEnum(in.readString(), SpellSchool.class);
            isRitual = in.readByte() != 0;
        }

        public static final Creator<KnownClassSpell> CREATOR = new Creator<KnownClassSpell>() {
            @Override
            public KnownClassSpell createFromParcel(Parcel in) {
                return new KnownClassSpell(in);
            }

            @Override
            public KnownClassSpell[] newArray(int size) {
                return new KnownClassSpell[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(spellName);
            dest.writeInt(spellLevel);
            dest.writeInt(levelKnown);
            dest.writeInt(levelIndex);
            dest.writeString(school.name());
            dest.writeByte((byte) (isRitual ? 1 : 0));
        }
    }

    private SpellSelectedListener listener;

    public void setListener(SpellSelectedListener listener) {
        this.listener = listener;
    }

    public static SelectKnownSpellDialogFragment createDialog(List<KnownClassSpell> knownSpells, SpellSelectedListener spellSelectedListener) {
        SelectKnownSpellDialogFragment dialog = new SelectKnownSpellDialogFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(SPELLS, new ArrayList<Parcelable>(knownSpells));

        dialog.setArguments(args);
        dialog.setListener(spellSelectedListener);
        return dialog;
    }

    public interface SpellSelectedListener {
        boolean spellSelected(KnownClassSpell charSpell);
    }

    @Nullable
    @Override
    protected String getTitle() {
        return getString(R.string.select_known_spell);
    }

    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.component_search_dialog, container);

        knownSpells = getArguments().getParcelableArrayList(SPELLS);

        listView = (RecyclerView) view.findViewById(R.id.list);

        listView.setHasFixedSize(true);
        listView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity()));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        listView.addItemDecoration(itemDecoration);

        adapter = new SpellAdapter(this);
        listView.setAdapter(adapter);


        return view;
    }


    static class SpellAdapter extends RecyclerView.Adapter<SpellViewHolder> {
        final List<KnownClassSpell> list;
        final SelectKnownSpellDialogFragment context;

        SpellAdapter(SelectKnownSpellDialogFragment context) {
            this.list = context.knownSpells;
            this.context = context;
        }

        @NonNull
        @Override
        public SpellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spell_known_list_item, parent, false);
            return new SpellViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull SpellViewHolder holder, int position) {
            holder.bind(context, this, list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    static class SpellViewHolder extends BindableComponentViewHolder<KnownClassSpell, SelectKnownSpellDialogFragment, SpellAdapter> {
        @NonNull
        private final TextView name;
        @NonNull
        private final TextView schoolTextView;
        @NonNull
        private final TextView levelTextView;
        @NonNull
        private final TextView ritualTextView;
        @NonNull
        private final TextView known_level;

        public SpellViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            schoolTextView = (TextView) itemView.findViewById(R.id.school);
            levelTextView = (TextView) itemView.findViewById(R.id.level);
            ritualTextView = (TextView) itemView.findViewById(R.id.ritual);
            known_level = (TextView) itemView.findViewById(R.id.known_level);
        }

        @Override
        public void bind(@NonNull final SelectKnownSpellDialogFragment context, SpellAdapter adapter, @NonNull final KnownClassSpell info) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context.listener.spellSelected(info)) {
                        context.dismiss();
                    }
                }
            });
            name.setText(info.spellName);
            levelTextView.setText(NumberUtils.formatNumber(info.spellLevel));
            known_level.setText(context.getString(R.string.known_spell_level, info.levelKnown));
            if (info.isRitual) {
                ritualTextView.setVisibility(View.VISIBLE);
            } else {
                ritualTextView.setVisibility(View.GONE);
            }
            schoolTextView.setText(info.school.getStringResId());
        }
    }

}
