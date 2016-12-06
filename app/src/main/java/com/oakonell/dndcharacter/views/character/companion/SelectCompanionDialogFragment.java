package com.oakonell.dndcharacter.views.character.companion;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.ApplyChangesToGenericComponent;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.character.SpeedType;
import com.oakonell.dndcharacter.model.character.companion.AbstractCompanionType;
import com.oakonell.dndcharacter.model.character.companion.CharacterCompanion;
import com.oakonell.dndcharacter.model.character.companion.CompanionRace;
import com.oakonell.dndcharacter.model.character.stats.BaseStatsType;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.model.companion.Companion;
import com.oakonell.dndcharacter.model.spell.SpellSchool;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.CursorIndexesByName;
import com.oakonell.dndcharacter.views.character.AbstractSelectComponentDialogFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;
import com.oakonell.dndcharacter.views.character.spell.SelectSpellDialogFragment;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 12/3/2016.
 */

public class SelectCompanionDialogFragment extends AbstractSelectComponentDialogFragment<AbstractSelectComponentDialogFragment.RowViewHolder> {
    private SelectCompanionDialogFragment.CompanionSelectedListener listener;


    private static class CompanionRowViewHolder extends RowViewHolder {

        @NonNull
        private final TextView crTextView;
        @NonNull
        private final TextView sizeTextView;

        public CompanionRowViewHolder(@NonNull View itemView) {
            super(itemView);
            crTextView = (TextView) itemView.findViewById(R.id.cr);
            sizeTextView = (TextView) itemView.findViewById(R.id.size);
        }


        @Override
        public void bindTo(final Cursor cursor, AbstractSelectComponentDialogFragment context, final CursorComponentListAdapter adapter, CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);

            final String cr = cursor.getString(cursorIndexesByName.getIndex(cursor, "cr"));
            crTextView.setText(cr);

            final String size = cursor.getString(cursorIndexesByName.getIndex(cursor, "creature_size"));
            sizeTextView.setText(size);
        }
    }

    public void setListener(SelectCompanionDialogFragment.CompanionSelectedListener listener) {
        this.listener = listener;
    }

    public interface CompanionSelectedListener {
        boolean companionSelected(long id);
    }

    @NonNull
    public static SelectCompanionDialogFragment createDialog(SelectCompanionDialogFragment.CompanionSelectedListener listener) {
        final SelectCompanionDialogFragment dialogFragment = new SelectCompanionDialogFragment();
        dialogFragment.setListener(listener);
        return dialogFragment;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.add_companion);
    }

    protected int getDialogResource() {
        return R.layout.companion_search_dialog;
    }


    protected int getListItemResource() {
        return R.layout.companion_search_item;
    }

    @NonNull
    @Override
    public RowViewHolder newRowViewHolder(@NonNull View newView) {
        return new CompanionRowViewHolder(newView);
    }

    @Nullable
    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateTheView(inflater, container, savedInstanceState);

        // TODO handle filter by companion type

        return view;
    }

    @NonNull
    @Override
    public Class<? extends Companion> getComponentClass() {
        return Companion.class;
    }

    @Override
    protected boolean applyAction(long id) {
        if (listener != null) {
            return listener.companionSelected(id);
        }
        throw new RuntimeException("No Listener!");
    }


    public static class AddCompanionToCharacterListener implements SelectCompanionDialogFragment.CompanionSelectedListener {

        private final CharacterActivity activity;

        public AddCompanionToCharacterListener(CharacterActivity activity) {
            this.activity = activity;
        }

        @Override
        public boolean companionSelected(long id) {
            final Companion companion = Companion.load(Companion.class, id);

            // prompt for name TODO make a more standard dialog
            final String type = "rangerCompanion";

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Name of new " + type + " " + companion.getName());

// Set up the input
            final EditText input = new EditText(activity);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = input.getText().toString();

                    addCompanion(companion, name, type);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();


            return true;
        }

        protected void addCompanion(Companion companion, String name, String type) {
            final Element root = XmlUtils.getDocument(companion.getXml()).getDocumentElement();
            Map<StatType, Integer> baseStats = new HashMap<>();
            Element stats = XmlUtils.getElement(root, "stats");


            final List<CharacterCompanion> companions = activity.getCharacter().getCompanions();

            // TODO copy data from model Companion to the character companion being added
            // TODO set the type from the search filter
            CharacterCompanion charCompanion = new CharacterCompanion();
            charCompanion.setName(name);
            charCompanion.setType(AbstractCompanionType.fromString(type));

            charCompanion.setStatsType(BaseStatsType.CUSTOM);

            baseStats.put(StatType.STRENGTH, Integer.parseInt(XmlUtils.getElementText(stats, "strength")));
            baseStats.put(StatType.DEXTERITY, Integer.parseInt(XmlUtils.getElementText(stats, "dexterity")));
            baseStats.put(StatType.CONSTITUTION, Integer.parseInt(XmlUtils.getElementText(stats, "constitution")));
            baseStats.put(StatType.INTELLIGENCE, Integer.parseInt(XmlUtils.getElementText(stats, "intelligence")));
            baseStats.put(StatType.WISDOM, Integer.parseInt(XmlUtils.getElementText(stats, "wisdom")));
            baseStats.put(StatType.CHARISMA, Integer.parseInt(XmlUtils.getElementText(stats, "charisma")));
            charCompanion.setBaseStats(baseStats);

            charCompanion.setBaseAC(Integer.parseInt(XmlUtils.getElementText(root, "ac")));
            charCompanion.setHP(charCompanion.getMaxHP());


            CompanionRace race = new CompanionRace();

            // TODO
            ApplyChangesToGenericComponent.applyToCharacter(activity, root, null, race, charCompanion, false);

            final List<Element> speeds = XmlUtils.getChildElements(root, "speed");
            SpeedType defaultType = SpeedType.WALK;
            for (Element each : speeds) {
                String speedFormula = each.getTextContent();
                String typeStr = each.getAttribute("type");
                SpeedType speedType = SpeedType.WALK;
                if (typeStr != null && typeStr.length() > 0) {
                    speedType = EnumHelper.stringToEnum(typeStr, SpeedType.class);
                }
                race.setSpeedFormula(speedType, speedFormula);
                String defaultStr = each.getAttribute("default");
                if ("true".equals(defaultStr)) {
                    defaultType = speedType;
                }
            }
            race.setName(XmlUtils.getElementText(root, "name"));

            charCompanion.setRace(race);
            companions.add(charCompanion);
            charCompanion.setSpeedType(defaultType);

            activity.updateViews();
            Runnable continuation = new Runnable() {
                @Override
                public void run() {
                    activity.updateViews();
                    activity.saveCharacter();
                }
            };
            continuation.run();
        }

    }

}
