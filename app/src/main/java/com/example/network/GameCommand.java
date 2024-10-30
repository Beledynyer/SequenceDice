package com.example.network;

import java.io.Serializable;

public class GameCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum CommandType {
        JOIN_GAME,
        GAME_STARTED,
        PLAYER_INFO,    // Used for sending player assignments
        DICE_ROLL,
        DICE_ROLL_RESULT,
        MAKE_MOVE,
        MOVE_RESULT,
        PLAYER_DISCONNECTED,
        GAME_STATE,
        GAME_WIN,
        GAME_END,
        ERROR,
        GAME_SETUP
    }

    private final CommandType type;
    private final String playerId;
    private final String data;

    public GameCommand(CommandType type, String playerId, String data) {
        this.type = type;
        this.playerId = playerId;
        this.data = data;
    }

    // Getters
    public CommandType getType() { return type; }
    public String getPlayerId() { return playerId; }
    public String getData() { return data; }
}
