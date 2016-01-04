package com.oakonell.dndcharacter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.FeatureInfo;
import com.oakonell.dndcharacter.model.components.UseType;
import com.oakonell.dndcharacter.views.ComponentLaunchHelper;

/**
 * Created by Rob on 1/4/2016.
 */
public class FeatureViewHolder extends BindableComponentViewHolder<FeatureInfo, MainActivity> {
    public TextView name;
    public TextView source;
    public TextView shortDescription;

    public ViewGroup limited_uses_group;
    public ViewGroup use_group;
    public TextView uses_label;
    public TextView uses_remaining;
    public Button useButton;

    public ViewGroup pool_apply_group;
    public TextView pool_value;
    public TextView remaining_uses;
    public ImageButton pool_apply_button;
    public ImageButton pool_cancel_button;

    public TextView refreshes_label;

    public FeatureViewHolder(View view) {
        super(view);
        name = (TextView) view.findViewById(R.id.name);
        source = (TextView) view.findViewById(R.id.source);

        limited_uses_group = (ViewGroup) view.findViewById(R.id.limited_uses_group);
        uses_label = (TextView) view.findViewById(R.id.uses_label);
        useButton = (Button) view.findViewById(R.id.use_button);
        uses_remaining = (TextView) view.findViewById(R.id.remaining);
        shortDescription = (TextView) view.findViewById(R.id.short_description);
        refreshes_label = (TextView) view.findViewById(R.id.refreshes_label);
        remaining_uses = (TextView) view.findViewById(R.id.remaining_uses);

        pool_apply_group = (ViewGroup) view.findViewById(R.id.pool_apply_group);
        use_group = (ViewGroup) view.findViewById(R.id.use_group);
        pool_value = (TextView) view.findViewById(R.id.pool_value);
        pool_apply_button = (ImageButton) view.findViewById(R.id.pool_apply_button);
        pool_cancel_button = (ImageButton) view.findViewById(R.id.pool_cancel_button);
    }


    @Override
    public void bind(final MainActivity context, final RecyclerView.Adapter<?> adapter, final FeatureInfo info) {
        final int position = getAdapterPosition();

        name.setText(info.getName());
        source.setText(info.getSourceString());
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentLaunchHelper.editComponent(context, info.getSource(), false);
            }
        });
        boolean hasLimitedUses = info.hasLimitedUses();
        pool_apply_group.setVisibility(View.GONE);
        if (!hasLimitedUses) {
            limited_uses_group.setVisibility(View.GONE);
            use_group.setVisibility(View.GONE);
        } else {
            limited_uses_group.setVisibility(View.VISIBLE);
            int maxUses = info.evaluateMaxUses(context.getCharacter());
            final int usesRemaining = context.getCharacter().getUsesRemaining(info);

            uses_remaining.setText(usesRemaining + "/" + maxUses);

            useButton.setEnabled(usesRemaining > 0);
            if (info.getUseType() == UseType.PER_USE) {
                use_group.setVisibility(View.VISIBLE);
                uses_label.setText("Uses");
                useButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.getCharacter().useFeature(info.getFeature(), 1);
                        adapter.notifyItemChanged(position);
                    }
                });

            } else if (info.getUseType() == UseType.POOL) {
                use_group.setVisibility(View.VISIBLE);
                uses_label.setText("Pool");

                remaining_uses.setText(" / " + usesRemaining);
                useButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pool_apply_group.setVisibility(View.VISIBLE);
                        pool_value.requestFocus();

                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(pool_value, InputMethodManager.SHOW_IMPLICIT);

                        refreshes_label.setVisibility(View.GONE);
                        use_group.setVisibility(View.GONE);
                    }
                });

                pool_cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pool_apply_group.setVisibility(View.GONE);
                        pool_value.setText("");
                        refreshes_label.setVisibility(View.VISIBLE);
                        use_group.setVisibility(View.VISIBLE);

                    }
                });
                pool_apply_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String string = pool_value.getText().toString().trim();
                        if (string.length() == 0) {
                            pool_value.setError("Enter a number <= " + usesRemaining);
                            return;
                        }
                        int value;
                        try {
                            value = Integer.parseInt(string);
                        } catch (NumberFormatException e) {
                            pool_value.setError("Enter a number <= " + usesRemaining);
                            return;
                        }
                        if (value > usesRemaining) {
                            pool_value.setError("Enter a number <= " + usesRemaining);
                            return;
                        }
                        context.getCharacter().useFeature(info.getFeature(), value);
                        context.saveCharacter();
                        pool_apply_group.setVisibility(View.GONE);
                        pool_value.setText("");
                        refreshes_label.setVisibility(View.VISIBLE);
                        use_group.setVisibility(View.VISIBLE);
                        adapter.notifyItemChanged(position);
                    }
                });

                pool_value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            pool_apply_button.performClick();
                            return true;
                        }
                        return false;
                    }
                });

                pool_value.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        pool_value.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s == null) return;
                        String string = s.toString();
                        if (string.length() == 0) return;
                        int value;
                        try {
                            value = Integer.parseInt(string);
                        } catch (NumberFormatException e) {
                            pool_value.setError("Enter a number <= " + usesRemaining);
                            return;
                        }
                        if (value > usesRemaining) {
                            pool_value.setError("Enter a number <= " + usesRemaining);
                        }
                    }
                });
            }

            refreshes_label.setText("Refreshes on " + info.getFeature().getRefreshesOn().toString());

        }
        shortDescription.setText(info.getShortDescription());
    }
}


