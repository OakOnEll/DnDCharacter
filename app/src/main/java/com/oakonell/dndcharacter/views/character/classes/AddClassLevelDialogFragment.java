package com.oakonell.dndcharacter.views.character.classes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.CharacterClass;
import com.oakonell.dndcharacter.model.character.SavedChoices;
import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.model.classes.ApplyClassToCharacterVisitor;
import com.oakonell.dndcharacter.utils.XmlUtils;

import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

/**
 * Created by Rob on 11/9/2015.
 */
public class AddClassLevelDialogFragment extends AbstractClassLevelEditDialogFragment {
    private int classLevel;

    @NonNull
    public static AddClassLevelDialogFragment createDialog() {
        return new AddClassLevelDialogFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.add_class_level);
    }

    @Override
    protected void modelChanged() {
        Integer currentLevel = getCharacter().getClassLevels().get(getModel().getName());
        if (currentLevel == null) currentLevel = 0;
        classLevel = currentLevel + 1;

        isValidClass = true;

        if (classLevel == 1 && !getCharacter().getClassLevels().isEmpty()) {
            // used to prevent choosing a new multiclass that fails the new class multiclass' prerequisite
            MulticlassFailureInfo info = getMulticlassFailure();
            if (info != null) {
                setClassError(getString(R.string.cannot_multiclass_to, info.className, info.formula), true);
                isValidClass = false;
            } else {
                setClassError(null, false);
            }
        } else {
            setClassError(null, false);
        }
    }

    @Override
    protected void applyToCharacter(SavedChoices savedChoices, Map<String, String> customChoices) {
        ApplyClassToCharacterVisitor.addClassLevel(getActivity(), getModel(), savedChoices, customChoices, getSubClass(), getSubClassChoices(), getCharacter(), getCharacter().getClasses().size() + 1, classLevel, hp);
    }


    @NonNull
    protected SavedChoices getSavedChoicesFromCharacter(Character character) {
        // a new class level won't have save choices
        return new SavedChoices();
    }

    protected String getCurrentName() {
        List<CharacterClass> classes = getCharacter().getClasses();
        if (classes.isEmpty()) return "";
        // continue with the last class
        return classes.get(classes.size() - 1).getName();
    }


    @Override
    @Nullable
    protected CharacterClass getCharacterClass() {
        return null;
    }

    @Override
    protected int getCurrentHpRoll() {
        return 0;
    }

    @Override
    protected boolean isFirstCharacterLevel() {
        return getCharacter().getCharacterLevel() < 1;
    }

    @Override
    protected int getClassLevel() {
        return classLevel;
    }

    protected boolean canModifySubclass() {
        return true;
    }

    boolean isValidClass = true;

    @Override
    protected boolean validate(@NonNull ViewGroup dynamicView, int pageIndex) {
        if (!isValidClass) {
            shakeClassError();
        }
        return super.validate(dynamicView, pageIndex) && isValidClass;
    }

    @Override
    protected boolean allowMainComponentChange() {
        List<CharacterClass> classes = getCharacter().getClasses();
        if (classes.isEmpty()) return true;

        // Used to prevent multiclassing FROM the current class, when current class's prerequisite not met
        if (getModel() != null && getCurrentName().equals(getModel().getName())) {
            MulticlassFailureInfo info = getMulticlassFailure();
            if (info != null) {
                setClassError(getString(R.string.cannot_multiclass_from, info.className, info.formula), false);
                return false;
            }
        }
        return super.allowMainComponentChange();
    }

    private MulticlassFailureInfo getMulticlassFailure() {
        AClass model = getModel();
        if (model == null) return null;
        final Element root = XmlUtils.getDocument(model.getXml()).getDocumentElement();
        String formula = XmlUtils.getElementText(root, "multiclassPrerequisite");
        if (formula != null && formula.trim().length() > 0) {
            if (!getCharacter().evaluateBooleanFormula(formula, null)) {
                MulticlassFailureInfo failure = new MulticlassFailureInfo();
                failure.className = model.getName();
                failure.formula = formula;
                return failure;
            }
        }
        return null;
    }

    private static class MulticlassFailureInfo {
        String formula;
        String className;
    }

}
