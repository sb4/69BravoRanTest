package com.example.android.bravo69rantest;

import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

public class InternetUtils
{
    private static final String TAG = "InternetUtils";

    public static String getResponse(String strUrl) throws IOException
    {
        URL url = new URL(strUrl);

        String strResponse = getResponse(url);

        return strResponse;
    }

    public static String getResponse(URL url) throws IOException
    {
        String thisMethod = ".getResponse() ";
        long LstartTime = System.currentTimeMillis();

        Log.d(TAG, thisMethod + "Start. url:" + url.toString());

        StringBuilder sbuf = new StringBuilder();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream inStream = null;
        String strResponse = null;

        try
        {
            inStream = new BufferedInputStream(conn.getInputStream());
            strResponse = InternetUtils.readStream(inStream, 4096, 999999, null);
//            BufferedReader rdr = new BufferedReader(new InputStreamReader(inStream));
//
//            String str;
//            int ix = 0;
//            while ((str = rdr.readLine()) != null)
//            {
//                sbuf.append(str);
//                ix++;
//            }
        } finally
        {
            if (inStream != null) inStream.close();
            if (conn != null) conn.disconnect();
        }

//        String strResponse = sbuf.toString();

        Log.d(TAG, thisMethod + "End. Elapsed millis: " + (System.currentTimeMillis() - LstartTime)
                + ", url:" + url.toString() + ", strResponse.length()="
                + (strResponse != null ? strResponse.length() : "(NULL)"));

        return strResponse;
    }

    public static class SendRequest implements Runnable
    {
        private String url;
        private String response;
        private Handler responseHandler;

        public String getErrorMessage()
        {
            return errorMessage;
        }

        private String errorMessage;

        public SendRequest(String url, Handler responseHandler)
        {
            this.url = url;
            this.responseHandler = responseHandler;
        }

        public void run()
        {
            try
            {
                response = InternetUtils.getResponse(url);
            } catch (IOException e)
            {
                e.printStackTrace();
                errorMessage = Arrays.toString(e.getStackTrace());
                response = errorMessage;
            }


            if (responseHandler != null)
            {
                Bundle bndl = new Bundle();
                bndl.putCharSequence("response", response);
                Message msg = Message.obtain(this.responseHandler, 1);
                msg.setData(bndl);
                responseHandler.dispatchMessage(msg);
            }
        }

        public String getResponse()
        {
            return this.response;
        }
    }

    /**
     * Converts the contents of an InputStream to a String.  Taken from Android docs https://developer.android.com/training/basics/network-ops/connecting#java
     */
    public static String readStream(InputStream stream, int maxReadSize)
            throws IOException, UnsupportedEncodingException
    {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuffer buffer = new StringBuffer();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0)
        {
            if (readSize > maxReadSize)
            {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }

    public static String readStream(InputStream stream, int readChunkSize, int maxReadSize,
                                    String charsetName)
            throws IOException, UnsupportedEncodingException
    {
        if (charsetName == null) charsetName = "UTF-8";
        Reader reader = null;
        reader = new InputStreamReader(stream, charsetName);
        char[] rawBuffer = new char[readChunkSize];
        int readSize;
        StringBuilder buffer = new StringBuilder();

        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0)
        {
            if (readSize > maxReadSize)
            {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }

    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body in String form. Otherwise,
     * it will throw an IOException.
     * This is a slightly modified version from https://developer.android.com/training/basics/network-ops/connecting
     *  to not rely on a visible "publishProgress()" method.  It would be more useful implement a
     *  progressbar and to couple it with an inputstream that also updated the progress bar. -RAN 5/14/2020.
     */
    public static  String downloadUrl(URL url, DownloadCallback progressCallback) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();

//            publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
            if (progressCallback != null)
                progressCallback.updateFromDownload("Connect successful.");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();

//            publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
            if (progressCallback != null)
                progressCallback.updateFromDownload("Input stream open successful.");

            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readStream(stream, 500);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        if (progressCallback != null)
            progressCallback.updateFromDownload("Download done.");

        return result;
    }

    // ===============================================================================
    public interface DownloadCallback<T> {
        interface Progress {
            int ERROR = -1;
            int CONNECT_SUCCESS = 0;
            int GET_INPUT_STREAM_SUCCESS = 1;
            int PROCESS_INPUT_STREAM_IN_PROGRESS = 2;
            int PROCESS_INPUT_STREAM_SUCCESS = 3;
        }

        /**
         * Indicates that the callback handler needs to update its appearance or information based on
         * the result of the task. Expected to be called from the main thread.
         */
        void updateFromDownload(T result);

        /**
         * Get the device's active network status in the form of a NetworkInfo object.
         */
        NetworkInfo getActiveNetworkInfo();

        /**
         * Indicate to callback handler any progress update.
         * @param progressCode must be one of the constants defined in DownloadCallback.Progress.
         * @param percentComplete must be 0-100.
         */
        void onProgressUpdate(int progressCode, int percentComplete);

        /**
         * Indicates that the download operation has finished. This method is called even if the
         * download hasn't completed successfully.
         */
        void finishDownloading();
    }
}
