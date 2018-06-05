package org.socratic.android.activities;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.socratic.android.BuildConfig;
import org.socratic.android.R;

import org.socratic.android.contract.CameraContract;
import org.socratic.android.events.NavTouchEvent;
import org.socratic.android.views.VerticalSlidingDrawer;
import org.socratic.android.adapter.InputControllerPagerAdapter;
import org.socratic.android.adapter.InputPagerAdapter;

import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.databinding.ActivityCameraBinding;
import org.socratic.android.events.TouchEvent;
import org.socratic.android.fragments.TextSearchFragment;
import org.socratic.android.globals.PhotoManager;
import org.socratic.android.util.CameraHelper;
import org.socratic.android.util.DimenUtil;
import org.socratic.android.util.MultiLog;
import org.socratic.android.util.PermissionsHelper;
import org.socratic.android.util.Util;
import org.socratic.android.views.CropWidgetView;
import org.socratic.android.views.InvitationsView;
import org.socratic.android.views.PagerClickTitleStrip;
import org.socratic.android.views.NavTabsView;

import javax.inject.Inject;

import static org.socratic.android.adapter.InputPagerAdapter.CAMERA_SCREEN;
import static org.socratic.android.adapter.InputPagerAdapter.TEXT_SEARCH_SCREEN;

/**
 * @date 2017-02-02
 */
public class CameraActivity extends BaseActivity<ActivityCameraBinding, CameraContract.ViewModel>
        implements CameraContract.View, InvitationsView.InvitationsClickListener,
        VerticalSlidingDrawer.OnDrawerOpenListener,
        VerticalSlidingDrawer.OnDrawerScrollListener,
        VerticalSlidingDrawer.OnDrawerCloseListener,
        VerticalSlidingDrawer.OnDrawerMoveListener {

    @Inject PhotoManager photoManager;
    @Inject SharedPreferences sharedPreferences;
    @Inject AnalyticsManager analyticsManager;

    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final int MAX_PHOTO_CAPTURE_SIZE = 2000;
    private static final int SHOW_ADD_FRIENDS_NUM = 6;

    RelativeLayout baseView;
    RelativeLayout viewContainer;
    ImageButton btnTakePicture;
    View fillerView;
    RelativeLayout noConnectivityContainer;
    ViewPager inputViewPager;
    VerticalSlidingDrawer invitationsDrawer;
    NavTabsView navControlView;
    ImageView handleImage;
    PagerClickTitleStrip inputTitleStrip;
    ViewPager inputController;
    TextView addFriendsFTUE;
    View anchorView;

    CameraHelper mCameraHelper;
    TextSearchFragment textSearchFragment;

    private int mTouchSlop;
    private int currentInputPage;
    private int scale;
    private int startX, startY;
    private boolean firstTimeUser;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory.
     */
    private InputPagerAdapter inputPagerAdapter;
    private InputControllerPagerAdapter inputControllerPagerAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setAndBindContentView(savedInstanceState, R.layout.activity_camera);
        setRegisterEventBus();

        MultiLog.d(TAG, "Camera screen created.");

        baseView = binding.baseView;
        viewContainer = binding.viewContainer;
        //temporarily using findViewById, will have to figure out DataBinding solution
        btnTakePicture = (ImageButton) binding.navControlView.findViewById(R.id.circle_btn);
        fillerView = binding.fillerView;
        inputViewPager = binding.inputViewPager;
        noConnectivityContainer = binding.noConnectivityContainer;
        invitationsDrawer = binding.invitationsDrawer;
        navControlView = binding.navControlView;
        handleImage = binding.handleImage;
        inputTitleStrip = binding.inputTitleStrip;
        inputController = binding.inputController;
        addFriendsFTUE = binding.friendsFtueText;
        anchorView = binding.anchorView;

        Bundle bundle = getIntent().getExtras();

        btnTakePicture.setEnabled(true);
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTakePicture();
            }
        });

        binding.cropWidget.setMode(CropWidgetView.MODE_CAPTURE);

        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();

        mCameraHelper = new CameraHelper();
        mCameraHelper.onCreate(this, binding.cameraPreviewContainer, MAX_PHOTO_CAPTURE_SIZE, mPictureListener);

        scale = (int) DimenUtil.getDisplayScale(this);

        firstTimeUser = sharedPreferences.getBoolean("firstTime", true);

        initializeInputMethods();
        initializeInvitationsDrawer();

        binding.inAppMessageView.setup(TAG);

        setupOnboardTips();
    }

    private void initializeInputMethods() {
        // create messagesListAdapter that returns placeholder (camera) and text input sections
        inputPagerAdapter = new InputPagerAdapter(getSupportFragmentManager());

        inputViewPager.setAdapter(inputPagerAdapter);

        //synchronize with input controller view pager
        inputViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                inputController.scrollTo(inputViewPager.getScrollX(), inputController.getScrollY());
            }

            @Override
            public void onPageSelected(int position) {
                currentInputPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    inputController.setCurrentItem(inputViewPager.getCurrentItem(), false);
                    setupPage(currentInputPage);
                }
            }
        });

        initializeInputMethodController();
    }

    private void initializeInvitationsDrawer() {
        //set up add friends ftue to disappear if invitations drawer is swiped
        invitationsDrawer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EventBus.getDefault().post(new TouchEvent(event));
                EventBus.getDefault().post(new NavTouchEvent(event));

                if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    addFriendsFTUE.setVisibility(View.GONE);
                }
                return false;
            }
        });

        //pass in widget views to be used in VerticalSlidingDrawer
        invitationsDrawer.setInputControlView(inputController);
        invitationsDrawer.setNavControlView(navControlView);

        //set up click listener for the content
        InvitationsView invitationsView = (InvitationsView) invitationsDrawer.getContent();
        invitationsView.setInvitationsClickListener(this);

        invitationsDrawer.setOnDrawerOpenListener(this);
        invitationsDrawer.setOnDrawerScrollListener(this);
        invitationsDrawer.setOnDrawerCloseListener(this);
        invitationsDrawer.setOnDrawerMoveListener(this);
    }

    private void initializeInputMethodController() {

        inputTitleStrip.getNextTitle().setAlpha(0.5f);

        //create messagesListAdapter that returns placeholder views
        inputControllerPagerAdapter = new InputControllerPagerAdapter(getSupportFragmentManager());

        inputController.setAdapter(inputControllerPagerAdapter);

        //synchronize with input method view pager
        inputController.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                inputViewPager.scrollTo(inputController.getScrollX(), inputViewPager.getScrollY());

                if (position == CAMERA_SCREEN) {
                    invitationsDrawer.setVisibility(View.VISIBLE);
                } else {
                    invitationsDrawer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                currentInputPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    inputViewPager.setCurrentItem(inputController.getCurrentItem(), false);
                    setupPage(currentInputPage);
                }
            }
        });

        //setup inputController to pass press events down to input view pager
        inputController.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EventBus.getDefault().post(new TouchEvent(event));

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    inputViewPager.dispatchTouchEvent(event);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    inputViewPager.dispatchTouchEvent(event);
                }
                return false;
            }
        });
    }

    private void setupPage(int position) {
        if (position == CAMERA_SCREEN) {
            binding.cropWidget.setFTUEMode(CropWidgetView.MODE_SHOW_FTUE);
            expandUI();

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inputViewPager.getWindowToken(), 0);

            navControlView.setVisibility(View.VISIBLE);
            invitationsDrawer.setVisibility(View.VISIBLE);
            viewContainer.setBackgroundColor(Color.TRANSPARENT);
            Util.hideStatusBar(this);

            if (textSearchFragment != null) {
                textSearchFragment.getTextSearchView().clearFocus();
            }
        } else if (position == TEXT_SEARCH_SCREEN) {
            binding.cropWidget.setFTUEMode(CropWidgetView.MODE_HIDE_FTUE);

            if (textSearchFragment != null) {
                textSearchFragment.setup();
            } else {
                textSearchFragment = (TextSearchFragment) inputPagerAdapter.instantiateItem(inputViewPager, inputViewPager.getCurrentItem());
                textSearchFragment.setup();
            }

            navControlView.setVisibility(View.GONE);
            invitationsDrawer.setVisibility(View.GONE);
            viewContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.translucent_black));
            addFriendsFTUE.setVisibility(View.GONE);
        }
    }

    private void setupOnboardTips() {
        //set up onboard tips
        boolean firstTimeUser = sharedPreferences.getBoolean("firstTime", true);
        int successfulQueries = sharedPreferences.getInt("successfulQueries", 0);
        boolean addFriendsShown = sharedPreferences.getBoolean("addFriendsShown", false);
        if (firstTimeUser) {
            inputPagerAdapter.setLocked(true);
            handleImage.setVisibility(View.GONE);
            inputController.setVisibility(View.GONE);
        }

        if (successfulQueries >= SHOW_ADD_FRIENDS_NUM && !addFriendsShown) {
            addFriendsFTUE.setVisibility(View.VISIBLE);
            sharedPreferences.edit().putBoolean("addFriendsShown", true).apply();
        }
    }

    public void compressUI() {

        if (fillerView.getHeight() == 0) {
            int keyboardHeight = 0;

            if (scale <= DisplayMetrics.DENSITY_260) {
                keyboardHeight = scale + scale / 2;
            } else {
                keyboardHeight = viewContainer.getHeight() / 2;
            }

            ValueAnimator viewAnimator = ValueAnimator.ofInt(viewContainer.getPaddingBottom(), keyboardHeight);
            viewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    viewContainer.setPadding(0, 0, 0, (Integer) animation.getAnimatedValue());
                }
            });

            ValueAnimator fillerAnimator = ValueAnimator.ofInt(0, keyboardHeight);
            fillerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fillerView.getLayoutParams();
                    params.height = (Integer) animation.getAnimatedValue();

                    fillerView.setLayoutParams(params);
                }
            });

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(viewAnimator, fillerAnimator);
            animatorSet.setDuration(200);
            animatorSet.start();

            inputTitleStrip.getCurrentTitle().setAlpha(1);
            inputTitleStrip.getPreviousTitle().setAlpha(0.5f);
            inputTitleStrip.setVisibility(View.INVISIBLE);
        }
    }

    public void expandUI() {

        if (fillerView.getHeight() != 0) {

            int keyboardHeight = 0;

            if (scale <= DisplayMetrics.DENSITY_260) {
                keyboardHeight = scale + scale / 2;
            } else {
                keyboardHeight = viewContainer.getHeight() / 2;
            }

            ValueAnimator viewAnimator = ValueAnimator.ofInt(viewContainer.getPaddingBottom(), 0);
            viewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    viewContainer.setPadding(0, 0, 0, (Integer) animation.getAnimatedValue());
                }
            });

            ValueAnimator fillerAnimator = ValueAnimator.ofInt(keyboardHeight, 0);
            fillerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fillerView.getLayoutParams();
                    params.height = (Integer) animation.getAnimatedValue();

                    fillerView.setLayoutParams(params);
                }
            });

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(viewAnimator, fillerAnimator);
            animatorSet.setDuration(200);
            animatorSet.start();

            inputTitleStrip.getCurrentTitle().setAlpha(1);
            inputTitleStrip.getNextTitle().setAlpha(0.5f);
            inputTitleStrip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        MultiLog.d(TAG, "Camera screen resumed.");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mConnectivityReceiver, intentFilter);

        // Check permissions each time, user may have removed them
        // in system settings while we were backgrounded.
        if (PermissionsHelper.hasCameraPermissions(this)) {
            photoManager.cancel();
            mCameraHelper.onResume();

        } else {

            MultiLog.d(TAG, "Camera permission missing on resume, sending user back to splash "
                    + "permissions screen.");

            Intent intent = new Intent(this, DefaultPermissionActivity.class);


            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Util.hideStatusBar(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        MultiLog.d(TAG, "Camera screen paused.");

        mCameraHelper.onPause();
    }

    private void startTakePicture() {
        MultiLog.d(TAG, "Start take picture.");

        btnTakePicture.setEnabled(false);
        mCameraHelper.takePicture();
    }

    private CameraHelper.PictureListener mPictureListener = new CameraHelper.PictureListener() {
        @Override
        public void pictureTaken(Bitmap bitmap) {

            MultiLog.d(TAG, "Photo was taken.");

            if (bitmap == null) {
                Crashlytics.logException(new Throwable("Photo is null."));
                return;
            } else if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
                Crashlytics.logException(new Throwable("Photo size is invalid: "
                        + bitmap.getWidth() + " x " + bitmap.getHeight()));
                return;
            }

            // Crop the image to the max crop rect and start uploading immediately.
            photoManager.setPhoto(CameraActivity.this, bitmap,
                    binding.cropWidget.getWidth(), binding.cropWidget.getHeight(),
                    mCameraHelper.getPreviewTransformInfo(),    
                    BuildConfig.WRITE_DEBUG_IMAGE_CAPTURE_TO_DISK);

            // Send user to new activity where they can manipulate the crop rectangle.
            Intent intent = new Intent(CameraActivity.this, CropperActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }

        @Override
        public void onCameraReady() {
            MultiLog.d(TAG, "Camera is ready.");
            btnTakePicture.setEnabled(true);
        }

        @Override
        public void onCameraBusy() {
            MultiLog.d(TAG, "Camera is busy.");
            btnTakePicture.setEnabled(false);
        }

        @Override
        public void onCameraFocused() {
            MultiLog.d(TAG, "Autofocus complete.");
        }

        @Override
        public void onCameraError() {
            MultiLog.e(TAG, "Camera error.");
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TouchEvent event) {
        MotionEvent motionEvent = event.getMotionEvent();

        if (!invitationsDrawer.isOpened()) {

            //if invitations pull down is NOT visible then touch events should go to tap camera focus
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) motionEvent.getX();
                    startY = (int) motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    int endX = (int) motionEvent.getX();
                    int endY = (int) motionEvent.getY();
                    int dX = Math.abs(endX - startX);
                    int dY = Math.abs(endY - startY);

                    if (Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2)) <= mTouchSlop) {
                        mCameraHelper.tapForFocus(endX, endY);
                    }
                    break;
            }
        } else {
            //if invitations pull down is visible then touch events
            // should go to the content view inside invitations
            invitationsDrawer.getContent().dispatchTouchEvent(motionEvent);
        }
    }

    private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkConnectivity();
        }
    };

    private void checkConnectivity() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Log.e(TAG, "Network state change, isConnected: " + isConnected);

        noConnectivityContainer.setVisibility(isConnected ? View.GONE : View.VISIBLE);
    }

    private void hideElements() {
        navControlView.setVisibility(View.GONE);
        inputController.setVisibility(View.GONE);
    }

    private void showElements() {
        if (!invitationsDrawer.isOpened()) {
            navControlView.setVisibility(View.VISIBLE);
        }

        if (!firstTimeUser && !invitationsDrawer.isOpened()) {
            inputController.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Click events for invitations
     */

    @Override
    public void onFacebookMessengerClick(String facebookShareURL) {
        Intent intent= new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message_copy)+" "+ facebookShareURL);
        intent.setType("text/plain");
        intent.setPackage("com.facebook.orca");

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.facebook.orca"));
            startActivity(intent);
        }
    }

    @Override
    public void onCopyLinkClick(String copyLinkShareURL) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", getString(R.string.share_message_copy)+" " + copyLinkShareURL);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getApplicationContext(), getString(R.string.link_copied_to_clipboard), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessagesClick(String messagesShareURL) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android-dir/mms-sms");
        intent.putExtra("sms_body",String.format(getString(R.string.share_message_copy)+" "+messagesShareURL));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Uri uri = Uri.parse("smsto:");
            intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body",String.format(getString(R.string.share_message_copy)+" "+messagesShareURL));
            startActivity(intent);
        }

    }

    @Override
    public void onMoreClick(String moreShareURL) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.share_message_copy)+" " + moreShareURL));
        intent.setType("text/plain");

        startActivity(intent);
    }

    @Override
    public void onSettingsClick() {
        Intent intent = new Intent(CameraActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDrawerOpened() {
        analyticsManager.track(
                new AnalyticsManager.InvitationsPaneOpened());

        hideElements();
    }

    @Override
    public void onScrollStarted() {
        hideElements();
    }

    @Override
    public void onScrollEnded() {
        //when invitations drawer is done moving, unlock navigation view pager
        showElements();
    }

    @Override
    public void onDrawerClosed() {
        showElements();
    }

    @Override
    public void onDrawerExpand() {
        showElements();
    }

    @Override
    public void onDrawerCollapse() {
        hideElements();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance() {
            PlaceholderFragment fragment = new PlaceholderFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
            return rootView;
        }
    }
}