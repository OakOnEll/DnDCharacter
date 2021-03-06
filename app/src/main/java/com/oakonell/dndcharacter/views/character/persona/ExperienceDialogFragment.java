package com.oakonell.dndcharacter.views.character.persona;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.utils.NumberUtils;
import com.oakonell.dndcharacter.utils.UIUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.DividerItemDecoration;
import com.oakonell.dndcharacter.views.character.AbstractCharacterDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 1/7/2016.
 */
public class ExperienceDialogFragment extends AbstractCharacterDialogFragment {


    private TextView start_xp;
    private TextView end_xp;

    private EditText party_size;
    private EditText xpTextView;

    private ViewGroup your_xp_group;
    private TextView your_xp;

    @NonNull
    public static ExperienceDialogFragment create() {
        return new ExperienceDialogFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.experience_title);
    }

    protected boolean preventAutoSoftKeyboard() {
        return false;
    }

    @Override
    protected View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xp_dialog, container);

        start_xp = (TextView) view.findViewById(R.id.start_xp);
        end_xp = (TextView) view.findViewById(R.id.end_xp);

        xpTextView = (EditText) view.findViewById(R.id.xp);
        party_size = (EditText) view.findViewById(R.id.party_size);

        your_xp_group = (ViewGroup) view.findViewById(R.id.your_xp_group);
        your_xp = (TextView) view.findViewById(R.id.your_xp);

        RecyclerView xp_levels = (RecyclerView) view.findViewById(R.id.xp_levels);

        XpLevelAdapter xpAdapter = new XpLevelAdapter(this, getLevels());
        xp_levels.setAdapter(xpAdapter);

        xp_levels.setHasFixedSize(false);
        xp_levels.setLayoutManager(UIUtils.createLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        xp_levels.addItemDecoration(itemDecoration);


        party_size.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    pressDone();
                }
                return true;
            }
        });

        return view;
    }

    @NonNull
    private List<XpLevel> getLevels() {
        List<XpLevel> result = new ArrayList<>();
        result.add(new XpLevel(1, 0));
        result.add(new XpLevel(2, 300));
        result.add(new XpLevel(3, 900));
        result.add(new XpLevel(4, 2700));
        result.add(new XpLevel(5, 6500));
        result.add(new XpLevel(6, 14000));
        result.add(new XpLevel(7, 23000));
        result.add(new XpLevel(8, 34000));
        result.add(new XpLevel(9, 48000));
        result.add(new XpLevel(10, 64000));
        result.add(new XpLevel(11, 85000));
        result.add(new XpLevel(12, 100000));
        result.add(new XpLevel(13, 120000));
        result.add(new XpLevel(14, 140000));
        result.add(new XpLevel(15, 165000));
        result.add(new XpLevel(16, 195000));
        result.add(new XpLevel(17, 225000));
        result.add(new XpLevel(18, 265000));
        result.add(new XpLevel(19, 305000));
        result.add(new XpLevel(20, 355000));
        return result;
    }

    @Override
    public void onCharacterLoaded(com.oakonell.dndcharacter.model.character.Character character) {
        super.onCharacterLoaded(character);
        updateViews();
    }

    @Override
    public void onCharacterChanged(@NonNull Character character) {
        super.onCharacterChanged(character);
        updateViews();
    }

    protected void updateViews() {
        start_xp.setText(NumberUtils.formatNumber(getCharacter().getXp()));

        int partySize = Math.abs(getPartySize());

        int xp = getInputXp();

        if (partySize != 1) {
            your_xp_group.setVisibility(View.VISIBLE);
            xp = xp / partySize;
            your_xp.setText(String.format("%,d", xp));
        } else {
            your_xp_group.setVisibility(View.GONE);
        }


        end_xp.setText(NumberUtils.formatNumber(getCharacter().getXp() + xp));


        party_size.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                party_size.setError(null);
                updateViews();
            }
        });

        xpTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateViews();
            }
        });
    }

    private int getInputXp() {
        String xpString = xpTextView.getText().toString();
        int xp = 0;
        if (xpString.trim().length() != 0) {
            try {
                xp = Integer.parseInt(xpString);
            } catch (NumberFormatException e) {
                // do nothing
            }
        }
        return xp;
    }

    private int getPartySize() {
        String partySizeString = party_size.getText().toString();
        int partySize = 1;
        if (partySizeString.length() > 0) {
            partySize = Integer.parseInt(partySizeString);
            if (partySize == 0) {
                party_size.setError(getString(R.string.enter_positive_number_error));
                partySize = -1;
            }
        }
        return partySize;
    }

    @Override
    protected boolean onDone() {
        int partySize = getPartySize();
        if (partySize < 0) {
            return false;
        }

        int xp = getInputXp();

        if (partySize != 1) {
            your_xp_group.setVisibility(View.VISIBLE);
            xp = xp / partySize;
        }
        getCharacter().addExperience(xp);

        return super.onDone();
    }

    static class XpLevel {
        final int level;
        final int xp;

        public XpLevel(int level, int xp) {
            this.level = level;
            this.xp = xp;
        }
    }

    static class XpLevelViewHolder extends BindableComponentViewHolder<XpLevel, ExperienceDialogFragment, XpLevelAdapter> {
        @NonNull
        final TextView xp;
        @NonNull
        final TextView level;
        @NonNull
        final TextView comment;

        public XpLevelViewHolder(@NonNull View itemView) {
            super(itemView);
            xp = (TextView) itemView.findViewById(R.id.xp);
            level = (TextView) itemView.findViewById(R.id.level);
            comment = (TextView) itemView.findViewById(R.id.comment);
        }

        @Override
        public void bind(@NonNull ExperienceDialogFragment context, XpLevelAdapter adapter, @NonNull XpLevel info) {
            xp.setText(NumberUtils.formatNumber(info.xp));
            level.setText(NumberUtils.formatNumber(info.level));
            int currentLevel = -1;
            int currentXp = 0;
            if (context.getCharacter() != null) {
                currentLevel = context.getCharacter().getCharacterLevel();
                currentXp = context.getCharacter().getXp();
            }
            comment.setText("");
            if (currentLevel == info.level) {
                // TODO highlight background
                comment.setText(R.string.current_level);
            } else if (currentLevel == info.level - 1) {
                int needed = info.xp - currentXp;
                if (needed > 0) {
                    comment.setText(context.getString(R.string.need_xp_for_next_level, needed));
                }
            }
        }
    }

    static class XpLevelAdapter extends RecyclerView.Adapter<XpLevelViewHolder> {
        final List<XpLevel> list;
        final ExperienceDialogFragment context;

        XpLevelAdapter(ExperienceDialogFragment context, List<XpLevel> list) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public XpLevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View newView = LayoutInflater.from(parent.getContext()).inflate(R.layout.xp_level_row, parent, false);
            return new XpLevelViewHolder(newView);
        }

        @Override
        public void onBindViewHolder(@NonNull XpLevelViewHolder holder, int position) {
            holder.bind(context, this, list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

}
