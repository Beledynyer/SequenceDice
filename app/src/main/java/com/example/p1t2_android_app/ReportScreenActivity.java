package com.example.p1t2_android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ReportScreenActivity extends AppCompatActivity {

    private TextView winnerLbl;
    private TextView roundsLbl;
    private TextView tokensPlacedLbl;
    private TextView tokensRemovedLbl;
    private TextView longestSequencesLbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_screen);

        // Get the UI elements
        winnerLbl = findViewById(R.id.leaderboardTextView);
        roundsLbl = findViewById(R.id.RoundsLbl);
        tokensPlacedLbl = findViewById(R.id.TokensPlacedLbl);
        tokensRemovedLbl = findViewById(R.id.TokensRemovedLbl);
        longestSequencesLbl = findViewById(R.id.longestSequences);

        // Retrieve the data from the Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String winner = extras.getString("winner");
            int rounds = extras.getInt("rounds");
            int tokensPlaced = extras.getInt("tokensPlaced");
            int tokensRemoved = extras.getInt("tokensRemoved");
            String longestSequences = extras.getString("longestSequences");

            // Update the UI elements with the retrieved data
            winnerLbl.setText("Winner: " + winner);
            roundsLbl.setText("Rounds Played: " + rounds);
            tokensPlacedLbl.setText("Tokens Placed: " + tokensPlaced);
            tokensRemovedLbl.setText("Tokens Removed: " + tokensRemoved);
            longestSequencesLbl.setText(longestSequences);
        }
    }
    public void goHome(View v){
        Intent i  = new Intent(this, MainScreenActivity.class);
        startActivity(i);
    }
}
