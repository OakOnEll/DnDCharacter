package com.oakonell.dndcharacter.views;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.oakonell.dndcharacter.R;
import com.oakonell.dndcharacter.views.character.AbstractDialogFragment;

/**
 * Created by Rob on 3/10/2016.
 */
public class AboutDialog extends AbstractDialogFragment {

    @NonNull
    public static AboutDialog create() {
        return new AboutDialog();
    }


    @Nullable
    @Override
    protected String getTitle() {
        return getString(R.string.about_app);
    }

    @Nullable
    @Override
    protected View onCreateTheView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_dialog, container);

        TextView versiontext = (TextView) view.findViewById(R.id.version);
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    getActivity().getPackageName(), 0);
            String version = info.versionName;

            versiontext.setText(version);
        } catch (Exception e) {
            versiontext.setText(e.getLocalizedMessage());
        }

        WebView webView = (WebView) view.findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/about.html");

        return view;

    }
}
