package com.example.chris.tamuhack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by Tyler on 1/28/18.
 */

public class RedirectUtils {

    private static final String LYFT_PACKAGE = "me.lyft.android";
    private static final String UBER_PACKAGE = "me.uber.android";

    private static void openLink(Activity activity, String link) {
        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
        playStoreIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        playStoreIntent.setData(Uri.parse(link));
        activity.startActivity(playStoreIntent);
    }

    private static boolean isPackageInstalled(Context context, String packageId) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageId, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // ignored.
        }
        return false;
    }

    public static void deepLinkIntoUber(String destLat, String destLong, Context context, Activity activity) {
        if (RedirectUtils.isPackageInstalled(context, UBER_PACKAGE)) {
            // Uber is installed on phone
            RedirectUtils.openLink(activity, "uber://?action=setPickup&client_id="+RideUtils.getUberClientId()+"&pickup=my_location&dropoff[latitude]="+destLat+"&dropoff[longitude]="+destLong);

        } else {
            // Uber is not installed on phone
            RedirectUtils.openLink(activity, "https://m.uber.com/ul/?action=setPickup&client_id="+RideUtils.getUberClientId()+"&pickup=my_location&dropoff[latitude]="+destLat+"&dropoff[longitude]="+destLong);
        }
    }

    public static void deepLinkIntoLyft(String myLat, String myLong, String destLat, String destLong, Context context, Activity activity) {
        if (RedirectUtils.isPackageInstalled(context, LYFT_PACKAGE)) {
            // Lyft is installed on phone
            RedirectUtils.openLink(activity, "lyft://ridetype?id=Lyft&pickup[latitude]=" + myLat + "&pickup[longitude]=" + myLong + "&destination[latitude]=" + destLat + "&destination[longitude]=" + destLong);

        } else {
            // Lyft is not installed on phone
            RedirectUtils.openLink(activity, "https://www.lyft.com/signup/SDKSIGNUP?clientId=" + RideUtils.getLyftClientId() + "&sdkName=android_direct");
        }
    }

}
