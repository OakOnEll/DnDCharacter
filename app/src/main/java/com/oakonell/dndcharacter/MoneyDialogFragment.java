package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.views.AbstractCharacterDialogFragment;

/**
 * Created by Rob on 12/2/2015.
 */
public class MoneyDialogFragment extends AbstractCharacterDialogFragment {
    private TextView copperResult;
    private TextView silverResult;
    private TextView electrumResult;
    private TextView goldResult;
    private TextView platinumResult;

    private TextView copperCurrent;
    private TextView silverCurrent;
    private TextView electrumCurrent;
    private TextView goldCurrent;
    private TextView platinumCurrent;

    private EditText copperPieces;
    private EditText silverPieces;
    private EditText electrumPieces;
    private EditText goldPieces;
    private EditText platinumPieces;

    public static MoneyDialogFragment createFragment(CoinType focus) {
        MoneyDialogFragment newMe = new MoneyDialogFragment();
        Bundle args = new Bundle();
        args.putInt("focus", focus.ordinal());
        newMe.setArguments(args);

        return newMe;
    }

    @Override
    protected String getTitle() {
        return "Money";
    }


    @Override
    public View onCreateTheView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.money_dialog, container);

        copperPieces = (EditText) rootView.findViewById(R.id.copper_pieces);
        silverPieces = (EditText) rootView.findViewById(R.id.silver_pieces);
        electrumPieces = (EditText) rootView.findViewById(R.id.electrum_pieces);
        goldPieces = (EditText) rootView.findViewById(R.id.gold_pieces);
        platinumPieces = (EditText) rootView.findViewById(R.id.platinum_pieces);

        copperCurrent = (TextView) rootView.findViewById(R.id.copper_current);
        silverCurrent = (TextView) rootView.findViewById(R.id.silver_current);
        electrumCurrent = (TextView) rootView.findViewById(R.id.electrum_current);
        goldCurrent = (TextView) rootView.findViewById(R.id.gold_current);
        platinumCurrent = (TextView) rootView.findViewById(R.id.platinum_current);


        copperResult = (TextView) rootView.findViewById(R.id.copper_result);
        silverResult = (TextView) rootView.findViewById(R.id.silver_result);
        electrumResult = (TextView) rootView.findViewById(R.id.electrum_result);
        goldResult = (TextView) rootView.findViewById(R.id.gold_result);
        platinumResult = (TextView) rootView.findViewById(R.id.platinum_result);

        EditText focusedInput = null;
        int ordinal = getArguments().getInt("focus", -1);
        if (ordinal >= 0) {
            // consume the focus
            getArguments().putInt("focus", -1);
            CoinType focus = CoinType.values()[ordinal];
            switch (focus) {
                case COPPER:
                    focusedInput = copperPieces;
                    break;
                case SILVER:
                    focusedInput = silverPieces;
                    break;
                case ELECTRUM:
                    focusedInput = electrumPieces;
                    break;
                case GOLD:
                    focusedInput = goldPieces;
                    break;
                case PLATINUM:
                    focusedInput = platinumPieces;
                    break;
            }
            focusedInput.requestFocus();
        }

        return rootView;
    }

    @Override
    public void onCharacterLoaded(Character character) {
        updateView(character);
    }

    private void updateView(Character character) {
        copperCurrent.setText(character.getCopper() + "");
        silverCurrent.setText(character.getSilver() + "");
        electrumCurrent.setText(character.getElectrum() + "");
        goldCurrent.setText(character.getGold() + "");
        platinumCurrent.setText(character.getPlatinum() + "");

        copperResult.setText(character.getCopper() + "");
        silverResult.setText(character.getSilver() + "");
        electrumResult.setText(character.getElectrum() + "");
        goldResult.setText(character.getGold() + "");
        platinumResult.setText(character.getPlatinum() + "");

        copperPieces.addTextChangedListener(new CoinInputWatcher(character.getCopper(), copperPieces, copperResult));
        silverPieces.addTextChangedListener(new CoinInputWatcher(character.getSilver(), silverPieces, silverResult));
        electrumPieces.addTextChangedListener(new CoinInputWatcher(character.getElectrum(), electrumPieces, electrumResult));
        goldPieces.addTextChangedListener(new CoinInputWatcher(character.getGold(), goldPieces, goldResult));
        platinumPieces.addTextChangedListener(new CoinInputWatcher(character.getPlatinum(), platinumPieces, platinumResult));
    }

    @Override
    public void onCharacterChanged(Character character) {

        super.onCharacterChanged(character);
        updateView(character);
    }


    @Override
    protected boolean onDone() {
        Character character = getCharacter();
        character.setCopper(getResultCoins(copperResult));
        character.setSilver(getResultCoins(silverResult));
        character.setElectrum(getResultCoins(electrumResult));
        character.setGold(getResultCoins(goldResult));
        character.setPlatinum(getResultCoins(platinumResult));

        return super.onDone();
    }

    private int getResultCoins(TextView result) {
        CharSequence value = result.getText();
        if (value.toString().trim().length() == 0) return 0;
        return Integer.parseInt(value.toString());
    }

    enum CoinType {
        COPPER, SILVER, ELECTRUM, GOLD, PLATINUM
    }

    static class CoinInputWatcher implements TextWatcher {
        private final TextView result;
        private final EditText input;
        private final int current;

        CoinInputWatcher(int current, EditText input, TextView result) {
            this.current = current;
            this.input = input;
            this.result = result;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int value = 0;
            final String str = s.toString().trim();
            result.setError(null);
            if (str.length() > 0 && !(str.equals("+") || str.equals("-"))) {
                try {
                    value = Integer.parseInt(s.toString());
                } catch (Exception e) {
                    result.setError("Invalid number");
                }
            }
            if (value > 0 && !str.startsWith("+")) {
                final String text = "+" + value;
                input.setText(text);
                input.setSelection(text.length());
            }
            if (value == 0 && !(str.equals("-") || str.equals(""))) {
                input.setText("");
            }
            result.setText(current + value + "");
        }
    }
}
