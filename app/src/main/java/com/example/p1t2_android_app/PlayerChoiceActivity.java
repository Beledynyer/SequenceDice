package com.example.p1t2_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.model.Player;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerChoiceActivity extends AppCompatActivity {

    private LinearLayout playerInputContainer;
    private final String[] allColors = {"Select colour", "Red", "Blue", "Green"};  // Include "Select colour" option

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_choice);

        playerInputContainer = findViewById(R.id.playerInputContainer);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            playerInputContainer.removeAllViews();
            if (checkedId == R.id.radioButton2Players) {
                addPlayerInputs(2, allColors);
            } else if (checkedId == R.id.radioButton3Players) {
                addPlayerInputs(3, allColors);
            } else if (checkedId == R.id.radioButton4Players) {
                addPlayerInputsForTeams(2, new String[]{"Select colour", "Red", "Blue"});
            }
        });

        findViewById(R.id.startGameButton).setOnClickListener(v -> validateAndStartGame());
    }

    private void addPlayerInputs(int numberOfPlayers, String[] colors) {
        for (int i = 1; i <= numberOfPlayers; i++) {
            EditText playerName = new EditText(this);
            playerName.setHint("Player " + i + " Name");
            playerInputContainer.addView(playerName);

            Spinner colorSpinner = new Spinner(this);
            ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colors);
            colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            colorSpinner.setAdapter(colorAdapter);
            playerInputContainer.addView(colorSpinner);
        }
    }

    private void addPlayerInputsForTeams(int numberOfTeams, String[] colors) {
        for (int team = 1; team <= numberOfTeams; team++) {
            for (int player = 1; player <= 2; player++) {
                EditText playerName = new EditText(this);
                playerName.setHint("Team " + team + " - Player " + player + " Name");
                playerInputContainer.addView(playerName);
            }

            Spinner colorSpinner = new Spinner(this);
            ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colors);
            colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            colorSpinner.setAdapter(colorAdapter);
            playerInputContainer.addView(colorSpinner);
        }
    }

    private void validateAndStartGame() {
        Set<String> selectedColors = new HashSet<>();
        List<Spinner> spinners = new ArrayList<>();
        List<EditText> playerNames = new ArrayList<>();

        for (int i = 0; i < playerInputContainer.getChildCount(); i++) {
            View view = playerInputContainer.getChildAt(i);
            if (view instanceof Spinner) {
                spinners.add((Spinner) view);
            } else if (view instanceof EditText) {
                playerNames.add((EditText) view);
            }
        }

        for (EditText playerName : playerNames) {
            if (playerName.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "All players must enter their names before starting the game!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        for (Spinner spinner : spinners) {
            String selectedColor = (String) spinner.getSelectedItem();
            if (selectedColor.equals("Select colour")) {
                Toast.makeText(this, "All players/teams must select a color before starting the game!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!selectedColors.add(selectedColor)) {
                Toast.makeText(this, "Each player/team must select a different color!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Create player objects and pass them to the GamePlayScreenActivity
        ArrayList<Player> players = new ArrayList<>();
        if(playerNames.size() != 4){
            for (int i = 0; i < playerNames.size(); i++) {
                String playerName = playerNames.get(i).getText().toString();
                String playerColor = (String) spinners.get(i).getSelectedItem();
                players.add(new Player(i + 1, playerName, playerColor));
            }
        }
        else{
            String team1Colour = (String)spinners.get(0).getSelectedItem();
            String team2Colour = (String)spinners.get(1).getSelectedItem();
            for(int i = 0 ;i < playerNames.size();i++){
                String playerName = playerNames.get(i).getText().toString();
                if(i < 2){
                    if(i == 1){//appropriate Ids e.g. (1 and 3 team 1. 2 and 4 team 2)
                        players.add(new Player(i + 2, playerName, team1Colour));
                    }
                    else{
                        players.add(new Player(i + 1, playerName, team1Colour));
                    }
                }
                else{
                    if(i == 2){
                        players.add(new Player(i,playerName,team2Colour));
                    }
                    else{
                        players.add(new Player(i+1,playerName,team2Colour));
                    }
                }
            }
            //swap players for appropriate turns
            Player temp = players.get(2);
            players.set(2,players.get(1));
            players.set(1,temp);

        }


        Intent intent = new Intent(this, GamePlayScreenActivity.class);
        intent.putExtra("players", players);
        startActivity(intent);
    }
}
