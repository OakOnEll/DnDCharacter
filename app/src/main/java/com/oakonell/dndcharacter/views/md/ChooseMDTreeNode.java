package com.oakonell.dndcharacter.views.md;

import java.util.List;

/**
 * Created by Rob on 12/29/2015.
 */
public interface ChooseMDTreeNode {
    void addChildChoice(ChooseMD<?> choiceMd);

    List<ChooseMD<?>> getChildChoiceMDs();
}
