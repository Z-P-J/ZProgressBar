package com.zpj.progressbar.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.zpj.progressbar.ZProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ZProgressBar progressBar;
    private Button restartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_test);
        restartBtn = findViewById(R.id.btn_restart);
        progressBar.setProgress(0);
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
        start();

//        Timer timer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                progressBar.setProgress(progressBar.getProgress() + 4);
//            }
//        };
//        timer.schedule(task, 0, 100);



        ZProgressBar bar = findViewById(R.id.progress_test2);
        Switch switchIntermediate = findViewById(R.id.switch_intermediate);
        TextView tvProgressBarWidth = findViewById(R.id.tv_progress_bar_width);
        TextView tvProgress = findViewById(R.id.tv_progress);
        TextView tvMaxProgress = findViewById(R.id.tv_max_progress);
        TextView tvMinProgress = findViewById(R.id.tv_min_progress);
        SeekBar progressBarWidthSeekBar = findViewById(R.id.seek_progress_bar_width);
        SeekBar progressSeekBar = findViewById(R.id.seek_progress);
        SeekBar maxProgressSeekBar = findViewById(R.id.seek_max_progress);
        SeekBar minProgressSeekBar = findViewById(R.id.seek_min_progress);


        switchIntermediate.setChecked(bar.isIntermediateMode());
        switchIntermediate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                progressSeekBar.setEnabled(!b);
                minProgressSeekBar.setEnabled(b);
                bar.setIsIntermediateMode(b);
                if (!b) {
                    tvProgress.setText("Progress(" + (int) bar.getProgress() + ")");
                }
            }
        });

        bar.post(new Runnable() {
            @Override
            public void run() {
                progressBarWidthSeekBar.setMax((int) bar.getRadius() - 1);
                progressBarWidthSeekBar.setProgress((int) bar.getProgressBarWidth());
                tvProgressBarWidth.setText("ProgressBar Width(" + (int) bar.getProgressBarWidth() + ")");
                progressBarWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        bar.setProgressBarWidth(i);
                        tvProgressBarWidth.setText("ProgressBar Width(" + i + ")");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        });




        progressSeekBar.setProgress((int) bar.getProgress());
        progressSeekBar.setEnabled(!bar.isIntermediateMode());
        progressSeekBar.setMax((int) bar.getMaxProgress());
        tvProgress.setText("Progress(" + progressSeekBar.getProgress() + ")");
        progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                bar.setProgress(i);
                tvProgress.setText("Progress(" + i+ ")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        maxProgressSeekBar.setProgress((int) bar.getMaxProgress());
        tvMaxProgress.setText("Max Progress(" + maxProgressSeekBar.getProgress() + ")");
        maxProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                bar.setMaxProgress(i);
                progressSeekBar.setMax(i);
                minProgressSeekBar.setMax(i);
                tvMaxProgress.setText("Max Progress(" + i + ")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        minProgressSeekBar.setProgress((int) bar.getMinProgress());
        minProgressSeekBar.setMax((int) bar.getMaxProgress());
        minProgressSeekBar.setEnabled(bar.isIntermediateMode());
        tvMinProgress.setText("Min Progress(" + minProgressSeekBar.getProgress() + ")");
        minProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                bar.setMinProgress(i);
                tvMinProgress.setText("Min Progress(" + i + ")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void start() {
        restartBtn.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 1; i <= 25; i++) {
                        Thread.sleep(100);
                        progressBar.setProgress(i * 4);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        restartBtn.setEnabled(true);
                    }
                });
            }
        }).start();
    }

}