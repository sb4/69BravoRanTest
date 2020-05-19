package com.example.android.bravo69rantest;

import android.icu.text.AlphabeticIndex;

import java.util.Map;

public class RecordDataMapped implements Comparable
{
    public long LobjectId = -1;
    public String objectType = "";
    public String errorCode = "";
    public String errorMessage = "";
    public String csvDataFilePath = "";
    public int iCsvRow = -1;
    public String[] csvData;
    public String mobileRecordId = "";
    public String isAnyChanged; // to support setRecordCodingFields return data. -RAN 1/12/15
    public Map<String,String> mapCodingInfo;
    public RecordDataMapped[] arDetailRecordDataMapped; // optionally null.


    @Override
    public int compareTo(Object o)
    {
        return mapCodingInfo.get("Sensor Name").compareTo(((RecordDataMapped)o).mapCodingInfo.get("Sensor Name"));
    }
}
