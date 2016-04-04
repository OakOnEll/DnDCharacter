package com.oakonell.dndcharacter.views.character.classes;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterClass;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.ItemTouchHelperAdapter;
import com.oakonell.dndcharacter.views.ItemTouchHelperViewHolder;
import com.oakonell.dndcharacter.views.SimpleItemTouchHelperCallback;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 12/11/2015.
 */
public class CharacterLevelsDialogFragment extends AbstractCharacterDialogFragment {
    private static final int UNDO_DELAY = 5000;
    private RecyclerView list;
    private TextView classesTextView;
    private ViewGroup level_up_group;
    private ClassAdapter classesAdapter;

    private final Map<CharacterClass, Long> recordsBeingDeleted = new HashMap<>();
    private boolean savedDeleteInProgress;

    @NonNull
    public static CharacterLevelsDialogFragment createDialog() {
        return new CharacterLevelsDialogFragment();
    }

    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.character_levels_dialog, container);

        classesTextView = (TextView) view.findViewById(R.id.classes);
        level_up_group = (ViewGroup) view.findViewById(R.id.level_up_group);

        list = (RecyclerView) view.findViewById(R.id.list);

        if (savedInstanceState != null) {
            savedDeleteInProgress = savedInstanceState.getByte("deleteInProgress", (byte) 0) != 0;
        }

        return view;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.character_class_levels);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (recordsBeingDeleted != null && recordsBeingDeleted.size() > 0) {
            outState.putByte("deleteInProgress", (byte) 1);
            recordsBeingDeleted.clear();
        }
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);
        classesAdapter = new ClassAdapter(this, character.getClasses());

        if (savedDeleteInProgress) {
            CharacterClass toDelete = classesAdapter.classes.get(classesAdapter.classes.size() - 1);
            recordsBeingDeleted.put(toDelete, System.currentTimeMillis());
            savedDeleteInProgress = false;
        }

        list.setAdapter(classesAdapter);
        list.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list.setHasFixedSize(false);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        list.addItemDecoration(itemDecoration);


        ItemTouchHelper.Callback armorCallback =
                new CharacterItemTouchHelperCallback(classesAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(armorCallback);
        touchHelper.attachToRecyclerView(list);


        level_up_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddClassLevelDialogFragment dialog = AddClassLevelDialogFragment.createDialog();
                dialog.show(getFragmentManager(), "level_up");
            }
        });

        updateView();
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);
        classesAdapter.reloadList(character);
        updateView();
    }

    @Override
    protected boolean onDone() {
        // any pending deletes should be done now, to avoid the delayed post failing because dialog/activity is lost
        //   and can't undo once clicking done, anyway

        for (Iterator<Map.Entry<CharacterClass, Long>> iter = recordsBeingDeleted.entrySet().iterator(); iter.hasNext(); ) {
            CharacterClass theClass = iter.next().getKey();
            classesAdapter.classes.remove(theClass);
            iter.remove();
        }

        return super.onDone();
    }

    private void updateView() {
        final String classesString = getCharacter().getClassesString();
        if (classesString == null) {
            classesTextView.setText(R.string.no_class);
        } else {
            classesTextView.setText(classesString);
        }
    }

    public static class DeleteClassViewHolder extends AbstractCharacterClassViewHolder {
        @NonNull
        final TextView name;
        @NonNull
        final Button undo;

        public DeleteClassViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            undo = (Button) itemView.findViewById(R.id.undo);
        }

        @Override
        public void bind(@NonNull final CharacterLevelsDialogFragment context, @NonNull final ClassAdapter adapter, @NonNull final CharacterClass characterClass) {
            name.setText(context.getString(R.string.class_and_level, characterClass.getName(), characterClass.getLevel()));
            undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.recordsBeingDeleted.remove(characterClass);
                    adapter.notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }

    public static class ClassAdapter extends RecyclerView.Adapter<AbstractCharacterClassViewHolder>
            implements ItemTouchHelperAdapter {
        private final CharacterLevelsDialogFragment context;
        private List<CharacterClass> classes;

        ClassAdapter(CharacterLevelsDialogFragment context, List<CharacterClass> classes) {
            this.context = context;
            this.classes = classes;
        }

        public void reloadList(@NonNull Character character) {
            classes = character.getClasses();
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            CharacterClass item = getItem(position);
            if (context.recordsBeingDeleted.containsKey(item)) {
                return -1;
            }
            return super.getItemViewType(position);
        }

        @NonNull
        @Override
        public AbstractCharacterClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == -1) {
                View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.deleting_equipment_row, parent, false);
                return new DeleteClassViewHolder(newView);
            }
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_level_item, parent, false);
            return new CharacterClassViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull AbstractCharacterClassViewHolder holder, int position) {
            holder.bind(context, this, getItem(position));
        }

        private CharacterClass getItem(int position) {
            return classes.get(position);
        }

        @Override
        public int getItemCount() {
            return classes.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            return false;
            // do nothing
        }

        @Override
        public void onItemDismiss(final int position) {
            final CharacterClass item = getItem(position);
            final CharacterActivity activity = ((CharacterActivity) context.getActivity());
            final Runnable deleteClass = new Runnable() {
                @Override
                public void run() {
                    context.recordsBeingDeleted.remove(item);
                    // actually delete the record, now
                    classes.remove(item);
                    if (activity.getCharacter().getHP()> activity.getCharacter().getMaxHP()) {
                        activity.getCharacter().setHP(activity.getCharacter().getMaxHP());
                    }
                    notifyItemRemoved(position);
                    if (context.getMainActivity() != null) {
                        context.updateView();
                        ((CharacterActivity) context.getActivity()).updateViews();
                        ((CharacterActivity) context.getActivity()).saveCharacter();
                    }

                }
            };
            if (context.recordsBeingDeleted.containsKey(item)) {
                deleteClass.run();
            }

            context.recordsBeingDeleted.put(item, System.currentTimeMillis());
            notifyItemChanged(position);

            context.list.postDelayed(new Runnable() {
                public void run() {
                    // may have been deleted, undone, and then redeleted
                    Long deletedTime = context.recordsBeingDeleted.get(item);
                    if (deletedTime == null) return;
                    if (System.currentTimeMillis() - deletedTime >= UNDO_DELAY) {
                        deleteClass.run();
                    }
                }
            }, UNDO_DELAY);

        }
    }

    public abstract static class AbstractCharacterClassViewHolder extends BindableComponentViewHolder<CharacterClass, CharacterLevelsDialogFragment, ClassAdapter> {
        protected AbstractCharacterClassViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class CharacterClassViewHolder extends AbstractCharacterClassViewHolder implements ItemTouchHelperViewHolder {
        @NonNull
        final TextView character_level;
        @NonNull
        final TextView class_name;
        @NonNull
        final TextView class_level;
        @NonNull
        final TextView hp;
        @NonNull
        final TextView hit_dice;
        @NonNull
        final TextView subclass_name;
        private Drawable originalBackground;


        public CharacterClassViewHolder(@NonNull View itemView) {
            super(itemView);
            character_level = (TextView) itemView.findViewById(R.id.character_level);
            class_name = (TextView) itemView.findViewById(R.id.class_name);
            class_level = (TextView) itemView.findViewById(R.id.class_level);
            hp = (TextView) itemView.findViewById(R.id.hp);
            hit_dice = (TextView) itemView.findViewById(R.id.hit_dice);
            subclass_name = (TextView) itemView.findViewById(R.id.subclass_name);

        }

        @Override
        public void bind(@NonNull final CharacterLevelsDialogFragment context, final ClassAdapter adapter, @NonNull final CharacterClass item) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditClassLevelDialogFragment dialog = EditClassLevelDialogFragment.createDialog(getAdapterPosition(), true);
                    dialog.show(context.getFragmentManager(), "class_edit");
                }
            });
            character_level.setText(NumberUtils.formatNumber(getAdapterPosition() + 1));
            class_name.setText(item.getName());
            class_level.setText(NumberUtils.formatNumber(item.getLevel()));
            final int conModifier = context.getCharacter().getStatBlock(StatType.CONSTITUTION).getModifier();
            hp.setText(NumberUtils.formatNumber((item.getHpRoll() + conModifier)));
            String conModStr = "";
            if (conModifier > 0) {
                conModStr = " + " + conModifier;
            } else if (conModifier < 0) {
                conModStr = " - " + Math.abs(conModifier);
            }
            hit_dice.setText("1d" + item.getHitDie() + conModStr);
            String subclassNameString = item.getSubclassName();
            if (subclassNameString != null) {
                subclass_name.setText(subclassNameString);
            } else {
                subclass_name.setText("");
            }
        }

        @Override
        public void onItemSelected() {
            originalBackground = itemView.getBackground();
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundDrawable(originalBackground);
        }

    }

    public class CharacterItemTouchHelperCallback extends SimpleItemTouchHelperCallback {
        public CharacterItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            super(adapter, false, true);
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getAdapterPosition() != recyclerView.getAdapter().getItemCount() - 1) {
                return makeMovementFlags(0, 0);
            }
            return super.getMovementFlags(recyclerView, viewHolder);
        }
    }
}