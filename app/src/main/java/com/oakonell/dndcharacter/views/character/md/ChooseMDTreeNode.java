package com.oakonell.dndcharacter.views.character.md;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Rob on 12/29/2015.
 */
public interface ChooseMDTreeNode {
    void addChildChoice(ChooseMD<?> choiceMd);

    @NonNull
    List<ChooseMD<?>> getChildChoiceMDs();
}
