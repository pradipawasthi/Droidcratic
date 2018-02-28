# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/markwyszomierski/Desktop/libs/java/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Application classes that will be serialized/deserialized over Gson
-keep class org.socratic.android.api.model.** { *; }
-keep class org.socratic.android.api.response.** { *; }

##---------------Begin: proguard configuration for Log Removal  ----------
# The below removes log statements of any result type. See:
# - http://stackoverflow.com/questions/4435773/android-proguard-removing-all-log-statements-and-merging-packages
# - http://stackoverflow.com/questions/14790549/android-proguard-options-stricter-rules-remove-log-statements/14792344#14792344
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** d(...);
    public static *** e(...);
}
##---------------End: proguard configuration for Log Removal  ----------

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

##---------------End: proguard configuration for Gson  ----------

# For OkHttp.
-dontwarn okhttp3.**
-dontwarn org.codehaus.**
-dontwarn java.nio.**

##---------------Begin: proguard configuration for EventBus  ----------
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
##---------------End: proguard configuration for EventBus  ----------

##---------------Begin: proguard configuration for Amplitude  ----------
-keep class com.google.android.gms.ads.** { *; }
-keepattributes InnerClasses
-keepclassmembers class org.socratic.android.analytics.AnalyticsManager$** {
    <fields>;
}
-keepclassmembers class org.socratic.android.analytics.AppReviewAnaltyics$** {
    <fields>;
}
-keepclassmembers class org.socratic.android.analytics.CameraPermissionAnalytics$** {
    <fields>;
}
-keepclassmembers class org.socratic.android.analytics.ContactsPermissionAnalytics$** {
    <fields>;
}
-keepclassmembers class org.socratic.android.analytics.Event$** {
    <fields>;
}
-keepclassmembers class org.socratic.android.analytics.OCRAnalytics$** {
    <fields>;
}
-keepclassmembers class org.socratic.android.analytics.OnboardingAnalytics$** {
    <fields>;
}
-keepclassmembers class org.socratic.android.analytics.ResultsAnalytics$** {
    <fields>;
}
-keepclassmembers class org.socratic.android.analytics.ChatAnalytics$** {
    <fields>;
}
-keep public enum org.socratic.android.analytics.AnalyticsManager$EventPropertyOptions$** {
    **[] $VALUES;
    public *;
}
-dontwarn okio.**
##---------------End: proguard configuration for Amplitude  ----------


##---------------Begin: proguard configuration for Crashyltics  ----------
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
##---------------End: proguard configuration for Crashyltics  ----------

##---------------Begin: Joda Time 2.3  ----------
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }
##---------------End: Joda Time 2.3  ----------

##---------------Begin: proguard configuation for Animation  ----------
-keep class org.socratic.android.views.AnimatableRectF { *; }
##---------------End: proguard configuation for Animation  ----------

##---------------Begin: proguard configuation for Appsee  ----------
-keep class com.appsee.** { *; }
-dontwarn com.appsee.**
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-keepattributes SourceFile,LineNumberTable

-dontwarn com.squareup.okhttp.**


