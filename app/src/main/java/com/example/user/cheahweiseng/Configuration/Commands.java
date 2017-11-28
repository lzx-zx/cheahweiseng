package com.example.user.cheahweiseng.Configuration;

import android.content.Context;
import android.util.Log;

import com.example.user.cheahweiseng.Class.Command;
import com.example.user.cheahweiseng.KeyData;

import org.json.JSONArray;
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
import java.util.ArrayList;

/**
 * Created by Lee Zi Xiang on 28/11/2017.
 */

public class Commands {
    private static final String remoteCommandsUrl = "https://raw.githubusercontent.com/lkloon123/BulletinBoard/master/RemoteConfig/commands.json";
    private static final String jsonFileName = "commands.json";
    public static ArrayList<Command> cmdList = new ArrayList<>();
    private static String remoteCommand;

    public static void parseCommand(final Context context) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(getCommands(context));
            Log.d("final command", obj.toString());

            JSONArray arr = obj.getJSONArray(KeyData.KEY_COMMAND_LIST);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject command = arr.getJSONObject(i);
                Command cmdTemp = new Command();
                cmdTemp.setName(command.getString(KeyData.KEY_COMMAND_NAME));
                cmdTemp.setCmdByte(command.getString(KeyData.KEY_COMMAND_BYTE));
                cmdTemp.setPayload(command.getString(KeyData.KEY_COMMAND_PAYLOAD));
                cmdTemp.setReserve(command.getString(KeyData.KEY_COMMAND_RESERVE));
                cmdList.add(cmdTemp);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
        }
    }

    private static String getCommands(Context context) throws JSONException {
        String localSetting = readLocalCommand(context);

        if (localSetting == null) {
            //try to get from remote setting
            if (remoteCommand == null) {
                //exit program
                return null;
            } else {
                //fetch from remote setting
                createAndWriteLocalCommand(context, remoteCommand);
                return remoteCommand;
            }
        } else {
            //check if remote setting available
            if (remoteCommand == null) {
                //read setting from local setting
                return localSetting;
            } else {
                //compare with remote setting
                if (compare(localSetting, remoteCommand)) {
                    //read setting from local setting
                    return localSetting;
                } else {
                    //check which version is newer
                    if (new JSONObject(localSetting).getDouble("Version") < new JSONObject(remoteCommand).getDouble("Version")) {
                        //remote setting is newer
                        //fetch from remote setting
                        createAndWriteLocalCommand(context, remoteCommand);
                        return remoteCommand;
                    } else {
                        //lcoal setting is newer
                        //read setting from local setting
                        return localSetting;
                    }
                }
            }
        }
    }

    private static boolean compare(String localCommand, String remoteCommand) {
        try {
            JSONAssert.assertEquals(localCommand, remoteCommand, JSONCompareMode.STRICT);
            return true;
        } catch (AssertionError ex) {
            return false;
        } catch (JSONException e) {
            return false;
        }
    }

    private static String readLocalCommand(Context context) {
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
            Log.d("local command : ", datax.toString());
            return datax.toString();
        } catch (IOException ioe) {
            return null;
        }
    }

    public static void checkRemoteCommand() {
        String result = null;
        HttpURLConnection urlConnection = null;
        try {

            URL url = new URL(remoteCommandsUrl);
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

            remoteCommand = result;
            remoteCommand = remoteCommand.substring(4);
            Log.d("remote command", remoteCommand);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            remoteCommand = null;
        } catch (IOException e) {
            e.printStackTrace();
            remoteCommand = null;
        } finally {
            urlConnection.disconnect();
        }
    }

    private static void createAndWriteLocalCommand(Context context, String body) {
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
