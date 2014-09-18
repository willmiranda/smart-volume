package smart_volume.com.smartvolume;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.widget.Toast;

public class ChangeVolumeService {

    public static void changeAccordingWifi(Context context) {

        SmartVolumeDAO dao = new SmartVolumeDAO(context);

        String current_wifi = WifiService.getCurrentWifiName(context);

        String wifi_saved = dao.getWifiSaved(context);
        String wifi2_saved = dao.getWifi2Saved(context);

        if (current_wifi != null) {
            if (wifi_saved != null && wifi_saved.equals(current_wifi)) {
                changeToVolumeSaved(context, null);
            } else if (wifi2_saved != null && wifi2_saved.equals(current_wifi)) {
                changeToVolumeSaved(context, 2);
            }
        } else {
            changeToDefaultVolume(context);
        }

        Toast.makeText(
                context,
                context.getString(R.string.volumes_updated),
                Toast.LENGTH_LONG
        ).show();
    }


    private static void changeToVolumeSaved(Context context, Integer volume_id) {

        SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(
                context.getString(R.string.database), Context.MODE_PRIVATE);

        int volume_saved;
        if (volume_id == null) {
            volume_saved = sharedPref.getInt(context.getString(R.string.volume), 0);
        } else {
            volume_saved = sharedPref.getInt(context.getString(R.string.volume) + volume_id.toString(), 0);
        }

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audio.setStreamVolume(AudioManager.STREAM_RING, volume_saved, AudioManager.FLAG_SHOW_UI);
    }


    public static void changeToDefaultVolume(Context context) {

        SmartVolumeDAO dao = new SmartVolumeDAO(context);

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audio.setStreamVolume(AudioManager.STREAM_RING, dao.getDefaultVolume(), AudioManager.FLAG_SHOW_UI);
    }


}
