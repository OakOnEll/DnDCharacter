package com.oakonell.dndcharacter.views.character.item;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.item.CharacterItem;
import com.oakonell.dndcharacter.model.character.item.CharacterWeapon;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.character.MainActivity;

import java.util.List;

/**
 * Created by Rob on 1/12/2016.
 */
public class AmmunitionViewHelper {
    ViewGroup ammunition_layout;
    private TextView ammunition_count;
    private TextView ammunition_name;
    private Button use_ammunition;
    private Button add_ammunition;

    public void createView(@NonNull View view) {
        ammunition_layout = (ViewGroup) view.findViewById(R.id.ammunition_layout);
        ammunition_count = (TextView) view.findViewById(R.id.ammunition_count);
        ammunition_name = (TextView) view.findViewById(R.id.ammunition_name);
        use_ammunition = (Button) view.findViewById(R.id.use_ammunition);
        add_ammunition = (Button) view.findViewById(R.id.add_ammunition);
    }

    public void bindView(@NonNull final MainActivity context, @NonNull CharacterWeapon item) {
        final String ammunitionName = item.getAmmunition();
        if (ammunitionName != null) {
            ammunition_layout.setVisibility(View.VISIBLE);
            ammunition_name.setText(ammunitionName);
// TODO link to the character's ammunition items, to count and adjust here
            final List<CharacterItem> ammo = context.getCharacter().getItemsNamed(ammunitionName);
            int amount = 0;
            for (CharacterItem each : ammo) {
                amount += each.getCount();
            }
            ammunition_count.setText(NumberUtils.formatNumber(amount));
            add_ammunition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final List<CharacterItem> ammo = context.getCharacter().getItemsNamed(ammunitionName);
                    if (ammo.isEmpty()) {
                        // TODO use a fancy visitor, create one from the model if exists
                        CharacterItem item = new CharacterItem();
                        item.setName(ammunitionName);
                        context.getCharacter().addItem(item);
                    } else {
                        final CharacterItem firstItem = ammo.get(0);
                        firstItem.setCount(firstItem.getCount() + 1);
                        ammunition_count.setText(NumberUtils.formatNumber(firstItem.getCount()));
                    }
                    ammunition_count.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.updateViews();
                            context.saveCharacter();
                        }
                    }, 500);
                }
            });
            use_ammunition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final List<CharacterItem> ammo = context.getCharacter().getItemsNamed(ammunitionName);
                    if (ammo.isEmpty()) {
                        // do nothing..
                    } else {
                        final CharacterItem firstItem = ammo.get(0);
                        firstItem.setCount(Math.max(firstItem.getCount() - 1, 0));
                        ammunition_count.setText(NumberUtils.formatNumber(firstItem.getCount()));
                        if (firstItem.getCount() <= 0) {
                            context.getCharacter().getItems().remove(firstItem);
                        }
                    }
                    ammunition_count.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.updateViews();
                            context.saveCharacter();
                        }
                    }, 500);
                }
            });
        } else {
            ammunition_layout.setVisibility(View.GONE);
        }
    }
}
