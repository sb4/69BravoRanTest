package com.example.android.bravo69rantest;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SqlTestActivity extends AppCompatActivity
{
    public static DatabaseHelper myDbh;
    private Button buttonExec;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql_test);
        buttonExec = findViewById(R.id.buttonExec);
        buttonExec.setOnClickListener(new SqlTestActivity.OnClickListenerSqlExec());

        try
        {

            myDbh = new DatabaseHelper(this);
//
//            Cursor cur = myDbh.getWritableDatabase().rawQuery("select * from " + DatabaseHelper.TABLE_NAME, null);
//            TextView textView1 = findViewById(R.id.text1);
//            String[] arstrCols = cur.getColumnNames();
//            StringBuilder sbuf = new StringBuilder();
//
//            while (cur.moveToNext())
//            {
//                for (int i = 0; i < arstrCols.length; i++)
//                {
//                    sbuf.append(arstrCols[i] + " = " + cur.getString(i) + "\n");
//                }
//
//                sbuf.append("\n");
//            }
//
//            cur.close();
//
//            textView1.setText(sbuf.toString());
        } finally
        {
            if (myDbh != null) myDbh.close();
        }
    }

    // ================================== Nested Classes ===================================
    public class OnClickListenerSqlExec implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            StringBuilder sbuf = new StringBuilder();
            Cursor myCur = null;
            TextView tvResult = findViewById(R.id.textViewSqlOutput);

            try
            {

                TextView tv = findViewById(R.id.textViewSqlInput);
                String strSql = tv.getText().toString();

                if (strSql != null && strSql.length() > 0)
                {
                    myCur = myDbh.getWritableDatabase().rawQuery(strSql, null);
                    int iCols = myCur.getColumnCount();

                    while (myCur.moveToNext())
                    {
                        for (int i = 0; i < iCols; i++)
                        {
                            sbuf.append(myCur.getColumnName(i) + " = " + myCur.getString(i) + "\r\n");
                        }

                        sbuf.append("\r\n");
                    }

                    myCur.close();


                    if (sbuf.length() > 0)
                        tvResult.setText(sbuf.toString());
                    else
                        tvResult.setText("**** No result from query received. ****");
                }
            } catch(Exception e)
            {
                tvResult.setText(e.toString());
            } finally
            {
                if (myCur != null) myCur.close();

                myDbh.close();
            }
        }
    }
}
