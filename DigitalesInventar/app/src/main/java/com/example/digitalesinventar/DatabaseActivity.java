package com.example.digitalesinventar;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Timestamp;

public class DatabaseActivity {


    public static void addEntry(String name, Float time) {
        Log.i("addEntry", name + " item added");
        Log.i("addEntry", time + " time");
    }

}
