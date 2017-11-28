package com.example.user.cheahweiseng;

import com.example.user.cheahweiseng.Class.Command;
import com.example.user.cheahweiseng.Configuration.Commands;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Lee Zi Xiang on 28/11/2017.
 */

public class Action {
    //Turn Hex to ASCll For example : 31 turn to 1
    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    //Turn ASCll to Hex For example : 1 turn to 31
    public static String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString((int) ch));
        }

        return hex.toString();
    }

    //find command with payload
    public static Command findCommandByPayload(String payload) {
        ArrayList<Command> cmdList = Commands.cmdList;
        for (int i = 0; i < cmdList.size(); i++) {
            if (cmdList.get(i).getPayload().equalsIgnoreCase(payload)) {
                return cmdList.get(i);
            }
        }
        return null;
    }

    //find Command with command name
    public static Command findCommandByName(String cmdName) {
        ArrayList<Command> cmdList = Commands.cmdList;
        for (int i = 0; i < cmdList.size(); i++) {
            if (cmdList.get(i).getName().equalsIgnoreCase(cmdName)) {
                return cmdList.get(i);
            }
        }
        return null;
    }

    //encode general command
    public static JSONObject encodePublicCmd(Command cmd) {
        //perform encoding
        JSONObject obj = new JSONObject();
        try {
            obj.put(KeyData.KEY_CMD, cmd.getPayload());
            obj.put(KeyData.KEY_RESERVE, cmd.getReserve());
            obj.put(KeyData.KEY_RECIPIENT, cmd.getRecipient());
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
