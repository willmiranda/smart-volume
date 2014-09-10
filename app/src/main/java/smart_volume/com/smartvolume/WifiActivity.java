package smart_volume.com.smartvolume;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class WifiActivity extends Activity {

    private WifiManager wifi;
    private Spinner spinner;
    private Spinner spinner2;
    private Button btnSubmit;
    private SeekBar volumeControl = null;
    private SeekBar volumeControl2 = null;
    private SeekBar defaultVolumeControl = null;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        configWifiService();

        if (!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);
            toastEnableWifi();
        }

        configPreferences();

        setContentView(R.layout.activity_wifi);

        List<String> wifis = new ArrayList<String>();
        if (wifi.getConfiguredNetworks() != null) {
            for (WifiConfiguration network : wifi.getConfiguredNetworks()) {
                wifis.add(WifiHelper.wifiName(network));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, wifis);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) findViewById(R.id.wifis_spinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition(getWifiSaved()));

        spinner2 = (Spinner) findViewById(R.id.wifis_spinner2);
        spinner2.setAdapter(adapter);
        spinner2.setSelection(adapter.getPosition(getWifiSaved(2)));

        addListenerOnButton();

        volumeControl = (SeekBar) findViewById(R.id.volume_bar);
        volumeControl.setProgress(volumeToPercentenge(getVolumeSaved()));

        volumeControl2 = (SeekBar) findViewById(R.id.volume_bar2);
        volumeControl2.setProgress(volumeToPercentenge(getVolumeSaved(2)));

        defaultVolumeControl = (SeekBar) findViewById(R.id.default_volume_bar);
        defaultVolumeControl.setProgress(volumeToPercentenge(getDefaultVolumeSaved()));
    }

    private void toastEnableWifi() {
        Toast.makeText(
                WifiActivity.this,
                getString(R.string.enable_wifi),
                Toast.LENGTH_LONG
        ).show();
    }

    public void configPreferences() {
        sharedPref = getSharedPreferences(getString(R.string.database), Context.MODE_PRIVATE);
    }

    public void configWifiService() {
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    public String getCurrentWifiName(){
        return WifiHelper.wifiName(wifi.getConnectionInfo());
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
                String wifi2_selected = String.valueOf(spinner2.getSelectedItem());

//                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.database), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.wifi), wifi_selected);
                editor.putString(getString(R.string.wifi)+"2", wifi2_selected);
                editor.putInt(getString(R.string.volume), percentengeToVolume(volumeControl.getProgress()));
                editor.putInt(getString(R.string.volume)+"2", percentengeToVolume(volumeControl2.getProgress()));
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
        return getWifiSaved(null);
    }

    public String getWifiSaved(Integer id) {
        if (id == null) {
            return sharedPref.getString(getString(R.string.wifi), null);
        } else {
            return sharedPref.getString(getString(R.string.wifi) + id.toString(), null);
        }
    }

    public int getVolumeSaved() {
        return getVolumeSaved(null);
    }

    public int getVolumeSaved(Integer id) {
        if (id == null) {
            return sharedPref.getInt(getString(R.string.volume), 0);
        } else {
            return sharedPref.getInt(getString(R.string.volume) + id.toString(), 0);
        }
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
