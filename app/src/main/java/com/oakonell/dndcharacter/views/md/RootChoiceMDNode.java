package com.oakonell.dndcharacter.views.md;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 12/29/2015.
 */
public class RootChoiceMDNode implements ChooseMDTreeNode {
    List<ChooseMD<?>> choiceMDs = new ArrayList<>();

    @Override
    public void addChildChoice(ChooseMD<?> choiceMd) {
        choiceMDs.add(choiceMd);
    }

    @Override
    public List<ChooseMD<?>> getChildChoiceMDs() {
        return choiceMDs;
    }
}
