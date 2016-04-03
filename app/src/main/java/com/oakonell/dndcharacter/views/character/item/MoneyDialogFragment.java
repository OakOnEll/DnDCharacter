package com.oakonell.dndcharacter.views.character.item;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.MoneyValue;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;
import com.oakonell.dndcharacter.views.character.feature.SelectEffectDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 12/2/2015.
 */
public class MoneyDialogFragment extends AbstractCharacterDialogFragment {
    public static final String FOCUS = "focus";
    public static final String ADD_GEM_DIALOG = "add_gem_dialog";
    public static final String SAVE_GEMS = "gems";
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
    private RecyclerView gem_listView;
    private TextView gemTitle;
    private View addGem;
    private GemAdapter gemAdapter;

    private ArrayList<Character.Gem> gems = new ArrayList<>();
    private boolean isFromSaveState;

    @NonNull
    public static MoneyDialogFragment createFragment(@Nullable CoinType focus) {
        MoneyDialogFragment newMe = new MoneyDialogFragment();
        if (focus != null) {
            Bundle args = new Bundle();
            args.putInt(FOCUS, focus.ordinal());
            newMe.setArguments(args);
        }
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

        gemTitle = (TextView) rootView.findViewById(R.id.gem_title);
        addGem = rootView.findViewById(R.id.addGem);
        gem_listView = (RecyclerView) rootView.findViewById(R.id.gems_list);

        if (getArguments() != null) {
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
        }

        addGem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // popup a gem input dialog, add to gems
                AddGemDialog dialog = AddGemDialog.createDialog(getGemListener());
                dialog.show(getFragmentManager(), ADD_GEM_DIALOG);
            }
        });

        gem_listView = (RecyclerView) rootView.findViewById(R.id.gems_list);

        if (savedInstanceState != null) {
            isFromSaveState = true;
            // restore gems list
            gems = savedInstanceState.getParcelableArrayList(SAVE_GEMS);

            // restore state of add gem dialog
            AddGemDialog gemDialog = (AddGemDialog) getFragmentManager().findFragmentByTag(ADD_GEM_DIALOG);
            if (gemDialog != null) {
                gemDialog.setAddGemListener(getGemListener());
            }
        }

        return rootView;
    }

    private AddGemDialog.AddGemListener getGemListener() {
        return new AddGemDialog.AddGemListener() {
            @Override
            public void addGem(Character.Gem gem) {
                gems.add(gem);
                gemAdapter.notifyDataSetChanged();
                updateView(getCharacter());
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVE_GEMS, gems);
    }

    @Override
    public void onCharacterLoaded(@NonNull Character character) {
        // TODO skil adding from gems, if saved state existed..
        if (!isFromSaveState) {
            gems.addAll(character.getGems());

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

        updateView(character);


        // TODO deal with save/restore state of deleted and added gems
        gemAdapter = new GemAdapter(this, gems);
        gem_listView.setAdapter(gemAdapter);

        gem_listView.setLayoutManager(new GridLayoutManager(this.getActivity(), getResources().getInteger(R.integer.gem_columns), GridLayoutManager.VERTICAL, false));
        gem_listView.setHasFixedSize(false);
    }

    private void updateView(@NonNull Character character) {

        if (gems.size() == 0) {
            gemTitle.setText(R.string.no_gems_title);
        } else {
            MoneyValue amount = new MoneyValue();
            for (Character.Gem each : gems) {
                amount = amount.add(each.getValue());
            }
            gemTitle.setText(getString(R.string.gems_title, amount.simplified().asFormattedString()));
        }


    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);
        // TODO update the current coins and the result for each ?
        //updateView(character);
    }


    @Override
    protected boolean onDone() {
        Character character = getCharacter();
        character.setCopper(getResultCoins(copperResult));
        character.setSilver(getResultCoins(silverResult));
        character.setElectrum(getResultCoins(electrumResult));
        character.setGold(getResultCoins(goldResult));
        character.setPlatinum(getResultCoins(platinumResult));

        character.getGems().clear();
        character.getGems().addAll(gems);

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

    private static class GemViewHolder extends BindableComponentViewHolder<Character.Gem, MoneyDialogFragment, GemAdapter> {

        private final TextView name;
        private final TextView value;
        private final View delete;

        public GemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            value = (TextView) itemView.findViewById(R.id.value);
            delete = itemView.findViewById(R.id.delete);
        }

        @Override
        public void bind(final MoneyDialogFragment context, final GemAdapter adapter, final Character.Gem info) {
            name.setText(info.getName());
            value.setText(info.getValue().asFormattedString());
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  delete a gem
                    context.gems.remove(info);
                    adapter.notifyDataSetChanged();
                    context.updateView(context.getCharacter());
                }
            });
        }


    }

    private static class GemAdapter extends RecyclerView.Adapter<GemViewHolder> {
        private final MoneyDialogFragment frag;
        private final List<Character.Gem> gems;

        GemAdapter(MoneyDialogFragment frag, List<Character.Gem> gems) {
            this.frag = frag;
            this.gems = gems;
        }

        @Override
        public GemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(frag.getActivity()).inflate(R.layout.gem_row, parent, false);
            return new GemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(GemViewHolder holder, int position) {
            final Character.Gem gem = gems.get(position);
            holder.bind(frag, this, gem);
        }

        @Override
        public int getItemCount() {
            return gems.size();
        }
    }
}
