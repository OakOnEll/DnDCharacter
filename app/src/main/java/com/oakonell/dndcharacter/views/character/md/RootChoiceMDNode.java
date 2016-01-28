package com.oakonell.dndcharacter.views.character.md;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 12/29/2015.
 */
public class RootChoiceMDNode implements ChooseMDTreeNode {
    final List<ChooseMD<?>> choiceMDs = new ArrayList<>();

    @Override
    public void addChildChoice(ChooseMD<?> choiceMd) {
        choiceMDs.add(choiceMd);
    }

    @NonNull
    @Override
    public List<ChooseMD<?>> getChildChoiceMDs() {
        return choiceMDs;
    }
}
