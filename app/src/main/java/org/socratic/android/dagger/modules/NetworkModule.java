package org.socratic.android.dagger.modules;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.socratic.android.BuildConfig;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.api.BaseRequest;
import org.socratic.android.dagger.qualifiers.AppContext;
import org.socratic.android.dagger.scopes.PerApplication;

import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.globals.BingPredictionsManager;
import org.socratic.android.globals.ChatListManager;
import org.socratic.android.globals.HttpManager;
import org.socratic.android.globals.InAppMessageManager;
import org.socratic.android.globals.InitManager;
import org.socratic.android.globals.MessagesManager;
import org.socratic.android.globals.OcrSearchManager;
import org.socratic.android.globals.TextSearchManager;
import org.socratic.android.globals.TokenManager;
import org.socratic.android.storage.InstallPref;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by williamxu on 7/10/17.
 */

@Module
public class NetworkModule {

    private static final int CONNECT_TIMEOUT = 15000;
    private static final int MAX_TRIES = 3;
    private static final int RETRY_TIME = 250;

    @Provides
    @PerApplication
    InAppMessageManager provideInAppMessageManager(HttpManager httpManager) {
        return new InAppMessageManager(httpManager);
    }

    @Provides
    @PerApplication
    InitManager provideInitManager(HttpManager httpManager) {
        return new InitManager(httpManager);
    }

    @Provides
    @PerApplication
    ChatListManager provideChatListManager(HttpManager httpManager) {
        return new ChatListManager(httpManager);
    }

    @Provides
    @PerApplication
    TokenManager provideTokenManager(HttpManager httpManager) {
        return new TokenManager(httpManager);
    }

    @Provides
    @PerApplication
    MessagesManager provideMessagesManager(HttpManager httpManager) {
        return new MessagesManager(httpManager);
    }

    @Provides
    @PerApplication
    BingPredictionsManager provideBingPredictionsManager(HttpManager httpManager) {
        return new BingPredictionsManager(httpManager);
    }

    @Provides
    @PerApplication
    TextSearchManager provideTextSearchManager(HttpManager httpManager, AnalyticsManager analyticsManager) {
        return new TextSearchManager(httpManager, analyticsManager);
    }

    @Provides
    @PerApplication
    OcrSearchManager provideOcrSearchManager(HttpManager httpManager, AnalyticsManager analyticsManager) {
        return new OcrSearchManager(httpManager, analyticsManager);
    }

    @Provides
    @PerApplication
    HttpManager provideHttpManager(@AppContext Context context, OkHttpClient okHttpClient, InstallPref installPref) {
        return new HttpManager(context, okHttpClient, installPref);
    }

    @Provides
    @PerApplication
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Provides
    @PerApplication
    OkHttpClient provideOkHttpClient(Interceptor retryInterceptor) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.retryOnConnectionFailure(false);
        httpClientBuilder.addInterceptor(retryInterceptor);

        //only setup logging interceptor in debug (prevent leak of sensitive info in production)
        if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggingInterceptor);
        }

        return httpClientBuilder
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }

    @Provides
    @PerApplication
    Interceptor provideRetryInterceptor() {
        Interceptor retryInterceptor = new Interceptor() {

            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Response response = null;
                int tryCount = 1;
                while (tryCount <= MAX_TRIES) {
                    try {
                        response = chain.proceed(request);
                        break;
                    } catch (Exception ex) {
                        if ("Canceled".equalsIgnoreCase(ex.getMessage())) {
                            throw ex;
                        }

                        if (tryCount == MAX_TRIES) {
                            throw ex;
                        }

                        if (BaseRequest.POST.equals(request.method())) {
                            throw ex;
                        }

                        try {
                            Thread.sleep(RETRY_TIME * tryCount);
                        } catch (InterruptedException exx) {
                            break;
                        }

                        tryCount++;
                    }
                }

                return response;
            }
        };

        return retryInterceptor;
    }
}
