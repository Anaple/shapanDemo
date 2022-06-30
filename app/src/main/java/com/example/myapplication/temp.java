package com.example.myapplication;

import android.os.Bundle;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class temp extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIOperation.SetFullScreen(this);
        VideoView videoView = findViewById(R.id.sss);
        videoView.setVideoPath("/sdcard/DCIM/sh.mp4");
    }
}
