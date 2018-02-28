package org.socratic.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import org.socratic.android.R;
import org.socratic.android.databinding.ActivitySearchInterstitialBinding;
import org.socratic.android.contract.SearchInterstitialContract;

/**
 * @date 2017-03-13
 */
public class SearchInterstitialActivity extends BaseActivity<ActivitySearchInterstitialBinding, SearchInterstitialContract.ViewModel>
    implements SearchInterstitialContract.View {

    private static final String TAG = SearchInterstitialActivity.class.getSimpleName();
    private static final long TIMEOUT = 3000L;
    private static final String EXTRA_IS_OCR = "@eio";

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setAndBindContentView(savedInstanceState, R.layout.activity_search_interstitial);

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToResults();
            }
        });

        mHandler = new Handler();
    }

    @Override
    public void onPause() {
        super.onPause();

        mHandler.removeCallbacks(mRunnableTimeout);
    }

    @Override
    public void onResume() {
        super.onResume();

        mHandler.postDelayed(mRunnableTimeout, TIMEOUT);
    }

    private void goToResults() {
        Intent intent = new Intent(SearchInterstitialActivity.this, ResultsActivity.class);

        //first queries should always be OCR
        intent.putExtra(EXTRA_IS_OCR, true);

        startActivity(intent);
        finish();
    }

    private Runnable mRunnableTimeout = new Runnable() {
        @Override
        public void run() {
            goToResults();
        }
    };
}