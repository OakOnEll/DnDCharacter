package com.oakonell.dndcharacter.views.classes;

import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;

/**
 * Created by Rob on 11/2/2015.
 */
public class ClassesListActivity extends AbstractComponentListActivity {

    @NonNull
    @Override
    protected Class<? extends Model> getComponentClass() {
        return AClass.class;
    }

    @Override
    protected AClass createNewRecord() {
        return new AClass();
    }

    @Override
    protected void openRecord(long id) {
        AClass aClass = AClass.load(AClass.class, id);

        EditClassDialogFragment dialog = EditClassDialogFragment.create(aClass);
        dialog.show(getSupportFragmentManager(), "class_edit");
    }

    @Override
    protected String getRecordTypeName() {
        return "Class";
    }

    @Override
    protected String getSubtitle() {
        return "Classes";
    }

    @Override
    protected void deleteRow(long id) {
        AClass.delete(AClass.class, id);
    }
}
