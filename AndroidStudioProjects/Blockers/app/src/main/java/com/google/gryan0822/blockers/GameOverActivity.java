package com.google.gryan0822.blockers;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.google.gryan0822.blockers.GameActivity.m_current_round_number;
import static com.google.gryan0822.blockers.GatherPlayersActivity.m_round_number;

// This class is responsible for generating the activity upon completion of a round of play
public class GameOverActivity extends AppCompatActivity {

    // This variable holds the array of players resulting from the end of a game
    private ArrayList<Player> m_end_game_players = new ArrayList<>();


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
            GameOverActivity. This is responsible for generating the appropriate
            results from the end of a game.

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            4:03pm 3/22/2018

    */
    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // establish the buttons from the round for saving and going to the next round
        final Button next_game = findViewById(R.id.next_game_button);
        final Button save_game = findViewById(R.id.save_button);


        // If the intent that started this activity was from the GameActivity, do the following
        if(getIntent().getExtras().getString("ActivityIDExtra").equals("GameActivity")) {

            // set the game text view to the correct round number
            TextView game_over_txtView = findViewById(R.id.round_results_txtView);
            game_over_txtView.setText("Game " + String.valueOf(m_current_round_number) + " of " + String.valueOf(m_round_number) + " Results");

            // Obtain the players array passed by the game activity
            ArrayList<Player> players_array = new ArrayList<Player>();
            players_array = (ArrayList<Player>) getIntent().getSerializableExtra("PlayersListExtra");

            // if the players array is empty, use the array passed from the previous activity
            // as the new players array
            if (m_end_game_players.size() == 0) {
                m_end_game_players = new ArrayList<Player>(players_array);
            }

            // Order the round from winner to loser
            OrderRound(players_array);
            // Initialized results table in xml
            InitializeResults(players_array);

            // Order the standings for the tournament
            OrderStandings(m_end_game_players);
            // Initialize standings table in the xml
            InitializeStandings(m_end_game_players);
            // clear hands and piece totals
            ClearPlayers();
        }
        // Otherwise, the game is being loaded from a previous file
        else{

            // obtain the file to load
            String file_to_load = getIntent().getExtras().getString("LoadedGameExtra");

            TextView game_over_txtView = findViewById(R.id.round_results_txtView);

            // load the array list loaded into the players array
            m_end_game_players = LoadFile(file_to_load);
            TableLayout player_results_layout = findViewById(R.id.player_results_table);
            // set the results table to invisible
            player_results_layout.setVisibility(View.INVISIBLE);

            // Set the correct next game
            game_over_txtView.setText("Next Game: " + String.valueOf(m_current_round_number+1) + " of " + String.valueOf(m_round_number));
            // Order and initialize the standings
            OrderStandings(m_end_game_players);
            InitializeStandings(m_end_game_players);
         }


        // If the current round number was the final round, do the following
        if (m_current_round_number == m_round_number) {
            // change the button to say conclude and disable save button
            next_game.setText("Conclude");
            save_game.setEnabled(false);
            m_current_round_number++;
        }
        // otherwise increment the round number
        else {
            m_current_round_number++;
            save_game.setEnabled(true);
        }


        // the following is responsible for when a user clicks the next game button
        next_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // The tournament has completley finished, take the user to the results page
                if (m_current_round_number>m_round_number){
                    ArrayList<Player> players_array = new ArrayList<Player>(m_end_game_players);
                    Intent intent = new Intent(GameOverActivity.this, ConclusionActivity.class);
                    intent.putExtra("PlayersListExtra", players_array);
                    GameOverActivity.this.startActivity(intent);
                }
                // Otherwise the game is still in progress, simulate another round
                else {
                    ArrayList<Player> players_array = new ArrayList<Player>(m_end_game_players);
                    Intent intent = new Intent(GameOverActivity.this, GameActivity.class);
                    intent.putExtra("PlayersListExtra", players_array);
                    GameOverActivity.this.startActivity(intent);
                }
            }
        });

        // the following handles if the user presses the save game button
        save_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the save file name
                // create an alert box for the user
                AlertDialog.Builder saveBuilder = new AlertDialog.Builder(GameOverActivity.this);

                final EditText fileEditText = new EditText(GameOverActivity.this);
                saveBuilder.setTitle("Please enter a file name to save to:");

                saveBuilder.setView(fileEditText);

                // set the save button to write to internal storage
                saveBuilder.setPositiveButton(
                        "Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // write the contents to internal storage
                                String fileName = fileEditText.getText().toString();
                                // remove all empty spaces
                                fileName = fileName.replaceAll("\\s+","");
                                String fileContents = "";
                                // get the current game state
                                fileContents = SaveFile(m_end_game_players);

                                try {
                                    // write to internal storage
                                    WriteInternalStorage(fileContents, fileName);
                                    // add file name to file list
                                    AddFileNameToList(fileName);

                                }
                                catch(Exception e){
                                    TextView txt = findViewById(R.id.round_results_txtView);
                                    txt.setText(e.getStackTrace()[0].getClassName() + " " + e.getStackTrace()[0].getLineNumber());
                                }

                            }
                        });

                // cancel the save request
                saveBuilder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog saveAlert = saveBuilder.create();
                saveAlert.show();
            }
        });
    }

    /**/
    /*
    InitializeResults()

    NAME

            InitializeResults()

    SYNOPSIS

            void InitializeResults(ArrayList<Player> a_players_list);
            a_players_list -> The players to be displayed in the results page

    DESCRIPTION

            This function is responsible for the generation of the results table from the
            previous round. It is given an array of players to display as a table and
            displays the round results with the winner/winners of the round.

            Award points of the round to the players according to where they finsihed.

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            6:03pm 3/24/2018

    */
    /**/
    private void InitializeResults(ArrayList<Player> a_players_list){

        // holds the results table in the xml
        TableLayout player_results_layout = (TableLayout) findViewById(R.id.player_results_table);
        int placement_count = 1;

        // set up the results list for players
        for(int i=0;i<a_players_list.size();i++){

            // create a new table row for each player
            TableRow tableRow = new TableRow(GameOverActivity.this);


            TextView txtView1 = new TextView(GameOverActivity.this);
            TextView txtView2 = new TextView(GameOverActivity.this);
            TextView txtView3 = new TextView(GameOverActivity.this);
            TextView txtView4 = new TextView(GameOverActivity.this);


            // check the previous index to see if there is a tie
            if (i != 0){
                // if the players tied, set the same number
                if (a_players_list.get(i).equals(a_players_list.get(i-1))){
                    a_players_list.get(i).AddPoints(placement_count);
                    txtView1.setText(String.valueOf(placement_count) + "(+" + String.valueOf(a_players_list.get(i).GetPoints().get(m_current_round_number-1)) + " pts)");
                }
                // otherwise, increment the counter
                else{
                    placement_count = i+1;
                    a_players_list.get(i).AddPoints(i+1);
                    txtView1.setText(String.valueOf(i+1) + " (+" + String.valueOf(a_players_list.get(i).GetPoints().get(m_current_round_number-1)) + " pts)");

                }
            }
            // otherwise, player has finished first
            else{
                a_players_list.get(i).AddPoints(1);
                txtView1.setText("1 (+" + String.valueOf(a_players_list.get(i).GetPoints().get(m_current_round_number-1)) + " pts)");

            }

            // set the views to the right values (according to table header)
            txtView2.setText(a_players_list.get(i).GetName());
            txtView3.setText(String.valueOf(a_players_list.get(i).GetGroups()));
            txtView4.setText(String.valueOf(a_players_list.get(i).GetPiecesCollected()));

            // give the views some padding
            txtView1.setPadding(2,2,2,2);
            txtView2.setPadding(2,2,2,2);
            txtView2.setPadding(2,2,2,2);
            txtView2.setPadding(2,2,2,2);

            // set the background color to the appropriate color
            txtView1.setBackgroundColor(Color.WHITE);
            txtView2.setBackgroundColor(Color.WHITE);
            txtView3.setBackgroundColor(Color.WHITE);
            txtView4.setBackgroundColor(Color.WHITE);

            Integer players_color = Color.WHITE;

            // players color is blue
            if (a_players_list.get(i).GetColor().equals("blue")){
                players_color = Color.BLUE;
            }
            // players color is red
            else if(a_players_list.get(i).GetColor().equals("red")){
                players_color = Color.RED;
            }
            // players color is green
            else if(a_players_list.get(i).GetColor().equals("green")){
                players_color = Color.GREEN;
            }
            // players color is yellow
            else if(a_players_list.get(i).GetColor().equals("yellow")){
                players_color = Color.YELLOW;
            }
            // players color is purple
            else{
                players_color = Color.parseColor("#551A8B");
            }

            // set the background color to the players color
            tableRow.setBackgroundColor(players_color);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            // give the row some padding
            tableRow.setPadding(10,10,10,10);


            // add the views to the row
            tableRow.addView(txtView1);
            tableRow.addView(txtView2);
            tableRow.addView(txtView3);
            tableRow.addView(txtView4);


            player_results_layout.addView(tableRow, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        }
    }

    /**/
    /*
    InitializeResults()

    NAME

            InitializeResults()

    SYNOPSIS

            void InitializeResults(ArrayList<Player> a_players_list);
            a_players_list -> The players to be displayed in the standings page

    DESCRIPTION

            This function is responsible for the generation of the standings table from the
            entire tournament. It is given an array of players to display as a table and
            displays the tournament results with the points from previous rounds.

            The function will have a table header for each round number and show the points
            the player has won in each of those rounds.

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            3:23pm 3/28/2018

    */
    /**/
    
    public void InitializeStandings(ArrayList<Player> a_players_list){

        // holds the standings table in the xml
        TableLayout player_standings_layout = (TableLayout) findViewById(R.id.player_standings_table);

        // Add the header row depending on how many rounds have been played
        TableRow tableHeader = new TableRow(GameOverActivity.this);
        tableHeader.setBackgroundColor(Color.DKGRAY);
        tableHeader.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        tableHeader.setPadding(10,10,10,10);

        // Create the name textview header
        TextView name_txtView = new TextView(GameOverActivity.this);
        name_txtView.setPadding(2,2,2,2);
        name_txtView.setBackgroundColor(Color.WHITE);
        name_txtView.setText("Name");
        name_txtView.setTypeface(null, Typeface.BOLD_ITALIC);

        tableHeader.addView(name_txtView);

        // Set a header field for each round
        for (int i = 0; i < m_current_round_number; i++){
            TextView txtView = new TextView(GameOverActivity.this);
            txtView.setPadding(2,2,2,2);
            txtView.setBackgroundColor(Color.WHITE);
            txtView.setText("R" + String.valueOf((i+1)));
            txtView.setTypeface(null, Typeface.BOLD_ITALIC);

            tableHeader.addView(txtView);
        }

        // Create the total textview header
        TextView total_txtView = new TextView(GameOverActivity.this);
        total_txtView.setPadding(2,2,2,2);
        total_txtView.setBackgroundColor(Color.WHITE);
        total_txtView.setText("T");
        total_txtView.setTypeface(null, Typeface.BOLD_ITALIC);

        tableHeader.addView(total_txtView);

        // Add the header to the game
        player_standings_layout.addView(tableHeader, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));



        // set up the standings list for players
        for(int i=0;i<a_players_list.size();i++){

            // Add a new for each player
            TableRow tableRow = new TableRow(GameOverActivity.this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tableRow.setPadding(10,10,10,10);

            Integer players_color = Color.WHITE;

            // players color is blue
            if (a_players_list.get(i).GetColor().equals("blue")){
                players_color = Color.BLUE;
            }
            // players color is red
            else if(a_players_list.get(i).GetColor().equals("red")){
                players_color = Color.RED;
            }
            // players color is green
            else if(a_players_list.get(i).GetColor().equals("green")){
                players_color = Color.GREEN;
            }
            // players color is yellow
            else if(a_players_list.get(i).GetColor().equals("yellow")){
                players_color = Color.YELLOW;
            }
            // players color is purple
            else{
                players_color = Color.parseColor("#551A8B");
            }

            // set the color of the background as the players color
            tableRow.setBackgroundColor(players_color);

            // set the correct parameters and values to the name view
            TextView players_name_txtView = new TextView(GameOverActivity.this);
            players_name_txtView.setPadding(2,2,2,2);
            players_name_txtView.setBackgroundColor(Color.WHITE);
            players_name_txtView.setText(a_players_list.get(i).GetName());

            // add the view to the row
            tableRow.addView(players_name_txtView);

            // holds the players round results for each round
            ArrayList<Integer> round_scores = a_players_list.get(i).GetPoints();

            // iterate and display each round result in the table
            for (Integer score : round_scores){
                TextView score_txtView = new TextView(GameOverActivity.this);
                score_txtView.setPadding(2,2,2,2);
                score_txtView.setBackgroundColor(Color.WHITE);
                score_txtView.setText(String.valueOf(score));

                // add the view to the row
                tableRow.addView(score_txtView);

            }

            // Create the total textview header
            TextView player_total_txtView = new TextView(GameOverActivity.this);
            player_total_txtView.setPadding(2,2,2,2);
            player_total_txtView.setBackgroundColor(Color.WHITE);
            player_total_txtView.setText(String.valueOf(a_players_list.get(i).GetTotal()) + " pts");

            // add the row to the table
            tableRow.addView(player_total_txtView);


            player_standings_layout.addView(tableRow, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        }

    }

    /**/
    /*
    WriteInternalStorage()

    NAME

            WriteInternalStorage()

    SYNOPSIS

            void WriteInternalStorage(String a_game_content, String a_file_name)
            a_game_content -> holds the content of the game state saved
            a_file_name -> file name the game will be saved to

    DESCRIPTION

            This function is responsible for saving the content given to the file name
            given as function parameters. It will catch any exception thrown and tell the
            user if the save failed.

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            12:00am 4/10/2018

    */
    /**/
    
    public void WriteInternalStorage(String a_game_content, String a_file_name){

        // get the state of the game
        String saved_game_state = a_game_content;


        // write the file to internal storage
        try{
            // write the state to the file and save
            FileOutputStream fileOutputStream = openFileOutput(a_file_name, MODE_PRIVATE);
            fileOutputStream.write(saved_game_state.getBytes());
            fileOutputStream.close();

            Toast toast = Toast.makeText(GameOverActivity.this, a_file_name + " saved", Toast.LENGTH_LONG);
            View toastView = toast.getView();
            toastView.setBackgroundColor(Color.parseColor("#eedfcb"));
            toast.setGravity(Gravity.BOTTOM, 0, 50);

            TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
            toastText.setTextColor(Color.BLACK);

            toast.show();

        }
        // catch any errors
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Toast.makeText(GameOverActivity.this, a_file_name + " not saved (FNF)", Toast.LENGTH_LONG).show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(GameOverActivity.this, a_file_name + " not saved (IO)", Toast.LENGTH_LONG).show();
        }


    }
    
    /**/
    /*
    AddFileNameToList()


    NAME

            AddFileNameToList()


    SYNOPSIS

            void AddFileNameToList(String a_file_name){
            a_file_name -> file name to add to the file name list

    DESCRIPTION

            This function is responsible for holding/writing the names of all the files saved
            into internal storage. The file name is added to the file_name list so it can
            be retrieved easily for later

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            12:12am 4/10/2018

    */
    /**/

    public void AddFileNameToList(String a_file_name){

        // first, read the file into memory
        try {
            // reads in the file into a message
            String message;
            FileInputStream fileInputStream = openFileInput("file_names.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            // appends an end line char to the end of each line
            while ((message = bufferedReader.readLine()) != null) {
                stringBuffer.append(message + "\n");
            }
            String fileContent = stringBuffer.toString();
            fileContent = fileContent + a_file_name + ",";
            WriteInternalStorage(fileContent, "file_names.txt");
            // throws an error if the file cannot be read
        }
        // If the file is not found, this must be the first time saving a file. Create the filelist
        catch(FileNotFoundException e){
            e.printStackTrace();
            // if the game is being run for the first time, write a new file_names.txt
            WriteInternalStorage(a_file_name+",", "file_names.txt");
            // throws an IOException otherwise
        }
        catch (IOException e){
            e.printStackTrace();
            // return an error string
        }

    }

    /**/
    /*
    SaveFile()


    NAME

            SaveFile()


    SYNOPSIS

            String SaveFile(ArrayList<Player> a_player_list)
            a_player_list -> list that will be saved to storage

    DESCRIPTION

            This function is responsible for converting an array of players into a readable string
            to be parsed when loading in a game. The function will return a big string with the
            correct information needed to continue where left off.

    RETURNS

            returns String
            -> the string represents the official state of the game

    AUTHOR

            Glenn Ryan

    DATE

            2:51pm 4/15/2018

    */
    /**/

    public static String SaveFile(ArrayList<Player> a_player_list){

        // holds the output of the string
        String output_string = "";

        // write the current round number and the ending round number into the string
        output_string = output_string + "Current Round: " + m_current_round_number + "\n";
        output_string = output_string + "Total Rounds: " + m_round_number + "\n\n";

        // write each players information into the string
        for (Player i : a_player_list){

            // Write the player type, color, name, style, and round results into the string
            output_string = output_string + "Player\n";
            output_string = output_string + "Color: " + i.GetColor() + "\n";
            output_string = output_string + "Name: " + i.GetName() + "\n";
            if (i.IsHuman()){
                output_string = output_string + "Type: Human\n";
            }
            else{
                output_string = output_string + "Type: Computer\n";
            }
            output_string = output_string + "Play Style: "+ i.GetStyle() + "\n";

            // iterate through and write each round number score into the string
            output_string = output_string + "Round Results: [ ";
            for (Integer j : i.GetPoints()){
                output_string = output_string + j + " ";
            }
            output_string = output_string + "]\n\n";


        }
        // return the string
        return output_string;
    }

    // removes the necessary chars, empty lines from the string passed
    public static String RemoveChars(String line){
        // remove all empty spaces
        line = line.replaceAll("\\s+","");
        // replace all letters with empty space
        line = line.replaceAll("[^\\d.]", "");
        // replace all - with empty space
        line = line.replaceAll("-", "");
        // remove all '.' from string
        line = line.replaceAll("\\.", "");
        // remove all ':" from string
        line = line.replaceAll(":", "");
        return line;

    }

    /**/
    /*
    LoadFile()


    NAME

            LoadFile()


    SYNOPSIS

            ArrayList<Player> LoadFile(String a_output)
            a_output -> string of the game state

    DESCRIPTION

            This function is responsible for converting the output of a saved file and
            loading its contents back into the game. The file is given as a string and parsed
            so it loads the current round number and total round number.

            It will also return the loaded array of players to be loaded back into the game.

    RETURNS

            returns ArrayList<Player>
            -> the list has the loaded players from the file given

    AUTHOR

            Glenn Ryan

    DATE

            11:56am 4/16/2018

    */
    /**/
    
    public static ArrayList<Player> LoadFile(String a_output){

        ArrayList<Player> list = new ArrayList<Player>();

        // Get the current game number
        String current_game_line = a_output.split("\n")[0];
        a_output = a_output.replace(current_game_line + "\n", "");
        current_game_line = RemoveChars(current_game_line);
        m_current_round_number = Integer.parseInt(current_game_line);

        // decrement the round number, the class will increment it prematurely
        m_current_round_number--;

        // Gets the total number of games to be played
        String total_game_line = a_output.split("\n")[0];
        a_output = a_output.replace(total_game_line + "\n\n", "");
        total_game_line = RemoveChars(total_game_line);
        m_round_number = Integer.parseInt(total_game_line);

        // Parse each player seen in the file given
        while(a_output.charAt(0)=='P'){

            // create a new player
            Player current_player = new Player();

            // parse the player line
            String player_line = a_output.split("\n")[0];
            a_output = a_output.replaceFirst(player_line + "\n", "");

            // parse the color line
            String color_line = a_output.split("\n")[0];
            a_output = a_output.replaceFirst(color_line + "\n", "");
            color_line = color_line.replace("Color: ", "");

            // parse the name line
            String name_line = a_output.split("\n")[0];
            a_output = a_output.replaceFirst(name_line + "\n", "");
            name_line = name_line.replace("Name: ", "");

            // parse the player type line
            String player_type_line = a_output.split("\n")[0];
            a_output = a_output.replaceFirst(player_type_line + "\n", "");
            player_type_line = player_type_line.replace("Type: ", "");

            // if the player is human, set the player as such
            if (player_type_line.equals("Human")){
                current_player.SetHuman();
            }

            // set the color and name
            current_player.SetColor(color_line);
            current_player.SetName(name_line);

            // get the player style line
            String player_style_line = a_output.split("\n")[0];
            a_output = a_output.replaceFirst(player_style_line + "\n", "");
            player_style_line = player_style_line.replace("Play Style: ", "");

            // set the player style
            if (player_style_line.equals("Aggressive")){
                current_player.SetStyle("Aggressive");
            }
            else if(player_style_line.equals("Conservative")){
                current_player.SetStyle("Conservative");
            }
            else{
                current_player.SetStyle("Balanced");
            }

            // get the player round results line
            String player_round_results_line = a_output.split("\n")[0];

            // if the next char is a P, we are obtaining another player
            if (a_output.indexOf("P")>0){
                a_output = a_output.substring(a_output.indexOf("P"));
                a_output.trim();
            }
            // otherwise we have reached the end of the file
            else{
                a_output="";
            }

            // parse the round results line
            player_round_results_line = RemoveChars(player_round_results_line);

            // iterate through the round results and add to the players hand
            for (int i = 0; i < player_round_results_line.length(); i++){
                char current = player_round_results_line.charAt(i);
                current_player.AddResult(Character.getNumericValue(current));
            }

            // add the player to the list
            list.add(current_player);

            // if the output is blank, we are done so we can break the loop
            if (a_output.length()==0){
                break;
            }



        }

        // return the loaded players
        return list;


    }

    // Override to the Comparator in order to compare players based upon group number then pieces collected
    private static void OrderRound(ArrayList<Player> a_player_list) {

        Collections.sort(a_player_list, new Comparator() {

            public int compare(Object o1, Object o2) {

                Integer p1Groups = ((Player) o1).GetGroups();
                Integer p2Groups = ((Player) o2).GetGroups();
                // Initially compare group numbers
                int sComp = p1Groups.compareTo(p2Groups);

                if (sComp != 0) {
                    return sComp;
                }
                // compare pieces collected as a tie breaker
                else {
                    Integer x1 = ((Player) o1).GetPiecesCollected();
                    Integer x2 = ((Player) o2).GetPiecesCollected();
                    return x1.compareTo(x2);
                }
            }});
    }

    // orders the a_player_list by total
    public static void OrderStandings(ArrayList<Player> a_player_list){
        Collections.sort(a_player_list, new Comparator() {

            public int compare(Object o1, Object o2) {

                // compare totals
                Integer p1Total = ((Player) o1).GetTotal();
                Integer p2Total = ((Player) o2).GetTotal();
                return -p1Total.compareTo(p2Total);

            }});
    }

    // clears the players hands and piece totals
    private void ClearPlayers(){
        for (Player i : this.m_end_game_players){
            i.ClearHand();
            i.ClearPieceTotal();
        }
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
        AlertDialog.Builder backBuilder = new AlertDialog.Builder(GameOverActivity.this);

        backBuilder.setTitle("Are you sure you wish to exit to the Main Menu?");
        backBuilder.setMessage("(all current progress will be lost)");
        // set the back button to go back to the start menu
        backBuilder.setPositiveButton(
                "Return",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // go back to the main
                        Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
                        GameOverActivity.this.startActivity(intent);
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
