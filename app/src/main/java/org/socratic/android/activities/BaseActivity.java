package org.socratic.android.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Camera;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.RemoteViews;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.socratic.android.R;

import org.socratic.android.BR;
import org.socratic.android.contract.ViewAdapter;
import org.socratic.android.SocraticApp;
import org.socratic.android.api.model.Channel;
import org.socratic.android.api.model.Message;
import org.socratic.android.api.model.Person;
import org.socratic.android.dagger.components.ActivityComponent;
import org.socratic.android.dagger.components.DaggerActivityComponent;
import org.socratic.android.dagger.modules.ActivityModule;
import org.socratic.android.events.MessageReceivedEvent;
import org.socratic.android.viewmodel.NoOpViewModel;
import org.socratic.android.viewmodel.ViewModelAdapter;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * @date 2017-02-02
 *
 * Base class for Activities and corresponding ViewModels
 * that provides the binding and view model to child class.
 *
 * Handles lifecycle events for view model.
 *
 * All children classes must call setAndBindContentView() in onCreate()
 * and inject with the ActivityComponent
 */
public abstract class BaseActivity<T extends ViewDataBinding, V extends ViewModelAdapter> extends AppCompatActivity {

    protected T binding;
    @Inject protected V viewModel;
    @Inject
    SharedPreferences sharedPreferences;

    private ActivityComponent activityComponent;
    private boolean registerEventBus;

    @Override
    @CallSuper
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(viewModel != null) { viewModel.saveInstanceState(outState); }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (registerEventBus) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {

        if (registerEventBus) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        super.onDestroy();

        if(viewModel != null) { viewModel.detachView(); }
        binding = null;
        viewModel = null;
        activityComponent = null;
    }

    public final ActivityComponent activityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .appComponent(SocraticApp.getAppComponent())
                    .build();
        }
        return activityComponent;
    }

    protected final void setAndBindContentView(@Nullable Bundle savedInstanceState, @LayoutRes int layoutResID) {
        if(viewModel == null) { throw new IllegalStateException("viewModel not found"); }
        binding = DataBindingUtil.setContentView(this, layoutResID);
        binding.setVariable(BR.vm, viewModel);

        try {
            //noinspection unchecked
            viewModel.attachView((ViewAdapter) this, savedInstanceState);
        } catch(ClassCastException e) {
            if (!(viewModel instanceof NoOpViewModel)) {
                throw new RuntimeException(getClass().getSimpleName() + " does not implement ViewAdapter subclass  (" + viewModel.getClass().getSimpleName() + ")");
            }
        }
    }

    protected void setRegisterEventBus() {
        this.registerEventBus = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getFlags() == Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) {
            overridePendingTransition(0, R.anim.in_app_message_web_view_slide_down);
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // For Calligraphy.
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageReceivedEvent event) {
        boolean firstTimeUser = sharedPreferences.getBoolean("firstTime", true);

        if (this instanceof ChatDetailActivity || firstTimeUser) {
            return;
        } else {
            Message message = event.getMessage();
            Person person = event.getPerson();
            Channel channel = event.getChannel();

            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
            contentView.setTextViewText(R.id.title, person.getDisplayName());
            contentView.setTextViewText(R.id.text, "sent you a message");

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setDefaults(NotificationCompat.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                            .setSmallIcon(R.drawable.cbo)
                            .setContent(contentView)
                            .setContentTitle(person.getDisplayName())
                            .setContentText("Sent a message");

            Intent resultIntent = new Intent(this, ChatDetailActivity.class);
            resultIntent.putExtra("open_chat_fragment", true);
            resultIntent.putExtra("chat_name", person.getDisplayName());
            resultIntent.putExtra("channel_id", channel.getChannelID());
            resultIntent.putExtra("chat_state", channel.getState());

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            int mNotificationId = 001;
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mBuilder.setContentIntent(resultPendingIntent);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
    }
}

