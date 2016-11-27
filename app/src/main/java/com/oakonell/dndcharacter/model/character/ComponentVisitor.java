package com.oakonell.dndcharacter.model.character;

/**
 * Created by Rob on 11/27/2016.
 */

public interface ComponentVisitor {
    void visitComponent(ICharacterComponent component);
}
