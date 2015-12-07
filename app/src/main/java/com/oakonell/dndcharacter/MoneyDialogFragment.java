package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.Character;

/**
 * Created by Rob on 12/2/2015.
 */
public class MoneyDialogFragment extends DialogFragment {
    private MainActivity mainActivity;
    private CoinType focusOn;

    public void setFocusOn(CoinType focusOn) {
        this.focusOn = focusOn;
    }

    public CoinType getFocusOn() {
        return focusOn;
    }

    enum CoinType {
        COPPER, SILVER, ELECTRUM, GOLD, PLATINUM;
    }

    public static MoneyDialogFragment createFragment(MainActivity activity, CoinType focus) {
        MoneyDialogFragment newMe = new MoneyDialogFragment();
        newMe.setMainActivity(activity);
        newMe.setFocusOn(focus);
        return newMe;
    }

    private void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.money_dialog, container);

        final EditText copperPieces = (EditText) rootView.findViewById(R.id.copper_pieces);
        final EditText silverPieces = (EditText) rootView.findViewById(R.id.silver_pieces);
        final EditText electrumPieces = (EditText) rootView.findViewById(R.id.electrum_pieces);
        final EditText goldPieces = (EditText) rootView.findViewById(R.id.gold_pieces);
        final EditText platinumPieces = (EditText) rootView.findViewById(R.id.platinum_pieces);

        TextView copperCurrent = (TextView) rootView.findViewById(R.id.copper_current);
        TextView silverCurrent = (TextView) rootView.findViewById(R.id.silver_current);
        TextView electrumCurrent = (TextView) rootView.findViewById(R.id.electrum_current);
        TextView goldCurrent = (TextView) rootView.findViewById(R.id.gold_current);
        TextView platinumCurrent = (TextView) rootView.findViewById(R.id.platinum_current);


        final TextView copperResult = (TextView) rootView.findViewById(R.id.copper_result);
        final TextView silverResult = (TextView) rootView.findViewById(R.id.silver_result);
        final TextView electrumResult = (TextView) rootView.findViewById(R.id.electrum_result);
        final TextView goldResult = (TextView) rootView.findViewById(R.id.gold_result);
        final TextView platinumResult = (TextView) rootView.findViewById(R.id.platinum_result);


        EditText focusedInput = null;
        switch (getFocusOn()) {
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

        final Character character = mainActivity.character;
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

        Button done = (Button) rootView.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                character.setCopper(getResultCoins(copperResult));
                character.setSilver(getResultCoins(silverResult));
                character.setElectrum(getResultCoins(electrumResult));
                character.setGold(getResultCoins(goldResult));
                character.setPlatinum(getResultCoins(platinumResult));
                dismiss();
                mainActivity.updateViews();
            }
        });

        return rootView;
    }

    private int getResultCoins(TextView result) {
        CharSequence value = result.getText();
        if (value.toString().trim().length() == 0) return 0;
        int intValue = Integer.parseInt(value.toString());
        return intValue;
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
