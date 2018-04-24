package com.google.gryan0822.blockers;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// This class is responisble for providing the player options of games to load and load them
// back into the game state
public class LoadActivity extends AppCompatActivity {

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
            LoadActivity. This function will generate the choices of files to load
            as well as set the onClickListener for the resume_game button.

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            2:03pm 4/11/2018

    */
    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        // get the spinner of choices from the xml
        final Spinner loaded_spinner = findViewById(R.id.loaded_spinner);
        // get the resume button from the xml
        final Button resume_button = findViewById(R.id.resume_button);

        // try for an error setting file names
        try {
            // obtain all the files (games saved)
            String file_names = ReadInFileNames();
            // parse empty lines
            file_names = file_names.replaceAll("\\s+","");

            // set the choices of file names into the spinner
            List<String> file_choices = Arrays.asList(file_names.split(","));
            // remove any empty choices
            for (String i : file_choices){
                if (i.equals("")){
                    file_choices.remove(i);
                }
            }

            // set the style and adapter for the spinner
            ArrayAdapter<String> style_adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, file_choices);
            loaded_spinner.setAdapter(style_adapter);
        }
        // set the error message if there is an error
        catch (Exception e){
            TextView t = findViewById(R.id.load_game_textView);
            t.setText(e.getMessage() +e.getStackTrace()[4].getClassName() +e.getStackTrace()[4].getLineNumber());
        }

        // the following handles the onClickListener for the resume_button
        resume_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get the file name from the spinner
                String file_read = loaded_spinner.getSelectedItem().toString();

                // set the contents of the file
                file_read = ReadInFile(file_read);

                // if the file is read correctly, load it into the next activity (GameOverActivity)
                if (!file_read.equals("Error File Not Found! The file does not exist! Try Again.")){
                    Intent intent = new Intent(LoadActivity.this, GameOverActivity.class);
                    intent.putExtra("ActivityIDExtra", "LoadActivity");
                    intent.putExtra("LoadedGameExtra", file_read);
                    LoadActivity.this.startActivity(intent);
                }

            }
        });
    }

    /**/
    /*
    ReadInFileNames()

    NAME

           ReadInFileNames()

    SYNOPSIS

            String ReadInFileNames()

    DESCRIPTION

            This function is responsible for generating the choices of files (saved games)
            for the user to pick from whn playing the game.

            The function will read from the file_names.txt file and convert its contents into
            a string to be parsed in the onCreate() and set into the spinner. If there are no
            games to choose from or this is the first time with the application, the file will
            return 'No games to choose from yet'

    RETURNS

            returns String
            -> This string holds the names of all the saved games

    AUTHOR

            Glenn Ryan

    DATE

            3:40pm 4/13/2018

    */
    /**/

    public String ReadInFileNames(){

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
            return fileContent;
        }
        // throws an error if the file cannot be read
        catch(FileNotFoundException e){
            e.printStackTrace();
            return "No saved games to load yet";
        }
        // throws an IOException otherwise
        catch (IOException e){
            e.printStackTrace();
            // return an error string
            return "No saved games to load yet";
        }


    }


    /**/
    /*
    ReadInFile()

    NAME

           ReadInFile()

    SYNOPSIS

            String ReadInFile(String a_file_name)
            a_file_name -> file name to be read into the game

    DESCRIPTION

            This function is responsible for retrieving the given file name into storage
            and returning its contents as a string to be used later.


    RETURNS

            returns String
            -> This string holds the contents of the given file

    AUTHOR

            Glenn Ryan

    DATE

            9:43pm 4/14/2018

    */
    /**/

    public String ReadInFile(String a_file_name){

        try {
            // reads in the file into a message
            String message;
            FileInputStream fileInputStream = openFileInput(a_file_name);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            // appends an end line char to the end of each line
            while ((message = bufferedReader.readLine()) != null) {
                stringBuffer.append(message + "\n");
            }
            String fileContent = stringBuffer.toString();

            Toast toast = Toast.makeText(LoadActivity.this, a_file_name + " has been read", Toast.LENGTH_LONG);

            View toastView = toast.getView();
            toastView.setBackgroundColor(Color.parseColor("#eedfcb"));
            toast.setGravity(Gravity.BOTTOM, 0, 50);

            TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
            toastText.setTextColor(Color.BLACK);

            toast.show();
            // return the content of the file
            return fileContent;
        }
        // throws an error if the file cannot be read
        catch(FileNotFoundException e){
            e.printStackTrace();
            Toast errorToast = Toast.makeText(LoadActivity.this, a_file_name + " does not exist!", Toast.LENGTH_LONG);

            View toastView = errorToast.getView();
            toastView.setBackgroundColor(Color.parseColor("#eedfcb"));
            errorToast.setGravity(Gravity.BOTTOM, 0, 50);

            TextView toastText = (TextView) errorToast.getView().findViewById(android.R.id.message);
            toastText.setTextColor(Color.RED);

            errorToast.show();
            // return an error string
            return "Error File Not Found! The file does not exist! Try Again.";
        }
        // throws an IOException otherwise
        catch (IOException e){
            Toast.makeText(LoadActivity.this, "IO error has occurred!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            // return an error string
            return "Error File Not Found! The file does not exist! Try Again.";
        }


    }


}
