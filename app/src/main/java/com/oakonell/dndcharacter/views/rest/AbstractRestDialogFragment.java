package com.oakonell.dndcharacter.views.rest;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractRestRequest;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.FeatureInfo;
import com.oakonell.dndcharacter.model.FeatureResetInfo;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.views.AbstractCharacterDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 11/8/2015.
 */
public abstract class AbstractRestDialogFragment extends AbstractCharacterDialogFragment {

    private View extraHealingGroup;

    private TextView extraHealingtextView;
    private TextView finalHp;
    private TextView startHp;
    private View finalHpGroup;
    private View featureResetsGroup;
    private View noHealingGroup;

    private FeatureResetsAdapter featureResetAdapter;
    private List<FeatureResetInfo> featureResets;
    private ListView featureListView;

    protected boolean allowExtraHealing() {
        return getCharacter().getHP() != getCharacter().getMaxHP();
    }

    protected void conditionallyShowExtraHealing() {
        extraHealingGroup.setVisibility(allowExtraHealing() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);

        conditionallyShowExtraHealing();

        if (character.getHP() == character.getMaxHP()) {
            noHealingGroup.setVisibility(View.VISIBLE);

            finalHpGroup.setVisibility(View.GONE);
        } else {
            noHealingGroup.setVisibility(View.GONE);

            finalHpGroup.setVisibility(View.VISIBLE);
        }

        startHp.setText(character.getHP() + " / " + character.getMaxHP());

        List<FeatureInfo> features = character.getFeatureInfos();
        featureResets = new ArrayList<>();
        for (FeatureInfo each : features) {
            RefreshType refreshOn = each.getFeature().getRefreshesOn();
            if (refreshOn == null) continue;

            FeatureResetInfo resetInfo = new FeatureResetInfo();
            resetInfo.name = each.getName();
            resetInfo.description = each.getShortDescription();
            resetInfo.reset = shouldReset(refreshOn);
            resetInfo.refreshOn = refreshOn;
            int maxUses = each.evaluateMaxUses(character);
            int usesRemaining = character.getUsesRemaining(each);
            if (resetInfo.reset) {
                resetInfo.numToRestore = maxUses - usesRemaining;
            }
            resetInfo.maxToRestore = maxUses - usesRemaining;
            resetInfo.uses = usesRemaining + " / " + maxUses;
            resetInfo.needsResfesh = usesRemaining != maxUses;
            featureResets.add(resetInfo);
        }
        featureResetAdapter = new FeatureResetsAdapter(getActivity(), featureResets);
        featureListView.setAdapter(featureResetAdapter);
    }

    protected void configureCommon(View view) {
        featureResetsGroup = view.findViewById(R.id.feature_resets);
        startHp = (TextView) view.findViewById(R.id.start_hp);
        finalHp = (TextView) view.findViewById(R.id.final_hp);
        finalHpGroup = (View) view.findViewById(R.id.final_hp_group);
        extraHealingGroup = (View) view.findViewById(R.id.extra_heal_group);
        extraHealingtextView = (TextView) view.findViewById(R.id.extra_healing);
        noHealingGroup = (View) view.findViewById(R.id.no_healing_group);

        featureListView = (ListView) view.findViewById(R.id.feature_list);


        extraHealingtextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateView();
            }
        });


    }

    protected abstract boolean shouldReset(RefreshType refreshesOn);

    protected int getExtraHealing() {
        String extraHealString = extraHealingtextView.getText().toString();
        if (extraHealString != null && extraHealString.trim().length() > 0) {
            return Integer.parseInt(extraHealString);
        }
        return 0;
    }

    public void updateView() {
        Character character = getCharacter();
        int hp = character.getHP();
        int healing = getHealing();
        hp = Math.min(hp + healing, character.getMaxHP());
        finalHp.setText(hp + " / " + character.getMaxHP());
    }

    protected abstract int getHealing();

    protected void updateCommonRequest(AbstractRestRequest request) {
        for (FeatureResetInfo each : featureResets) {
            if (each.reset) {
                request.addFeatureReset(each.name, each.numToRestore);
            }
        }
    }


    static class FeatureResetsAdapter extends BaseAdapter {
        private final List<FeatureResetInfo> resets;
        private Context context;

        public FeatureResetsAdapter(Context context, List<FeatureResetInfo> resets) {
            this.context = context;
            this.resets = resets;
        }

        @Override
        public int getCount() {
            return resets.size();
        }

        @Override
        public FeatureResetInfo getItem(int position) {
            return resets.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            final ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(context, R.layout.feature_reset_item, null);
                viewHolder = new ViewHolder();

                viewHolder.name = (CheckBox) view.findViewById(R.id.feature_name);
                viewHolder.description = (TextView) view.findViewById(R.id.description);
                viewHolder.uses = (TextView) view.findViewById(R.id.uses);
                viewHolder.numToRestore = (EditText) view.findViewById(R.id.num_to_restore);
                view.setTag(viewHolder);

            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
                viewHolder.numToRestore.removeTextChangedListener(viewHolder.watcher);
                viewHolder.name.setOnCheckedChangeListener(null);
            }
            final FeatureResetInfo row = getItem(position);

            viewHolder.name.setText(row.name);
            viewHolder.name.setChecked(row.reset);

            viewHolder.description.setText(row.description);

            viewHolder.uses.setText(row.uses);

            viewHolder.numToRestore.setText(row.numToRestore + "");
            viewHolder.numToRestore.setEnabled(row.reset);

            if (!row.needsResfesh) {
                viewHolder.name.setChecked(false);
                viewHolder.name.setEnabled(false);
                viewHolder.numToRestore.setEnabled(false);
            } else {
                viewHolder.name.setEnabled(true);
            }

            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    viewHolder.numToRestore.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // TODO error handling, if val is too large
                    if (s == null) return;
                    String stringVal = s.toString().trim();
                    if (stringVal.length() == 0) return;
                    int value;
                    try {
                        value = Integer.parseInt(stringVal);
                    } catch (Exception e) {
                        viewHolder.numToRestore.setError("Enter a value <= " + row.maxToRestore);
                        return;
                    }
                    if (value > row.maxToRestore) {
                        viewHolder.numToRestore.setError("Enter a value <= " + row.maxToRestore);
                    }

                    row.numToRestore = value;
                }
            };
            viewHolder.numToRestore.addTextChangedListener(watcher);
            viewHolder.watcher = watcher;


            viewHolder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    row.reset = isChecked;
                    viewHolder.numToRestore.setEnabled(row.reset);
                }
            });

            return view;

        }

        class ViewHolder {
            public TextWatcher watcher;
            CheckBox name;
            TextView description;
            TextView uses;
            EditText numToRestore;
        }
    }

}
