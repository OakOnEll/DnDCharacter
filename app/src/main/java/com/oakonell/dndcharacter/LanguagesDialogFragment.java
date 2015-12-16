package com.oakonell.dndcharacter;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.BaseCharacterComponent;
import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.views.ComponentLaunchHelper;

import java.util.List;

/**
 * Created by Rob on 11/30/2015.
 */
public class LanguagesDialogFragment extends DialogFragment {
    private MainActivity mainActivity;

    public static LanguagesDialogFragment create(MainActivity activity) {
        LanguagesDialogFragment frag = new LanguagesDialogFragment();
        frag.setMainActivity(activity);
        return frag;
    }

    private void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.language_proficiency_dialog, container);

        Button done = (Button) view.findViewById(R.id.done);
        ListView listView = (ListView) view.findViewById(R.id.list);

        final List<Character.LanguageAndReason> languageAndReasons = mainActivity.character.deriveLanguages();


        ListAdapter adapter = new LanguagesReasonAdapter(this, languageAndReasons);
        listView.setAdapter(adapter);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }


    private static class ViewHolder {
        TextView value;
        TextView source;
    }

    public static class LanguagesReasonAdapter extends BaseAdapter {
        private List<Character.LanguageAndReason> list;
        LanguagesDialogFragment fragment;

        LanguagesReasonAdapter(LanguagesDialogFragment fragment, List<Character.LanguageAndReason> list) {
            this.list = list;
            this.fragment = fragment;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Character.LanguageAndReason getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder holder;
            if (view != null) {
                holder = (ViewHolder) view.getTag();
            } else {
                view = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.skill_prof_row, parent, false);
                holder = new ViewHolder();
                holder.value = (TextView) view.findViewById(R.id.value);
                holder.source = (TextView) view.findViewById(R.id.source);
                view.setTag(holder);
            }

            Character.LanguageAndReason item = getItem(position);
            String language = item.getLanguage();

            holder.value.setText(language);
            final BaseCharacterComponent source = item.getSource();
            if (source == null) {
                // a base stat
                holder.source.setText("Base Stat");
            } else {
                holder.source.setText(source.getSourceString());
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Character character = fragment.mainActivity.character;
                    if (source == null) {
                        // ?? probably not possible
                    } else {
                        ComponentLaunchHelper.editComponent((MainActivity) fragment.getActivity(), character, source);
                    }
                }
            });

            return view;
        }
    }


}
