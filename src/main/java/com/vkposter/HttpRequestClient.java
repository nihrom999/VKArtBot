package com.vkposter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HttpRequestClient {

    private static HttpClient client = new DefaultHttpClient();

    public static String httpGet(String url){

        HttpGet request = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = client.execute(request);

            BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
