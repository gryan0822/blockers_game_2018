package com.google.gryan0822.blockers;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import static com.google.gryan0822.blockers.GameActivity.m_current_round_number;
import static com.google.gryan0822.blockers.GameOverActivity.OrderStandings;


public class ConclusionActivity extends AppCompatActivity {


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
            Conclusion Activity. This function will display all the important
            information regarding the tournament played as well as who won the
            game.

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            10:03pm 4/10/2018

    */
    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conclusion);

        // textView that displays the winner of the game
        TextView winner = findViewById(R.id.final_winner_textView);
        // button used to take the user back to the main menu
        final Button main_menu = findViewById(R.id.main_menu_button);
        // get the final players list of the game
        ArrayList<Player> final_player_results = (ArrayList<Player>) getIntent().getSerializableExtra("PlayersListExtra");

        // order the standings by total
        OrderStandings(final_player_results);

        // check if the game is a tie, if so state it
        if (final_player_results.get(0).GetTotal()==final_player_results.get(1).GetTotal()){
            winner.setText("Nobody! Its a tie!");
        }
        // otherwise the winner is at the top of the players array
        else{
            // set the winner textView along with correct color
            winner.setText(final_player_results.get(0).GetName());
            String winners_color = final_player_results.get(0).GetColor();
            if (winners_color.equals("blue")){
                winner.setTextColor(Color.BLUE);
            }
            else if (winners_color.equals("red")){
                winner.setTextColor(Color.RED);
            }
            else if (winners_color.equals("green")){
                winner.setTextColor(Color.GREEN);
            }
            else if (winners_color.equals("yellow")){
                winner.setTextColor(Color.YELLOW);
            }
            else{
                winner.setTextColor(Color.parseColor("#551A8B"));
            }
        }

        // Generate the final standings table for the completed game
        InitializeFinalStandings(final_player_results);

        // main menu button simply takes the user back to the MainActivity
        main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ConclusionActivity.this, MainActivity.class);
                ConclusionActivity.this.startActivity(intent);
            }
        });

    }

    /**/
    /*
    InitializeFinalStandings()

    NAME

            InitializeFinalStandings()

    SYNOPSIS

            void InitializeFinalStandings( ArrayList<Player> a_players_list );

            a_players_list -> generates a standings list based upon the order
            colors of the players in this list

    DESCRIPTION

            This function is responsible for displaying the final results table of the game
            through a table in the activities xml called  final_player_standings_table.

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            10:10pm 4/10/2018

    */
    /**/
    public void InitializeFinalStandings(ArrayList<Player> a_players_list) {

        // table layout to hold the standings in the xml
        TableLayout player_standings_layout = (TableLayout) findViewById(R.id.final_player_standings_table);
        m_current_round_number--;

        // Add the header row depending on how many rounds have been played
        TableRow tableHeader = new TableRow(ConclusionActivity.this);
        // Set the following property parameters to the tableRow
        tableHeader.setBackgroundColor(Color.DKGRAY);
        tableHeader.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tableHeader.setPadding(10, 10, 10, 10);

        // Create the name textview header
        TextView name_txtView = new TextView(ConclusionActivity.this);
        // Set the following property parameters to the textView
        name_txtView.setPadding(2, 2, 2, 2);
        name_txtView.setBackgroundColor(Color.WHITE);
        name_txtView.setText("Name");
        name_txtView.setTypeface(null, Typeface.BOLD_ITALIC);

        tableHeader.addView(name_txtView);

        // Set a header field for each round
        for (int i = 0; i < m_current_round_number; i++) {
            TextView txtView = new TextView(ConclusionActivity.this);
            txtView.setPadding(2, 2, 2, 2);
            txtView.setBackgroundColor(Color.WHITE);
            txtView.setText("R" + String.valueOf((i + 1)));
            txtView.setTypeface(null, Typeface.BOLD_ITALIC);

            tableHeader.addView(txtView);
        }

        // Create the total textview header
        TextView total_txtView = new TextView(ConclusionActivity.this);
        total_txtView.setPadding(2, 2, 2, 2);
        total_txtView.setBackgroundColor(Color.WHITE);
        total_txtView.setText("T");
        total_txtView.setTypeface(null, Typeface.BOLD_ITALIC);

        tableHeader.addView(total_txtView);

        // Add the header to the game
        player_standings_layout.addView(tableHeader, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));


        // set up the standings list for players
        for (int i = 0; i < a_players_list.size(); i++) {

            // Add a new for each player
            TableRow tableRow = new TableRow(ConclusionActivity.this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tableRow.setPadding(10, 10, 10, 10);

            Integer players_color = Color.WHITE;

            // players color is blue
            if (a_players_list.get(i).GetColor().equals("blue")) {
                players_color = Color.BLUE;
            }
            // players color is red
            else if (a_players_list.get(i).GetColor().equals("red")) {
                players_color = Color.RED;
            }
            // players color is green
            else if (a_players_list.get(i).GetColor().equals("green")) {
                players_color = Color.GREEN;
            }
            // players color is yellow
            else if (a_players_list.get(i).GetColor().equals("yellow")) {
                players_color = Color.YELLOW;
            }
            // players color is purple
            else {
                players_color = Color.parseColor("#551A8B");
            }
            // set the background color to the players color
            tableRow.setBackgroundColor(players_color);

            TextView players_name_txtView = new TextView(ConclusionActivity.this);
            players_name_txtView.setPadding(2, 2, 2, 2);
            players_name_txtView.setBackgroundColor(Color.WHITE);
            players_name_txtView.setText(a_players_list.get(i).GetName());

            tableRow.addView(players_name_txtView);

            // Obtain current players previous round scores
            ArrayList<Integer> round_scores = a_players_list.get(i).GetPoints();

            // Create a textView to add to the table for each round played
            for (Integer score : round_scores) {
                TextView score_txtView = new TextView(ConclusionActivity.this);
                score_txtView.setPadding(2, 2, 2, 2);
                score_txtView.setBackgroundColor(Color.WHITE);
                score_txtView.setText(String.valueOf(score));


                tableRow.addView(score_txtView);

            }

            // Create the total textview header
            TextView player_total_txtView = new TextView(ConclusionActivity.this);
            player_total_txtView.setPadding(2, 2, 2, 2);
            player_total_txtView.setBackgroundColor(Color.WHITE);
            player_total_txtView.setText(String.valueOf(a_players_list.get(i).GetTotal()) + " pts");

            tableRow.addView(player_total_txtView);

            // set the parameters for the table
            player_standings_layout.addView(tableRow, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        }
    }

    // overridden function simply takes the user back to the MainActivity
    @Override
    public void onBackPressed(){

        Intent intent = new Intent(ConclusionActivity.this, MainActivity.class);
        ConclusionActivity.this.startActivity(intent);

    }
}
