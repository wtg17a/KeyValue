package com.example.keyvalue.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.keyvalue.R;

import java.util.Timer;
import java.util.TimerTask;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private TextView tv;
    private TextView hsText;
    private int state = 0;
    private int currentScore = 0;
    private Timer timer = new Timer(true);
    private Timer timer2 = new Timer(true);
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        tv = root.findViewById(R.id.scoreText);
        hsText = root.findViewById(R.id.highScoreText);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) { tv.setText(s);
            }
        });

        if(!sharedPref.contains(getString(R.string.button_game_high_score)))
        {
            editor.putInt(getString(R.string.button_game_high_score), 0);
            editor.commit();
        }

        hsText.setText("High Score: ");

        Button b = root.findViewById(R.id.mash_button_2);
        b.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if(state == 0)
                {
                    state = 1;
                    currentScore = 1;
                    tv.setText("" + currentScore + '!');
                    timer.schedule(new TimerTask()
                    {
                        public void run()
                        {
                            state = 2;
                            if(currentScore > sharedPref.getInt(getString(R.string.button_game_high_score), 0))
                            {
                                editor.putInt(getString(R.string.button_game_high_score), currentScore);
                                editor.commit();
                            }

                            getActivity().runOnUiThread(new Runnable()
                            {
                                public void run()
                                {
                                    int hs = sharedPref.getInt(getString(R.string.button_game_high_score), 0);
                                    tv.setText("Time up!");
                                    hsText.setText("High Score: " + hs);
                                }
                            });

                            timer2.schedule(new TimerTask()
                            {
                                public void run()
                                {
                                    state = 0;
                                }
                            }, 2000);
                        }
                    }, 5000);
                }
                else if(state == 1)
                {
                    ++currentScore;
                    tv.setText("" + currentScore + '!');
                }

            }
        });

        return root;
    }
}