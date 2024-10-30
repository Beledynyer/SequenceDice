package com.example.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.model.Player;
import com.example.network.GameCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NetworkManager {
    private static final String SERVER_IP = "10.0.2.2";
    private static final int SERVER_PORT = 8888;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Handler handler;

    public interface NetworkCallback {
        void onConnected();
        void onPlayerInfoReceived(List<Player> players);
        void onError(String message);
        void onDiceRollResult(int playerIndex, int diceTotal);
    }

    public NetworkManager() {
        handler = new Handler(Looper.getMainLooper());
    }

    private NetworkCallback callback;

    // ... other existing code ...

    public void setCallback(NetworkCallback callback) {
        this.callback = callback;
    }
    public void connectToServer(final NetworkCallback callback) {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                handler.post(callback::onConnected);
                listenForServerMessages(callback);
            } catch (IOException e) {
                handler.post(() -> callback.onError("Failed to connect: " + e.getMessage()));
            }
        }).start();
    }

    private void listenForServerMessages(NetworkCallback callback) {
        new Thread(() -> {
            try {
                while (true) {
                    GameCommand command = (GameCommand) in.readObject();
                    processCommand(command, callback);
                }
            } catch (IOException | ClassNotFoundException e) {
                handler.post(() -> callback.onError("Connection lost: " + e.getMessage()));
            }
        }).start();
    }

    private List<Player> allPlayers = new ArrayList<>();

    private void processCommand(GameCommand command, NetworkCallback callback) {
        switch (command.getType()) {
            case DICE_ROLL_RESULT:
                int playerIndex = Integer.parseInt(command.getPlayerId());
                int diceTotal = Integer.parseInt(command.getData());
                callback.onDiceRollResult(playerIndex, diceTotal);
                break;
            case GAME_SETUP:
                Log.d("NetworkManager", "Received GAME_SETUP: " + command.getData());
                // Clear previous players when new game setup arrives
                allPlayers.clear();
                break;

            case PLAYER_INFO:
                Log.d("NetworkManager", "Received PLAYER_INFO: " + command.getData());
                processPlayerInfo(command.getData(), callback);
                break;

            case GAME_STARTED:
                Log.d("NetworkManager", "Received GAME_STARTED");
                if (!allPlayers.isEmpty()) {
                    handler.post(() -> callback.onPlayerInfoReceived(new ArrayList<>(allPlayers)));
                }
                break;

            default:
                Log.d("NetworkManager", "Received unknown command: " + command.getType());
        }
    }

    private void processPlayerInfo(String data, NetworkCallback callback) {
        String[] parts = data.split("\\|");
        if (parts.length == 4) { // Format: PLAYER_INFO|id|name|color
            int id = Integer.parseInt(parts[1]);
            String name = parts[2];
            String color = parts[3];

            Player player = new Player(id, name, color);

            // Check if this player is already in the list
            boolean playerExists = false;
            for (int i = 0; i < allPlayers.size(); i++) {
                if (allPlayers.get(i).getId() == id) {
                    allPlayers.set(i, player);
                    playerExists = true;
                    break;
                }
            }

            if (!playerExists) {
                allPlayers.add(player);
            }

            // Only notify when we have all players
            if (allPlayers.size() == 2) {
                handler.post(() -> callback.onPlayerInfoReceived(new ArrayList<>(allPlayers)));
            }
        }
    }

    public void sendCommand(GameCommand command) throws IOException {
        out.writeObject(command);
        out.flush();
    }

    public void sendCommandAsync(GameCommand command, Runnable onSuccess, Consumer<Exception> onError) {
        new Thread(() -> {
            try {
                sendCommand(command);
                handler.post(onSuccess);
            } catch (IOException e) {
                handler.post(() -> onError.accept(e));
            }
        }).start();
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}