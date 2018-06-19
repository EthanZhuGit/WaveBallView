package com.example.waveballview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;

import com.example.waveballview.view.WaveBezierView;
import com.example.waveballview.view.WaveChargeView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TAG" + "MainActivity";
//    private SeekBar mSeekBar;
//    private WaveBezierView mWaveBezierView;
//    private WaveChargeView mWaveChargeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
//        mWaveBezierView = (WaveBezierView) findViewById(R.id.Wave1);
//        mWaveChargeView = (WaveChargeView) findViewById(R.id.Wave2);
//        mWaveBezierView.setDistance(String.valueOf(0));
//        mWaveBezierView.setWorkType("0");
//        mWaveBezierView.setProgress(0);

//        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Log.d(TAG, "onProgressChanged: " + progress);
//                mWaveBezierView.setProgress(progress / 100.0f);
//                mWaveBezierView.setDistance(String.valueOf(progress));
//                mWaveBezierView.setWorkType(String.valueOf(progress));
//                mWaveChargeView.setProgress(progress / 100f);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
