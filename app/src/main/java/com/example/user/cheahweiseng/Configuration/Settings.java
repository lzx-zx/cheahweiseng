package com.example.user.cheahweiseng.Configuration;

/**
 * Created by Lee Zi Xiang on 28/11/2017.
 */

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.content.Context;
import android.util.Log;

import com.example.user.cheahweiseng.KeyData;

import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Settings {

    private static final String remoteSettingUrl = "https://raw.githubusercontent.com/lkloon123/BulletinBoard/master/RemoteConfig/settings.json";
    private static final String jsonFileName = "settings.json";
    public static String mqttServerUrl;
    public static String mqttTopic;
    public static String mqttUsername;
    public static String mqttPassword;
    public static String dbServerUrl;
    public static String dbUser;
    public static String dbPassword;
    public static String dbName;
    public static String androidUpdateUrl;
    public static String javaUpdateUrl;
    public static String feedbackReceiveEmail;
    private static String remoteSetting;

    public static void parseSetting(final Context context) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(getSettings(context));
            Log.d("final setting", obj.toString());
            mqttServerUrl = obj.getString(KeyData.KEY_MQTT_SERVER_URL);
            mqttTopic = obj.getString(KeyData.KEY_MQTT_TOPIC);
            mqttUsername = obj.getString(KeyData.KEY_MQTT_USERNAME);
            mqttPassword = obj.getString(KeyData.KEY_MQTT_PASSWORD);
            dbServerUrl = obj.getString(KeyData.KEY_DB_SERVER_URL);
            dbUser = obj.getString(KeyData.KEY_DB_USER);
            dbPassword = obj.getString(KeyData.KEY_DB_PASSWORD);
            dbName = obj.getString(KeyData.KEY_DB_NAME);
            androidUpdateUrl = obj.getString(KeyData.KEY_UPDATE_URL_ANDROID);
            javaUpdateUrl = obj.getString(KeyData.KEY_UPDATE_URL_JAVA);
            feedbackReceiveEmail = obj.getString(KeyData.KEY_FEEDBACK_RECEIVE_EMAIL);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
        }
    }

    private static String getSettings(Context context) throws JSONException {
        String localSetting = readLocalSetting(context);

        if (localSetting == null) {
            //try to get from remote setting
            if (remoteSetting == null) {
                //exit program
                return null;
            } else {
                //fetch from remote setting
                createAndWriteLocalSetting(context, remoteSetting);
                return remoteSetting;
            }
        } else {
            //check if remote setting available
            if (remoteSetting == null) {
                //read setting from local setting
                return localSetting;
            } else {
                //compare with remote setting
                if (compare(localSetting, remoteSetting)) {
                    //read setting from local setting
                    return localSetting;
                } else {
                    //check which version is newer
                    if (new JSONObject(localSetting).getDouble("Version") < new JSONObject(remoteSetting).getDouble("Version")) {
                        //remote setting is newer
                        //fetch from remote setting
                        createAndWriteLocalSetting(context, remoteSetting);
                        return remoteSetting;
                    } else {
                        //lcoal setting is newer
                        //read setting from local setting
                        return localSetting;
                    }
                }
            }
        }
    }

    private static boolean compare(String localSetting, String remoteSetting) {
        try {
            JSONAssert.assertEquals(localSetting, remoteSetting, JSONCompareMode.STRICT);
            return true;
        } catch (AssertionError ex) {
            return false;
        } catch (JSONException e) {
            return false;
        }
    }

    private static String readLocalSetting(Context context) {
        try {
            StringBuffer datax = new StringBuffer("");
            FileInputStream fIn = context.openFileInput(jsonFileName);
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader bufferedReader = new BufferedReader(isr);

            String readString = bufferedReader.readLine();
            while (readString != null) {
                datax.append(readString);
                readString = bufferedReader.readLine();
            }

            isr.close();
            Log.d("local setting : ", datax.toString());
            return datax.toString();
        } catch (IOException ioe) {
            return null;
        }
    }

    public static void checkRemoteSetting() {
        String result = null;
        HttpURLConnection urlConnection = null;
        try {

            URL url = new URL(remoteSettingUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            int code = urlConnection.getResponseCode();

            if (code == 200) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                if (in != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null)
                        result += line;
                }
                in.close();
            }

            remoteSetting = result;
            remoteSetting = remoteSetting.substring(4);
            Log.d("remote setting", remoteSetting);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            remoteSetting = null;
        } catch (IOException e) {
            e.printStackTrace();
            remoteSetting = null;
        } finally {
            urlConnection.disconnect();
        }
    }

    private static void createAndWriteLocalSetting(Context context, String body) {
        try {
            File file = new File(context.getFilesDir(), jsonFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fOut = context.openFileOutput(jsonFileName, Context.MODE_PRIVATE);
            fOut.write(body.getBytes());
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

