package smart_volume.com.smartvolume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.nio.channels.Channel;

public class ScheduleVolumeReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private WifiActivity mActivity;
    private AudioManager audio;
    private SharedPreferences sharedPref;

    public ScheduleVolumeReceiver(){
    };

    public ScheduleVolumeReceiver(WifiP2pManager manager, Channel channel,
                                       WifiActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (WifiService.isConnected(context)) {

            if(!connectedToSameWifi(context)) {
                ChangeVolumeService.changeAccordingWifi(context);
            } else {
                Toast.makeText(
                        context,
                        "Mudanca agendada, mas volume nao alterado pois continua no mesmo wifi",
                        Toast.LENGTH_LONG
                ).show();
            }

            playNotification(context);

        } else {

            ChangeVolumeService.changeToDefaultVolume(context);
        }
    }

    private boolean connectedToSameWifi(Context context) {

        SmartVolumeDAO dao = new SmartVolumeDAO(context);

        return !dao.getLastWifiConnected().equals(WifiService.getCurrentWifiName(context));
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

}