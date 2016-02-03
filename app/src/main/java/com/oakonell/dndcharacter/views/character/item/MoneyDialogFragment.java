package com.oakonell.dndcharacter.views.character.item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

/**
 * Created by Rob on 12/2/2015.
 */
public class MoneyDialogFragment extends AbstractCharacterDialogFragment {
    public static final String FOCUS = "focus";
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

    @NonNull
    public static MoneyDialogFragment createFragment(@NonNull CoinType focus) {
        MoneyDialogFragment newMe = new MoneyDialogFragment();
        Bundle args = new Bundle();
        args.putInt(FOCUS, focus.ordinal());
        newMe.setArguments(args);

        return newMe;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.money_title);
    }


    @Override
    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        int ordinal = getArguments().getInt(FOCUS, -1);
        if (ordinal >= 0) {
            // consume the focus
            getArguments().putInt(FOCUS, -1);
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
    public void onCharacterLoaded(@NonNull Character character) {
        updateView(character);
    }

    private void updateView(@NonNull Character character) {
        copperCurrent.setText(NumberUtils.formatNumber(character.getCopper()));
        silverCurrent.setText(NumberUtils.formatNumber(character.getSilver()));
        electrumCurrent.setText(NumberUtils.formatNumber(character.getElectrum()));
        goldCurrent.setText(NumberUtils.formatNumber(character.getGold()));
        platinumCurrent.setText(NumberUtils.formatNumber(character.getPlatinum()));

        copperResult.setText(NumberUtils.formatNumber(character.getCopper()));
        silverResult.setText(NumberUtils.formatNumber(character.getSilver()));
        electrumResult.setText(NumberUtils.formatNumber(character.getElectrum()));
        goldResult.setText(NumberUtils.formatNumber(character.getGold()));
        platinumResult.setText(NumberUtils.formatNumber(character.getPlatinum()));

        copperPieces.addTextChangedListener(new CoinInputWatcher(character.getCopper(), copperPieces, copperResult));
        silverPieces.addTextChangedListener(new CoinInputWatcher(character.getSilver(), silverPieces, silverResult));
        electrumPieces.addTextChangedListener(new CoinInputWatcher(character.getElectrum(), electrumPieces, electrumResult));
        goldPieces.addTextChangedListener(new CoinInputWatcher(character.getGold(), goldPieces, goldResult));
        platinumPieces.addTextChangedListener(new CoinInputWatcher(character.getPlatinum(), platinumPieces, platinumResult));
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
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

    private int getResultCoins(@NonNull TextView result) {
        CharSequence value = result.getText();
        if (value.toString().trim().length() == 0) return 0;
        return Integer.parseInt(value.toString());
    }

    enum CoinType {
        COPPER, SILVER, ELECTRUM, GOLD, PLATINUM;

        @Override
        public String toString() {
            return super.toString();
        }
        }

    class CoinInputWatcher implements TextWatcher {
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
        public void afterTextChanged(@NonNull Editable s) {
            int value = 0;
            final String str = s.toString().trim();
            result.setError(null);
            if (str.length() > 0 && !(str.equals("+") || str.equals("-"))) {
                try {
                    value = Integer.parseInt(s.toString());
                } catch (Exception e) {
                    result.setError(getString(R.string.invalid_number));
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
            result.setText(NumberUtils.formatNumber(current + value));
        }
    }
}
