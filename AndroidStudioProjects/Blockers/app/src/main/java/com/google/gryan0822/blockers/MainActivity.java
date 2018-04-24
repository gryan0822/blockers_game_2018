package com.google.gryan0822.blockers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

// This class is responsible for generating the main menu of the application
public class MainActivity extends AppCompatActivity {


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
            MainActivity. It aims to generate the xml layout and design of the page as well
            as provide functionality for the load_game, about_button, and new_game buttons.

            The function will parse the users game choices as to what the user wishes to do
            (either start or load a game)

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            4:53pm 1/29/2018

    */
    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // set the xml layout
        setContentView(R.layout.activity_main);

        // holds the xml button for loading
        final Button load_game = findViewById(R.id.load_button);
        // holds the xml button for a new game
        final Button start_button = (Button) findViewById(R.id.start_button);
        // holds the about button
        final Button about_button = findViewById(R.id.about_button);

        // holds the number of players spinner
        final Spinner player_dropdown = findViewById(R.id.numOfPlayers);
        // allow player to have choices to how many players he/she would like have
        String[] player_number_choices = new String[]{ "2", "3", "4", "5"};
        // set the layout for the spinner
        ArrayAdapter<String> player_adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, player_number_choices);
        // set the adpater
        player_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        player_dropdown.setAdapter(player_adapter);

        // hold the number of rounds
        final Spinner round_dropdown = findViewById(R.id.numOfRounds);
        // allows the user choices to how many games the user would like to play
        String[] round_choices = new String[]{ "1", "2", "3", "4", "5"};
        // set the layout of the spinner
        ArrayAdapter<String> round_adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, round_choices);
        // set the adpater
        round_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        round_dropdown.setAdapter(round_adapter);

        // rounds the blockers logo image view in the xml
        Bitmap logoImage = BitmapFactory.decodeResource(MainActivity.this.getResources(),
                R.drawable.blockers_logo);
        ImageView logo = (ImageView) findViewById(R.id.blockers_logo);
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(MainActivity.this.getResources(), logoImage);
        // round the corner radius
        dr.setCornerRadius(Math.max(logoImage.getWidth(), logoImage.getHeight()) / 2.0f);
        logo.setImageDrawable(dr);

        // the following code is responsible for the start button onClickListener
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Collect the number of players specified by the user
                int playerNumberSelected = 0;
                String numberOfPlayers = player_dropdown.getSelectedItem().toString();
                if (numberOfPlayers.equals("2")){
                    playerNumberSelected=2;
                }
                else if (numberOfPlayers.equals("3")){
                    playerNumberSelected=3;
                }
                else if (numberOfPlayers.equals("4")){
                    playerNumberSelected=4;
                }
                else if (numberOfPlayers.equals("5")){
                    playerNumberSelected=5;
                }
                else{
                    Toast.makeText(MainActivity.this, "You must first select the number of players!",
                            Toast.LENGTH_LONG).show();
                }

                // Collect the number of rounds specified by the user
                int roundNumberSelected = 0;
                String numberOfRounds = round_dropdown.getSelectedItem().toString();
                if (numberOfRounds.equals("1")){
                    roundNumberSelected=1;
                }
                else if (numberOfRounds.equals("2")){
                    roundNumberSelected=2;
                }
                else if (numberOfRounds.equals("3")){
                    roundNumberSelected=3;
                }
                else if (numberOfRounds.equals("4")){
                    roundNumberSelected=4;
                }
                else if (numberOfRounds.equals("5")){
                    roundNumberSelected=5;
                }
                else{
                    Toast.makeText(MainActivity.this, "You must first select the number of rounds!",
                            Toast.LENGTH_LONG).show();
                }

                // reset member variables of other activities (in case this activity was started
                // from another activity and not on startup)
                GameActivity.m_current_round_number = 1;
                GatherPlayersActivity.m_round_number = 0;

                // send the number of rounds and number of players to the GatherPlayersActivity
                Intent myIntent = new Intent(MainActivity.this, GatherPlayersActivity.class);
                myIntent.putExtra("playerNumber", playerNumberSelected);
                myIntent.putExtra("roundNumber", roundNumberSelected);
                // start the next activity
                MainActivity.this.startActivity(myIntent);
            }
        });

        // the following code is responsible for the load_game button onClickListener
        load_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send the user to the LoadActivity
                Intent myIntent = new Intent(MainActivity.this, LoadActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        // the following code is responsible for the about button onClickListener
        about_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send the user to the AboutGameActivity
                Intent myIntent = new Intent(MainActivity.this, AboutGameActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }



}
