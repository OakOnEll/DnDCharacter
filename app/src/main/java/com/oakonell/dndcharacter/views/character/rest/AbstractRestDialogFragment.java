package com.oakonell.dndcharacter.views.character.rest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.rest.AbstractRestRequest;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.FeatureInfo;
import com.oakonell.dndcharacter.model.character.FeatureResetInfo;
import com.oakonell.dndcharacter.model.components.RefreshType;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.DividerItemDecoration;

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
    private View noHealingGroup;

    private FeatureResetsAdapter featureResetAdapter;
    private android.support.v7.widget.RecyclerView featureListView;
    private Bundle savedFeatureResets;

    protected boolean allowExtraHealing() {
        return getCharacter().getHP() != getCharacter().getMaxHP();
    }

    protected void conditionallyShowExtraHealing() {
        extraHealingGroup.setVisibility(allowExtraHealing() ? View.VISIBLE : View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedFeatureResets = savedInstanceState.getBundle("featureResets");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle resets = new Bundle();
        for (FeatureResetInfo each : featureResetAdapter.resets) {
            Bundle reset = new Bundle();
            reset.putByte("reset", (byte) (each.reset ? 1 : 0));
            reset.putInt("numToRestore", each.numToRestore);
            resets.putBundle(each.name, reset);
        }
        outState.putBundle("featureResets", resets);
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

        startHp.setText(getString(R.string.fraction_d_slash_d, character.getHP(),character.getMaxHP()));

        List<FeatureInfo> features = character.getFeatureInfos();
        List<FeatureResetInfo> featureResets = new ArrayList<>();
        for (FeatureInfo each : features) {
            RefreshType refreshOn = each.getFeature().getRefreshesOn();
            if (refreshOn == null) continue;

            FeatureResetInfo resetInfo = new FeatureResetInfo();
            resetInfo.name = each.getName();
            resetInfo.description = each.getShortDescription();
            int maxUses = each.evaluateMaxUses(character);
            int usesRemaining = character.getUsesRemaining(each);
            Bundle resetBundle = null;
            if (savedFeatureResets != null) {
                resetBundle = savedFeatureResets.getBundle(each.getName());
            }
            if (resetBundle != null) {
                resetInfo.reset = resetBundle.getByte("reset") != 0;
                resetInfo.numToRestore = resetBundle.getInt("numToRestore");
            } else {
                resetInfo.reset = shouldReset(refreshOn);
                if (resetInfo.reset) {
                    resetInfo.numToRestore = maxUses - usesRemaining;
                }
            }
            resetInfo.refreshOn = refreshOn;
            resetInfo.maxToRestore = maxUses - usesRemaining;
            resetInfo.uses = usesRemaining + " / " + maxUses;
            resetInfo.needsResfesh = usesRemaining != maxUses;
            featureResets.add(resetInfo);
        }
        savedFeatureResets = null;
        featureResetAdapter = new FeatureResetsAdapter(getActivity(), featureResets);
        featureListView.setAdapter(featureResetAdapter);

        featureListView.setHasFixedSize(false);
        featureListView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        featureListView.addItemDecoration(itemDecoration);

    }

    protected void configureCommon(View view) {
        //featureResetsGroup = view.findViewById(R.id.feature_resets);
        startHp = (TextView) view.findViewById(R.id.start_hp);
        finalHp = (TextView) view.findViewById(R.id.final_hp);
        finalHpGroup = view.findViewById(R.id.final_hp_group);
        extraHealingGroup = view.findViewById(R.id.extra_heal_group);
        extraHealingtextView = (TextView) view.findViewById(R.id.extra_healing);
        noHealingGroup = view.findViewById(R.id.no_healing_group);

        featureListView = (RecyclerView) view.findViewById(R.id.feature_list);


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
        if (extraHealString.trim().length() > 0) {
            return Integer.parseInt(extraHealString);
        }
        return 0;
    }

    public void updateView() {
        Character character = getCharacter();
        int hp = character.getHP();
        int healing = getHealing();
        hp = Math.min(hp + healing, character.getMaxHP());
        finalHp.setText(getString(R.string.fraction_d_slash_d,hp ,character.getMaxHP()));
    }

    protected abstract int getHealing();

    protected boolean updateCommonRequest(AbstractRestRequest request) {
        boolean isValid = true;
        for (FeatureResetInfo each : featureResetAdapter.resets) {
            if (each.reset) {
                isValid = isValid && each.numToRestore <= each.maxToRestore;
                request.addFeatureReset(each.name, each.numToRestore);
            }
        }
        return isValid;
    }


    static class FeatureResetsAdapter extends RecyclerView.Adapter<FeatureResetViewHolder> {
        private final List<FeatureResetInfo> resets;
        private Context context;

        public FeatureResetsAdapter(Context context, List<FeatureResetInfo> resets) {
            this.context = context;
            this.resets = resets;
        }


        public FeatureResetInfo getItem(int position) {
            return resets.get(position);
        }

        @Override
        public FeatureResetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context, R.layout.feature_reset_item, null);
            FeatureResetViewHolder viewHolder = new FeatureResetViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final FeatureResetViewHolder viewHolder, int position) {
            final FeatureResetInfo row = getItem(position);
            viewHolder.bind(context, this, row);

        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return resets.size();
        }


    }

    static class FeatureResetViewHolder extends BindableComponentViewHolder<FeatureResetInfo, Context, FeatureResetsAdapter> {
        public TextWatcher watcher;
        CheckBox name;
        TextView description;
        TextView uses;
        EditText numToRestore;

        public FeatureResetViewHolder(View view) {
            super(view);
            name = (CheckBox) view.findViewById(R.id.feature_name);
            description = (TextView) view.findViewById(R.id.description);
            uses = (TextView) view.findViewById(R.id.uses);
            numToRestore = (EditText) view.findViewById(R.id.num_to_restore);
        }

        @Override
        public void bind(final Context context, final FeatureResetsAdapter adapter, final FeatureResetInfo row) {
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
                public void afterTextChanged(Editable s) {
                    // TODO error handling, if val is too large
                    if (s == null) return;
                    if (!row.reset) return;

                    String stringVal = s.toString().trim();
                    if (stringVal.length() == 0) return;
                    int value;
                    try {
                        value = Integer.parseInt(stringVal);
                    } catch (Exception e) {
                        numToRestore.setError("Enter a value <= " + row.maxToRestore);
                        return;
                    }
                    if (value > row.maxToRestore) {
                        numToRestore.setError("Enter a value <= " + row.maxToRestore);
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
}
