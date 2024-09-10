package com.example.p1t2_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.model.Game;
import com.example.model.Player;

import java.util.ArrayList;

public class GamePlayScreenActivity extends AppCompatActivity {

    private BoardView boardView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        boardView = findViewById(R.id.board2);

        // Retrieve the list of players from the intent
        Intent intent = getIntent();
        ArrayList<Player> players = (ArrayList<Player>) intent.getSerializableExtra("players");

        // Initialize the game with the players
        assert players != null;
        boardView.game = new Game(players);

        // Setup UI components
        TextView playerLabel = findViewById(R.id.playerLabel);
        TextView dieLabel = findViewById(R.id.leaderboardTextView);
        Button playAgainBtn = findViewById(R.id.playAgainBtn);
        Button exitBtn = findViewById(R.id.homeBtn);
        ImageView die1 = findViewById(R.id.die1_view);
        ImageView die2 = findViewById(R.id.die2_view);
        boardView.setUpGame(playAgainBtn, exitBtn, playerLabel, dieLabel,die1,die2,this);
        boardView.gameObserver.oneTurn();
    }
    public void home(View v){
        Intent i = new Intent(this, MainScreenActivity.class);
        startActivity(i);
    }

    public void playAgainClicked(View v){
        boardView.resetGameBoard();
        boardView.invalidate();
        boardView.gameObserver.oneTurn();
    }
}