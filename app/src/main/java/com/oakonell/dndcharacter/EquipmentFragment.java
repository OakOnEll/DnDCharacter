package com.oakonell.dndcharacter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterArmor;
import com.oakonell.dndcharacter.model.CharacterItem;
import com.oakonell.dndcharacter.model.CharacterWeapon;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.views.AbstractSheetFragment;
import com.oakonell.dndcharacter.views.DividerItemDecoration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 10/26/2015.
 */
public class EquipmentFragment extends AbstractSheetFragment {
    private static final int UNDO_DELAY = 5000;
    TextView armor_proficiency;
    TextView weapon_proficiency;
    TextView tools_proficiency;
    private ViewGroup armor_group;
    private ViewGroup weapon_group;
    private ViewGroup tools_group;
    private TextView goldPieces;
    private TextView copperPieces;
    private TextView silverPieces;
    private TextView electrumPieces;
    private TextView platinumPieces;
    private EquipmentAdapter equipmentAdapter;
    private ArmorAdapter armorAdapter;
    private WeaponsAdapter weaponsAdapter;
    private Map<CharacterItem, Long> beingDeleted = new HashMap<CharacterItem, Long>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.equipment_sheet, container, false);

        superCreateViews(rootView);

        armor_proficiency = (TextView) rootView.findViewById(R.id.armor_proficiency);
        weapon_proficiency = (TextView) rootView.findViewById(R.id.weapon_proficiency);
        tools_proficiency = (TextView) rootView.findViewById(R.id.tool_proficiency);

        armor_group = (ViewGroup) rootView.findViewById(R.id.armor_group);
        weapon_group = (ViewGroup) rootView.findViewById(R.id.weapon_group);
        tools_group = (ViewGroup) rootView.findViewById(R.id.tools_group);

        copperPieces = (TextView) rootView.findViewById(R.id.copper_pieces);
        silverPieces = (TextView) rootView.findViewById(R.id.silver_pieces);
        electrumPieces = (TextView) rootView.findViewById(R.id.electrum_pieces);
        goldPieces = (TextView) rootView.findViewById(R.id.gold_pieces);
        platinumPieces = (TextView) rootView.findViewById(R.id.platinum_pieces);

        ViewGroup copperGroup = (ViewGroup) rootView.findViewById(R.id.copper_group);
        ViewGroup silverGroup = (ViewGroup) rootView.findViewById(R.id.silver_group);
        ViewGroup electrumGroup = (ViewGroup) rootView.findViewById(R.id.electrum_group);
        ViewGroup goldGroup = (ViewGroup) rootView.findViewById(R.id.gold_group);
        ViewGroup platinumGroup = (ViewGroup) rootView.findViewById(R.id.platinum_group);


        copperGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoneyDialogFragment dialog = MoneyDialogFragment.createFragment((MainActivity) getActivity(), MoneyDialogFragment.CoinType.COPPER);
                dialog.show(getFragmentManager(), "money_dialog");
            }
        });
        silverGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoneyDialogFragment dialog = MoneyDialogFragment.createFragment((MainActivity) getActivity(), MoneyDialogFragment.CoinType.SILVER);
                dialog.show(getFragmentManager(), "money_dialog");
            }
        });
        electrumGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoneyDialogFragment dialog = MoneyDialogFragment.createFragment((MainActivity) getActivity(), MoneyDialogFragment.CoinType.ELECTRUM);
                dialog.show(getFragmentManager(), "money_dialog");
            }
        });
        goldGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoneyDialogFragment dialog = MoneyDialogFragment.createFragment((MainActivity) getActivity(), MoneyDialogFragment.CoinType.GOLD);
                dialog.show(getFragmentManager(), "money_dialog");
            }
        });
        platinumGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoneyDialogFragment dialog = MoneyDialogFragment.createFragment((MainActivity) getActivity(), MoneyDialogFragment.CoinType.PLATINUM);
                dialog.show(getFragmentManager(), "money_dialog");
            }
        });


// armor
        RecyclerView armorView = (RecyclerView) rootView.findViewById(R.id.armor_list);
        armorAdapter = new ArmorAdapter(this, character);
        armorView.setAdapter(armorAdapter);
        armorView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        armorView.setHasFixedSize(false);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        armorView.addItemDecoration(itemDecoration);

        ViewGroup armorItemsView = (ViewGroup) rootView.findViewById(R.id.armor_items_group);
        armorItemsView.findViewById(R.id.equip).setVisibility(View.INVISIBLE);
//        ((TextView)armorItemsView.findViewById(R.id.ac)).setText("AC/Mod");

        Button addArmor = (Button) rootView.findViewById(R.id.addArmor);
        addArmor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArmor();
            }
        });


// weapons
        RecyclerView weaponsView = (RecyclerView) rootView.findViewById(R.id.weapons_list);
        weaponsAdapter = new WeaponsAdapter(this, character);
        weaponsView.setAdapter(weaponsAdapter);
        weaponsView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        weaponsView.setHasFixedSize(false);
        weaponsView.addItemDecoration(itemDecoration);

        Button addWeapon = (Button) rootView.findViewById(R.id.addWeapon);
        addWeapon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWeapon();
            }
        });

//        ViewGroup weaponsItemsView = (ViewGroup) rootView.findViewById(R.id.weapons_items_group);
//        ((TextView)weaponsItemsView.findViewById(R.id.hit_bonus)).setText("+To Hit");
//        ((TextView)weaponsItemsView.findViewById(R.id.damage)).setText("Dmg");

// regular equipment
        Button addEquipment = (Button) rootView.findViewById(R.id.addItem);
        addEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEquipment();
            }
        });

        RecyclerView itemsView = (RecyclerView) rootView.findViewById(R.id.items_list);
        equipmentAdapter = new EquipmentAdapter(this, character);
        itemsView.setAdapter(equipmentAdapter);
        itemsView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        itemsView.setHasFixedSize(false);
        itemsView.addItemDecoration(itemDecoration);


        updateViews(rootView);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(equipmentAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(itemsView);


        ItemTouchHelper.Callback armorCallback =
                new SimpleItemTouchHelperCallback(armorAdapter);
        ItemTouchHelper armorTouchHelper = new ItemTouchHelper(armorCallback);
        armorTouchHelper.attachToRecyclerView(armorView);


        ItemTouchHelper.Callback weaponsCallback =
                new SimpleItemTouchHelperCallback(weaponsAdapter);
        ItemTouchHelper weaponsTouchHelper = new ItemTouchHelper(weaponsCallback);
        weaponsTouchHelper.attachToRecyclerView(weaponsView);


        // need to hook a notes text watcher, to update the model
        return rootView;
    }

    private void addWeapon() {
        // TODO simple add for now
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Weapon");

// Set up the input
        final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                CharacterWeapon item = new CharacterWeapon();
                item.setName(name);
                character.addWeapon(item);
                weaponsAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addArmor() {
        // TODO simple add for now
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Armor");

// Set up the input
        final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                CharacterArmor item = new CharacterArmor();
                item.setName(name);
                character.addArmor(item);
                armorAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addEquipment() {
        // TODO simple add for now
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Equipment");

// Set up the input
        final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                CharacterItem item = new CharacterItem();
                item.setName(name);
                character.addItem(item);
                equipmentAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    protected void updateViews(View rootView) {
        super.updateViews(rootView);

        armor_proficiency.setText(character.getArmorProficiencyString());
        weapon_proficiency.setText(character.getWeaponsProficiencyString());
        tools_proficiency.setText(character.getToolsProficiencyString());

        goldPieces.setText(character.getGold() + "");
        copperPieces.setText(character.getCopper() + "");
        silverPieces.setText(character.getSilver() + "");
        electrumPieces.setText(character.getElectrum() + "");
        platinumPieces.setText(character.getPlatinum() + "");

        armor_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolProficiencyDialogFragment dialog = ToolProficiencyDialogFragment.create((MainActivity) getActivity(), ProficiencyType.ARMOR);
                dialog.show(getFragmentManager(), "tool_frag");
            }
        });
        weapon_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolProficiencyDialogFragment dialog = ToolProficiencyDialogFragment.create((MainActivity) getActivity(), ProficiencyType.WEAPON);
                dialog.show(getFragmentManager(), "tool_frag");
            }
        });
        tools_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolProficiencyDialogFragment dialog = ToolProficiencyDialogFragment.create((MainActivity) getActivity(), ProficiencyType.TOOL);
                dialog.show(getFragmentManager(), "tool_frag");
            }
        });


    }

    public interface ItemTouchHelperAdapter {

        void onItemMove(int fromPosition, int toPosition);

        void onItemDismiss(int position);
    }

    static class ItemViewHolder extends AbstractItemViewHolder {
        public ItemViewHolder(View view) {
            super(view);
        }
    }

    static class ArmorViewHolder extends AbstractItemViewHolder<CharacterArmor> {
        TextView ac;
        CheckBox equipped;

        public ArmorViewHolder(View view) {
            super(view);
            equipped = (CheckBox) view.findViewById(R.id.equip);
            ac = (TextView) view.findViewById(R.id.ac);
        }

        @Override
        public void bindTo(final CharacterArmor item, EquipmentFragment context, SubAdapter adapter) {
            super.bindTo(item, context, adapter);
            ac.setText("15");
            equipped.setChecked(item.isEquipped());
            equipped.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setEquipped(isChecked);
                    // TODO update views of any AC related fields
                }
            });
        }
    }

    static class WeaponViewHolder extends AbstractItemViewHolder<CharacterWeapon> {
        TextView bonus;
        TextView damage;

        public WeaponViewHolder(View view) {
            super(view);
            bonus = (TextView) view.findViewById(R.id.hit_bonus);
            damage = (TextView) view.findViewById(R.id.damage);
        }

        @Override
        public void bindTo(final CharacterWeapon item, final EquipmentFragment context, SubAdapter adapter) {
            super.bindTo(item, context, adapter);
            damage.setText("1d8 + 3 / Piercing");
            bonus.setText("+5");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeaponAttackDialogFragment fragment = WeaponAttackDialogFragment.create((MainActivity) context.getActivity(), item);
                    fragment.show(context.getFragmentManager(), "weapon_dialog");
                }
            });
        }

    }


    public static class BindableRecyclerViewHolder<I extends CharacterItem> extends RecyclerView.ViewHolder {

        public BindableRecyclerViewHolder(View itemView) {
            super(itemView);
        }

        public void bindTo(I item, EquipmentFragment context, SubAdapter adapter) {

        }
    }

    static class AbstractItemViewHolder<I extends CharacterItem> extends BindableRecyclerViewHolder<I> {
        TextView name;

        public AbstractItemViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
        }

        @Override
        public void bindTo(I item, EquipmentFragment context, SubAdapter adapter) {
            name.setText(item.getName());
            // TODO force child classes to implement onClick
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }


    }

    public static class DeleteRowViewHolder extends BindableRecyclerViewHolder {
        TextView name;
        Button undo;

        public DeleteRowViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            undo = (Button) itemView.findViewById(R.id.undo);
        }

        @Override
        public void bindTo(final CharacterItem item, final EquipmentFragment context, final SubAdapter adapter) {
            super.bindTo(item, context, adapter);
            name.setText(item.getName());
            undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.getItemPositionsBeingDeleted().remove(item);
                    adapter.notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }

    public abstract static class SubAdapter<I extends CharacterItem> extends RecyclerView.Adapter<BindableRecyclerViewHolder> implements ItemTouchHelperAdapter {
        protected Context context;
        protected EquipmentFragment fragment;
        protected List<I> list;

        public SubAdapter(EquipmentFragment fragment, List<I> list) {
            this.context = fragment.getContext();
            this.fragment = fragment;
            this.list = list;
        }


        @Override
        public int getItemCount() {
            if (list == null) return 0;
            return list.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public I getItem(int position) {
            if (list == null) return null;
            return list.get(position);
        }

        public int getItemViewType(int position) {
            if (getItemPositionsBeingDeleted().containsKey(getItem(position))) {
                return 1;
            }
            return 0;
        }

        @Override
        public final BindableRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.deleting_equipment_row, parent, false);
                DeleteRowViewHolder holder = new DeleteRowViewHolder(newView);
                return holder;
            }
            return onSubCreateViewHolder(parent, viewType);
        }

        protected abstract BindableRecyclerViewHolder onSubCreateViewHolder(ViewGroup parent, int viewType);


        protected Map<CharacterItem, Long> getItemPositionsBeingDeleted() {
            return fragment.beingDeleted;
        }

        @Override
        public void onBindViewHolder(BindableRecyclerViewHolder holder, int position) {
            holder.bindTo(getItem(position), fragment, this);
        }

        @Override
        public void onItemDismiss(final int position) {
            final Map<CharacterItem, Long> beingDeleted = getItemPositionsBeingDeleted();
            final CharacterItem item = getItem(position);
            if (beingDeleted.containsKey(item)) {
                // actually delete the record, now
                deleteRow(item);
                beingDeleted.remove(item);
                notifyItemRemoved(position);
            }

            beingDeleted.put(item, System.currentTimeMillis());
            notifyItemChanged(position);

            fragment.getView().postDelayed(new Runnable() {
                public void run() {
                    // may have been deleted, undone, and then redeleted
                    Long deletedTime = (Long) beingDeleted.get(item);
                    if (deletedTime == null) return;
                    if (System.currentTimeMillis() - deletedTime >= UNDO_DELAY) {
                        // actually delete the record, now
                        deleteRow(item);
                        beingDeleted.remove(item);
                        notifyItemRemoved(position);
                    }
                }
            }, UNDO_DELAY);


        }

        protected void deleteRow(CharacterItem item) {
            list.remove(item);
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(list, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(list, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    public static class ArmorAdapter extends SubAdapter<CharacterArmor> {
        public ArmorAdapter(EquipmentFragment fragment, Character character) {
            super(fragment, character.getArmor());
        }

        @Override
        public ArmorViewHolder onSubCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.armor_row, parent, false);
            return new ArmorViewHolder(view);
        }

    }

    public static class WeaponsAdapter extends SubAdapter<CharacterWeapon> {
        public WeaponsAdapter(EquipmentFragment fragment, Character character) {
            super(fragment, character.getWeapons());
        }

        @Override
        public WeaponViewHolder onSubCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weapon_row, parent, false);
            return new WeaponViewHolder(view);
        }
    }

    public static class EquipmentAdapter extends SubAdapter<CharacterItem> {

        public EquipmentAdapter(EquipmentFragment fragment, Character character) {
            super(fragment, character.getItems());
        }

        @Override
        public ItemViewHolder onSubCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.equipment_row, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BindableRecyclerViewHolder holder, int position) {
            holder.bindTo(getItem(position), fragment, this);
        }

    }

    public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

        private final ItemTouchHelperAdapter mAdapter;

        public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }

    }
}
