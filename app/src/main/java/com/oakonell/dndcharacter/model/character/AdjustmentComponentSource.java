package com.oakonell.dndcharacter.model.character;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Rob on 3/21/2016.
 */
public class AdjustmentComponentSource implements ComponentSource {
    CustomAdjustments.Adjustment adjustment;

    AdjustmentComponentSource(CustomAdjustments.Adjustment adjustment) {
        this.adjustment = adjustment;
    }

    @Override
    public String getName() {
        return "Custom";
    }

    @NonNull
    @Override
    public String getSourceString(Resources resources) {
        return resources.getString(getType().getStringResId()) + ": " + adjustment.comment;
    }

    @NonNull
    @Override
    public String getAsSourceString(Resources resources) {
        return resources.getString(getType().getStringResId()) + ": " + adjustment.comment;
    }

    @Override
    public boolean originatesFrom(ComponentSource currentComponent) {
        return false;
    }

    @Nullable
    @Override
    public ComponentType getType() {
        return ComponentType.CUSTOM_ADJUSTMENT;
    }

    @Nullable
    @Override
    public String getActiveFormula() {
        return null;
    }

    public CustomAdjustments.Adjustment getAdjustment() {
        return adjustment;
    }

    public void setEquipped(Resources resources, AbstractCharacter character, boolean equipped) {
        adjustment.applied = equipped;
    }
}
