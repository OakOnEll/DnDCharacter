package com.oakonell.dndcharacter.views.character.item;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.item.CharacterArmor;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.model.character.item.CharacterWeapon;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.ItemTouchHelperAdapter;
import com.oakonell.dndcharacter.views.ItemTouchHelperViewHolder;
import com.oakonell.dndcharacter.views.SimpleItemTouchHelperCallback;
import com.oakonell.dndcharacter.views.character.AbstractSheetFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 10/26/2015.
 */
public class EquipmentFragment extends AbstractSheetFragment {
    private static final int UNDO_DELAY = 5000;
    private static final String EQUIPMENT_FRAG = "equipment_frag";
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
    private TextView gem_summary;

    private EquipmentAdapter equipmentAdapter;
    private ArmorAdapter armorAdapter;
    private WeaponsAdapter weaponsAdapter;
    private View rootView;

    private RecyclerView armorView;
    private TextView armor_class;
    private View ac_title;
    private ViewGroup armorItemsView;

    private RecyclerView weaponsView;
    private ViewGroup weaponItemsView;
    private RecyclerView itemsView;

    private final Map<CharacterItem, Long> beingDeleted = new HashMap<>();


    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        gem_summary = (TextView) rootView.findViewById(R.id.gem_summary);

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
        gem_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoneyDialogFragment dialog = MoneyDialogFragment.createFragment(null);
                dialog.show(getFragmentManager(), "money_dialog");

            }
        });

        armorView = (RecyclerView) rootView.findViewById(R.id.armor_list);
        armorItemsView = (ViewGroup) rootView.findViewById(R.id.armor_items_group);
        armor_class = (TextView) rootView.findViewById(R.id.armor_class);
        ac_title = rootView.findViewById(R.id.ac_title);
        weaponsView = (RecyclerView) rootView.findViewById(R.id.weapons_list);
        weaponItemsView = (ViewGroup) rootView.findViewById(R.id.weapons_items_group);
        itemsView = (RecyclerView) rootView.findViewById(R.id.items_list);


        View addArmor = rootView.findViewById(R.id.addArmor);
        addArmor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArmor();
            }
        });


        View addWeapon = rootView.findViewById(R.id.addWeapon);
        addWeapon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWeapon();
            }
        });

        View addEquipment = rootView.findViewById(R.id.addItem);
        addEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEquipment();
            }
        });

        return rootView;
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        super.onCharacterLoaded(character);
// armor
        armorAdapter = new ArmorAdapter(this, character);
        armorView.setAdapter(armorAdapter);
        armorView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        armorView.setHasFixedSize(false);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        armorView.addItemDecoration(itemDecoration);

        armorItemsView.findViewById(R.id.equip).setVisibility(View.INVISIBLE);
        armorItemsView.findViewById(R.id.handle).setVisibility(View.INVISIBLE);
        armorItemsView.findViewById(R.id.delete).setVisibility(View.GONE);
//        ((TextView)armorItemsView.findViewById(R.id.ac)).setText("AC/Mod");
// weapons
        weaponsAdapter = new WeaponsAdapter(this, character);
        weaponsView.setAdapter(weaponsAdapter);
        weaponsView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        weaponsView.setHasFixedSize(false);
        weaponsView.addItemDecoration(itemDecoration);

        weaponItemsView.findViewById(R.id.handle).setVisibility(View.INVISIBLE);
        weaponItemsView.findViewById(R.id.delete).setVisibility(View.GONE);
// regular equipment
        equipmentAdapter = new EquipmentAdapter(this, character);
        itemsView.setAdapter(equipmentAdapter);
        itemsView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
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
        CharacterWeaponEditDialogFragment dialog = CharacterWeaponEditDialogFragment.createAddDialog();
        dialog.show(getFragmentManager(), EQUIPMENT_FRAG);
    }

    private void addArmor() {
        CharacterArmorEditDialogFragment dialog = CharacterArmorEditDialogFragment.createAddDialog();
        dialog.show(getFragmentManager(), EQUIPMENT_FRAG);
    }

    private void addEquipment() {
        CharacterItemEditDialogFragment fragment = CharacterItemEditDialogFragment.createAddDialog();
        fragment.show(getFragmentManager(), EQUIPMENT_FRAG);
    }

    @Override
    protected void updateViews(View rootView) {
        super.updateViews(rootView);
        Character character = getCharacter();
        if (goldPieces == null) return;

        // proficiency strings query the db for collapsing categories/items
        AsyncTask<Character, Void, Void> profGetter = new AsyncTask<Character, Void, Void>() {
            String armor;
            String weapon;
            String tools;

            @Nullable
            @Override
            protected Void doInBackground(Character... params) {
                Character character = params[0];
                armor = character.getArmorProficiencyString();
                weapon = character.getWeaponsProficiencyString();
                tools = character.getToolsProficiencyString();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                armor_proficiency.setText(armor);
                weapon_proficiency.setText(weapon);
                tools_proficiency.setText(tools);
            }
        };
        profGetter.execute(character);

        goldPieces.setText(NumberUtils.formatNumber(character.getGold()));
        copperPieces.setText(NumberUtils.formatNumber(character.getCopper()));
        silverPieces.setText(NumberUtils.formatNumber(character.getSilver()));
        electrumPieces.setText(NumberUtils.formatNumber(character.getElectrum()));
        platinumPieces.setText(NumberUtils.formatNumber(character.getPlatinum()));

        gem_summary.setText(getString(R.string.gem_summary, character.getGems().size(), character.getGemsValue().asFormattedString()));

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

        armor_class.setText(getString(R.string.armor_class_paren_value, character.getArmorClass()));

        ac_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArmorClassDialogFragment dialog = ArmorClassDialogFragment.createDialog();
                dialog.show(getFragmentManager(), "ac");
            }
        });

        equipmentAdapter.reloadList(character);
        armorAdapter.reloadList(character);
        weaponsAdapter.reloadList(character);
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
        @NonNull
        final TextView count;

        public ItemViewHolder(@NonNull View view, OnStartDragListener mDragStartListener) {
            super(view, mDragStartListener);
            count = (TextView) view.findViewById(R.id.count);
        }

        @Override
        public void bind(final EquipmentFragment context, SubAdapter<CharacterItem> adapter, @NonNull final CharacterItem item) {
            super.bind(context, adapter, item);
            if (item.getCount() != 1) {
                count.setText("(" + item.getCount() + ")");
            } else {
                count.setText("");
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharacterItemEditDialogFragment fragment = CharacterItemEditDialogFragment.createDialog(context.getCharacter(), item);
                    fragment.show(context.getFragmentManager(), "weapon_dialog");
                }
            });
        }

        @Override
        public void onItemSelected() {
            super.onItemSelected();
        }
    }

    static class ArmorViewHolder extends AbstractItemViewHolder<CharacterArmor> {
        @NonNull
        final TextView ac;
        @NonNull
        final CheckBox equipped;

        public ArmorViewHolder(@NonNull View view, OnStartDragListener mDragStartListener) {
            super(view, mDragStartListener);
            equipped = (CheckBox) view.findViewById(R.id.equip);
            ac = (TextView) view.findViewById(R.id.ac);
        }

        @NonNull
        protected String getNameString(@NonNull CharacterArmor item, @NonNull EquipmentFragment context) {
            boolean isProficient = context.getCharacter().isProficientWith(item);
            return item.getName() + (isProficient ? "" : context.getString(R.string.not_proficient));
        }

        @Override
        public void bind(@NonNull final EquipmentFragment context, final SubAdapter<CharacterArmor> adapter, @NonNull final CharacterArmor item) {
            super.bind(context, adapter, item);

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
                    item.setEquipped(context.getResources(), context.getCharacter(), isChecked);
                    // TODO update only views of any AC related fields
                    // if the delay is too low, the check animation is laggy
                    ac.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((CharacterActivity) context.getActivity()).updateViews();
                            ((CharacterActivity) context.getActivity()).saveCharacter();
                        }
                    }, 500);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharacterArmorEditDialogFragment fragment = CharacterArmorEditDialogFragment.createDialog(context.getCharacter(), item);
                    fragment.show(context.getFragmentManager(), "armor_dialog");
                }
            });

        }
    }

    static class WeaponViewHolder extends AbstractItemViewHolder<CharacterWeapon> {
        @NonNull
        final TextView bonus;
        @NonNull
        final TextView damage;

        @NonNull
        private final AmmunitionViewHelper ammunitionViewHelper;


        public WeaponViewHolder(@NonNull View view, OnStartDragListener mDragStartListener) {
            super(view, mDragStartListener);
            bonus = (TextView) view.findViewById(R.id.hit_bonus);
            damage = (TextView) view.findViewById(R.id.damage);

            ammunitionViewHelper = new AmmunitionViewHelper();
            ammunitionViewHelper.createView(view);
        }

        @Override
        protected String getNameString(@NonNull CharacterWeapon item, @NonNull EquipmentFragment context) {
            String base = super.getNameString(item, context);
            StringBuilder builder = new StringBuilder(" [");
            boolean hasExtra = false;
            if (item.isRanged()) {
                //noinspection ConstantConditions
                if (hasExtra) {
                    builder.append(", ");
                }
                builder.append("(").append(item.getRange()).append(")");
                hasExtra = true;
            }
            // TODO short for small screens?
            if (item.isVersatile()) {
                if (hasExtra) {
                    builder.append(", ");
                }
                builder.append(context.getString(R.string.versatile));
                hasExtra = true;
            }
            if (item.isFinesse()) {
                if (hasExtra) {
                    builder.append(", ");
                }
                builder.append(context.getString(R.string.finesse));
                hasExtra = true;
            }


            boolean isProficient = context.getCharacter().isProficientWith(item);
            if (!isProficient) {
                if (hasExtra) {
                    builder.append(", ");
                }
                builder.append(context.getString(R.string.not_proficient));
                hasExtra = true;
            }

            if (hasExtra) {
                builder.append("]");
                return base + builder.toString();
            }
            return base;
        }

        @Override
        public void bind(@NonNull final EquipmentFragment context, final SubAdapter<CharacterWeapon> adapter, @NonNull final CharacterWeapon item) {
            super.bind(context, adapter, item);

            final CharacterWeapon.AttackModifier attackModifiers = item.getBaseAttackModifier(context.getCharacter(), false);
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
            damage.setText(item.getDamageString(context.getResources()) + damageModString);
            bonus.setText(NumberUtils.formatNumber(attackModifiers.getAttackBonus()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeaponAttackDialogFragment fragment = WeaponAttackDialogFragment.create(item);
                    fragment.show(context.getFragmentManager(), "weapon_dialog");
                }
            });


            ammunitionViewHelper.bindView(context.getMainActivity(), item);
        }

    }

    public abstract static class BindableRecyclerViewHolder<I extends CharacterItem> extends BindableComponentViewHolder<I, EquipmentFragment, SubAdapter<I>> {

        public BindableRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

    static class SwipeOrDragListener implements View.OnTouchListener {
        static final int MIN_DISTANCE = 10;
        private final OnStartDragListener mDragStartListener;
        final AbstractItemViewHolder holder;
        boolean lookForDrag;
        private float downX;
        private float downY;

        SwipeOrDragListener(OnStartDragListener mDragStartListener, AbstractItemViewHolder holder) {
            this.holder = holder;
            this.mDragStartListener = mDragStartListener;
        }

        @Override
        public boolean onTouch(View v, @NonNull MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN && !lookForDrag) {
                Log.i("Equipment", "touch down");
                downX = event.getX();
                downY = event.getY();
                lookForDrag = true;
                //mSwipeDetected = Action.None;
                return true; // allow other events like Click to be processed
            } else if (action == MotionEvent.ACTION_MOVE && lookForDrag) {
                Log.i("Equipment", "action move ");
                float upX = event.getX();
                float upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // horizontal swipe detection
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    //  eliminate swipes
//                    mDragStartListener.onStartSwipe(holder);
//                    lookForDrag = false;
//                    return false;
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
        @NonNull
        private final ImageView handleView;
        private final OnStartDragListener mDragStartListener;
        @NonNull
        final TextView name;
        private final ImageView deleteView;
        private Drawable originalBackground;

        public AbstractItemViewHolder(@NonNull View view, OnStartDragListener mDragStartListener) {
            super(view);
            this.mDragStartListener = mDragStartListener;
            handleView = (ImageView) itemView.findViewById(R.id.handle);
            deleteView = (ImageView) itemView.findViewById(R.id.delete);
            name = (TextView) view.findViewById(R.id.name);
        }

        @Override
        public void bind(EquipmentFragment context, final SubAdapter<I> adapter, @NonNull I item) {
            name.setText(getNameString(item, context));
            // TODO force child classes to implement onClick
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.onItemDismiss(getAdapterPosition());
                }
            });

            handleView.setOnTouchListener(new SwipeOrDragListener(mDragStartListener, this));
        }

        protected String getNameString(@NonNull I item, EquipmentFragment context) {
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
        @NonNull
        final TextView name;
        @NonNull
        final Button undo;

        public DeleteRowViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            undo = (Button) itemView.findViewById(R.id.undo);
        }

        @Override
        public void bind(final EquipmentFragment context, @NonNull final SubAdapter<I> adapter, @NonNull final I item) {
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
        protected final Context context;
        @NonNull
        protected final EquipmentFragment fragment;
        protected List<I> list;
        @NonNull
        final ListRetriever<I> listRetriever;
        OnStartDragListener mDragStartListener;

        interface ListRetriever<I> {
            @NonNull
            List<I> getList(Character character);
        }

        public SubAdapter(@NonNull EquipmentFragment fragment, @NonNull ListRetriever<I> listRetriever) {
            this.context = fragment.getContext();
            this.fragment = fragment;
            this.listRetriever = listRetriever;
            this.list = listRetriever.getList(fragment.getCharacter());
        }

        public void reloadList(Character character) {
            this.list = listRetriever.getList(character);
            notifyDataSetChanged();
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

        @Nullable
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

        @NonNull
        @Override
        public final BindableRecyclerViewHolder<I> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.deleting_equipment_row, parent, false);
                return new DeleteRowViewHolder<>(newView);
            }
            return onSubCreateViewHolder(parent, viewType);
        }

        @NonNull
        protected abstract BindableRecyclerViewHolder<I> onSubCreateViewHolder(ViewGroup parent, int viewType);


        @NonNull
        protected Map<CharacterItem, Long> getItemPositionsBeingDeleted() {
            return fragment.beingDeleted;
        }

        @Override
        public void onBindViewHolder(@NonNull BindableRecyclerViewHolder<I> holder, int position) {
            holder.bind(fragment, this, getItem(position));
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
        public ArmorAdapter(@NonNull EquipmentFragment fragment, Character character) {
            super(fragment, new ListRetriever<CharacterArmor>() {
                @NonNull
                @Override
                public List<CharacterArmor> getList(@NonNull Character character) {
                    return character.getArmor();
                }
            });
        }

        @NonNull
        @Override
        public ArmorViewHolder onSubCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.armor_row, parent, false);
            return new ArmorViewHolder(view, mDragStartListener);
        }

    }

    public static class WeaponsAdapter extends SubAdapter<CharacterWeapon> {
        public WeaponsAdapter(@NonNull EquipmentFragment fragment, Character character) {
            super(fragment, new ListRetriever<CharacterWeapon>() {
                @NonNull
                @Override
                public List<CharacterWeapon> getList(@NonNull Character character) {
                    return character.getWeapons();
                }
            });
        }

        @NonNull
        @Override
        public WeaponViewHolder onSubCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weapon_row, parent, false);
            return new WeaponViewHolder(view, mDragStartListener);
        }
    }

    public static class EquipmentAdapter extends SubAdapter<CharacterItem> {

        public EquipmentAdapter(@NonNull EquipmentFragment fragment, Character character) {
            super(fragment, new ListRetriever<CharacterItem>() {
                @NonNull
                @Override
                public List<CharacterItem> getList(@NonNull Character character) {
                    return character.getItems();
                }
            });
        }

        @NonNull
        @Override
        public ItemViewHolder onSubCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.equipment_row, parent, false);
            return new ItemViewHolder(view, mDragStartListener);
        }

        @Override
        public void onBindViewHolder(@NonNull BindableRecyclerViewHolder<CharacterItem> holder, int position) {
            holder.bind(fragment, this, getItem(position));
        }

    }


}
