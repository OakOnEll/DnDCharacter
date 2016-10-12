package com.oakonell.dndcharacter.views.character;

import android.app.Activity;
import android.os.Handler;
import android.provider.Settings;

/*import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ServerManagedPolicy;
*/
/**
 * Created by Rob on 3/14/2016.
 */
public class LicenseChecker {
    public static final int RETRY_REASON = 0;
/*    public static final int RETRY_REASON = Policy.RETRY;
    private LicenseCheckerCallback mLicenseCheckerCallback;
    private com.google.android.vending.licensing.LicenseChecker mChecker;
    private Handler mHandler;
    // Generate 20 random bytes, and put them here.
    private static final byte[] SALT = new byte[]{
            -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95,
            -45, 77, -117, -36, -113, -11, 32, -64, 89
    };
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAora7BLaHK7LJAHLn6h16j8PC5/Z3q68vB5SknwsRGgPMJBP6T0UlMfa/X8vwgPcyULLZMY51hgNpH0HXIbdAITQ0CfbgtnUI50zY0/9XjbNSLDZF4jMztB4Zs08Gipa0foixnOzbhXC88w3BqYyQYMF9+UdfimSBvjW+bTMayLtH24KcI7EzmsRfXK9UH22bxJQ+MUalk0jjE3FlE5gCDwPmmhBFDVvzNM10EQo1wu+Q+Z9FhdBpENRe1awnDvtn2/Qz4mhrAca2ueVPYekjaaG3wVKcUSdYCxB9JJ+YZhmcDrnRARsvJpYoPuXhkKUXRr/0LdSYFYkfrXM0xc/mxwIDAQAB";
    private LicenseCallback callback;
    private Activity context;

*/    public interface LicenseCallback {

        void applicationError(int errorCode);

        void allow(int reason);

        void dontAllow(int reason);
    }
    public void onCreate(Activity activity, LicenseCallback callback) {
/*
        mHandler = new Handler();
        this.callback = callback;
        this.context = activity;
        // Construct the LicenseCheckerCallback. The library calls this when done.
        mLicenseCheckerCallback = new MyLicenseCheckerCallback();
        // Construct the LicenseChecker with a Policy.
        String deviceId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        mChecker = new com.google.android.vending.licensing.LicenseChecker(
                activity, new ServerManagedPolicy(activity,
                new AESObfuscator(SALT, activity.getPackageName(), deviceId)),
                BASE64_PUBLIC_KEY  // Your public licensing key.
        );
*/    }

    public void doCheck(Activity activity) {
  /*      activity.setProgressBarIndeterminateVisibility(true);
        mChecker.checkAccess(mLicenseCheckerCallback);
        */
    }

    public void onDestroy(CharacterActivity characterActivity) {
        /* mChecker.onDestroy(); */
    }

/*    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {

        public void allow(final int reason) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    context.setProgressBarIndeterminateVisibility(false);
                    callback.allow(reason);
                }
            });
        }

        public void dontAllow(final int reason) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    context.setProgressBarIndeterminateVisibility(false);
                    callback.dontAllow(reason);
                }
            });
        }

        @Override
        public void applicationError(int errorCode) {
            callback.applicationError(errorCode);
        }
    }
*/
}
