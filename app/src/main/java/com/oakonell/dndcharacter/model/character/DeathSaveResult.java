package com.oakonell.dndcharacter.model.character;

import com.oakonell.dndcharacter.R;

/**
 * Created by Rob on 3/9/2016.
 */
public enum DeathSaveResult {
    CRIT_FAIL(R.string.death_save_critical_failure), CRIT_PASS(R.string.death_save_critical_pass),
    FAIL(R.string.death_save_failure), PASS(R.string.death_save_pass);

    private final int stringResId;

    DeathSaveResult(int stringResId) {
        this.stringResId = stringResId;
    }

    public int getStringResId() {
        return stringResId;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
