package com.yesipov;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpUtil {
    public static String sendRequest(String url, String requestType, Map<String, String> headers, String request) throws Exception {
        String result = null;
        HttpURLConnection urlConnection = null;
        //GET,HEAD
        try {
            URL requestUrl = new URL(url);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setReadTimeout(20000);
            urlConnection.setConnectTimeout(20000);
                urlConnection.setRequestMethod(requestType); // optional, GET already by default


            //POST
            if (request != null) {
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod(requestType); // optional, setDoOutput(true) set value to POST
                DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                outputStream.writeBytes(request);
                outputStream.flush();
                outputStream.close();
            }

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    urlConnection.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            int status = urlConnection.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_PARTIAL) {
                if (requestType.equals("HEAD")) {
                    result = urlConnection.getHeaderField("Content-Length");
                } else {
                    result = getStringFromStream(urlConnection.getInputStream());
                }
            } else {
                System.out.println(status + " " + urlConnection.getResponseMessage());
            }
        } catch (Exception e) {
            throw new Exception(" troubles with connection to server! Try again later");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    private static String getStringFromStream(InputStream inputStream) throws IOException {
        final int BUFFER_SIZE = 4096;
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream(BUFFER_SIZE);
        byte[] buffer = new byte[BUFFER_SIZE];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            resultStream.write(buffer, 0, length);
        }
        return resultStream.toString("UTF-8");
    }
}
