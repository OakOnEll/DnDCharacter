package com.oakonell.dndcharacter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.CharacterArmor;
import com.oakonell.dndcharacter.model.CharacterItem;
import com.oakonell.dndcharacter.model.CharacterWeapon;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.views.AbstractSheetFragment;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.ItemTouchHelperAdapter;
import com.oakonell.dndcharacter.views.ItemTouchHelperViewHolder;
import com.oakonell.dndcharacter.views.SimpleItemTouchHelperCallback;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 10/26/2015.
 */
public class EquipmentFragment extends AbstractSheetFragment {
    private static final int UNDO_DELAY = 5000;
    private TextView armor_proficiency;
    private TextView weapon_proficiency;
    private TextView tools_proficiency;
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
    private View rootView;
    private RecyclerView armorView;
    private ViewGroup armorItemsView;
    private RecyclerView weaponsView;
    private ViewGroup weaponItemsView;
    private RecyclerView itemsView;

    private Map<CharacterItem, Long> beingDeleted = new HashMap<>();


    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.equipment_sheet, container, false);

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
                MoneyDialogFragment dialog = MoneyDialogFragment.createFragment(MoneyDialogFragment.CoinType.COPPER);
                dialog.show(getFragmentManager(), "money_dialog");
            }
        });
        silverGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoneyDialogFragment dialog = MoneyDialogFragment.createFragment(MoneyDialogFragment.CoinType.SILVER);
                dialog.show(getFragmentManager(), "money_dialog");
            }
        });
        electrumGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoneyDialogFragment dialog = MoneyDialogFragment.createFragment(MoneyDialogFragment.CoinType.ELECTRUM);
                dialog.show(getFragmentManager(), "money_dialog");
            }
        });
        goldGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoneyDialogFragment dialog = MoneyDialogFragment.createFragment(MoneyDialogFragment.CoinType.GOLD);
                dialog.show(getFragmentManager(), "money_dialog");
            }
        });
        platinumGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoneyDialogFragment dialog = MoneyDialogFragment.createFragment(MoneyDialogFragment.CoinType.PLATINUM);
                dialog.show(getFragmentManager(), "money_dialog");
            }
        });


        armorView = (RecyclerView) rootView.findViewById(R.id.armor_list);
        armorItemsView = (ViewGroup) rootView.findViewById(R.id.armor_items_group);
        weaponsView = (RecyclerView) rootView.findViewById(R.id.weapons_list);
        weaponItemsView = (ViewGroup) rootView.findViewById(R.id.weapons_items_group);
        itemsView = (RecyclerView) rootView.findViewById(R.id.items_list);


        Button addArmor = (Button) rootView.findViewById(R.id.addArmor);
        addArmor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArmor();
            }
        });


        Button addWeapon = (Button) rootView.findViewById(R.id.addWeapon);
        addWeapon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWeapon();
            }
        });

        Button addEquipment = (Button) rootView.findViewById(R.id.addItem);
        addEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEquipment();
            }
        });

        return rootView;
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);
        // armor
        armorAdapter = new ArmorAdapter(this, character);
        armorView.setAdapter(armorAdapter);
        armorView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        armorView.setHasFixedSize(false);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        armorView.addItemDecoration(itemDecoration);

        armorItemsView.findViewById(R.id.equip).setVisibility(View.INVISIBLE);
        armorItemsView.findViewById(R.id.handle).setVisibility(View.INVISIBLE);
//        ((TextView)armorItemsView.findViewById(R.id.ac)).setText("AC/Mod");
// weapons
        weaponsAdapter = new WeaponsAdapter(this, character);
        weaponsView.setAdapter(weaponsAdapter);
        weaponsView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        weaponsView.setHasFixedSize(false);
        weaponsView.addItemDecoration(itemDecoration);

        weaponItemsView.findViewById(R.id.handle).setVisibility(View.INVISIBLE);
// regular equipment
        equipmentAdapter = new EquipmentAdapter(this, character);
        itemsView.setAdapter(equipmentAdapter);
        itemsView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        itemsView.setHasFixedSize(false);
        itemsView.addItemDecoration(itemDecoration);


        updateViews(rootView);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(equipmentAdapter, true, false);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(itemsView);
        equipmentAdapter.setOnStartDrag(new OnStartDragListener() {
            @Override
            public void onStartDrag(AbstractItemViewHolder viewHolder) {
                touchHelper.startDrag(viewHolder);
            }

            @Override
            public void onStartSwipe(AbstractItemViewHolder holder) {
                touchHelper.startSwipe(holder);
            }
        });

        ItemTouchHelper.Callback armorCallback =
                new SimpleItemTouchHelperCallback(armorAdapter, true, false);
        final ItemTouchHelper armorTouchHelper = new ItemTouchHelper(armorCallback);
        armorTouchHelper.attachToRecyclerView(armorView);
        armorAdapter.setOnStartDrag(new OnStartDragListener() {
            @Override
            public void onStartDrag(AbstractItemViewHolder viewHolder) {
                armorTouchHelper.startDrag(viewHolder);
            }

            @Override
            public void onStartSwipe(AbstractItemViewHolder holder) {
                armorTouchHelper.startSwipe(holder);
            }
        });

        ItemTouchHelper.Callback weaponsCallback =
                new SimpleItemTouchHelperCallback(weaponsAdapter, true, false);
        final ItemTouchHelper weaponsTouchHelper = new ItemTouchHelper(weaponsCallback);
        weaponsTouchHelper.attachToRecyclerView(weaponsView);
        weaponsAdapter.setOnStartDrag(new OnStartDragListener() {
            @Override
            public void onStartDrag(AbstractItemViewHolder viewHolder) {
                weaponsTouchHelper.startDrag(viewHolder);
            }

            @Override
            public void onStartSwipe(AbstractItemViewHolder holder) {
                weaponsTouchHelper.startSwipe(holder);
            }
        });
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
                getCharacter().addWeapon(item);
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
                getCharacter().addArmor(item);
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
                getCharacter().addItem(item);
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
        Character character = getCharacter();
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
                ToolProficiencyDialogFragment dialog = ToolProficiencyDialogFragment.create(ProficiencyType.ARMOR);
                dialog.show(getFragmentManager(), "tool_frag");
            }
        });
        weapon_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolProficiencyDialogFragment dialog = ToolProficiencyDialogFragment.create(ProficiencyType.WEAPON);
                dialog.show(getFragmentManager(), "tool_frag");
            }
        });
        tools_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolProficiencyDialogFragment dialog = ToolProficiencyDialogFragment.create(ProficiencyType.TOOL);
                dialog.show(getFragmentManager(), "tool_frag");
            }
        });


        equipmentAdapter.notifyDataSetChanged();
        armorAdapter.notifyDataSetChanged();
        weaponsAdapter.notifyDataSetChanged();
    }

    public interface OnStartDragListener {

        /**
         * Called when a view is requesting a start of a drag.
         *
         * @param viewHolder The holder of the view to drag.
         */
        void onStartDrag(AbstractItemViewHolder viewHolder);

        void onStartSwipe(AbstractItemViewHolder holder);
    }

    static class ItemViewHolder extends AbstractItemViewHolder<CharacterItem> {
        public ItemViewHolder(View view, OnStartDragListener mDragStartListener) {
            super(view, mDragStartListener);
        }
    }

    static class ArmorViewHolder extends AbstractItemViewHolder<CharacterArmor> {
        TextView ac;
        CheckBox equipped;

        public ArmorViewHolder(View view, OnStartDragListener mDragStartListener) {
            super(view, mDragStartListener);
            equipped = (CheckBox) view.findViewById(R.id.equip);
            ac = (TextView) view.findViewById(R.id.ac);
        }

        @Override
        public void bindTo(final CharacterArmor item, final EquipmentFragment context, SubAdapter adapter) {
            super.bindTo(item, context, adapter);

            String acString = "?";
            if (item.isBaseArmor()) {
                int acVal = context.getCharacter().evaluateFormula(item.getBaseAcFormula(), null);
                acString = acVal + "";

            } else {
                String formula = item.getModifyingAcFormula();
                if (formula != null) {
                    int acVal = context.getCharacter().evaluateFormula(formula, null);
                    acString = "+" + acVal;
                }
            }
            ac.setText(acString);

            equipped.setChecked(item.isEquipped());
            equipped.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // TODO unequip any contraindicated armor? Or just notify the user?
                    // eg an effect for non-proficient armor use
                    item.setEquipped(isChecked);
                    // TODO update only views of any AC related fields
                    ((MainActivity) context.getActivity()).updateViews();
                    ((MainActivity) context.getActivity()).saveCharacter();
                }
            });
        }
    }

    static class WeaponViewHolder extends AbstractItemViewHolder<CharacterWeapon> {
        TextView bonus;
        TextView damage;

        public WeaponViewHolder(View view, OnStartDragListener mDragStartListener) {
            super(view, mDragStartListener);
            bonus = (TextView) view.findViewById(R.id.hit_bonus);
            damage = (TextView) view.findViewById(R.id.damage);
        }

        protected String getNameString(CharacterWeapon item) {
            String base = super.getNameString(item);
            StringBuilder builder = new StringBuilder(" [");
            boolean hasExtra = false;
            if (item.isRanged()) {
                if (hasExtra) {
                    builder.append(", ");
                }
                builder.append("(" + item.getRange() + ")");
                hasExtra = true;
            }
            // TODO short for small screens?
            if (item.isVersatile()) {
                if (hasExtra) {
                    builder.append(", ");
                }
                builder.append("Versatile");
                hasExtra = true;
            }
            if (item.isFinesse()) {
                if (hasExtra) {
                    builder.append(", ");
                }
                builder.append("Finesse");
                hasExtra = true;
            }

            if (hasExtra) {
                builder.append("]");
                return base + builder.toString();
            }

            return base;
        }

        @Override
        public void bindTo(final CharacterWeapon item, final EquipmentFragment context, SubAdapter adapter) {
            super.bindTo(item, context, adapter);


            final CharacterWeapon.AttackModifiers attackModifiers = item.getAttackModifiers(context.getCharacter(), false);
            final int damageModifier = attackModifiers.getDamageModifier();
            String damageModString = "";
            if (damageModifier != 0) {
                if (damageModifier < 0) {
                    damageModString = " - ";
                } else {
                    damageModString = " + ";
                }
                damageModString += Math.abs(damageModifier);
            }
            damage.setText(item.getDamageString() + damageModString);
            bonus.setText(attackModifiers.getAttackBonus() + "");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeaponAttackDialogFragment fragment = WeaponAttackDialogFragment.create(item);
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

    static class SwipeOrDragListener implements View.OnTouchListener {
        static final int MIN_DISTANCE = 10;
        private final OnStartDragListener mDragStartListener;
        AbstractItemViewHolder holder;
        boolean lookForDrag;
        private float downX;
        private float downY;

        SwipeOrDragListener(OnStartDragListener mDragStartListener, AbstractItemViewHolder holder) {
            this.holder = holder;
            this.mDragStartListener = mDragStartListener;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN && !lookForDrag) {
                Log.i("Equpiment", "touch down");
                downX = event.getX();
                downY = event.getY();
                lookForDrag = true;
                //mSwipeDetected = Action.None;
                return true; // allow other events like Click to be processed
            } else if (action == MotionEvent.ACTION_MOVE && lookForDrag) {
                Log.i("Equpiment", "action move ");
                float upX = event.getX();
                float upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // horizontal swipe detection
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    mDragStartListener.onStartSwipe(holder);
                    lookForDrag = false;
                    return false;
                } else if (Math.abs(deltaY) > MIN_DISTANCE) {
                    // vertical swipe
                    mDragStartListener.onStartDrag(holder);
                    lookForDrag = false;
                    return false;
                }
                return true;
            }
            return false;
        }
    }

    static class AbstractItemViewHolder<I extends CharacterItem> extends BindableRecyclerViewHolder<I> implements ItemTouchHelperViewHolder {
        private final ImageView handleView;
        private final OnStartDragListener mDragStartListener;
        TextView name;
        private Drawable originalBackground;

        public AbstractItemViewHolder(View view, OnStartDragListener mDragStartListener) {
            super(view);
            this.mDragStartListener = mDragStartListener;
            handleView = (ImageView) itemView.findViewById(R.id.handle);
            name = (TextView) view.findViewById(R.id.name);
        }

        @Override
        public void bindTo(I item, EquipmentFragment context, SubAdapter adapter) {
            name.setText(getNameString(item));
            // TODO force child classes to implement onClick
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            handleView.setOnTouchListener(new SwipeOrDragListener(mDragStartListener, this));
        }

        protected String getNameString(I item) {
            return item.getName();
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

    public static class DeleteRowViewHolder<I extends CharacterItem> extends BindableRecyclerViewHolder<I> {
        TextView name;
        Button undo;

        public DeleteRowViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            undo = (Button) itemView.findViewById(R.id.undo);
        }

        @Override
        public void bindTo(final I item, final EquipmentFragment context, final SubAdapter adapter) {
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

    public abstract static class SubAdapter<I extends CharacterItem> extends RecyclerView.Adapter<BindableRecyclerViewHolder<I>> implements ItemTouchHelperAdapter {
        protected Context context;
        protected EquipmentFragment fragment;
        protected List<I> list;
        OnStartDragListener mDragStartListener;

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
        public final BindableRecyclerViewHolder<I> onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.deleting_equipment_row, parent, false);
                return new DeleteRowViewHolder<>(newView);
            }
            return onSubCreateViewHolder(parent, viewType);
        }

        protected abstract BindableRecyclerViewHolder<I> onSubCreateViewHolder(ViewGroup parent, int viewType);


        protected Map<CharacterItem, Long> getItemPositionsBeingDeleted() {
            return fragment.beingDeleted;
        }

        @Override
        public void onBindViewHolder(BindableRecyclerViewHolder<I> holder, int position) {
            holder.bindTo(getItem(position), fragment, this);


        }

        @Override
        public void onItemDismiss(final int position) {
            final Map<CharacterItem, Long> beingDeleted = getItemPositionsBeingDeleted();
            final I item = getItem(position);
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
                    Long deletedTime = beingDeleted.get(item);
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

        protected void deleteRow(I item) {
            list.remove(item);
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
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
            return true;
        }

        public void setOnStartDrag(OnStartDragListener onStartDrag) {
            this.mDragStartListener = onStartDrag;
        }
    }

    public static class ArmorAdapter extends SubAdapter<CharacterArmor> {
        public ArmorAdapter(EquipmentFragment fragment, Character character) {
            super(fragment, character.getArmor());
        }

        @Override
        public ArmorViewHolder onSubCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.armor_row, parent, false);
            return new ArmorViewHolder(view, mDragStartListener);
        }

    }

    public static class WeaponsAdapter extends SubAdapter<CharacterWeapon> {
        public WeaponsAdapter(EquipmentFragment fragment, Character character) {
            super(fragment, character.getWeapons());
        }

        @Override
        public WeaponViewHolder onSubCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weapon_row, parent, false);
            return new WeaponViewHolder(view, mDragStartListener);
        }
    }

    public static class EquipmentAdapter extends SubAdapter<CharacterItem> {

        public EquipmentAdapter(EquipmentFragment fragment, Character character) {
            super(fragment, character.getItems());
        }

        @Override
        public ItemViewHolder onSubCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.equipment_row, parent, false);
            return new ItemViewHolder(view, mDragStartListener);
        }

        @Override
        public void onBindViewHolder(BindableRecyclerViewHolder<CharacterItem> holder, int position) {
            holder.bindTo(getItem(position), fragment, this);
        }

    }


}
