package com.oakonell.dndcharacter.views.character;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.character.*;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;

import org.solovyev.android.views.llm.LinearLayoutManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Rob on 2/7/2016.
 */
public class NameDialog extends AbstractCharacterDialogFragment {

    private EditText name;
    RecyclerView nameTypes;
    private NameTypeAdapter nameTypeAdapter;
    private View rootView;
    private View namesGroup;
    private TextView common_names_title;

    @NonNull
    public static NameDialog create() {
        return new NameDialog();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.choose_name);
    }

    @Override
    protected View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.name_dialog, container);

        name = (EditText) rootView.findViewById(R.id.name);
        nameTypes = (RecyclerView) rootView.findViewById(R.id.race_name_types);
        namesGroup = rootView.findViewById(R.id.race_names_group);
        common_names_title = (TextView) rootView.findViewById(R.id.common_names_title);
        return rootView;
    }

    @Override
    public void onCharacterLoaded(com.oakonell.dndcharacter.model.character.Character character) {
        super.onCharacterLoaded(character);
        name.setText(character.getName());

        String raceName = character.getRaceName();
        String subraceName = character.getSubRaceName();
        String raceTitle = subraceName;

        Race subrace = new Select().from(Race.class).where("name = ? ", subraceName).executeSingle();
        Element raceElem = XmlUtils.getDocument(subrace.getXml()).getDocumentElement();
        List<Element> nameElems = XmlUtils.getChildElements(raceElem, "names");
        if (nameElems.isEmpty()) {
            Race race = new Select().from(Race.class).where("name = ? ", raceName).executeSingle();
            raceElem = XmlUtils.getDocument(race.getXml()).getDocumentElement();
            nameElems = XmlUtils.getChildElements(raceElem, "names");
            raceTitle = raceName;
        }

        List<NameType> nameTypesList = new ArrayList<>();
        addNameTypes(nameElems, nameTypesList);
        if (nameTypesList.isEmpty()) {
            namesGroup.setVisibility(View.GONE);
        } else {
            common_names_title.setText(getString(R.string.racial_names_title, raceTitle));
            namesGroup.setVisibility(View.VISIBLE);
        }

        nameTypeAdapter = new NameTypeAdapter(this, nameTypesList);
        nameTypes.setAdapter(nameTypeAdapter);

        nameTypes.setHasFixedSize(false);
        nameTypes.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


        updateViews();
    }

    private void addNameTypes(List<Element> nameElems, List<NameType> nameTypesList) {
/*
    <names>
        <list typeLabel="Male Names">Alberich,Adrik</list>
        <list typeLabel="Female Names">Amber,Arten</list>
        <list typeLabel="Clan Names">Battlehammer,Balderk</list>
    </names>

 */
        if (nameElems.isEmpty()) return;

        final Element namesElem = nameElems.get(0);
        List<Element> lists = XmlUtils.getChildElements(namesElem, "list");
        for (Element eachList : lists) {
            NameType nameType = new NameType();
            nameType.typeName = eachList.getAttribute("typeLabel");
            List<String> names = new ArrayList<>();
            String namesString = eachList.getTextContent();
            final String[] namesArray = namesString.split(",");
            for (String eachName : namesArray) {
                final String trimmedName = eachName.trim();
                if (trimmedName.length() == 0) continue;
                names.add(trimmedName);
            }
            nameType.names = names;
            nameTypesList.add(nameType);
        }
    }

//    @Override
//    public void onCharacterChanged(com.oakonell.dndcharacter.model.character.Character character) {
//        super.onCharacterChanged(character);
//        updateViews();
//    }

    protected void updateViews() {
    }

    @Override
    protected boolean onDone() {
        String newName = name.getText().toString();
        if (newName == null || newName.trim().length() == 0) {
            name.setError(getString(R.string.choose_name));
            Animation shake = AnimationUtils.loadAnimation(getMainActivity(), R.anim.shake);
            name.startAnimation(shake);
            return false;
        }
        getCharacter().setName(newName);

        return super.onDone();
    }

    public static class NameType {
        String typeName;
        List<String> names;

    }

    private static class NameViewHolder extends BindableComponentViewHolder<String, NameDialog, NameAdapter> {
        private final TextView name;

        public NameViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
        }

        @Override
        public void bind(NameDialog context, NameAdapter adapter, final String info) {
            name.setText(info);
            // append this to the current name in the dialog...
            final EditText dialogNameTextView = adapter.dialog.name;
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nameToPaste = info;
                    int start = Math.max(dialogNameTextView.getSelectionStart(), 0);
                    int end = Math.max(dialogNameTextView.getSelectionEnd(), 0);
                    if (end == start && start != 0) {
                        // possibly prepend a space
                        if (dialogNameTextView.getText().charAt(Math.min(start, end) - 1) != ' ') {
                            nameToPaste = " " + info;
                        }
                    }
                    dialogNameTextView.getText().replace(Math.min(start, end), Math.max(start, end),
                            nameToPaste, 0, nameToPaste.length());

                }
            });
        }
    }

    private static class NameAdapter extends RecyclerView.Adapter<NameViewHolder> {
        private NameType type;
        private final NameDialog dialog;

        NameAdapter(NameDialog dialog, NameType type) {
            this.type = type;
            this.dialog = dialog;
        }

        @Override
        public NameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(dialog.getMainActivity()).inflate(R.layout.race_name_item, parent, false);
            NameViewHolder holder = new NameViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(NameViewHolder holder, int position) {
            final String aName = type.names.get(position);
            holder.bind(dialog, this, aName);
        }

        @Override
        public int getItemCount() {
            return type.names.size();
        }
    }

    private static class NameTypeViewHolder extends BindableComponentViewHolder<NameType, NameDialog, NameTypeAdapter> {
        private final TextView name_type;
        private final RecyclerView names;
        private NameAdapter nameAdapter;
        private static float textPx;

        public NameTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            name_type = (TextView) itemView.findViewById(R.id.name_type);
            names = (RecyclerView) itemView.findViewById(R.id.names);
        }

        @Override
        public void bind(NameDialog context, NameTypeAdapter adapter, NameType info) {
            name_type.setText(info.typeName);

            if (nameAdapter == null) {
                nameAdapter = new NameAdapter(adapter.dialog, info);
                names.setAdapter(nameAdapter);

                names.setHasFixedSize(false);
            } else {
                nameAdapter.type = info;
                nameAdapter.notifyDataSetChanged();
            }


            int maxLength = 0;
            int mTextWidth;
            int maxWidth = 0;
            Paint mTextPaint = new Paint();

//                mPaint.setAntiAlias(true);
//            mPaint.setStrokeWidth(5);
//            mPaint.setStrokeCap(Paint.Cap.ROUND);
//            mPaint.setTextSize(64);
//            mPaint.setTypeface(Typeface.create(Typeface.SERIF,
//                                               Typeface.ITALIC));

            for (String each : info.names) {
                // Now lets calculate the size of the text
                Rect textBounds = new Rect();
                mTextPaint.getTextBounds(each, 0, each.length(), textBounds);
                int width = (int) mTextPaint.measureText(each); // Use measureText to calculate width
                if (width > maxWidth) {
                    maxWidth = width;
                }
                if (each.length() > maxLength) {
                    maxLength = each.length();
                }
            }
            mTextPaint = null;
/*
            //Display display = context.getActivity().getWindowManager().getDefaultDisplay();
            WindowManager wm = (WindowManager) context.getActivity().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;


//            int dialogWidth = context.rootView.getWidth();
            int numColumns = width / maxWidth;
//
//
//            names.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            int width = names.getMeasuredWidth();
            names.setLayoutManager(new com.oakonell.dndcharacter.views.GridLayoutManager(context.getActivity(), numColumns));
*/

            WindowManager wm = (WindowManager) context.getActivity().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;

            if (textPx == 0) {
                textPx = new TextView(context.getActivity()).getTextSize();
            }
            int numColumns = (int) (0.5 * width / (maxLength * textPx)) - 1;
            if (numColumns < 1) numColumns = 1;

            names.setLayoutManager(new com.oakonell.dndcharacter.views.GridLayoutManager(context.getActivity(), numColumns));

        }
    }

    private static class NameTypeAdapter extends RecyclerView.Adapter<NameTypeViewHolder> {
        private final List<NameType> nameTypes;
        private final NameDialog dialog;

        NameTypeAdapter(NameDialog dialog, List<NameType> nameTypes) {
            this.dialog = dialog;
            this.nameTypes = nameTypes;
        }

        @Override
        public NameTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(dialog.getMainActivity()).inflate(R.layout.race_name_type, parent, false);
            NameTypeViewHolder holder = new NameTypeViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(NameTypeViewHolder holder, int position) {
            final NameType nameType = nameTypes.get(position);
            holder.bind(dialog, this, nameType);
        }

        @Override
        public int getItemCount() {
            return nameTypes.size();
        }
    }

}
