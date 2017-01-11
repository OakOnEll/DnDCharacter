package com.oakonell.dndcharacter.views.character.rest.longRest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;

/**
 * Created by Rob on 12/19/2016.
 */

class HitDiceRestoreViewHolder extends BindableComponentViewHolder<HitDieRestoreRow, Context, HitDiceRestoreAdapter> {
    @NonNull
    final TextView dieSides;
    @NonNull
    final TextView currentDiceRemaining;
    @NonNull
    final EditText numDiceToRestore;
    @NonNull
    final TextView totalDice;
    @NonNull
    final TextView resultDice;
    TextWatcher numDiceToRestoreWatcher;

    public HitDiceRestoreViewHolder(@NonNull View view) {
        super(view);
        dieSides = (TextView) view.findViewById(R.id.die);
        currentDiceRemaining = (TextView) view.findViewById(R.id.current_dice_remaining);
        numDiceToRestore = (EditText) view.findViewById(R.id.dice_to_restore);
        totalDice = (TextView) view.findViewById(R.id.total);
        resultDice = (TextView) view.findViewById(R.id.resultant_dice);
    }

    @Override
    public void bind(@NonNull final Context context, final HitDiceRestoreAdapter adapter, @NonNull final HitDieRestoreRow row) {
        if (numDiceToRestoreWatcher != null) {
            numDiceToRestore.removeTextChangedListener(numDiceToRestoreWatcher);
        }

        dieSides.setText("d" + row.dieSides);
        currentDiceRemaining.setText(NumberUtils.formatNumber(row.currentDiceRemaining));
        numDiceToRestore.setText(NumberUtils.formatNumber(row.numDiceToRestore));
        totalDice.setText(NumberUtils.formatNumber(row.totalDice));
        updateResultDice(row);

        numDiceToRestore.setEnabled(row.currentDiceRemaining != row.totalDice);

        numDiceToRestoreWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                numDiceToRestore.setError(null);
            }

            @Override
            public void afterTextChanged(@NonNull Editable s) {
                String string = s.toString();
                if (string.length() == 0) {
                    row.numDiceToRestore = 0;
                    updateResultDice(row);
                    return;
                }
                try {
                    int value = Integer.parseInt(string);
                    if (value > row.totalDice - row.currentDiceRemaining) {
                        row.numDiceToRestore = 0;
                        numDiceToRestore.setError(context.getString(R.string.enter_number_less_than_equal_n, (row.totalDice - row.currentDiceRemaining)));
                        return;
                    }
                    row.numDiceToRestore = value;
                    updateResultDice(row);
                } catch (NumberFormatException e) {
                    row.numDiceToRestore = 0;
                    numDiceToRestore.setError(context.getString(R.string.enter_number_less_than_equal_n, (row.totalDice - row.currentDiceRemaining)));
                }
            }
        };
        numDiceToRestore.addTextChangedListener(numDiceToRestoreWatcher);
    }

    protected void updateResultDice(@NonNull HitDieRestoreRow row) {
        resultDice.setText(NumberUtils.formatNumber(Math.min(row.currentDiceRemaining + row.numDiceToRestore, row.totalDice)));
    }
}