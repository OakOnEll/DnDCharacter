package com.oakonell.dndcharacter.views.character.rest;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.rest.RestRequest;

/**
 * Created by Rob on 12/19/2016.
 */

public abstract class RestHealingViewHelper<T extends RestRequest> {
    private final AbstractRestDialogFragment context;

    private View extraHealingGroup;
    private TextView extraHealingtextView;
    private TextView finalHp;
    private TextView startHp;
    private View finalHpGroup;
    private View noHealingGroup;

    private T request;
    private TextWatcher textWatcher;

    public T getRequest() {
        return request;
    }

    protected RestHealingViewHelper(AbstractRestDialogFragment context) {
        this.context = context;
    }

    protected AbstractRestDialogFragment getFragment() {
        return context;
    }

    protected boolean allowExtraHealing() {
        return request.getStartHP() != request.getMaxHP();
    }

    public void conditionallyShowExtraHealing() {
        extraHealingGroup.setVisibility(allowExtraHealing() ? View.VISIBLE : View.GONE);
    }


    public void onCharacterLoaded(T request) {
        this.request = request;
        conditionallyShowExtraHealing();

        if (!allowExtraHealing()) {
            noHealingGroup.setVisibility(View.VISIBLE);
            finalHpGroup.setVisibility(View.GONE);
        } else {
            noHealingGroup.setVisibility(View.GONE);
            finalHpGroup.setVisibility(View.VISIBLE);
        }

        startHp.setText(context.getString(R.string.fraction_d_slash_d, request.getStartHP(), request.getMaxHP()));
    }

    public void configureCommon(View view) {
        startHp = (TextView) view.findViewById(R.id.start_hp);
        finalHp = (TextView) view.findViewById(R.id.final_hp);
        finalHpGroup = view.findViewById(R.id.final_hp_group);
        extraHealingGroup = view.findViewById(R.id.extra_heal_group);
        extraHealingtextView = (TextView) view.findViewById(R.id.extra_healing);
        noHealingGroup = view.findViewById(R.id.no_healing_group);
    }


    public void updateView(final T request) {
        if (textWatcher != null) {
            extraHealingtextView.removeTextChangedListener(textWatcher);
            textWatcher = null;
        }

        int hp = request.getStartHP();
        int healing = request.getTotalHealing();
        hp = Math.min(hp + healing, request.getMaxHP());

        finalHp.setText(context.getString(R.string.fraction_d_slash_d, hp, request.getMaxHP()));
        extraHealingtextView.setText("" + request.getExtraHealing());

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO adjust the request's extra healing
                if (request == null) return;
                int hp = 0;
                try {
                    hp = Integer.parseInt(s.toString());
                } catch (NumberFormatException e) {
                    request.setExtraHealing(0);
                    // don't update the view
                    return;
                }
                request.setExtraHealing(hp);
                updateView(request);
            }
        };
        extraHealingtextView.addTextChangedListener(textWatcher);
    }


    public abstract int getCompanionRestLayoutId();

}
