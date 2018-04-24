package com.google.gryan0822.blockers;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// class responsible for obtaining each player entry in the form
public class GatherPlayersActivity extends AppCompatActivity {

    // holds a list of gathered players from the activity
    private static ArrayList<Player> m_players = new ArrayList<Player>();
    // holds the number of players chosen
    private static int m_player_number = 0;
    // holds the current player entry index
    private static int m_player_counter = 1;
    // holds the total number of rounds
    public static int m_round_number = 1;


    /**/
    /*
    onCreate()

    NAME

            onCreate()

    SYNOPSIS

            void onCreate( Bundle savedInstanceState );
                Bundle savedInstanceState -> Default bundle responsible for using this inherited method

    DESCRIPTION

            This function is responsible for the initial generation of the
            GatherPlayersActivity. The function will create an onClickListener for the
            submit_player button as well as obtain the amount of rounds from the
            MainActivity that started it.

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            4:03pm 2/11/2018

    */
    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gather_players);

        // Obtain the intent that started this activity
        Intent MainIntent = getIntent();
        int playerNumber = MainIntent.getIntExtra("playerNumber", 0);
        int roundNumber = MainIntent.getIntExtra("roundNumber", 0);

        // set the total round number and number of players
        m_player_number = playerNumber;
        m_round_number = roundNumber;

        // clear any previous players (if possible)
        ClearPlayers();

        // get the player name edit text and submit player button from xml
        final EditText playerName = (EditText) findViewById(R.id.player_name);
        final Button submit_player = findViewById(R.id.submit_button);

        // Obtain and set choices for the color spinner
        final Integer[] colorChoices = new Integer[]{R.drawable.a_blue, R.drawable.a_green, R.drawable.a_purple, R.drawable.a_red, R.drawable.a_yellow};
        final Spinner color_spinner = findViewById(R.id.color_spinner);
        final SimpleImageArrayAdapter color_adapter = new SimpleImageArrayAdapter(this, colorChoices);


        // set the color spinner adapter
        color_spinner.setAdapter(color_adapter);

        // Obtain the spinner for the type of player
        final Spinner type_spinner = findViewById(R.id.type_spinner);
        // Establish the two types of players for the game
        String[] typeChoices = new String[]{ "Human", "Computer"};
        ArrayAdapter<String> type_adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, typeChoices);
        // add the adapter to the spinner
        type_spinner.setAdapter(type_adapter);

        // Obtain the style of play spinner
        final Spinner style_spinner = findViewById(R.id.style_spinner);
        // Establish the choices of style of play (if player is human, this it the type
        // of suggestions the human will get!)
        String[] styleChoices = new String[]{ "Balanced", "Aggressive", "Conservative"};
        ArrayAdapter<String> style_adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, styleChoices);
        // set adapter to spinner
        style_spinner.setAdapter(style_adapter);

        // sets the onClickListener for the submit player button
        submit_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Obtain the type spinner choice
                String players_type = type_spinner.getSelectedItem().toString();
                // create a new player intance
                Player tempPlayer = new Player();

                // set the player to human or computer
                if (!players_type.equals("Computer")) {
                  tempPlayer.SetHuman();
                }
                // get the players name chosen from the xml
                String chosenName = playerName.getText().toString();
                 // Check if the player name entered is valid
                if (chosenName.equals("")) {
                    Toast.makeText(GatherPlayersActivity.this, "You must enter a valid name!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                // check to see if another player has this name
                for (Player i : m_players) {
                    if (i.GetName().equals(chosenName)) {

                        // pop a toast and set color properties of the toast
                        Toast errorToast = Toast.makeText(GatherPlayersActivity.this, "This name is taken!",
                                Toast.LENGTH_LONG);

                        View toastView = errorToast.getView();
                        toastView.setBackgroundColor(Color.parseColor("#eedfcb"));
                        errorToast.setGravity(Gravity.BOTTOM, 0, 50);

                        TextView toastText = (TextView) errorToast.getView().findViewById(android.R.id.message);
                        toastText.setTextColor(Color.RED);

                        errorToast.show();
                        return;
                    }
                }
                // set the name of the player
                tempPlayer.SetName(chosenName);

                // Check if the players Color is valid
                Integer playersColor = (Integer) color_spinner.getSelectedItem();
                // set the players color to the player choice
                if (playersColor == R.drawable.a_blue) {
                    tempPlayer.SetColor("blue");

                } else if (playersColor == R.drawable.a_red) {
                    tempPlayer.SetColor("red");


                } else if (playersColor == R.drawable.a_yellow) {
                    tempPlayer.SetColor("yellow");


                } else if (playersColor == R.drawable.a_green) {
                    tempPlayer.SetColor("green");


                } else {
                    tempPlayer.SetColor("purple");

                }

                // if another player has this color, tell the user it is no good
                 for (Player i : m_players) {
                     if (i.GetColor().equals(tempPlayer.GetColor())) {
                         Toast.makeText(GatherPlayersActivity.this, "This color is taken!",
                                 Toast.LENGTH_LONG).show();
                         return;
                     }
                 }

                // get the style of play the player has chosen
                String players_style = style_spinner.getSelectedItem().toString();
                // set the players style
                tempPlayer.SetStyle(players_style);

                // add the player to the array
                m_players.add(tempPlayer);
                // increment the current player
                m_player_counter++;

                // if the player counter has surpassed the number of players, do the following
                if (m_player_counter>m_player_number){

                    // copy the player list
                    ArrayList<Player> players_sent = new ArrayList<Player>(m_players);
                    // clear the players of the activity
                    ClearPlayers();

                    // start and send the array to the next activity (GameActivity)
                    Intent intent = new Intent(GatherPlayersActivity.this, GameActivity.class);
                    intent.putExtra("PlayersListExtra", players_sent);
                    GatherPlayersActivity.this.startActivity(intent);
                }
                // otherwise, continue to add players
                else {

                    // clear the edit text and increment the counter
                    playerName.setText("");
                    TextView pText = findViewById(R.id.player_number);
                    pText.setText("P: " + (m_player_counter));

                    // Pop a toast and set Color properties of the toast
                    Toast toast = Toast.makeText(GatherPlayersActivity.this, "Player Successfully Entered",
                            Toast.LENGTH_LONG);
                    View toastView = toast.getView();
                    toastView.setBackgroundColor(Color.parseColor("#eedfcb"));
                    toast.setGravity(Gravity.BOTTOM, 0, 50);

                    TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
                    toastText.setTextColor(Color.BLACK);

                    toast.show();
                }

            }
        });



    }


    // clears the players and player counter of the activity
    private void ClearPlayers(){
        m_players.clear();
        m_player_counter = 1;
    }

    /**/
    /*
    onBackPressed()

    NAME

            onBackPressed()

    SYNOPSIS

            void onBackPressed()

    DESCRIPTION

            This function is responsible for taking the user back to the main menu if trying to
            go back to the previous activity. A player should not be able to go back because
            it hurts the integrity of the game for the player could redo bad rounds.

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            5:23pm 4/16/2018

    */
    /**/
    @Override
    public void onBackPressed(){
        AlertDialog.Builder backBuilder = new AlertDialog.Builder(GatherPlayersActivity.this);

        backBuilder.setTitle("Are you sure you wish to exit to the Main Menu?");
        backBuilder.setMessage("(all current progress will be lost)");
        // set the back button to go back to the start menu
        backBuilder.setPositiveButton(
                "Return",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // go back to the main
                        Intent intent = new Intent(GatherPlayersActivity.this, MainActivity.class);
                        GatherPlayersActivity.this.startActivity(intent);
                    }
                });

        // cancel the request
        backBuilder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog backAlert = backBuilder.create();
        backAlert.show();
    }

}

