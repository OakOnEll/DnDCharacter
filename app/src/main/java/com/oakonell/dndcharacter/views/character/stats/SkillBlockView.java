package com.oakonell.dndcharacter.views.character.stats;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.Proficient;
import com.oakonell.dndcharacter.model.character.stats.SkillBlock;
import com.oakonell.dndcharacter.model.character.stats.SkillType;
import com.oakonell.dndcharacter.utils.NumberUtils;

/**
 * Created by Rob on 10/22/2015.
 */
public class SkillBlockView extends LinearLayout {
    SkillType type;
    ImageView proficiency;
    TextView skill_label;
    TextView bonus;
    Character character;


    public SkillBlockView(Context context) {
        super(context);
        init(context);
    }

    public SkillBlockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SkillBlockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.skill_layout, this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        proficiency = (ImageView) findViewById(R.id.proficient);
        skill_label = (TextView) findViewById(R.id.skill_label);
        bonus = (TextView) findViewById(R.id.bonus);
    }

    protected void setBonus(int bonus) {
        this.bonus.setText(NumberUtils.formatNumber(bonus));
    }

    protected void setLabel(int resId) {
        skill_label.setText(resId);
    }

    protected void setProficiency(@NonNull Proficient proficient) {
        int imageResource = R.drawable.not_proficient;
        switch (proficient) {
            case NONE:
                imageResource = R.drawable.not_proficient;
                break;
            case PROFICIENT:
                imageResource = R.drawable.proficient;
                break;
            case HALF:
                imageResource = R.drawable.half_proficient;
                break;
            case EXPERT:
                imageResource = R.drawable.expert_proficient;
                break;
        }
        this.proficiency.setImageResource(imageResource);

    }

    public void setType(SkillType type) {
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
            SkillBlock statblock = character.getSkillBlock(this.type);
            setProficiency(statblock.getProficiency());
            setBonus(statblock.getBonus());
        } else {
            setProficiency(Proficient.NONE);
            setBonus(0);
        }
        setLabel(this.type.getStringResId());
    }


}
