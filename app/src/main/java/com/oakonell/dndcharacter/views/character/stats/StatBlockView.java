package com.oakonell.dndcharacter.views.character.stats;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.stats.StatBlock;
import com.oakonell.dndcharacter.model.character.stats.StatType;
import com.oakonell.dndcharacter.utils.NumberUtils;


/**
 * Created by Rob on 10/22/2015.
 */
public class StatBlockView extends LinearLayout {
    Character character;
    StatType type;

    TextView stat_label;
    TextView stat_value;
    TextView stat_mod;

    public StatBlockView(Context context) {
        super(context);
        init(context);
    }

    public StatBlockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StatBlockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    protected void init(Context context) {
        View.inflate(context, R.layout.stat_layout, this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        stat_label = (TextView) findViewById(R.id.stat_label);
        stat_value = (TextView) findViewById(R.id.stat_value);
        stat_mod = (TextView) findViewById(R.id.stat_mod);
    }

    protected void setLabel(String label) {
        stat_label.setText(label);
    }

    protected void setValue(int value) {
        stat_value.setText(NumberUtils.formatNumber(value));
    }

    protected void setModifier(int mod) {
        stat_mod.setText(NumberUtils.formatNumber(mod ));
    }

    public void setType(StatType type) {
        this.type = type;
        if (character != null) {
            updateViews();
        }
    }

    public void setCharacter(Character character) {
        this.character = character;
        if (type != null) {
            updateViews();
        }
    }

    private void updateViews() {
        if (character != null) {
            StatBlock statblock = character.getStatBlock(this.type);
            setValue(statblock.getValue());
            setModifier(statblock.getModifier());
        } else {
            setValue(10);
            setModifier(0);
        }
        setLabel(this.type.name());
    }
}
