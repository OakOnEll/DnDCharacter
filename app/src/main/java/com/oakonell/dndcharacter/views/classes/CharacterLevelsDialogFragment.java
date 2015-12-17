package com.oakonell.dndcharacter.views.classes;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.oakonell.dndcharacter.views.ComponentLaunchHelper;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.ItemTouchHelperAdapter;
import com.oakonell.dndcharacter.views.ItemTouchHelperViewHolder;
import com.oakonell.dndcharacter.views.SimpleItemTouchHelperCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 12/11/2015.
 */
public class CharacterLevelsDialogFragment extends DialogFragment {
    private static final int UNDO_DELAY = 5000;
    private Character character;
    RecyclerView list;
    private Map<CharacterClass, Long> recordsBeingDeleted = new HashMap<>();
    private TextView classesTextView;

    public static CharacterLevelsDialogFragment createDialog(Character character) {
        CharacterLevelsDialogFragment newMe = new CharacterLevelsDialogFragment();
        newMe.setCharacter(character);
        return newMe;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Character getCharacter() {
        return character;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.character_levels_dialog, container);

        classesTextView = (TextView) view.findViewById(R.id.classes);
        ViewGroup level_up_group = (ViewGroup) view.findViewById(R.id.level_up_group);

        updateView();

        Button done = (Button) view.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        list = (RecyclerView) view.findViewById(R.id.list);

        final ClassAdapter classesAdapter = new ClassAdapter(this, character.getClasses());
        list.setAdapter(classesAdapter);
        list.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list.setHasFixedSize(false);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        list.addItemDecoration(itemDecoration);


        ItemTouchHelper.Callback armorCallback =
                new CharacterItemTouchHelperCallback(classesAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(armorCallback);
        touchHelper.attachToRecyclerView(list);


        final ComponentLaunchHelper.OnDialogDone onDone = new ComponentLaunchHelper.OnDialogDone() {
            @Override
            public void done(boolean changed) {
                classesAdapter.notifyDataSetChanged();
            }
        };

        level_up_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddClassLevelDialogFragment dialog = AddClassLevelDialogFragment.createDialog(character, null, onDone);
                dialog.show(getFragmentManager(), "level_up");
            }
        });


        return view;
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
        private final List<CharacterClass> classes;

        ClassAdapter(CharacterLevelsDialogFragment context, List<CharacterClass> classes) {
            this.context = context;
            this.classes = classes;
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
                context.updateView();
                ((MainActivity) context.getActivity()).updateViews();
            }

            context.recordsBeingDeleted.put(item, System.currentTimeMillis());
            notifyItemChanged(position);

            final MainActivity activity = ((MainActivity) context.getActivity());
            context.list.postDelayed(new Runnable() {
                public void run() {
                    // may have been deleted, undone, and then redeleted
                    Long deletedTime = (Long) context.recordsBeingDeleted.get(item);
                    if (deletedTime == null) return;
                    if (System.currentTimeMillis() - deletedTime >= UNDO_DELAY) {
                        // actually delete the record, now
                        classes.remove(item);
                        context.recordsBeingDeleted.remove(item);
                        notifyItemRemoved(position);
                        context.updateView();
                        activity.updateViews();

                    }
                }
            }, UNDO_DELAY);

        }
    }

    private void updateView() {
        classesTextView.setText(character.getClassesString());
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
                    // TODO
                    ComponentLaunchHelper.OnDialogDone onChange = new ComponentLaunchHelper.OnDialogDone() {
                        @Override
                        public void done(boolean changed) {
                            adapter.notifyDataSetChanged();
                            ((MainActivity) adapter.context.getActivity()).updateViews();
                        }
                    };
                    EditClassLevelDialogFragment dialog = EditClassLevelDialogFragment.createDialog(context.character, item, getAdapterPosition(), onChange);
                    dialog.show(context.getFragmentManager(), "class_edit");
                }
            });
            character_level.setText((getAdapterPosition() + 1) + "");
            class_name.setText(item.getName());
            class_level.setText(item.getLevel() + "");
            final int conModifier = context.character.getStatBlock(StatType.CONSTITUTION).getModifier();
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