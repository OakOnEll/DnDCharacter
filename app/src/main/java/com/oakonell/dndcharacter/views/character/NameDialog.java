package com.oakonell.dndcharacter.views.character;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.model.race.Race;
import com.oakonell.dndcharacter.utils.XmlUtils;
import com.oakonell.dndcharacter.views.BindableComponentViewHolder;

import org.solovyev.android.views.llm.LinearLayoutManager;
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
    public void onCharacterLoaded(@NonNull com.oakonell.dndcharacter.model.character.Character character) {
        super.onCharacterLoaded(character);
        name.setText(character.getName());

        String raceName = character.getRaceName();
        String subraceName = character.getSubRaceName();
        String raceTitle = subraceName;

        Element raceElem;
        List<Element> nameElems = Collections.emptyList();
        if (subraceName != null) {
            Race subrace = new Select().from(Race.class).where("name = ? ", subraceName).executeSingle();
            raceElem = XmlUtils.getDocument(subrace.getXml()).getDocumentElement();
            nameElems = XmlUtils.getChildElements(raceElem, "names");
        }
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

    private void addNameTypes(@NonNull List<Element> nameElems, @NonNull List<NameType> nameTypesList) {
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

    private static class NameViewHolder {
            //extends BindableComponentViewHolder<String, NameDialog, NameAdapter> {
        @NonNull
        private final TextView name;

        public NameViewHolder(@NonNull View itemView) {
            //super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
        }

        //@Override
        public void bind(NameDialog context, @NonNull NameAdapter adapter, final String info) {
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

    private static class NameAdapter extends
            BaseAdapter {
        //RecyclerView.Adapter<NameViewHolder> {
        private NameType type;
        private final NameDialog dialog;

        NameAdapter(NameDialog dialog, NameType type) {
            this.type = type;
            this.dialog = dialog;
        }

        public NameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(dialog.getMainActivity()).inflate(R.layout.race_name_item, parent, false);
            NameViewHolder holder = new NameViewHolder(view);
            return holder;
        }


        public void onBindViewHolder(@NonNull NameViewHolder holder, int position) {
            final String aName = type.names.get(position);
            holder.bind(dialog, this, aName);
        }


        public int getItemCount() {
            return type.names.size();
        }

        @Override
        public int getCount() {
            return getItemCount();
        }

        @Override
        public Object getItem(int position) {
            return type.names.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View theView;
            NameViewHolder holder;
            if (convertView != null) {
                theView = convertView;
                holder = (NameViewHolder) theView.getTag();
            } else {
                theView = LayoutInflater.from(dialog.getMainActivity()).inflate(R.layout.race_name_item, parent, false);
                holder = new NameViewHolder(theView);
                theView.setTag(holder);
            }
            onBindViewHolder(holder, position);
            return theView;
        }


    }

    private static class NameTypeViewHolder extends BindableComponentViewHolder<NameType, NameDialog, NameTypeAdapter> {
        @NonNull
        private final TextView name_type;
        @NonNull
        //private final RecyclerView names;
        private final GridView names;
        private NameAdapter nameAdapter;
        private static float textPx;
        private int widthPx;

        public NameTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            name_type = (TextView) itemView.findViewById(R.id.name_type);
            names = (GridView) itemView.findViewById(R.id.names);
            //names = (RecyclerView) itemView.findViewById(R.id.names);
        }

        @Override
        public void bind(@NonNull NameDialog context, @NonNull NameTypeAdapter adapter, final @NonNull NameType info) {
            name_type.setText(info.typeName);

            if (nameAdapter == null) {
                nameAdapter = new NameAdapter(adapter.dialog, info);
                names.setAdapter(nameAdapter);
//                names.setHasFixedSize(true);
//                final GridLayoutManager layoutManager = new GridLayoutManager(context.getActivity(), 1);
//                names.setLayoutManager(layoutManager);
//
//                names.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        final int numColumns = (int) Math.floor(names.getWidth() / (widthPx));
//                        int currentColumns = layoutManager.getSpanCount();
//                        if (currentColumns != numColumns) {
//                            layoutManager.setSpanCount(numColumns);
//                            names.requestLayout();
//                        }
//                    }
//
//                });

            } else {
                nameAdapter.type = info;
            }

            int maxLength = 0;
            String largest = "";
            for (String each : info.names) {
                if (each.length() > maxLength) {
                    maxLength = each.length();
                    largest = each;
                }
            }

            widthPx = measureTextWidth(context.getActivity(), largest) + (int) convertDpToPixel(10);
            names.setColumnWidth(widthPx);
            nameAdapter.notifyDataSetChanged();
        }

        public static float convertDpToPixel(@SuppressWarnings("SameParameterValue") float dp) {
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            float px = dp * (metrics.densityDpi / 160f);
            return Math.round(px);
        }

        public int measureTextWidth(Context context, String string) {
            // We need a fake parent
            FrameLayout buffer = new FrameLayout(context);
            TextView text = new TextView(context);
            int numChars = string.length();
            String sampleString = "";
            for (int i = 0; i < numChars; i++) {
                sampleString += "M";
            }
            text.setText(sampleString);
            android.widget.AbsListView.LayoutParams layoutParams = new android.widget.AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
            buffer.addView(text, layoutParams);

            text.forceLayout();
            text.measure(1000, 1000);

            int width = text.getMeasuredWidth();

            buffer.removeAllViews();

            return width;
        }
    }


    private static class NameTypeAdapter extends RecyclerView.Adapter<NameTypeViewHolder> {
        private final List<NameType> nameTypes;
        private final NameDialog dialog;

        NameTypeAdapter(NameDialog dialog, List<NameType> nameTypes) {
            this.dialog = dialog;
            this.nameTypes = nameTypes;
        }

        @NonNull
        @Override
        public NameTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(dialog.getMainActivity()).inflate(R.layout.race_name_type, parent, false);
            NameTypeViewHolder holder = new NameTypeViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull NameTypeViewHolder holder, int position) {
            final NameType nameType = nameTypes.get(position);
            holder.bind(dialog, this, nameType);
        }

        @Override
        public int getItemCount() {
            return nameTypes.size();
        }
    }

}
