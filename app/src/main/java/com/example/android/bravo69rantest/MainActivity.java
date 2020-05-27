package com.example.android.bravo69rantest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

//import com.example.android.bravo69rantest.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity
{
    public static DatabaseHelper myDbh;
//    private Button buttonSqlTest;
    private ImageButton buttonSensors;
    private ImageButton buttonWater;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSensors = findViewById(R.id.imageButtonSensors);
        buttonSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openActivity(SensorsActivity.class);
            }
        });

        buttonWater = findViewById(R.id.imageButtonWater);
        buttonWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openActivity(WaterActivity.class);
            }
        });
//        buttonSqlTest = findViewById(R.id.imageViewSensors);
//        buttonSqlTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                openActivity(SensorsActivity.class);
//            }
//        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
//        openActivity(LoginActivity.class);
        ActivityManager am = (ActivityManager)this.getSystemService(this.ACTIVITY_SERVICE);
        int iHeapAvailable = am.getMemoryClass();
        Toast.makeText(this, "iHeapAvailable=" + iHeapAvailable, Toast.LENGTH_LONG).show();
    }

    public void openActivity(Class classActivity)
    {
        Intent intentActivity = new Intent(this, classActivity);
        startActivity(intentActivity);
    }

    // ================================== Nested Classes ===================================
//    public class OnClickListenerSqlTest implements View.OnClickListener
//    {
//        @Override
//        public void onClick(View v)
//        {
//            Intent intentSqlTest = new Intent(MainActivity.this, SqlTestActivity.class);
//           MainActivity.this.startActivity(intentSqlTest);
//        }
//    }

}
