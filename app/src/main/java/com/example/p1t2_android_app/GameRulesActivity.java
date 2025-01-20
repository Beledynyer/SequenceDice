package com.example.p1t2_android_app;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GameRulesActivity extends AppCompatActivity {

    private TextView rulesTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_rules);

        rulesTextView = findViewById(R.id.rulesTextView);

        loadGameRules();
    }

    /**
     * read game rules from text file
     */
    private void loadGameRules() {
        AssetManager assetManager = getAssets(); //text file in assets directory
        try (InputStream is = assetManager.open("game_rules.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            builder.append("\n\nWatch how to play video");
            SpannableString spannableString = new SpannableString(builder.toString());
            //clickable link
            ClickableSpan clickableSpan = new ClickableSpan() {
                //Intent
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            android.net.Uri.parse("https://www.youtube.com/watch?v=3o3EWAIfU10"));
                    startActivity(intent);
                }
            };

            int start = builder.indexOf("Watch how to play video");
            int end = start + "Watch how to play video".length();
            spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            rulesTextView.setText(spannableString);
            //for link to respond to user clicks
            rulesTextView.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goHome(View v) {
        Intent i = new Intent(this, MainScreenActivity.class);
        startActivity(i);
    }
}
