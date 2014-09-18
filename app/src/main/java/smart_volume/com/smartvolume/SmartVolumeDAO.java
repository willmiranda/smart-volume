package smart_volume.com.smartvolume;

import android.content.Context;
import android.content.SharedPreferences;

public class SmartVolumeDAO {

    public static final String LAST_WIFI_CONNECTED = "lastWifiConnected";
    private Context context;
    private SharedPreferences sharedPref;

    public SmartVolumeDAO(Context context) {
        this.context = context;
        this.sharedPref = context.getApplicationContext().getSharedPreferences(
                context.getString(R.string.database), Context.MODE_PRIVATE);
    }

    public String getWifiSaved(Context context){
        return sharedPref.getString(context.getString(R.string.wifi), null);
    }

    public String getWifi2Saved(Context context) {
        return sharedPref.getString(context.getString(R.string.wifi) + "2", null);
    }

    public boolean hasChangeVolumeScheduled(Context context) {

        long changeScheduledAt = sharedPref.getLong(context.getString(R.string.schedule), 0L);

        return changeScheduledAt > System.currentTimeMillis();
    }

    public void saveChangeVolumeSchedule(long changeAt) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(context.getString(R.string.schedule), changeAt);
        editor.commit();
    }

    public int getDefaultVolume() {
        return sharedPref.getInt(context.getString(R.string.default_volume), 0);
    }

    public void saveLastWifiConnected(String currentWifiName) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LAST_WIFI_CONNECTED, currentWifiName);
        editor.commit();
    }

    public String getLastWifiConnected() {
        return sharedPref.getString(LAST_WIFI_CONNECTED, null);
    }
}
