package com.elixsr.elixsrcore.timeline.list.viewholder;

import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.elixsr.elixsrcore.BuildConfig;
import com.elixsr.elixsrcore.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Niall McShane on 12/06/2016.
 */
public abstract class AbstractTimelineViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "AbstractTimelineViewH";
    View mView;
    private int advertisementLayout = R.layout.timeline_advertisement_item;

    public AbstractTimelineViewHolder(View itemView) {
        super(itemView);
        this.mView = itemView;
    }

    public void setDayText(String text){
        TextView field = (TextView) mView.findViewById(R.id.timeline_day_heading_text);
        field.setText(text);
    }

    public void setMonthText(String text){
        TextView field = (TextView) mView.findViewById(R.id.timeline_month_heading_text);
        field.setText(text);
    }

    //TODO: remove - only an example
    public void showAdvertisement(){


//        new Handler(mView.getContext().getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {

//                final NativeExpressAdView adView = (NativeExpressAdView) mView.findViewById(R.id
//                        .timeline_ad_view);

                final NativeExpressAdView adView = null;

                final AdRequest adRequest;
                if(BuildConfig.DEBUG){

                    String android_id = Settings.Secure.getString(mView.getContext().getContentResolver(), Settings
                            .Secure.ANDROID_ID);
                    String deviceId = md5(android_id).toUpperCase();

                    adRequest = new AdRequest.Builder()
                            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                            .addTestDevice(deviceId)
                            .addTestDevice("ENU7N15A10001100")
                            .build();

                    Log.i(TAG, "showAdvertisement: device id " + deviceId);
                }else{
                    adRequest = new AdRequest.Builder().build();
                }

                Log.i(TAG, "showAdvertisement: fetching a new ad");
                adView.loadAd(adRequest); //Fetching the ads item
//            }
//        });
    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "md5: error converting device ID", e);
        }
        return "";
    }

    public View getmView() {
        return mView;
    }

    public void addAdvertisement(View view) {
        LinearLayout adContainer = (LinearLayout) mView.findViewById(R.id.timeline_ad_container);

        Log.i(TAG, "addAdvertisement: add container already has children, count: " + adContainer
                .getChildCount());
//
//        if(adContainer
//                .getChildCount() > 0){
//            adContainer.removeViewAt(0);
//        }

        //we need to remove the previous reference
        // http://stackoverflow.com/questions/28071349/the-specified-child-already-has-a-parent
        // -you-must-call-removeview-on-the-chil - used to understand issue
        if(view != null && view.getParent() != null){
            ((ViewGroup)view.getParent()).removeView(view);
        }

        adContainer.removeAllViews();



//        adContainer.removeView(view);
        adContainer.addView(view);

    }
}
