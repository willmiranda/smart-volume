package smart_volume.com.smartvolume;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;

class WifiHelper {

    public static String wifiName(WifiConfiguration config) {
        if (config != null) {
            return wifiName(config.SSID);
        }
        return "";
    }

    public static String wifiName(WifiInfo info) {
        if (info != null) {
            return wifiName(info.getSSID());
        }
        return "";
    }

    public static String wifiName(String wifi_name) {
        if (wifi_name != null) {
            return wifi_name.substring(1, wifi_name.length() - 1);
        }
        return "";
    }
}