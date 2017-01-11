package com.oakonell.dndcharacter.model.character.rest;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.AbstractCharacter;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.FeatureInfo;
import com.oakonell.dndcharacter.model.character.FeatureResetInfo;
import com.oakonell.dndcharacter.model.character.SpellSlotResetInfo;
import com.oakonell.dndcharacter.model.character.companion.CharacterCompanion;
import com.oakonell.dndcharacter.model.components.RefreshType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Rob on 11/9/2015.
 */
public abstract class AbstractRestRequest<C extends CompanionRest> implements RestRequest, Parcelable {
    final List<FeatureResetInfo> featureResets;
    final List<SpellSlotResetInfo> spellSlotResets;
    final List<C> companionRestRequests;

    private int maxHP;
    private int startHP;
    private int extraHealing;

    protected AbstractRestRequest(Parcel source) {
        maxHP = source.readInt();
        startHP = source.readInt();
        extraHealing = source.readInt();
        featureResets = source.readArrayList(ClassLoader.getSystemClassLoader());
        spellSlotResets = source.readArrayList(ClassLoader.getSystemClassLoader());
        companionRestRequests = source.readArrayList(ClassLoader.getSystemClassLoader());
    }

    protected AbstractRestRequest() {
        featureResets = new ArrayList<>();
        spellSlotResets = new ArrayList<>();
        companionRestRequests = new ArrayList<>();
    }

    @Override
    public int getExtraHealing() {
        return extraHealing;
    }

    @Override
    public void setExtraHealing(int hp) {
        extraHealing = hp;
    }

    @NonNull
    public List<FeatureResetInfo> getFeatureResets() {
        return featureResets;
    }


    public List<C> getCompanionRestRequests() {
        return companionRestRequests;
    }

    @NonNull
    public List<SpellSlotResetInfo> getSpellSlotResets() {
        return spellSlotResets;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getStartHP() {
        return startHP;
    }

    public void setStartHP(int startHP) {
        this.startHP = startHP;
    }


    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(maxHP);
        dest.writeInt(startHP);
        dest.writeInt(extraHealing);
        dest.writeList(featureResets);
        dest.writeList(spellSlotResets);
        dest.writeList(companionRestRequests);
    }

    public void populateFeatureResets(AbstractCharacter character, Context context) {
        Collection<FeatureInfo> features = character.getFeatureInfos();
        for (FeatureInfo each : features) {
            RefreshType refreshOn = each.getRefreshesOn();
            if (refreshOn == null) continue;

            FeatureResetInfo resetInfo = new FeatureResetInfo();
            resetInfo.name = each.getName();
            resetInfo.description = each.getShortDescription();
            int maxUses = each.evaluateMaxUses(character);
            int usesRemaining = character.getUsesRemaining(each);
            resetInfo.reset = shouldReset(refreshOn);
            if (resetInfo.reset) {
                resetInfo.numToRestore = maxUses - usesRemaining;
            } else {
                resetInfo.numToRestore = 0;
            }
            resetInfo.refreshOn = refreshOn;
            resetInfo.maxToRestore = maxUses - usesRemaining;
            // TODO should this be constructed on the UI
            resetInfo.uses = context.getString(R.string.fraction_d_slash_d, usesRemaining, maxUses);
            resetInfo.needsResfesh = usesRemaining != maxUses;
            getFeatureResets().add(resetInfo);
        }
    }

    protected abstract boolean shouldReset(RefreshType refreshesOn);

    public void populateSpellSlotResets(Character character) {
        final List<Character.SpellLevelInfo> spellInfos = character.getSpellInfos();
        for (Character.SpellLevelInfo each : spellInfos) {
            if (each.getLevel() == 0) continue;

            SpellSlotResetInfo resetInfo = new SpellSlotResetInfo();
            resetInfo.level = each.getLevel();
            resetInfo.maxSlots = each.getMaxSlots();
            resetInfo.availableSlots = each.getSlotsAvailable();

            resetInfo.restoreSlots = 0;

            int toRestore = getSlotsToRestore(character, each);
            resetInfo.reset = toRestore > 0;
            if (resetInfo.reset) {
                resetInfo.restoreSlots = toRestore;
            } else {
                resetInfo.restoreSlots = 0;
            }

            spellSlotResets.add(resetInfo);
        }
    }

    protected abstract int getSlotsToRestore(Character character, Character.SpellLevelInfo each);

    public void populateCompanionRests(Character character, Context context) {
        List<C> companionRequests = getCompanionRestRequests();
        int index = -1;
        for (CharacterCompanion each : character.getCompanions()) {
            index++;
            if (!each.isActive()) continue;
            C companionRequest = createCompanionRequest(each, context);
            companionRequest.setCompanionIndex(index);
            companionRequests.add(companionRequest);
        }

    }

    protected abstract C createCompanionRequest(CharacterCompanion each, Context context);

    public abstract void apply(AbstractCharacter character);
}
