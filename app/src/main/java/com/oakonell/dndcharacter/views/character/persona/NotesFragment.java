package com.oakonell.dndcharacter.views.character.persona;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.Character;
import com.oakonell.dndcharacter.model.character.ContextNote;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;
import com.oakonell.dndcharacter.views.character.AbstractSheetFragment;
import com.oakonell.dndcharacter.views.character.CharacterActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob on 10/26/2015.
 */
public class NotesFragment extends AbstractSheetFragment {
    //    Button toXml;
    EditText notes;
    private NotesAdapter adapter;
    private RecyclerView notes_list;

    public View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notes_sheet, container, false);

        superCreateViews(rootView);
//        toXml = (Button) rootView.findViewById(R.id.to_xml);
        notes = (EditText) rootView.findViewById(R.id.notes);
        notes_list = (RecyclerView) rootView.findViewById(R.id.notes_list);

//        toXml.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Serializer serializer = new Persister();
//                OutputStream out = new ByteArrayOutputStream();
//                try {
//                    serializer.write(getCharacter(), out);
//                    out.close();
//                } catch (Exception e) {
//                    notes.setText("Error converting to xml\n" + e.toString());
//
//                    return;
//                }
//
//
//                //notes.setText("character size = " +character + out.toString());
//                notes.setText(out.toString());
//            }
//        });

        updateViews(rootView);

        // need to hook a notes text watcher, to update the model
        notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getCharacter() != null) {
                    getCharacter().setNotes(s.toString());
                }
            }
        });
        return rootView;
    }

    @Override
    protected void updateViews(View rootView) {
        super.updateViews(rootView);
        if (notes == null) return;
        if (getCharacter() == null) {
            notes.setText("");
        } else {
            notes.setText(getCharacter().getNotes());
            if (adapter != null) {
                adapter.reloadList(getCharacter());
            }
        }
    }

    @Override
    public void onCharacterLoaded(@NonNull com.oakonell.dndcharacter.model.character.Character character) {
        super.onCharacterLoaded(character);
        if (getActivity() == null) return;

        adapter = new NotesAdapter((CharacterActivity) this.getActivity());
        notes_list.setAdapter(adapter);
        // decide on 1 or 2 columns based on screen size
        int numColumns = getResources().getInteger(R.integer.feature_columns);
        notes_list.setLayoutManager(new StaggeredGridLayoutManager(numColumns, StaggeredGridLayoutManager.VERTICAL));

        updateViews();
    }

    public static class NoteViewHolder extends BindableComponentViewHolder<ContextNote, CharacterActivity, NotesAdapter> {

        private final TextView title;
        private final ImageView delete;
        private final TextView text;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            text = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        public void bind(final CharacterActivity context, final NotesAdapter adapter, final ContextNote info) {
            if (info.getContext().getArgument() != null) {
                title.setText(info.getContext().getContext().toString() + "(" + info.getContext().getArgument() + ")");
            } else {
                title.setText(info.getContext().getContext().toString());
            }
            text.setText(info.getText());
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<ContextNote> notes = context.getCharacter().getContextNotes(info.getContext().getContext());
                    notes.remove(info);

                    delete.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.saveCharacter();
                            context.updateViews();
                        }
                    }, 100);
                }
            });
        }
    }

    public static class NotesAdapter extends RecyclerView.Adapter<NoteViewHolder> {
        @NonNull
        private final CharacterActivity context;
        private List<ContextNote> list;

        public NotesAdapter(@NonNull CharacterActivity context) {
            this.context = context;
            list = new ArrayList<>(context.getCharacter().getContextNotes());
        }


        public void reloadList(@NonNull Character character) {
            list = new ArrayList<>(character.getContextNotes());
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        @Nullable
        public ContextNote getItem(int position) {
            return list.get(position);
        }

        @NonNull
        @Override
        public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.context_note_layout, parent, false);
            NoteViewHolder holder = new NoteViewHolder(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(@NonNull final NoteViewHolder viewHolder, final int position) {
            final ContextNote note = getItem(position);
            viewHolder.bind(context, this, note);
        }


    }

}
