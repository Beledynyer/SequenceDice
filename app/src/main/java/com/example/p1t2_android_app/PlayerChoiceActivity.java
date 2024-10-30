package com.example.p1t2_android_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.model.Player;
import com.example.network.GameCommand;
import com.example.network.NetworkManager;

import java.util.ArrayList;
import java.util.List;

public class PlayerChoiceActivity extends AppCompatActivity implements NetworkManager.NetworkCallback {

    private TextView statusText;
    private NetworkManager networkManager;
    private List<Player> gamePlayers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_choice);

        statusText = findViewById(R.id.statusText);

        // Remove the play button setup since we won't need it
        networkManager = new NetworkManager();
        networkManager.connectToServer(this);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onPlayerInfoReceived(List<Player> players) {
        runOnUiThread(() -> {
            gamePlayers.clear();
            gamePlayers.addAll(players);

            StringBuilder playerInfo = new StringBuilder("Players in game:\n");
            for (Player player : players) {
                playerInfo.append(String.format("Player %d (%s)\n",
                        player.getId(), player.getColour()));
            }

            statusText.setText(playerInfo.toString());

            // If we have both players, automatically start the game
            if (players.size() == 2) {
                startGame();
            }
        });
    }

    private void startGame() {
        GameCommand joinCommand = new GameCommand(GameCommand.CommandType.JOIN_GAME, "CLIENT", "");
        networkManager.sendCommandAsync(
                joinCommand,
                this::onJoinGameSuccess,
                this::onJoinGameError
        );
    }

    private void onJoinGameSuccess() {
        Intent intent = new Intent(this, GamePlayScreenActivity.class);
        intent.putExtra("players", new ArrayList<>(gamePlayers));
        startActivity(intent);
    }

    private void onJoinGameError(Exception e) {
        Log.e("PlayerChoiceActivity", "Failed to send JOIN_GAME command", e);
        runOnUiThread(() ->
                Toast.makeText(this, "Failed to start game: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onConnected() {
        runOnUiThread(() -> {
            statusText.setText("Connected to server. Waiting for other players...");
        });
    }

    @Override
    public void onError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onDiceRollResult(int playerIndex, int diceTotal) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkManager.disconnect();
    }
}