package com.example.p1t2_android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaderboardActivity extends AppCompatActivity {
    private TextView leaderboardTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        leaderboardTextView = findViewById(R.id.leaderboardTextView);

        LeaderboardManager leaderboardManager = new LeaderboardManager(this);
        Map<String, Integer> leaderboard = leaderboardManager.getAllPlayers();

        // Check if the leaderboard is empty
        if (!leaderboard.isEmpty()) {
            // Sort the leaderboard in descending order based on wins
            List<Map.Entry<String, Integer>> sortedLeaderboard = new ArrayList<>(leaderboard.entrySet());
            Collections.sort(sortedLeaderboard, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

            // Get the top 10 players (or fewer if there are less than 10 players)
            List<Map.Entry<String, Integer>> top10Players = sortedLeaderboard.stream()
                    .limit(10)
                    .collect(Collectors.toList());

            // Prepare the leaderboard text
            StringBuilder leaderboardText = new StringBuilder();
            for (Map.Entry<String, Integer> entry : top10Players) {
                leaderboardText.append(entry.getKey()).append(": ").append(entry.getValue()).append(" wins\n");
            }

            // Set the leaderboard text to the TextView
            leaderboardTextView.setText(leaderboardText.toString());
        }
        // If leaderboard is empty, the TextView will remain unchanged with its current message
    }

    public void goHome(View v) {
        Intent i = new Intent(this, MainScreenActivity.class);
        startActivity(i);
    }
}
