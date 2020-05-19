package com.example.android.bravo69rantest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

public class SensorsActivity extends AppCompatActivity
{
    public static final String TAG = "SensorsActivity";
    public static Object lockDataThread = new Object();

    public float dpRatio;
    public int idp8;
    public static String strUrl = "https://www.rcofalcon.com/Image2000/rest/recordservice/getRecordsUpdatedXFiltered/69BAdmin/69BAdmin/Sensor/-5000/0/+/+/+/+/+/+/+/+/SensorId%2CSensor+Name%2CValue/+/+";

    public TextView txt1;

    private Button buttonGetData;
    private Handler httpHandler;
    private SensorDataWorker sensorDataWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dpRatio = this.getResources().getDisplayMetrics().density;
        idp8 = (int) (8 * dpRatio);

        Log.d(TAG, "onCreate() Start.");
        setContentView(R.layout.activity_sensors);

        httpHandler = new SensorHandler(this, Looper.getMainLooper());
        Log.d(TAG, "onCreate() End.");

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        startSensorDataWorkerThread();
        Log.d(TAG, "onStart() Start, End.");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause() Start.");

        if (sensorDataWorker != null)
        {
            Log.d(TAG, "onPause() calling  killSensorDataWorkerThread() and setting member to null.");
            killSensorDataWorkerThread();
            this.sensorDataWorker = null;
        }
        Log.d(TAG, "onPause() End.");
    }

    public void onClickExit(View view)
    {
        super.onPause();
        Log.d(TAG, "onPause() Start.");

        Log.d(TAG, "onPause() End.");
        super.finish();
    }

    public void killSensorDataWorkerThread()
    {
        if (sensorDataWorker != null)
        {
            Log.d(TAG, "onPause() calling  sensorDataWorker.kill().");
//            synchronized (lockDataThread)
//            {
//                lockDataThread.notify();
//            }

            sensorDataWorker.kill();
        }
    }

    public void onClickButtonGetData(View view)
    {
        Log.d(TAG, "onClickButtonGetData start.");
        startSensorDataWorkerThread();
//        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.sensorlayout);
//        if (txt1 == null)
//        {
//            Log.d(TAG, "handleMessage() creating txt1 TextView, adding to linearLayout.");
//            txt1 = new TextView(this);
//            txt1.setWidth(ActionBar.LayoutParams.MATCH_PARENT);
//            linearLayout.addView(txt1);
//            linearLayout.setBackgroundColor(Color.TRANSPARENT);
//        }
//
//        txt1.setText("Hello");
        Toast.makeText(this, "onClickButtonGetData", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "onClickButtonGetData End.");
    }

    private void startSensorDataWorkerThread()
    {
        Log.d(TAG, "startSensorDataWorkerThread() Start.");

        if (sensorDataWorker == null)
        {
            Log.d(TAG, "startSensorDataWorkerThread() Starting new sensorDataWorker thread.");
            Log.d(TAG, "onClickButtonGetData starting sensorDataWorker thread.");
            sensorDataWorker = new SensorDataWorker(httpHandler);
            sensorDataWorker.start();
        }
        else
            Log.d(TAG, "startSensorDataWorkerThread() sensorDataWorker thread already started.");

        Log.d(TAG, "startSensorDataWorkerThread() End.");
    }

    public static String getSensorData() throws IOException
    {
        String strResp = InternetUtils.getResponse(strUrl);

        Log.d(TAG, "getSensorData() strResp=" + strResp);

        return strResp;
    }

    /**
     *
     */
    public static class SensorDataWorker extends Thread
    {
        public static final String TAG = "SensorDataWorker";
        private AtomicBoolean isActive = new AtomicBoolean(false);
        private String strResp;
        private Handler responseHandler;

        public SensorDataWorker(Handler responseHandler)
        {
            this.responseHandler = responseHandler;
            Log.d(TAG, "Exiting constructor.");
        }

        @Override
        public void run()
        {
            Log.d(TAG, "run() Start.");
            isActive.set(true);

            while (isActive.get())
            {
                try
                {
                    Log.d(TAG, "run() About to call getSensorData().");
                    strResp = getSensorData();
//                    strResp = "My Fake Response... Hah!" +
//                            " time:" + System.currentTimeMillis();
                    if (false) throw new IOException("My Fake IO Exception, just to make compiler happy.");
                    Log.d(TAG, "run() After call getSensorData().  strResp=\r\n" + strResp);
                    Bundle bndl = new Bundle();
                    bndl.putCharSequence("response", strResp);
                    String strTest = bndl.getString("response");

                    Message msg = this.responseHandler.obtainMessage(1, null);
                    msg.setData(bndl);
                    responseHandler.sendMessage(msg);
                    Log.d(TAG, "run() After call sendMessage(), about to wait. time: " + System.currentTimeMillis());
//                    this.wait(5000);
//                    sleep(8000);
                    synchronized (lockDataThread)
                    {
                        lockDataThread.wait(8000);
                    }

                    Log.d(TAG, "run() After wake from wait. time: " + System.currentTimeMillis());
                } catch (IOException e)
                {
                    e.printStackTrace();
                    strResp = Arrays.toString(e.getStackTrace());
                } catch (InterruptedException e)
                {
                    break;
                }
            }

        }


        public String getStrResp()
        {
            return strResp;
        }

        public void kill()
        {
            synchronized (lockDataThread)
            {
                this.isActive.set(false);
                lockDataThread.notify();
            }

        }
    }

    /**
     *
     */
    public static class SensorHandler extends Handler
    {
        public static final String TAG = "SensorHandler";
        private Gson gson = new Gson();

        public AppCompatActivity activity;
        private Map<String, TextView> mapSensorViews = new HashMap<String, TextView>();
        public float dpRatio;
        DecimalFormat nfmt;
        int idp8;
        SimpleDateFormat dfmt;

        public SensorHandler(AppCompatActivity activity, Looper looper)
        {
            super(looper);
            this.activity = activity;
            dpRatio = activity.getResources().getDisplayMetrics().density;
            idp8 = (int) (8 * dpRatio);
            nfmt = new DecimalFormat("################.#");
            Log.d(TAG, "Exiting constructor.");
            dfmt = new SimpleDateFormat("HH:mm:ss");
        }

        @SuppressLint("ResourceType")
        @Override
        public void handleMessage(@NonNull Message msg)
        {
            Log.d(TAG, "handleMessage() Start. msg=" + msg.toString());

            if (msg.what == 1)
            {
//            super.handleMessage(msg);

                Bundle bndl = msg.getData();

                Object obj = bndl.get("response");
                Log.d(TAG, "obj=" + obj);

                String strResp = bndl.getCharSequence("response").toString();

                RecordDataMapped[] recs = gson.fromJson(strResp, RecordDataMapped[].class);
                Arrays.sort(recs);

                Log.d(TAG, "handleMessage() Start. strResp=" + strResp + ", recs.length=" + recs.length);
                Log.d(TAG, "");

                LinearLayout linearLayout = (LinearLayout) activity.findViewById(R.id.sensorlayout);
                // {"LobjectId":133,"objectType":"NRT298","errorCode":"","errorMessage":"","csvDataFilePath":"","iCsvRow":-1,"mobileRecordId":"","mapCodingInfo":{"Value":"0","SensorId":"CountyFMC","Sensor Name":"FMC_Flow_from_County_GPM","RMS Timestamp":"1588252268940"}
                // mapCodingInfo={{Value=0, SensorId=FMP1, Sensor Name=FMP1_Flow_to_Pumpkin_1_GPM, RMS Timestamp=1568393839190}}
                    // ****** Note: time textview can be hard-coded into layout file.  Also it is child of the linear layout, so tparams was wrong time. -RAN 5/18/2020
//                TextView tvTime = new TextView(activity);
                TextView tvTime = activity.findViewById(R.id.textViewLastRefresh);
//                RelativeLayout.LayoutParams tparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
////                tparams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//                tparams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//                tvTime.setLayoutParams(tparams);
                tvTime.setText("Last Refresh: " + dfmt.format(System.currentTimeMillis()));
//                linearLayout.addView(tvTime);

                for (int i = 0; i < recs.length; i++)
                {
                    RecordDataMapped rec = recs[i];
                    int iObjectId = (int) rec.LobjectId;
                    Map<String,String> mapCodingInfo = rec.mapCodingInfo;

                    Log.d(TAG, "i=" + i + ", mapCodingInfo=" + mapCodingInfo);
                    String sensorId = mapCodingInfo.get("SensorId");

                   TextView tvSensorValue = mapSensorViews.get(sensorId);

                if (tvSensorValue == null)
                {
                    Log.d(TAG, "handleMessage() creating tv TextView, adding to linearLayout.");
                    RelativeLayout sensorLayout = new RelativeLayout(this.activity);
                    RelativeLayout.LayoutParams vparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                    sensorLayout.setBackgroundColor(Color.GREEN);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    sensorLayout.setOrientation(RelativeLayout.HORIZONTAL);
//                    sensorLayout.setLayoutParams(layoutParams);
//                    sensorLayout.getLayoutParams().width = ActionBar.LayoutParams.MATCH_PARENT;
                    TextView tvSensorName = new TextView(this.activity);
                    tvSensorName.setId(1);
                    tvSensorName.setTextColor(Color.BLACK);
                    tvSensorName.setTextSize(16);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    params.setMargins(idp8, 0, idp8, 0);
                    tvSensorName.setLayoutParams(params);
//                    button1.setLayoutParams(params);
                    //                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);

                    String strSensorName = mapCodingInfo.get("Sensor Name");

                    Log.d(TAG, "strSensorName=" + strSensorName);

//                    tvSensorName.setText(mapCodingInfo.get(mapCodingInfo.get("Sensor Name")));
                    tvSensorName.setText(strSensorName);
                    Log.d(TAG, "tvSensorName:" + tvSensorName.getText().toString());
//                    tvSensorName.setGravity(Gravity.LEFT);
                    sensorLayout.addView(tvSensorName);
                    tvSensorValue = new TextView(this.activity);
                    tvSensorValue.setId(2);
                    tvSensorValue.setTextColor(Color.BLACK);
                    tvSensorValue.setTextSize(16);
//                    tvSensorValue.setGravity(Gravity.RIGHT);
                    params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    params.setMargins(idp8, 0, idp8, 0);
                    tvSensorValue.setLayoutParams(params);
                    sensorLayout.addView(tvSensorValue);
//                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 2);
                    this.mapSensorViews.put(sensorId, tvSensorValue);

//                    sensorLayout.setLayoutParams(layoutParams);
                    linearLayout.addView(sensorLayout);
//                    linearLayout.setBackgroundColor(Color.TRANSPARENT);

                }
                    String strValue = mapCodingInfo.get("Value");
                    float fValue = Float.parseFloat(strValue);
                    tvSensorValue.setText(nfmt.format(fValue));
                    Log.d(TAG, "tvSensorValue:" + tvSensorValue.getText().toString());
//                    v.setText(System.currentTimeMillis() + "\r\n" + strResp);
                }

//                txt1.setText(System.currentTimeMillis() + "\r\n" + strResp);
            }
            else
            {
                Log.d(TAG, "handleMessage() Not my message, msg.what=" + msg.what);
                super.handleMessage(msg);
            }

            Log.d(TAG, "handleMessage() End.");
        }
    }
}
