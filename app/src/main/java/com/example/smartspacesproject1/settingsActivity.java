package com.example.smartspacesproject1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

public class settingsActivity extends FragmentActivity
{
    private static Double smallThreshold;
    private static Double largeThreshold;
    private static int windowSize;

    private TextView windowsizeText, smallthreshText, largethreshText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        windowsizeText = (TextView) findViewById(R.id.windowsize);
        smallthreshText = (TextView) findViewById((R.id.smallthresh));
        largethreshText = (TextView) findViewById((R.id.largethresh));

        windowsizeText.setText(""+windowSize);
        smallthreshText.setText(""+smallThreshold);
        largethreshText.setText(""+largeThreshold);


        Button backButton = (Button) findViewById(R.id.settings);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public void UpWindow(View v)
    {
        windowSize+=1;
        windowsizeText.setText(""+windowSize);
    }

    public void DownWindow(View v)
    {
        windowSize-=1;
        windowsizeText.setText(""+windowSize);
    }

    public void UpSmallThresh(View v)
    {
        smallThreshold+=0.5;
        smallthreshText.setText(""+smallThreshold);
    }

    public void DownSmallThresh(View v)
    {
        smallThreshold-=0.5;
        smallthreshText.setText(""+smallThreshold);
    }

    public void UpLargeThresh(View v)
    {
        largeThreshold+=0.5;
        largethreshText.setText(""+largeThreshold);
    }

    public void DownLargeThresh(View v)
    {
        largeThreshold-=0.5;
        largethreshText.setText(""+largeThreshold);
    }



    public static Double getSmallThreshold()
    {
        return smallThreshold;
    }

    public static Double getLargeThreshold()
    {
        return largeThreshold;
    }

    public static int getWindowSize()
    {
        return windowSize;
    }
}
