package com.example.queueapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class WindowPage2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_page2); // Ensure this matches the layout file name

        // Optional: Add log to confirm activity loads
        System.out.println("WindowPage2: Activity loaded");
    }
}
