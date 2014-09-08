package smart_volume.com.smartvolume;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class WifiActivity extends Activity {

    private WifiManager wifi;
    private Spinner spinner;
    private Button btnSubmit;
    private SeekBar volumeControl = null;
    private SeekBar defaultVolumeControl = null;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        configPreferences();
        configWifiService();

        setContentView(R.layout.activity_wifi);

        List<String> wifis = new ArrayList<String>();
        if (wifi.getConfiguredNetworks() != null){
            for(WifiConfiguration network : wifi.getConfiguredNetworks()) {
                String wifi_name = network.SSID.substring(1, network.SSID.length() - 1);
                wifis.add(wifi_name);
            }
        }

        String wifi_saved = getWifiSaved();

        spinner = (Spinner) findViewById(R.id.wifis_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item, wifis);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition(wifi_saved));

        addListenerOnButton();

        volumeControl = (SeekBar) findViewById(R.id.volume_bar);
        volumeControl.setProgress(volumeToPercentenge(getVolumeSaved()));

        defaultVolumeControl = (SeekBar) findViewById(R.id.default_volume_bar);
        defaultVolumeControl.setProgress(volumeToPercentenge(getDefaultVolumeSaved()));
    }

    public void configPreferences() {
        sharedPref = getSharedPreferences(getString(R.string.database), Context.MODE_PRIVATE);
    }

    public void configWifiService() {
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    public String getCurrentWifiName(){
        return wifi.getConnectionInfo().getSSID().substring(1, wifi.getConnectionInfo().getSSID().length() - 1);
    }

    public void addListenerOnButton() {

        spinner = (Spinner) findViewById(R.id.wifis_spinner);
        volumeControl = (SeekBar) findViewById(R.id.volume_bar);
        defaultVolumeControl = (SeekBar) findViewById(R.id.default_volume_bar);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String wifi_selected = String.valueOf(spinner.getSelectedItem());

//                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.database), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.wifi), wifi_selected);
                editor.putInt(getString(R.string.volume), percentengeToVolume(volumeControl.getProgress()));
                editor.putInt(getString(R.string.default_volume), percentengeToVolume(defaultVolumeControl.getProgress()));
                editor.commit();

                Toast.makeText(
                        WifiActivity.this,
                        getString(R.string.saved),
                        Toast.LENGTH_LONG
                ).show();

                if (getWifiSaved().equals(getCurrentWifiName())) {
                    AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audio.setStreamVolume(AudioManager.STREAM_RING, getVolumeSaved(), AudioManager.FLAG_SHOW_UI);
                    Toast.makeText(
                            WifiActivity.this,
                            getString(R.string.volumes_updated),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
    }

    private int percentengeToVolume(int percentage) {
        if(percentage == 0) {
            return 0;
        }else {
            return (percentage * 7) / 100;
        }
    }

    private int volumeToPercentenge(int volume) {
        if(volume == 0) {
            return 0;
        }else {
            return (100 * volume) / 7;
        }
    }

    public String getWifiSaved() {
        return sharedPref.getString(getString(R.string.wifi), null);
    }

    public int getVolumeSaved() {
        return sharedPref.getInt(getString(R.string.volume), 0);
    }

    public int getDefaultVolumeSaved() {
        return sharedPref.getInt(getString(R.string.default_volume), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wifi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
