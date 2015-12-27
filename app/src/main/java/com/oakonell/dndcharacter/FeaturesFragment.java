package com.oakonell.dndcharacter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oakonell.dndcharacter.model.Character;
import com.oakonell.dndcharacter.model.FeatureInfo;
import com.oakonell.dndcharacter.model.components.UseType;
import com.oakonell.dndcharacter.views.AbstractSheetFragment;
import com.oakonell.dndcharacter.views.ComponentLaunchHelper;

/**
 * Created by Rob on 10/26/2015.
 */
public class FeaturesFragment extends AbstractSheetFragment {

    private FeatureAdapter adapter;
    private RecyclerView gridView;

    public View onCreateTheView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.feature_sheet, container, false);

        superCreateViews(rootView);

        gridView = (RecyclerView) rootView.findViewById(R.id.features);

        return rootView;
    }

    @Override
    protected void updateViews(View rootView) {
        super.updateViews(rootView);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onCharacterLoaded(Character character) {
        super.onCharacterLoaded(character);

        adapter = new FeatureAdapter((MainActivity) this.getActivity(), getCharacter());
        gridView.setAdapter(adapter);
        // decide on 1 or 2 columns based on screen size
        int numColumns = getResources().getInteger(R.integer.feature_columns);
        gridView.setLayoutManager(new StaggeredGridLayoutManager(numColumns, StaggeredGridLayoutManager.VERTICAL));

        updateViews();
    }


    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView source;
        TextView shortDescription;

        public ViewGroup limited_uses_group;
        public ViewGroup use_group;
        TextView uses_label;
        public TextView uses_remaining;
        Button useButton;

        public ViewGroup pool_apply_group;
        public TextView pool_value;
        public TextView remaining_uses;
        public ImageButton pool_apply_button;
        public ImageButton pool_cancel_button;


        TextView refreshes_label;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class FeatureAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Character character;
        private MainActivity context;

        public FeatureAdapter(MainActivity context, Character character) {
            this.context = context;
            this.character = character;
        }

        @Override
        public int getItemCount() {
            if (character == null) return 0;
            return character.getFeatureInfos().size();
        }


        public FeatureInfo getItem(int position) {
            if (character == null) return null;
            return character.getFeatureInfos().get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.feature_layout, parent, false);
            ViewHolder holder = new ViewHolder(view);

            holder.name = (TextView) view.findViewById(R.id.name);
            holder.source = (TextView) view.findViewById(R.id.source);

            holder.limited_uses_group = (ViewGroup) view.findViewById(R.id.limited_uses_group);
            holder.uses_label = (TextView) view.findViewById(R.id.uses_label);
            holder.useButton = (Button) view.findViewById(R.id.use_button);
            holder.uses_remaining = (TextView) view.findViewById(R.id.remaining);
            holder.shortDescription = (TextView) view.findViewById(R.id.short_description);
            holder.refreshes_label = (TextView) view.findViewById(R.id.refreshes_label);
            holder.remaining_uses = (TextView) view.findViewById(R.id.remaining_uses);

            holder.pool_apply_group = (ViewGroup) view.findViewById(R.id.pool_apply_group);
            holder.use_group = (ViewGroup) view.findViewById(R.id.use_group);
            holder.pool_value = (TextView) view.findViewById(R.id.pool_value);
            holder.pool_apply_button = (ImageButton) view.findViewById(R.id.pool_apply_button);
            holder.pool_cancel_button = (ImageButton) view.findViewById(R.id.pool_cancel_button);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            final FeatureInfo info = getItem(position);
            viewHolder.name.setText(info.getName());
            viewHolder.source.setText(info.getSourceString());
            viewHolder.source.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ComponentLaunchHelper.editComponent(context, character, info.getSource(), false);
                }
            });
            boolean hasLimitedUses = info.hasLimitedUses();
            viewHolder.pool_apply_group.setVisibility(View.GONE);
            if (!hasLimitedUses) {
                viewHolder.limited_uses_group.setVisibility(View.GONE);
                viewHolder.use_group.setVisibility(View.GONE);
            } else {
                viewHolder.limited_uses_group.setVisibility(View.VISIBLE);
                int maxUses = info.evaluateMaxUses(character);
                final int usesRemaining = character.getUsesRemaining(info);

                viewHolder.uses_remaining.setText(usesRemaining + "/" + maxUses);

                viewHolder.useButton.setEnabled(usesRemaining > 0);
                if (info.getUseType() == UseType.PER_USE) {
                    viewHolder.use_group.setVisibility(View.VISIBLE);
                    viewHolder.uses_label.setText("Uses");
                    viewHolder.useButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            character.useFeature(info.getFeature(), 1);
                            notifyItemChanged(position);
                        }
                    });

                } else if (info.getUseType() == UseType.POOL) {
                    viewHolder.use_group.setVisibility(View.VISIBLE);
                    viewHolder.uses_label.setText("Pool");

                    viewHolder.remaining_uses.setText(" / " + usesRemaining);
                    viewHolder.useButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewHolder.pool_apply_group.setVisibility(View.VISIBLE);
                            viewHolder.pool_value.requestFocus();

                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(viewHolder.pool_value, InputMethodManager.SHOW_IMPLICIT);

                            viewHolder.refreshes_label.setVisibility(View.GONE);
                            viewHolder.use_group.setVisibility(View.GONE);
                        }
                    });

                    viewHolder.pool_cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewHolder.pool_apply_group.setVisibility(View.GONE);
                            viewHolder.pool_value.setText("");
                            viewHolder.refreshes_label.setVisibility(View.VISIBLE);
                            viewHolder.use_group.setVisibility(View.VISIBLE);

                        }
                    });
                    viewHolder.pool_apply_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String string = viewHolder.pool_value.getText().toString().trim();
                            if (string.length() == 0) {
                                viewHolder.pool_value.setError("Enter a number <= " + usesRemaining);
                                return;
                            }
                            int value;
                            try {
                                value = Integer.parseInt(string);
                            } catch (NumberFormatException e) {
                                viewHolder.pool_value.setError("Enter a number <= " + usesRemaining);
                                return;
                            }
                            if (value > usesRemaining) {
                                viewHolder.pool_value.setError("Enter a number <= " + usesRemaining);
                                return;
                            }
                            character.useFeature(info.getFeature(), value);
                            context.saveCharacter();
                            viewHolder.pool_apply_group.setVisibility(View.GONE);
                            viewHolder.pool_value.setText("");
                            viewHolder.refreshes_label.setVisibility(View.VISIBLE);
                            viewHolder.use_group.setVisibility(View.VISIBLE);
                            notifyItemChanged(position);
                        }
                    });

                    viewHolder.pool_value.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                viewHolder.pool_apply_button.performClick();
                                return true;
                            }
                            return false;
                        }
                    });

                    viewHolder.pool_value.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            viewHolder.pool_value.setError(null);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (s == null) return;
                            String string = s.toString();
                            if (string.length() == 0) return;
                            int value;
                            try {
                                value = Integer.parseInt(string);
                            } catch (NumberFormatException e) {
                                viewHolder.pool_value.setError("Enter a number <= " + usesRemaining);
                                return;
                            }
                            if (value > usesRemaining) {
                                viewHolder.pool_value.setError("Enter a number <= " + usesRemaining);
                            }
                        }
                    });
                }

                viewHolder.refreshes_label.setText("Refreshes on " + info.getFeature().getRefreshesOn().toString());

            }
            viewHolder.shortDescription.setText(info.getShortDescription());
        }


        public void setCharacter(Character character) {
            if (this.character != character) {
                this.character = character;
                notifyDataSetChanged();
            }
        }
    }
}