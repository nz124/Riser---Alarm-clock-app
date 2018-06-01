package com.example.hello.alarm;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SendChallenge{


    private static String SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static String FCM_CHALLENGE_ENDPOINT
            = "https://fcm.googleapis.com/fcm/send";

    SendChallenge(Context context, String title, String msg, String receiver) {
        String challengeMessage = getFcmMessageJSONDataAndNotification(title, msg, receiver);
        sendMessageToFcm(context, challengeMessage);
    }


    //Using HttpURLConnection it send http post request containing data to FCM server
    private void sendMessageToFcm(Context context, String postData) {
        try {

            HttpURLConnection httpConn = getConnection(context);
            httpConn.setDoOutput(true);
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("POST");

            DataOutputStream wr = new DataOutputStream(httpConn.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Log.e("MESSAGE TOKEN", "sendMessageToFcm: " + response.toString() );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getAccessToken(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        GoogleCredential googleCredential = GoogleCredential
                .fromStream(assetManager.open("riser-alarm-firebase-adminsdk-wnkfx-1b4fc1722e.json"))
                .createScoped(Arrays.asList(SCOPE));
        googleCredential.refreshToken();
        return googleCredential.getAccessToken();
    }

    //create HttpURLConnection setting Authorization token
    //and Content-Type header
    private HttpURLConnection getConnection(Context context) throws Exception {
        URL url = new URL(FCM_CHALLENGE_ENDPOINT);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + getAccessToken(context));
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        return httpURLConnection;
    }

    private String getFcmMessageJSONDataAndNotification(String title, String msg, String receiver) {

        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", title);
            jNotification.put("body", msg);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            jNotification.put("icon", "ic_notification");

            jData.put("picture", "http://opsbug.com/static/google-io.jpg");


            jPayload.put("to", receiver);


            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jData);

            Log.e("WHY NO ONE LIKED ME", "getFcmMessageJSONDataAndNotification: " +jPayload.toString() );
            return jPayload.toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }
            return null;
    }

    }
