package smart_volume.com.smartvolume;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.nio.channels.Channel;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private WifiActivity mActivity;
    private AudioManager audio;
    private SharedPreferences sharedPref;

    public WiFiDirectBroadcastReceiver(){
    };

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                       WifiActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        sharedPref = context.getApplicationContext().getSharedPreferences(context.getString(R.string.database), Context.MODE_PRIVATE);

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String current_wifi = wifi.getConnectionInfo().getSSID().substring(1, wifi.getConnectionInfo().getSSID().length() - 1);

        String wifi_saved = sharedPref.getString(context.getString(R.string.wifi), null);

        if (current_wifi != null && wifi_saved != null && wifi_saved.equals(current_wifi)) {
            setVolumeSaved(context);
        } else {
            setDefaultVolume(context);
        }

        playNotification(context);
    }

    private void playNotification(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setVolumeSaved(Context context) {
        int volume_saved = sharedPref.getInt(context.getString(R.string.volume), 0);
        audio.setStreamVolume(AudioManager.STREAM_RING, volume_saved, AudioManager.FLAG_SHOW_UI);
        Toast.makeText(
                context,
                context.getString(R.string.volumes_updated),
                Toast.LENGTH_LONG
        ).show();
    }

    private void setDefaultVolume(Context context) {
        int default_volume_saved = sharedPref.getInt(context.getString(R.string.default_volume), 0);
        audio.setStreamVolume(AudioManager.STREAM_RING, default_volume_saved, AudioManager.FLAG_SHOW_UI);
        Toast.makeText(
                context,
                context.getString(R.string.volumes_updated),
                Toast.LENGTH_LONG
        ).show();
    }
}
