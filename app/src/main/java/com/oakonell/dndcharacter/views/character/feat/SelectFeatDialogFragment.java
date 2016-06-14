package com.oakonell.dndcharacter.views.character.feat;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.Model;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.EnumHelper;
import com.oakonell.dndcharacter.model.feat.Feat;
import com.oakonell.dndcharacter.model.spell.SpellSchool;
import com.oakonell.dndcharacter.views.CursorIndexesByName;
import com.oakonell.dndcharacter.views.character.AbstractSelectComponentDialogFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;

/**
 * Created by Rob on 2/1/2016.
 */
public class SelectFeatDialogFragment extends AbstractSelectComponentDialogFragment<AbstractSelectComponentDialogFragment.RowViewHolder> {

    private FeatSelectedListener listener;

    public void setListener(FeatSelectedListener listener) {
        this.listener = listener;
    }

    public interface FeatSelectedListener {
        boolean featSelected(long id);
    }

    @NonNull
    public static SelectFeatDialogFragment createDialog(@NonNull FeatSelectedListener listener) {
        SelectFeatDialogFragment dialog = new SelectFeatDialogFragment();
        dialog.setListener(listener);
        return dialog;
    }


    @Override
    protected String getTitle() {
        return getString(R.string.select_feat);
    }

    protected int getListItemResource() {
        return R.layout.feat_list_item;
    }

    @NonNull
    @Override
    public RowViewHolder newRowViewHolder(@NonNull View newView) {
        return new FeatRowViewHolder(newView);
    }


    @NonNull
    @Override
    public Class<? extends Model> getComponentClass() {
        return Feat.class;
    }

    @Override
    protected boolean applyAction(long id) {
        if (listener != null) {
            return listener.featSelected(id);
        }
        throw new RuntimeException("No Listener!");
    }

    public static class FeatRowViewHolder extends RowViewHolder {

        @NonNull
        private final TextView description;
        @NonNull
        private final ImageView expand;

        public FeatRowViewHolder(@NonNull View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.description);
            expand = (ImageView) itemView.findViewById(R.id.expand);
        }


        @Override
        public void bindTo(final Cursor cursor, AbstractSelectComponentDialogFragment context, final CursorComponentListAdapter adapter, CursorIndexesByName cursorIndexesByName) {
            super.bindTo(cursor, context, adapter, cursorIndexesByName);

            String text = cursor.getString(cursorIndexesByName.getIndex(cursor, "description"));
            if (text == null) text = "";
            description.setText(text);

            final int position = cursor.getPosition();
            boolean isExpanded = adapter.isExpanded(cursor.getPosition());
            if (isExpanded) {
                description.setEllipsize(null);
                description.setLines(5);
                description.setMaxLines(Integer.MAX_VALUE);
                expand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.setExpanded(position, false);
                        adapter.notifyDataSetChanged();
                    }
                });
            } else {
                description.setEllipsize(TextUtils.TruncateAt.END);
                description.setLines(1);
                description.setMaxLines(1);
                expand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.setExpanded(position, true);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

}
