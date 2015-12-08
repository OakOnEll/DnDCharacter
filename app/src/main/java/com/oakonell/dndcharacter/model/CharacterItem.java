package com.oakonell.dndcharacter.model;

import com.oakonell.dndcharacter.model.item.ItemType;

import org.simpleframework.xml.Element;

/**
 * Created by Rob on 12/2/2015.
 */
public class CharacterItem extends BaseCharacterComponent {
    @Element(required = false)
    ComponentType source;
    @Element(required = false)
    private ItemType type;
    /*    @Element(required = false)
        private boolean isContainer;

        @ElementList(required = false)
        private List<CharacterItem> items = new ArrayList<CharacterItem>();
    */
    @Element(required = false)
    private String category;

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

    public ItemType getItemType() {
        return type;
    }

    public void setItemType(ItemType type) {
        this.type = type;
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

    public String toString() {
        return getName();
    }
}
