package com.oakonell.dndcharacter.views.character.item;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.SimpleItemTouchHelperCallback;
import com.oakonell.dndcharacter.views.character.AbstractSheetFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob on 12/5/2016.
 */

public class EquipmentFragmentHelper {
    private static final String EQUIPMENT_FRAG = "equipment_frag";

    private final AbstractSheetFragment fragment;
    private final boolean isForCompanion;
    private EquipmentFragment.EquipmentAdapter equipmentAdapter;
    private EquipmentFragment.ArmorAdapter armorAdapter;
    private EquipmentFragment.WeaponsAdapter weaponsAdapter;

    private RecyclerView armorView;
    private TextView armor_class;
    private View ac_title;
    private ViewGroup armorItemsView;

    private RecyclerView weaponsView;
    private ViewGroup weaponItemsView;
    private RecyclerView itemsView;

    public final Map<CharacterItem, Long> beingDeleted = new HashMap<>();

    public EquipmentFragmentHelper(AbstractSheetFragment fragment, boolean isForCompanion) {
        this.fragment = fragment;
        this.isForCompanion = isForCompanion;
    }

    public void onCreateTheView(View rootView) {
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
    }

    public void onCharacterLoaded(Character character) {
        // armor
        armorAdapter = new EquipmentFragment.ArmorAdapter(this, getDisplayedCharacter());
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
        weaponsAdapter = new EquipmentFragment.WeaponsAdapter(this, getDisplayedCharacter());
        weaponsView.setAdapter(weaponsAdapter);
        weaponsView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        weaponsView.setHasFixedSize(false);
        weaponsView.addItemDecoration(itemDecoration);

        weaponItemsView.findViewById(R.id.handle).setVisibility(View.INVISIBLE);
        weaponItemsView.findViewById(R.id.delete).setVisibility(View.GONE);
// regular equipment
        equipmentAdapter = new EquipmentFragment.EquipmentAdapter(this, getDisplayedCharacter());
        itemsView.setAdapter(equipmentAdapter);
        itemsView.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        itemsView.setHasFixedSize(false);
        itemsView.addItemDecoration(itemDecoration);


        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(equipmentAdapter, true, false);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(itemsView);
        equipmentAdapter.setOnStartDrag(new EquipmentFragment.OnStartDragListener() {
            @Override
            public void onStartDrag(EquipmentFragment.AbstractItemViewHolder viewHolder) {
                touchHelper.startDrag(viewHolder);
            }

            @Override
            public void onStartSwipe(EquipmentFragment.AbstractItemViewHolder holder) {
                touchHelper.startSwipe(holder);
            }
        });

        ItemTouchHelper.Callback armorCallback =
                new SimpleItemTouchHelperCallback(armorAdapter, true, false);
        final ItemTouchHelper armorTouchHelper = new ItemTouchHelper(armorCallback);
        armorTouchHelper.attachToRecyclerView(armorView);
        armorAdapter.setOnStartDrag(new EquipmentFragment.OnStartDragListener() {
            @Override
            public void onStartDrag(EquipmentFragment.AbstractItemViewHolder viewHolder) {
                armorTouchHelper.startDrag(viewHolder);
            }

            @Override
            public void onStartSwipe(EquipmentFragment.AbstractItemViewHolder holder) {
                armorTouchHelper.startSwipe(holder);
            }
        });

        ItemTouchHelper.Callback weaponsCallback =
                new SimpleItemTouchHelperCallback(weaponsAdapter, true, false);
        final ItemTouchHelper weaponsTouchHelper = new ItemTouchHelper(weaponsCallback);
        weaponsTouchHelper.attachToRecyclerView(weaponsView);
        weaponsAdapter.setOnStartDrag(new EquipmentFragment.OnStartDragListener() {
            @Override
            public void onStartDrag(EquipmentFragment.AbstractItemViewHolder viewHolder) {
                weaponsTouchHelper.startDrag(viewHolder);
            }

            @Override
            public void onStartSwipe(EquipmentFragment.AbstractItemViewHolder holder) {
                weaponsTouchHelper.startSwipe(holder);
            }
        });

    }

    private FragmentActivity getActivity() {
        return fragment.getActivity();
    }

    private FragmentManager getFragmentManager() {
        return fragment.getFragmentManager();
    }

    private void addWeapon() {
        CharacterWeaponEditDialogFragment dialog = CharacterWeaponEditDialogFragment.createAddDialog(isForCompanion);
        dialog.show(getFragmentManager(), EQUIPMENT_FRAG);
    }


    private void addArmor() {
        CharacterArmorEditDialogFragment dialog = CharacterArmorEditDialogFragment.createAddDialog(isForCompanion);
        dialog.show(getFragmentManager(), EQUIPMENT_FRAG);
    }

    private void addEquipment() {
        CharacterItemEditDialogFragment fragment = CharacterItemEditDialogFragment.createAddDialog(isForCompanion);
        fragment.show(getFragmentManager(), EQUIPMENT_FRAG);
    }

    public void updateViews(View rootView) {
        final AbstractCharacter character = getDisplayedCharacter();
        if (character == null) return;
        armor_class.setText(fragment.getString(R.string.armor_class_paren_value, character.getArmorClass()));

        ac_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArmorClassDialogFragment dialog = ArmorClassDialogFragment.createDialog(false);
                dialog.show(getFragmentManager(), "ac");
            }
        });

        if (equipmentAdapter == null) return;

        equipmentAdapter.reloadList(character);
        armorAdapter.reloadList(character);
        weaponsAdapter.reloadList(character);
    }

    public AbstractCharacter getDisplayedCharacter() {
        if (isForCompanion) {
            return fragment.getCharacter().getDisplayedCompanion();
        }
        return fragment.getCharacter();
    }

    public Context getContext() {
        return fragment.getActivity();
    }

    public AbstractSheetFragment getFragment() {
        return fragment;
    }
}
