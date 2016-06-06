package com.oakonell.dndcharacter.views.classes;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.activeandroid.Model;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.AbstractComponentModel;
import com.oakonell.dndcharacter.model.classes.AClass;
import com.oakonell.dndcharacter.views.AbstractComponentListActivity;
import com.oakonell.dndcharacter.views.CursorIndexesByName;

/**
 * Created by Rob on 11/2/2015.
 */
public class ClassesListActivity extends AbstractComponentListActivity {

    @Override
    protected int getListItemResource() {
        return R.layout.class_list_item;
    }

    @NonNull
    @Override
    protected Class<? extends Model> getComponentClass() {
        return AClass.class;
    }

    @NonNull
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

    @NonNull
    protected ClassRowViewHolderCursor newRowViewHolder(@NonNull View newView) {
        return new ClassRowViewHolderCursor(newView);
    }

    @NonNull
    @Override
    protected String getSubtitle() {
        return getString(R.string.classes);
    }

    @Override
    protected void deleteRow(long id) {
        AClass.delete(AClass.class, id);
    }

    protected static class ClassRowViewHolderCursor extends RowViewHolderCursor<AbstractComponentModel> {
        @NonNull
        public final TextView parentClass;

        public ClassRowViewHolderCursor(@NonNull View itemView) {
            super(itemView);
            parentClass = (TextView) itemView.findViewById(R.id.parent_class);
        }

        @Override
        public void bindTo(Cursor cursor, AbstractComponentListActivity context, ComponentListAdapter<AbstractComponentListActivity<AbstractComponentModel>, AbstractComponentModel> adapter, CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);
            final String parentName = cursor.getString(cursorIndexesByName.getIndex(cursor, "parentClass"));
            parentClass.setText(parentName);
        }
    }
}
