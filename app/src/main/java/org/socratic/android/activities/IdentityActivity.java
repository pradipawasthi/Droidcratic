package org.socratic.android.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import org.socratic.android.R;
import org.socratic.android.contract.IdentityContract;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.analytics.OnboardingAnalytics;
import org.socratic.android.databinding.ActivityIdentityBinding;

import javax.inject.Inject;

import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class IdentityActivity extends BaseActivity<ActivityIdentityBinding, IdentityContract.ViewModel>
    implements IdentityContract.View {

    @Inject SharedPreferences sharedPreferences;
    @Inject AnalyticsManager analyticsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setAndBindContentView(savedInstanceState, R.layout.activity_identity);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("atStart", false);
        editor.apply();

        initializeUI();

        binding.checkboxAge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.btnContinue.setEnabled(true);
                    binding.btnContinue.setBackground(ContextCompat.getDrawable(IdentityActivity.this, R.drawable.ripple_strong_background));
                } else {
                    binding.btnContinue.setEnabled(false);
                    binding.btnContinue.setBackground(ContextCompat.getDrawable(IdentityActivity.this, R.drawable.purple_btn_disabled));
                }
            }
        });

        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.editName.getText().toString().trim();

                if (!name.isEmpty()) {

                    viewModel.sendIdentity(name);
                }
            }
        });
    }

    private void initializeUI() {
        binding.checkboxAge.setChecked(false);
        binding.btnContinue.setEnabled(false);
        binding.btnContinue.setBackground(ContextCompat.getDrawable(IdentityActivity.this, R.drawable.purple_btn_disabled));        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/cerapro-regular.otf");
        binding.checkboxAge.setTypeface(font);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        binding.konfettiView.build()
                .addColors(ContextCompat.getColor(IdentityActivity.this, R.color.confetti_blue),
                        ContextCompat.getColor(IdentityActivity.this, R.color.confetti_green),
                        ContextCompat.getColor(IdentityActivity.this, R.color.confetti_orange),
                        ContextCompat.getColor(IdentityActivity.this, R.color.confetti_red),
                        ContextCompat.getColor(IdentityActivity.this, R.color.confetti_yellow),
                        Color.WHITE)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, binding.konfettiView.getWidth() + 50f, -50f, -50f)
                .stream(300, 5000L);
    }

    @Override
    public void showCreatePersonErrorDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View alertDialogView = inflater.inflate(R.layout.dialog_create_person_error, null);

        Button okBtn = alertDialogView.findViewById(R.id.ok_btn);

        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(alertDialogView);
        alertDialog.show();
    }

    @Override
    public void navigateToPermissionsScreen() {
        Intent intent = new Intent(IdentityActivity.this, SplashPermissionsActivity.class);
        startActivity(intent);

        analyticsManager.track(
                new OnboardingAnalytics.ProfileCreateContinue());

        finish();
    }
}
