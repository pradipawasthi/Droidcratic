package org.socratic.android.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import org.socratic.android.R;
import org.socratic.android.contract.ViewAdapter;
import org.socratic.android.databinding.ActivitySettingsBinding;
import org.socratic.android.viewmodel.NoOpViewModel;

public class SettingsActivity extends BaseActivity<ActivitySettingsBinding, NoOpViewModel> implements ViewAdapter {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setAndBindContentView(savedInstanceState, R.layout.activity_settings);

        binding.toolbar.setNavigationIcon(R.drawable.back_arrow);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.acknowledgementsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLicensesAlertDialog();
            }
        });
    }

    private void displayLicensesAlertDialog() {
        WebView view = (WebView) LayoutInflater.from(this).inflate(R.layout.dialog_licenses, null);
        view.loadUrl("file:///android_asset/licenses.html");
        new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(getString(R.string.licenses))
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
