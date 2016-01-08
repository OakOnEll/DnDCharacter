package com.oakonell.dndcharacter.views.character;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;

/**
 * Created by Rob on 1/7/2016.
 */
public class ExperienceDialogFragment extends AbstractCharacterDialogFragment {


    private TextView start_xp;
    private TextView end_xp;

    private EditText party_size;
    private EditText xpTextView;

    private ViewGroup your_xp_group;
    private TextView your_xp;

    public static ExperienceDialogFragment create() {
        return new ExperienceDialogFragment();
    }

    @Override
    protected String getTitle() {
        return "Experience";
    }

    @Override
    protected View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xp_dialog, container);

        start_xp = (TextView) view.findViewById(R.id.start_xp);
        end_xp = (TextView) view.findViewById(R.id.end_xp);

        xpTextView = (EditText) view.findViewById(R.id.xp);
        party_size = (EditText) view.findViewById(R.id.party_size);

        your_xp_group = (ViewGroup) view.findViewById(R.id.your_xp_group);
        your_xp = (TextView) view.findViewById(R.id.your_xp);

        return view;
    }

    @Override
    public void onCharacterLoaded(com.oakonell.dndcharacter.model.character.Character character) {
        super.onCharacterLoaded(character);
        updateViews();
    }

    @Override
    public void onCharacterChanged(Character character) {
        super.onCharacterChanged(character);
        updateViews();
    }

    protected void updateViews() {
        start_xp.setText(getCharacter().getXp() + "");

        int partySize = Math.abs(getPartySize());

        int xp = getInputXp();

        if (partySize != 1) {
            your_xp_group.setVisibility(View.VISIBLE);
            xp = xp / partySize;
            your_xp.setText(xp + "");
        } else {
            your_xp_group.setVisibility(View.GONE);
        }


        end_xp.setText((getCharacter().getXp() + xp) + "");


        party_size.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                party_size.setError(null);
                updateViews();
            }
        });

        xpTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateViews();
            }
        });
    }

    private int getInputXp() {
        String xpString = xpTextView.getText().toString();
        int xp = 0;
        if (xpString.trim().length() != 0) {
            try {
                xp = Integer.parseInt(xpString);
            } catch (NumberFormatException e) {
                // do nothing
            }
        }
        return xp;
    }

    private int getPartySize() {
        String partySizeString = party_size.getText().toString();
        int partySize = 1;
        if (partySizeString.length() > 0) {
            partySize = Integer.parseInt(partySizeString);
            if (partySize == 0) {
                party_size.setError("Enter a positive number");
                partySize = -1;
            }
        }
        return partySize;
    }

    @Override
    protected boolean onDone() {
        int partySize = getPartySize();
        if (partySize < 0) {
            return false;
        }

        int xp = getInputXp();

        if (partySize != 1) {
            your_xp_group.setVisibility(View.VISIBLE);
            xp = xp / partySize;
        }
        getCharacter().addExperience(xp);

        return super.onDone();
    }
}
