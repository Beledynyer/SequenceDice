package com.example.p1t2_android_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.model.Game;
import com.example.model.Observer;
import com.example.model.Player;

public class GameObserver implements Observer {
    private Button playAgainBtn;
    private Button exitBtn;
    private TextView playerTurn;
    private TextView dieText;

    Die die;
    public Game game;
    public int diceTotal;

    int totalTurns = 0;
    private Context context;

    public GameObserver(Button b1, Button b2, TextView textView, TextView t2, ImageView d1, ImageView d2, Context context) {
        playAgainBtn = b1;
        exitBtn = b2;
        playerTurn = textView;
        dieText = t2;
        die = new Die(d1, d2);
        this.context = context;  // Initialize context

        // Set listener to handle the result when dice roll is done
        die.setOnDiceRollCompleteListener(new Die.OnDiceRollCompleteListener() {
            @Override
            public void onDiceRollComplete(int total) {
                onDiceRolled(total); // Call the onDiceRolled method
                diceTotal = total; // Save the total
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTurnStarted(Player player) {
        playerTurn.setText(player.getName() + "'s Turn");

        if (player.getColour().equals("Red")) {
            playerTurn.setTextColor(Color.RED);
        } else if (player.getColour().equals("Blue")) {
            playerTurn.setTextColor(Color.BLUE);
        } else {
            playerTurn.setTextColor(Color.GREEN);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDiceRolled(int total) {
        String text = "";
        if (total == 10) {
            text = "Defensive roll";
        } else if (total == 11) {
            text = "Wild roll!";
        } else if (total == 12 || total == 2) {
            text = "Another turn!";
        }
        dieText.setText(text);
        totalTurns++;
    }

    public void oneTurn() {
        this.onTurnStarted(game.players.get(game.currentPlayerIndex));
        die.rollDice(); // This will roll the dice and call onDiceRolled when done
    }

    public boolean winnerCheck() {
        if (game.isGameWon(game.players.get(game.currentPlayerIndex))) {
            this.onGameWon(game.players.get(game.currentPlayerIndex));
            return true;
        }
        return false;
    }

    @Override
    public void onGameStarted() {
    }

    @Override
    public boolean onTokenPlaced(Player player, int row, int col) {

        return true;
    }

    @Override
    public boolean onTokenRemoved(Player player, int row, int col) {

        return true;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onGameWon(Player player) {
        LeaderboardManager leaderboardManager = new LeaderboardManager(context);
        String winningTeamOrPlayer;

        if (game.players.size() == 4) {
            playerTurn.setText(player.getColour() + " team Won!!!!");
            winningTeamOrPlayer = player.getColour() + " team";

            int teamId = player.getId() % 2;
            for (int i = 0; i < 4; i++) {
                Player teamPlayer = game.players.get(i);
                if (teamPlayer.getId() % 2 == teamId) {
                    leaderboardManager.addPlayerIfNew(teamPlayer.getName());
                    leaderboardManager.incrementWin(teamPlayer.getName());
                }
                else {
                    leaderboardManager.addPlayerIfNew(teamPlayer.getName());
                }
            }
        } else {
            playerTurn.setText(player.getName() + " Won!!!!");
            winningTeamOrPlayer = player.getName();

            for(int i = 0 ;  i < game.players.size();i++){
                leaderboardManager.addPlayerIfNew(game.players.get(i).getName());
                if(game.players.get(i).getName().equals(winningTeamOrPlayer)){
                    leaderboardManager.incrementWin(player.getName());
                }
            }
        }

        int rounds = totalTurns / game.players.size();
        String longestSequences = game.getLongestSequences();

        // Pass the details to ReportScreenActivity
        Intent intent = new Intent(context, ReportScreenActivity.class);
        intent.putExtra("winner", winningTeamOrPlayer);
        intent.putExtra("rounds", rounds);
        intent.putExtra("tokensPlaced", game.getTokensPlaced());
        intent.putExtra("tokensRemoved", game.getTokensRemoved());
        intent.putExtra("longestSequences", longestSequences);

        context.startActivity(intent);
    }

}
