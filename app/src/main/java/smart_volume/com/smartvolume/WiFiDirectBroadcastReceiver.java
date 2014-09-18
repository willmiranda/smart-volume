package smart_volume.com.smartvolume;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.nio.channels.Channel;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private WifiActivity mActivity;
    private SharedPreferences sharedPref;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

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
    public void onReceive(final Context context, Intent intent) {

        SmartVolumeDAO dao = new SmartVolumeDAO(context);

        if (dao.hasChangeVolumeScheduled(context)) {

            Toast.makeText(
                    context,
                    "Mudanca de status wifi ignorada ...",
                    Toast.LENGTH_LONG
            ).show();

        } else {

            if (WifiService.isConnected(context)) {

                ChangeVolumeService.changeAccordingWifi(context);

            } else {

                int delay = 20000;
                long changeVolumeAt = System.currentTimeMillis() + delay;

                Intent intentVolumeReceiver = new Intent(context, ScheduleVolumeReceiver.class);
                alarmIntent = PendingIntent.getBroadcast(context, 0, intentVolumeReceiver, 0);

                alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmMgr.set(AlarmManager.RTC_WAKEUP, changeVolumeAt, alarmIntent);

                dao.saveChangeVolumeSchedule(changeVolumeAt);
                dao.saveLastWifiConnected(WifiService.getCurrentWifiName(context));

                Toast.makeText(
                        context,
                        "O volume será alterado daqui "+(delay/1000)+" segundos",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}
