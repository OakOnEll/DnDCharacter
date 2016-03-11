package com.oakonell.dndcharacter.model.character.item;

import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.model.character.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.character.ComponentType;
import com.oakonell.dndcharacter.model.item.ItemType;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 12/2/2015.
 */
public class CharacterItem extends BaseCharacterComponent {
    @Element(required = false)
    private ComponentType source;
    /*    @Element(required = false)
        private boolean isContainer;

        @ElementList(required = false)
        private List<CharacterItem> items = new ArrayList<CharacterItem>();
    */
    @Element(required = false)
    private String category;
    @Element(required = false)
    private int count = 1;

    @NonNull
    @Override
    public ComponentType getType() {
        return ComponentType.ITEM;
    }

    public ComponentType getSource() {
        return source;
    }

    public void setSource(ComponentType source) {
        this.source = source;
    }

    @NonNull
    public ItemType getItemType() {
        return ItemType.EQUIPMENT;
    }


    /*
        public boolean isContainer() {
            return isContainer;
        }

        public void setIsContainer(boolean isContainer) {
            this.isContainer = isContainer;
        }

        public List<CharacterItem> getItems() {
            return items;
        }
    */
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @NonNull
    public String toString() {
        return getItemType() + ":" + getName();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
