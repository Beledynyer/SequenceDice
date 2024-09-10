package com.example.p1t2_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void playBtnClicked(View v){
        Intent i = new Intent(this, PlayerChoiceActivity.class);
        startActivity(i);
    }
    public void goToLeaderboard(View v){
        Intent i = new Intent(this, LeaderboardActivity.class);
        startActivity(i);
    }
    public void goToGameRules(View v){
        Intent i = new Intent(this, GameRulesActivity.class);
        startActivity(i);
    }
}