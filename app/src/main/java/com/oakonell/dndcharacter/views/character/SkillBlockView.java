package com.oakonell.dndcharacter.views.character;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.Proficient;
import com.oakonell.dndcharacter.model.character.SkillBlock;
import com.oakonell.dndcharacter.model.character.SkillType;

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
        this.bonus.setText(bonus + "");
    }

    protected void setLabel(String label) {
        skill_label.setText(label);
    }

    protected void setProficiency(Proficient proficient) {
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
        SkillBlock statblock = character.getSkillBlock(this.type);

        setProficiency(statblock.getProficiency());
        setBonus(statblock.getBonus());
        setLabel(this.type.name());
    }


}
