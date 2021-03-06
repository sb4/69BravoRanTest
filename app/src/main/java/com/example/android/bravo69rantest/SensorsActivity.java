package com.example.android.bravo69rantest;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rco.water.businesslogic.rms.Rms;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;

public class SensorsActivity extends AppCompatActivity
{
    public static final String TAG = "SensorsActivity";
    public static Object lockDataThread = new Object();

    public float dpRatio;
    public int idp8;
//    public static String strUrl = "https://www.rcofalcon.com/Image2000/rest/recordservice/getRecordsUpdatedXFiltered/69BAdmin/69BAdmin/Sensor/-5000/+/+/+/+/Active/+/true/+/+/SensorId%2CSensor+Name%2CTitle%2CValue%2CSensorType/+/+";
    public static String URL_ACTIVITY = InternetUtils.HOST_PAT + "/Image2000/rest/recordservice/getRecordsUpdatedXFiltered/" + InternetUtils.LOGIN_PAT + "/" + InternetUtils.PASSWORD_PAT + "/Sensor/-5000/+/+/+/+/Active/+/true/+/+/SensorId%2CSensor+Name%2CTitle%2CValue%2CSensorType/+/+";
//    public static String strUrlWater = "https://localhost/Image2000/rest/recordservice/getRecordsUpdatedXFiltered/69BAdmin/69BAdmin/Analytic/-5000/+/+/+/+/SampleRateUnits/+/now/+/+/+/+/+";

    public TextView txt1;

    private Button buttonGetData;
    private Handler httpHandler;
    private SensorsActivity.dataWorker dataWorker;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dpRatio = this.getResources().getDisplayMetrics().density;
        idp8 = (int) (8 * dpRatio);
        url = InternetUtils.getResolvedUrl(URL_ACTIVITY, Rms.getUrl(), Rms.getUsername(), Rms.getPassword());

        Log.d(TAG, "onCreate() Start.");
        setContentView(R.layout.activity_sensors);

        httpHandler = new DataResultHandler(this, Looper.getMainLooper());
        Log.d(TAG, "onCreate() End.");

        Log.d(TAG, "onCreate() Rms.getUrl()=" + Rms.getUrl());
        Toast.makeText(this, "[" + TAG + ".onCreate] Rms.getUrl()=" + Rms.getUrl(), Toast.LENGTH_LONG).show();;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        startDataWorkerThread(url);
        Log.d(TAG, "onStart() Start, End.");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause() Start.");

        if (dataWorker != null)
        {
            Log.d(TAG, "onPause() calling  killSensorDataWorkerThread() and setting member to null.");
            killSensorDataWorkerThread();
            this.dataWorker = null;
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
        if (dataWorker != null)
        {
            Log.d(TAG, "onPause() calling  sensorDataWorker.kill().");
//            synchronized (lockDataThread)
//            {
//                lockDataThread.notify();
//            }

            dataWorker.kill();
        }
    }

    /**
     * @param view
     */
    @Deprecated // just for testing.
    public void onClickButtonGetData(View view)
    {
        Log.d(TAG, "onClickButtonGetData start.");
        startDataWorkerThread(url);
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

    private void startDataWorkerThread(String url)
    {
        Log.d(TAG, "startSensorDataWorkerThread() Start.");

        if (dataWorker == null || !dataWorker.isAlive())
        {
            Log.d(TAG, "startDataWorkerThread() Starting new sensorDataWorker thread.");
            Log.d(TAG, "onClickButtonGetData starting sensorDataWorker thread.");
            dataWorker = new dataWorker(httpHandler, url);
            dataWorker.start();
        } else
            Log.d(TAG, "startDataWorkerThread() sensorDataWorker thread already started.");

        Log.d(TAG, "startDataWorkerThread() End.");
    }

    public static String getData(String url) throws IOException
    {
        String strResp = InternetUtils.getResponse(url);

        Log.d(TAG, "getSensorData() strResp=" + strResp);

        return strResp;
    }

    /**
     *
     */
    public static class dataWorker extends Thread
    {
        public static final String TAG = "dataWorker";
        private AtomicBoolean isCanceled = new AtomicBoolean(false);
        private String strResp;
        private Handler responseHandler;
        private String url;

        public dataWorker(Handler responseHandler, String url)
        {
            this.responseHandler = responseHandler;
            this.url = url;
            Log.d(TAG, "Exiting constructor.");
        }

        @Override
        public void run()
        {
            Log.d(TAG, "run() Start.");

            while (!isCanceled.get())
            {
                try
                {
                    Log.d(TAG, "run() About to call getData().");
                    try
                    {
                        strResp = getData(url);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                        strResp = Arrays.toString(e.getStackTrace());
                    }
//                    strResp = "My Fake Response... Hah!" +
//                            " time:" + System.currentTimeMillis();
                    if (false)
                        throw new IOException("My Fake IO Exception, just to make compiler happy.");
                    Log.d(TAG, "run() After call getData().  strResp=\r\n" + strResp);
                    Bundle bndl = new Bundle();
                    bndl.putCharSequence("response", strResp);
//                    String strTest = bndl.getString("response");

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
                this.isCanceled.set(true);
                lockDataThread.notify();
            }

        }
    }

    /**
     *
     */
    public static class DataResultHandler extends Handler
    {
        public static final String TAG = "SensorHandler";
        private Gson gson = new Gson();

        public AppCompatActivity activity;
        private Map<String, TextView> mapSensorViews = new HashMap<String, TextView>();
        public float dpRatio;
        private DecimalFormat nfmt;
        private int idp8;
        private SimpleDateFormat dfmt;
        private Comparator<RecordDataMapped> compData;
        private U.ISimpleFilter filt;
        public static final String COMPARE_FIELD = "Title";

        public DataResultHandler(AppCompatActivity activity, Looper looper)
        {
            super(looper);
            this.activity = activity;
            dpRatio = activity.getResources().getDisplayMetrics().density;
            idp8 = (int) (8 * dpRatio);
            nfmt = new DecimalFormat("################.#");
            Log.d(TAG, "Exiting constructor.");
            dfmt = new SimpleDateFormat("HH:mm:ss");

            filt = new U.ISimpleFilter()
            {
                @Override
                public String get(String val)
                {
                    // Assume val starts with a number followed by ".".  If single digit number, pad with leading zero.
                    int iDot = val.indexOf(".");
                    if (iDot == 1)
                        return "0" + val;
                    else return val;
                }
            };

            // Initialize comparator used to sort sensor records by Sensor Name.
            compData = new Comparator<RecordDataMapped>()
            {
                @Override
                public int compare(RecordDataMapped o1, RecordDataMapped o2)
                {
                    return o1.getSortVal(COMPARE_FIELD, filt).compareTo(o2.getSortVal(COMPARE_FIELD, filt));
                }
            };

        }

//        public static final String ifNull(String str)
//        {
//            if (str == null) str = "";
//            return str;
//        }


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

                RecordDataMapped[] arRecsAll = gson.fromJson(strResp, RecordDataMapped[].class);

                RecordDataMapped[] arRecs = new RecordDataMapped[arRecsAll.length];
                int ix = 0;

                // one pass to eliminate certain sensors before sort.
                for (int i = 0; i < arRecsAll.length; i++)
                {
                    RecordDataMapped rec = arRecsAll[i];
                    int iObjectId = (int) rec.LobjectId;
                    Map<String, String> mapCodingInfo = rec.mapCodingInfo;
                    String strSensorId = mapCodingInfo.get("SensorId");
                    String strSensorName = mapCodingInfo.get("Sensor Name");
                    String strSensorType = mapCodingInfo.get("Sensor Type");
                    if (!U.isIgnoreSensor(strSensorId, strSensorName, strSensorType))
                        arRecs[ix++] = rec;
                }

                int iSensors = ix;

                Log.d(TAG, "iSensors=" + iSensors + ", arRecs.length=" + arRecs.length
                        + ", arRecs={" + Arrays.toString(arRecs) + "}");


                Arrays.sort(arRecs, 0, ix, compData);

                Log.d(TAG, "handleMessage() Start. strResp=" + strResp + ", arRecs.length=" + arRecs.length);
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

                for (int i = 0; i < iSensors; i++)
                {
                    RecordDataMapped rec = arRecs[i];
                    int iObjectId = (int) rec.LobjectId;
                    Map<String, String> mapCodingInfo = rec.mapCodingInfo;

                    Log.d(TAG, "i=" + i + ", mapCodingInfo=" + mapCodingInfo);
                    String sensorId = mapCodingInfo.get("SensorId");
                    String strSensorTitle = mapCodingInfo.get("Title");

                    TextView tvSensorValue = mapSensorViews.get(sensorId);

                    if (tvSensorValue == null)
                    {
                        Log.d(TAG, "handleMessage() creating tv TextView, adding to linearLayout.");
                        RelativeLayout sensorLayout = new RelativeLayout(this.activity);
//                        RelativeLayout.LayoutParams vparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        // ******* Note - must use LinearLayout.LayoutParams, the layout type of the parent. -RAN 5/24/2020
                        LinearLayout.LayoutParams vparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        vparams.setMargins(0, idp8*2, 0, idp8*2);
//                        vparams.height = 4*idp8;
                        sensorLayout.setLayoutParams(vparams); // -RAN 5/24/2020
//                    sensorLayout.setBackgroundColor(Color.GREEN);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    sensorLayout.setOrientation(RelativeLayout.HORIZONTAL);
//                    sensorLayout.setLayoutParams(layoutParams);
//                    sensorLayout.getLayoutParams().width = ActionBar.LayoutParams.MATCH_PARENT;
                        TextView tvSensorTitle = new TextView(this.activity);
                        tvSensorTitle.setId(1);
                        tvSensorTitle.setTextColor(Color.BLACK);
                        tvSensorTitle.setTextSize(16);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                        params.setMargins(idp8, 0, idp8, 0);
                        tvSensorTitle.setLayoutParams(params);
//                    button1.setLayoutParams(params);
                        //                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);


                        Log.d(TAG, "strSensorTitle=" + strSensorTitle);

//                    tvSensorTitle.setText(mapCodingInfo.get(mapCodingInfo.get("Sensor Name")));
                        tvSensorTitle.setText(strSensorTitle);
                        Log.d(TAG, "tvSensorTitle:" + tvSensorTitle.getText().toString());
//                    tvSensorTitle.setGravity(Gravity.LEFT);
                        sensorLayout.addView(tvSensorTitle);
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
            } else
            {
                Log.d(TAG, "handleMessage() Not my message, msg.what=" + msg.what);
                super.handleMessage(msg);
            }

            Log.d(TAG, "handleMessage() End.");
        }
    }
}
