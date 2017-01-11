package com.oakonell.dndcharacter.views.character.rest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.FeatureResetInfo;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;

/**
 * Created by Rob on 1/10/2017.
 */
class FeatureResetViewHolder extends BindableComponentViewHolder<FeatureResetInfo, Context, FeatureResetsAdapter> {
    @Nullable
    public TextWatcher watcher;
    @NonNull
    final CheckBox name;
    @NonNull
    final TextView description;
    @NonNull
    final TextView uses;
    @NonNull
    final EditText numToRestore;

    public FeatureResetViewHolder(@NonNull View view) {
        super(view);
        name = (CheckBox) view.findViewById(R.id.feature_name);
        description = (TextView) view.findViewById(R.id.description);
        uses = (TextView) view.findViewById(R.id.uses);
        numToRestore = (EditText) view.findViewById(R.id.num_to_restore);
    }

    @Override
    public void bind(@NonNull final Context context, final FeatureResetsAdapter adapter, @NonNull final FeatureResetInfo row) {
        if (watcher != null) {
            numToRestore.removeTextChangedListener(watcher);
        }
        name.setOnCheckedChangeListener(null);

        name.setText(row.name);
        name.setChecked(row.reset);

        description.setText(row.description);

        uses.setText(row.uses);

        numToRestore.setText(NumberUtils.formatNumber(row.numToRestore));
        numToRestore.setEnabled(row.reset);

        if (!row.needsResfesh) {
            name.setChecked(false);
            name.setEnabled(false);
            numToRestore.setEnabled(false);
        } else {
            name.setEnabled(true);
        }

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                numToRestore.setError(null);
            }

            @Override
            public void afterTextChanged(@Nullable Editable s) {
                // TODO error handling, if val is too large
                if (s == null) return;
                if (!row.reset) return;

                String stringVal = s.toString().trim();
                if (stringVal.length() == 0) return;
                int value;
                try {
                    value = Integer.parseInt(stringVal);
                } catch (Exception e) {
                    numToRestore.setError(context.getString(R.string.enter_number_less_than_equal_n, row.maxToRestore));
                    return;
                }
                if (value > row.maxToRestore) {
                    numToRestore.setError(context.getString(R.string.enter_number_less_than_equal_n, row.maxToRestore));
                }

                row.numToRestore = value;
            }
        };
        numToRestore.addTextChangedListener(watcher);
        this.watcher = watcher;

        name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                row.reset = isChecked;
                numToRestore.setEnabled(row.reset);
                if (row.reset) {
                    // force a validation
                    numToRestore.setText(numToRestore.getText());
                } else {
                    numToRestore.setError(null);
                }
            }
        });
    }
}
