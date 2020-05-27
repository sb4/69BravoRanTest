package com.example.android.bravo69rantest;

public class U
{
    public static boolean isWaterSensor(String sensorId, String sensorName, String sensorType)
    {
        boolean bRet = false;
        if (sensorName != null)
        {
            String strLc = sensorType.toLowerCase();
            if (strLc.contains("flow") || strLc.contains("gallons")) return true;
        }

        return bRet;
    }

    public static boolean isIgnoreSensor(String sensorId, String sensorName, String sensorType)
    {
        boolean bRet = false;
        if (sensorName != null)
        {
            String strLc = sensorName.toLowerCase();
            if (strLc.contains("flow") || strLc.contains("gallons")) return true;
        }

        return bRet;
    }

    public static interface ISimpleFilter
    {
        String get(String val);
    }
}
