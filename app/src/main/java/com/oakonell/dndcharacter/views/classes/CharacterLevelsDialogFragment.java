package com.oakonell.dndcharacter.views.classes;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.MainActivity;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterClass;
import com.oakonell.dndcharacter.model.StatType;
import com.oakonell.dndcharacter.views.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.ItemTouchHelperAdapter;
import com.oakonell.dndcharacter.views.ItemTouchHelperViewHolder;
import com.oakonell.dndcharacter.views.SimpleItemTouchHelperCallback;

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

    private Map<CharacterClass, Long> recordsBeingDeleted = new HashMap<>();
    private boolean savedDeleteInProgress;

    public static CharacterLevelsDialogFragment createDialog() {
        return new CharacterLevelsDialogFragment();
    }

    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (recordsBeingDeleted != null && recordsBeingDeleted.size() > 0) {
            outState.putByte("deleteInProgress", (byte) 1);
            recordsBeingDeleted.clear();
        }
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        classesAdapter = new ClassAdapter(this, character.getClasses());

        if (savedDeleteInProgress) {
            CharacterClass toDelete = classesAdapter.classes.get(classesAdapter.classes.size() - 1);
            recordsBeingDeleted.put(toDelete, System.currentTimeMillis());
            savedDeleteInProgress = false;
        }

        list.setAdapter(classesAdapter);
        list.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
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
    public void onCharacterChanged(Character character) {
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

    public class CharacterItemTouchHelperCallback extends SimpleItemTouchHelperCallback {
        public CharacterItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            super(adapter, false, true);
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getAdapterPosition() != recyclerView.getAdapter().getItemCount() - 1) {
                return makeMovementFlags(0, 0);
            }
            return super.getMovementFlags(recyclerView, viewHolder);
        }
    }

    public static class DeleteRowViewHolder extends BindableViewHolder {
        TextView name;
        Button undo;

        public DeleteRowViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            undo = (Button) itemView.findViewById(R.id.undo);
        }

        @Override
        public void bindTo(final CharacterClass item, final CharacterLevelsDialogFragment context, final ClassAdapter adapter) {
            name.setText(item.getName() + " - " + item.getLevel());
            undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.recordsBeingDeleted.remove(item);
                    adapter.notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }

    public static class ClassAdapter extends RecyclerView.Adapter<BindableViewHolder>
            implements ItemTouchHelperAdapter {
        private final CharacterLevelsDialogFragment context;
        private List<CharacterClass> classes;

        ClassAdapter(CharacterLevelsDialogFragment context, List<CharacterClass> classes) {
            this.context = context;
            this.classes = classes;
        }

        public void reloadList(Character character) {
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

        @Override
        public BindableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == -1) {
                View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.deleting_equipment_row, parent, false);
                return new DeleteRowViewHolder(newView);
            }
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_level_item, parent, false);
            return new BindableRecyclerViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(BindableViewHolder holder, int position) {
            holder.bindTo(getItem(position), context, this);
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
            if (context.recordsBeingDeleted.containsKey(item)) {
                // actually delete the record, now
                classes.remove(item);
                context.recordsBeingDeleted.remove(item);
                notifyItemRemoved(position);
                if (context.getMainActivity() != null) {
                    context.updateView();
                    ((MainActivity) context.getActivity()).updateViews();
                }
            }

            context.recordsBeingDeleted.put(item, System.currentTimeMillis());
            notifyItemChanged(position);

            final MainActivity activity = ((MainActivity) context.getActivity());
            context.list.postDelayed(new Runnable() {
                public void run() {
                    // may have been deleted, undone, and then redeleted
                    Long deletedTime = context.recordsBeingDeleted.get(item);
                    if (deletedTime == null) return;
                    if (System.currentTimeMillis() - deletedTime >= UNDO_DELAY) {
                        // actually delete the record, now
                        context.recordsBeingDeleted.remove(item);
                        if (context.getMainActivity() != null) {
                            classes.remove(item);
                            notifyItemRemoved(position);
                            context.updateView();
                            activity.updateViews();
                        }

                    }
                }
            }, UNDO_DELAY);

        }
    }

    private void updateView() {
        classesTextView.setText(getCharacter().getClassesString());
    }

    public static abstract class BindableViewHolder extends RecyclerView.ViewHolder {
        public BindableViewHolder(View itemView) {
            super(itemView);
        }

        abstract void bindTo(final CharacterClass item, final CharacterLevelsDialogFragment context, final ClassAdapter adapter);
    }

    public static class BindableRecyclerViewHolder extends BindableViewHolder implements ItemTouchHelperViewHolder {
        TextView character_level;
        TextView class_name;
        TextView class_level;
        TextView hp;
        TextView hit_dice;
        private Drawable originalBackground;


        public BindableRecyclerViewHolder(View itemView) {
            super(itemView);
            character_level = (TextView) itemView.findViewById(R.id.character_level);
            class_name = (TextView) itemView.findViewById(R.id.class_name);
            class_level = (TextView) itemView.findViewById(R.id.class_level);
            hp = (TextView) itemView.findViewById(R.id.hp);
            hit_dice = (TextView) itemView.findViewById(R.id.hit_dice);

        }

        public void bindTo(final CharacterClass item, final CharacterLevelsDialogFragment context, final ClassAdapter adapter) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditClassLevelDialogFragment dialog = EditClassLevelDialogFragment.createDialog(getAdapterPosition(), true);
                    dialog.show(context.getFragmentManager(), "class_edit");
                }
            });
            character_level.setText((getAdapterPosition() + 1) + "");
            class_name.setText(item.getName());
            class_level.setText(item.getLevel() + "");
            final int conModifier = context.getCharacter().getStatBlock(StatType.CONSTITUTION).getModifier();
            hp.setText((item.getHpRoll() + conModifier) + "");
            String conModStr = "";
            if (conModifier > 0) {
                conModStr = " + " + conModifier;
            } else if (conModifier < 0) {
                conModStr = " - " + Math.abs(conModifier);
            }
            hit_dice.setText("1d" + item.getHitDie() + conModStr);


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
}