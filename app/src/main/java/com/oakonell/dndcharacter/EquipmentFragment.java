package com.oakonell.dndcharacter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;
import com.oakonell.dndcharacter.model.*;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.components.ProficiencyType;
import com.oakonell.dndcharacter.views.AbstractSheetFragment;

import java.util.Collections;
import java.util.List;

/**
 * Created by Rob on 10/26/2015.
 */
public class EquipmentFragment extends AbstractSheetFragment {
    MergeAdapter mergeAdapter;
    TextView armor_proficiency;
    TextView weapon_proficiency;
    TextView tools_proficiency;
    private ViewGroup armor_group;
    private ViewGroup weapon_group;
    private ViewGroup tools_group;
    private ListView equipmentListView;
    private TextView goldPieces;
    private TextView copperPieces;
    private TextView silverPieces;
    private TextView electrumPieces;
    private TextView platinumPieces;
    private EquipmentAdapter equipmentAdapter;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.equipment_sheet, container, false);

        mergeAdapter = new MergeAdapter();

        View commonView = inflater.inflate(R.layout.common_character_section, container, false);
        superCreateViews(commonView);
        mergeAdapter.addView(commonView);

        View preView = inflater.inflate(R.layout.equipment_proficiency_and_money_layout, container, false);
        armor_proficiency = (TextView) preView.findViewById(R.id.armor_proficiency);
        weapon_proficiency = (TextView) preView.findViewById(R.id.weapon_proficiency);
        tools_proficiency = (TextView) preView.findViewById(R.id.tool_proficiency);

        armor_group = (ViewGroup) preView.findViewById(R.id.armor_group);
        weapon_group = (ViewGroup) preView.findViewById(R.id.weapon_group);
        tools_group = (ViewGroup) preView.findViewById(R.id.tools_group);

        copperPieces = (TextView) preView.findViewById(R.id.copper_pieces);
        silverPieces = (TextView) preView.findViewById(R.id.silver_pieces);
        electrumPieces = (TextView) preView.findViewById(R.id.electrum_pieces);
        goldPieces = (TextView) preView.findViewById(R.id.gold_pieces);
        platinumPieces = (TextView) preView.findViewById(R.id.platinum_pieces);

        ViewGroup copperGroup = (ViewGroup) preView.findViewById(R.id.copper_group);
        ViewGroup silverGroup = (ViewGroup) preView.findViewById(R.id.silver_group);
        ViewGroup electrumGroup = (ViewGroup) preView.findViewById(R.id.electrum_group);
        ViewGroup goldGroup = (ViewGroup) preView.findViewById(R.id.gold_group);
        ViewGroup platinumGroup = (ViewGroup) preView.findViewById(R.id.platinum_group);


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

        mergeAdapter.addView(preView);


        equipmentListView = (ListView) rootView.findViewById(R.id.merge_list);


// armor
        View armorTitle = inflater.inflate(R.layout.equipment_title, container, false);
        TextView armorLabel = (TextView) armorTitle.findViewById(R.id.title);
        armorLabel.setText("Armor");
        mergeAdapter.addView(armorTitle);

        final List<CharacterItem> armor = Collections.emptyList();
        ListAdapter armorAdapter = new ArrayAdapter<CharacterItem>(getActivity(), android.R.layout.simple_list_item_1, armor);
        mergeAdapter.addAdapter(armorAdapter);


// weapons
        View weaponsTitle = inflater.inflate(R.layout.equipment_title, container, false);
        TextView weaponsLabel = (TextView) weaponsTitle.findViewById(R.id.title);
        weaponsLabel.setText("Weapons");
        mergeAdapter.addView(weaponsTitle);

        final List<CharacterItem> weapons = Collections.emptyList();
        ListAdapter weaponsAdapter = new ArrayAdapter<CharacterItem>(getActivity(), android.R.layout.simple_list_item_1, weapons);
        mergeAdapter.addAdapter(weaponsAdapter);

// regular equipment
        View equpimentTitle = inflater.inflate(R.layout.equipment_title, container, false);
        TextView equipmentLabel = (TextView) equpimentTitle.findViewById(R.id.title);
        Button addEquipment = (Button) equpimentTitle.findViewById(R.id.add);
        equipmentLabel.setText("Equipment");
        mergeAdapter.addView(equpimentTitle);


        addEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEquipment();
            }
        });

        final List<CharacterItem> items = character.getItems();
        equipmentAdapter = new EquipmentAdapter(this, character);
        mergeAdapter.addAdapter(equipmentAdapter);

        equipmentListView.setAdapter(mergeAdapter);

        updateViews(rootView);

        // need to hook a notes text watcher, to update the model
        return rootView;
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

    public static class EquipmentAdapter extends BaseAdapter {
        private Context context;
        private EquipmentFragment fragment;
        Character character;

        static class ViewHolder {
            TextView name;
            ImageView delete;
        }

        public EquipmentAdapter(EquipmentFragment fragment, Character character) {
            this.context = fragment.getContext();
            this.fragment = fragment;
            this.character = character;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            ViewHolder holder;
            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.equipment_row, null);
                holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.delete = (ImageView) view.findViewById(R.id.action_delete);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            final CharacterItem item = getItem(position);

            holder.name.setText(item.getName());
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.promptToDeleteItem(item);
                }
            });


            return view;
        }

        @Override
        public int getCount() {
            if (character == null) return 0;
            return character.getItems().size();
        }

        @Override
        public CharacterItem getItem(int position) {
            if (character == null) return null;
            return character.getItems().get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void setCharacter(Character character) {
            if (this.character != character) {
                this.character = character;
                notifyDataSetInvalidated();
            }
        }
    }

    private void promptToDeleteItem(final CharacterItem item) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete item " + item.getName())
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        character.getItems().remove(item);
                        equipmentAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
