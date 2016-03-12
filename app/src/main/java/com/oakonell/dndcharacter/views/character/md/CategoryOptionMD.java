package com.oakonell.dndcharacter.views.character.md;

import android.view.ViewGroup;

/**
 * Created by Rob on 2/1/2016.
 */
public abstract class CategoryOptionMD extends OptionMD<CategoryChoicesMD> {

    public CategoryOptionMD(CategoryChoicesMD chooseMD) {
        super(chooseMD);
    }

    public abstract boolean isPopulated();

    public abstract void showRequiredError(ViewGroup dynamicView);

    public abstract String getText();
}
