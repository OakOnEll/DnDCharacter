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

    @Element(required = false)
    private String weight;
    @Element(required = false)
    private String cost;
    @Element(required = false)
    private String notes;
    @Element(required = false)
    private long id;


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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
