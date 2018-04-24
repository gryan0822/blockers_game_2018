package com.google.gryan0822.blockers;


import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.DragEvent;
import android.view.Gravity;

import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.google.gryan0822.blockers.GatherPlayersActivity.m_round_number;


public class GameActivity extends AppCompatActivity {

    // stores the piece the user has currently chosen
    static public Integer m_current_chosen_piece = R.drawable.a_blue;
    // stores the list of players of the round
    static public ArrayList<Player> m_player_list = new ArrayList<Player>();
    // stores the model gameboard object
    static public GameBoard m_game_board = new GameBoard();
    // stores the index of the piece chosen in the humans hand
    static public int m_piece_chosen_index = 0;

    // stores the ID's of the 5 possible pieces to be referenced
    static public Integer m_piece_array[] = {0,0,0,0,0};

    // stores the current index of players turn
    static public int m_player_index = 0;
    // holds true if the human has moved on his turn
    static public boolean m_human_has_moved = false;
    // holds true if the humans move was successful
    static public boolean m_human_successful_move = false;

    // stores the current round number
    static public int m_current_round_number = 1;
    // holds boolean value for if the game is over
    static public boolean m_game_over = false;

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
            Game Activity. This function will initialize the default layout of
            the game dynamically upon startup. It will first display whose turn it is
            of the game, then it will shuffle everyones deck and draw hands for simulation
            of the round.

            This function then will take the information generated and use it to generate the
            model and view components of the activity.

            The function is also responsible for setting the onClickListeners for the
            buttons including 'help' and 'proceed'

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            8:23pm 2/19/2018

    */
    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // holds the buttons and textviews to be altered during the game
        final Button proceed = findViewById(R.id.proceed_button);
        final Button help = findViewById(R.id.help_button);
        TextView round_tournament_TextView = findViewById(R.id.round_tournament_number);

        // clear any previous information of the game
        ClearGame();

        // display the current round number
        round_tournament_TextView.setText("Round " +  m_current_round_number + " of " + m_round_number );

        // retrieve and set the player array given by the previous activity
        ArrayList<Player> players_array = new ArrayList<Player>();
        players_array = (ArrayList<Player>) getIntent().getSerializableExtra("PlayersListExtra");
        m_player_list = players_array;


        // Initialize a new model gameboard
        m_game_board = new GameBoard();

        // Shuffle each players deck
        for(Player i : m_player_list){
            i.ShuffleDeck();
            i.GetDealt();
        }

        // Initialize view board and update the view
        InitializeBoard();
        UpdateView(proceed, help);


        // The following code is to be processed during an onClick event of the proceed Button
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If the game is over, send to the new activity
                if (proceed.getText().equals("End Round")) {
                    SendToGameOverActivity();
                }

                // If the human has not moved yet and wants to continue
                else if (m_player_list.get(m_player_index).IsHuman() && m_human_has_moved == false){
                    Toast errorToast = Toast.makeText(GameActivity.this, "You must move first!",
                            Toast.LENGTH_LONG);
                    View toastView = errorToast.getView();
                    toastView.setBackgroundColor(Color.parseColor("#eedfcb"));
                    errorToast.setGravity(Gravity.BOTTOM, 0, 50);

                    TextView toastText = (TextView) errorToast.getView().findViewById(android.R.id.message);
                    toastText.setTextColor(Color.RED);

                    errorToast.show();
                }

                // otherwise process the move
                else{


                    // if the player is computer
                    if (m_player_list.get(m_player_index).IsHuman() == false) {

                        // disable the button while the computer moves
                        proceed.setEnabled(false);
                        // Obtain the computer's move
                        ArrayList<String> computer_move = m_player_list.get(m_player_index).Play(m_game_board);

                        Piece moved = new Piece();
                        moved.SetColor(computer_move.get(0));
                        moved.SetType(computer_move.get(1));

                        // Add pieces collected to the computers total if the piece at these coordinates was from another player
                        if (m_game_board.GetPiece(computer_move.get(2)).GetColor().equals(m_player_list.get(m_player_index).GetColor()) == false && m_game_board.GetPiece(computer_move.get(2)).GetColor().equals("")==false){
                            m_player_list.get(m_player_index).AddPieceCollected();
                            UpdatePieces();
                        }

                        // Add the piece to the model gameboard
                        m_game_board.AddPiece(moved, computer_move.get(2));

                            // Make the player draw a new piece;
                        if (m_player_list.get(m_player_index).GetDeck().IsEmpty()==false) {
                            m_player_list.get(m_player_index).DrawPiece();
                        }


                        LinearLayout players_hand = findViewById(R.id.linearLayout);

                        // Animation added for the computers move to fade in
                        Animation fadeIn = new AlphaAnimation(0, 1);
                        fadeIn.setInterpolator(new DecelerateInterpolator());
                        fadeIn.setStartOffset(1000);
                        fadeIn.setDuration(1000);

                        // Animation added for the computers move to fade out
                        Animation fadeOut = new AlphaAnimation(1, 0);
                        fadeOut.setInterpolator(new AccelerateInterpolator());
                        fadeOut.setDuration(1000);

                        // Animation set created for implementing the fade out
                        AnimationSet animation_fadeOut = new AnimationSet(false);
                        animation_fadeOut.addAnimation(fadeOut);

                        players_hand.getChildAt(Integer.valueOf(computer_move.get(3))).setAnimation(animation_fadeOut);
                        players_hand.getChildAt(Integer.valueOf(computer_move.get(3))).startAnimation(animation_fadeOut);

                        // Animation set created for implementing the fade in
                        AnimationSet animation_fadeIn = new AnimationSet(false);
                        animation_fadeIn.addAnimation(fadeIn);

                        m_current_chosen_piece = m_piece_array[ Integer.valueOf(computer_move.get(3))];

                        // convert the destination of the move to coordinates on the view
                        int x_coor = Character.getNumericValue(computer_move.get(2).charAt(0));
                        int y_coor = Character.getNumericValue(computer_move.get(2).charAt(1));

                        // set the move on the board and begin the animation
                        GridLayout m_game_board_layout = findViewById(R.id.gridBoardView);
                        m_game_board_layout.getChildAt((x_coor*9)+y_coor).setAnimation(animation_fadeIn);
                        m_game_board_layout.getChildAt((x_coor*9)+y_coor).setBackgroundResource(m_current_chosen_piece);
                        m_game_board_layout.getChildAt((x_coor*9)+y_coor).startAnimation(animation_fadeIn);



                    }




                    // delay the system while computer moves
                    Runnable r = new Runnable() {
                        @Override
                        public void run(){
                            proceed.setEnabled(true);
                            UpdateView(proceed, help);
                        }
                    };


                    Handler h = new Handler();

                    // if the player is computer
                    if (m_player_list.get(m_player_index).IsHuman()==false){

                        // increase the index after the move and check if the game is over
                        m_player_index++;

                        if (m_player_index==m_player_list.size()){
                            m_player_index=0;
                            if(GameOver()){
                                m_game_over = true;
                            }
                        }

                        // delay the system while the animation occurs, update the view
                        h.postDelayed(r, 1000); // <-- the "1000" is the delay time in miliseconds.
                    }

                    // Otherwise the player is human
                    else {

                        // increase the index after the move and check if the game is over
                        m_player_index++;

                        if (m_player_index == m_player_list.size()) {
                            m_player_index = 0;
                            if(GameOver()){
                                m_game_over = true;
                            }
                        }

                        // update the view
                        UpdateView(proceed, help);
                    }

                }

            }
        });
        // The following code is to be processed during an onClick event of the help Button
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Obtain a move through the players 'Play' function
                ArrayList<String> suggestion = m_player_list.get(m_player_index).Play(m_game_board);
                // Get the coordinates and index of players suggested move
                String coordinates = suggestion.get(2);
                String index = suggestion.get(3);

                // Parse the coordinate suggestion on the gameboard
                char sx_coor = coordinates.charAt(0);
                char sy_coor = coordinates.charAt(1);

                int yc = Character.getNumericValue(sy_coor);
                // increment the y coordinate (so its not starting at 0)
                yc++;

                int sug_index = Integer.parseInt(index);
                // holds the destination of the suggested move
                String sug_destination = "";

                // Check the x coordinate to convert to the correct board coordinates
                switch(sx_coor){
                    case '0':
                        sug_destination = "[A " + yc + "]";
                        break;
                    case '1':
                        sug_destination = "[B " + yc + "]";
                        break;
                    case '2':
                        sug_destination = "[C " + yc + "]";
                        break;
                    case '3':
                        sug_destination = "[D " + yc + "]";
                        break;
                    case '4':
                        sug_destination = "[E " + yc + "]";
                        break;
                    case '5':
                        sug_destination = "[F " + yc + "]";
                        break;
                    case '6':
                        sug_destination = "[G " + yc + "]";
                        break;
                    case '7':
                        sug_destination = "[H " + yc + "]";
                        break;
                    case '8':
                        sug_destination = "[I " + yc + "]";
                        break;
                    default:
                        sug_destination = "Nowhere";
                        break;
                }
                // retrieve the type of piece of the suggested move
                String piece_type = m_player_list.get(m_player_index).GetHand().get(sug_index).GetType();

                // Pop a toast for the suggestion
                Toast toast = Toast.makeText(GameActivity.this, "Place the " + piece_type + " at coordinates " + sug_destination,
                        Toast.LENGTH_LONG);

                View toastView = toast.getView();
                toastView.setBackgroundColor(Color.parseColor("#eedfcb"));
                toast.setGravity(Gravity.BOTTOM, 0, 50);

                TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
                toastText.setTextColor(Color.BLACK);

                toast.show();


            }
        });

    }


    /**/
    /*
    SendToGameOverActivity()

    NAME

            SendToGameOverActivity()

    SYNOPSIS

            void SendToGameOverActivity();

    DESCRIPTION

            This function is responsible for calculating the groups of each player
            of the game.

            The function will then send the final players array to the GameOverActivity to
            be processed to determine the winner.

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            8:00pm 2/22/2018

    */
    /**/
    private void SendToGameOverActivity(){

        // set each players groups according to the board
        for (Player i : m_player_list){
            i.SetGroups(m_game_board.GetGroups(i.GetColor()));
        }


        ArrayList<Player> final_player_results = new ArrayList<Player>(m_player_list);

        // send the official resulting players list to the GameOverActivity
        Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
        intent.putExtra("PlayersListExtra", final_player_results);
        intent.putExtra("ActivityIDExtra", "GameActivity");

        // Start the new activity
        GameActivity.this.startActivity(intent);




    }

    /**/
    /*
    UpdateView()

    NAME

            UpdateView()

    SYNOPSIS

            void UpdateView(Button a_proceed, Button a_help);
            a_proceed -> activity button used to either change color or set text according
            to progress in the round and players turn

            a_help -> button disabled if it is the computer turn

    DESCRIPTION

            This function is responsible for updating the view according to the model.
            The function is called after every turn in order to consistently calculate
            and set properties of the game so the user does not get lost.

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            3:32pm 2/28/2018

    */
    /**/
    private void UpdateView(Button a_proceed, Button a_help){

        // obtain the hand layout and moves remaining views from the xml
        LinearLayout players_hand = findViewById(R.id.linearLayout);
        TextView moves_remaining = findViewById(R.id.moves_left_textView);

        // if the game is not over, do the following
        if (!m_game_over) {

            // Set the next players turn and show how many moves are left
            TextView player_name_TextView = findViewById(R.id.player_name);
            TextView player_type = findViewById(R.id.player_type);
            player_name_TextView.setText("Player: " + m_player_list.get(m_player_index).GetName());
            moves_remaining.setText("Moves Left: " + String.valueOf(m_player_list.get(m_player_index).GetDeck().GetSize()+1));

            // update collected pieces
            UpdatePieces();

            // set the proceed button to the players color
            // players color is blue
            if (m_player_list.get(m_player_index).GetColor().equals("blue")){
                a_proceed.setBackgroundColor(Color.parseColor("#b9faff"));
            }
            // players color is red
            else if(m_player_list.get(m_player_index).GetColor().equals("red")){
                a_proceed.setBackgroundColor(Color.parseColor("#ffb3b3"));
            }
            // players color is green
            else if(m_player_list.get(m_player_index).GetColor().equals("green")){
                a_proceed.setBackgroundColor(Color.parseColor("#c2ffb9"));
            }
            // players color is yellow
            else if(m_player_list.get(m_player_index).GetColor().equals("yellow")){
                a_proceed.setBackgroundColor(Color.parseColor("#feffb3"));
            }
            // players color is purple
            else{
                a_proceed.setBackgroundColor(Color.parseColor("#ebb3ff"));
            }

            // reset these member variables
            m_human_has_moved = false;
            m_human_successful_move = false;

            // if the player is human, initialize a human hand and set player type
            if (m_player_list.get(m_player_index).IsHuman()) {
                player_type.setText("Player Type: Human" + " (" + m_player_list.get(m_player_index).GetStyle().substring(0,1) + ") ");
                InitializeHand(m_player_list.get(m_player_index), 'h');
                // disable the help button
                a_help.setEnabled(true);


            }
            // if the player is computer, initialize a human hand and set player type
            else {

                player_type.setText("Player Type: Computer" + " (" + m_player_list.get(m_player_index).GetStyle().substring(0,1) + ") ");
                InitializeHand(m_player_list.get(m_player_index), 'c');
                a_help.setEnabled(false);
            }
        }
        // otherwise the game is over
        else{
            players_hand.setVisibility(View.INVISIBLE);
            moves_remaining.setText("Moves Left: 0");
            a_proceed.setBackgroundColor(Color.parseColor("#e5c59b"));
            a_proceed.setText("End Round");
        }


    }

    /**/
    /*
    ClearGame()

    NAME

            ClearGame()

    SYNOPSIS

            void ClearGame();

    DESCRIPTION

            This function clears the underlying member variables of the class
            used when the activity is created. It ensures there is no previous data
            lying around from any previous games.

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            4:32pm 2/28/2018

    */
    /**/

    private void ClearGame(){

        this.m_current_chosen_piece = 0;
        this.m_player_list.clear();
        this.m_game_board = new GameBoard();
        this.m_piece_chosen_index = 0;

        this.m_player_index = 0;
        this.m_human_has_moved = false;
        this.m_human_successful_move = false;

        this.m_game_over = false;
    }

    // this function simply updates the view to the amount of pieces in the current player's hand
    private void UpdatePieces(){
        TextView collected_pieces_TextView = findViewById(R.id.pieces_colloected);
        collected_pieces_TextView.setText("Collected Pieces: " + m_player_list.get(m_player_index).GetPiecesCollected());
    }

    // this function simply initializes the view game board existing in the xml
    private void InitializeBoard(){
        GridLayout m_game_board_layout = (GridLayout) findViewById(R.id.gridBoardView);

        // set up the game board and add all view objects to it
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){

                // give the textview the correct parameter attributes
                TextView txtView= SetBoardAttributes();
                // add the coordinates of the view as extra data attached to the textview
                txtView.setTag(""+i+""+j);
                m_game_board_layout.addView(txtView);


            }
        }


    }

    /**/
    /*
    SetBoardAttributes()

    NAME

            SetBoardAttributes()

    SYNOPSIS

            TextView SetBoardAttributes();

    DESCRIPTION

            This function creates and returns a View object of the correct parameters to
            be placed on the board for manipulation. Each TextView will be given an
            attribute to detect drops from a drag and drop event.

    RETURNS

            returns TextView
            -> This textView will have all the attached attributes to it so it fits on the
            board correctly

    AUTHOR

            Glenn Ryan

    DATE

            7:42pm 3/03/2018

    */
    /**/
    private TextView SetBoardAttributes(){
        // create a new TextView object
        TextView txtView = new TextView(this);
        txtView.setGravity(Gravity.CENTER);

        txtView.setTextColor(Color.TRANSPARENT);

        // add the correct parameters to the object
        GridLayout.LayoutParams param =new GridLayout.LayoutParams();

        param.height = 110;
        param.width = 110;

        param.rightMargin = 1;
        param.leftMargin = 1;
        param.topMargin = 1;
        param.bottomMargin = 1;

        param.setGravity(Gravity.CENTER);
        txtView.setLayoutParams (param);

        // allow the view to implement a drop listener
        txtView.setOnDragListener(MyDragListenerBoard);

        return txtView;
    }


    /**/
    /*
    InitializeHand();

    NAME

            InitializeHand();

    SYNOPSIS

            InitializeHand(Player a_current_player, char a_type)
            a_current_player -> current player whose turn it is
            a_type -> type of the current player

    DESCRIPTION

            This function initializes the views according to the a_current_player's hand.
            This is used whenever a player's hand needs to be regenerated and is called
            every time it becomes a new player's turn.

            If the player is human, the pieces are draggable.
            If the player is computer, the pieces are disabled from being draggable and
            are also given an animation attribute

    RETURNS

            returns void
            -> This function does not return anything for it simply sets the views
            of pieces according to the players hand

    AUTHOR

            Glenn Ryan

    DATE

            12:42pm 3/05/2018

    */
    /**/
    private void InitializeHand(Player a_current_player, char a_type){

        // linear layout that holds the views of pieces on the board
        LinearLayout layout2 = (LinearLayout) findViewById(R.id.linearLayout);
        // remove all previous views from the last turn
        layout2.removeAllViews();

        // get the current players hand
        List<Piece> current_Hand = a_current_player.GetHand();

        // create a new image button for each piece in the players hand
        for (int i = 0; i < current_Hand.size(); i++){
            ImageButton imageButton = new ImageButton(this);
            Piece current_piece = current_Hand.get(i);

            // add the view to the layout
            layout2.addView(imageButton);

            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f);

            // gather the correct pieces here
            String Piece_a_type = current_piece.GetType();

            // This switch statement updates the type of piece in the players hand according
            // to the stored png drawables in the system.
            // [NOTE: Although this is one big switch statement, there were no other ways of
            // accomplishing this result according to various internet sources]


            // set the image button view according to the player's color and type of piece
            // set m_piece_array to the drawable that matches its position (used later when
            // updating views to hold the correct png)

            // If the player is blue, set the following
            if (a_current_player.GetColor().equals("blue")){
                switch(Piece_a_type){
                    case "triangle":
                        imageButton.setBackgroundResource(R.drawable.triangle_blue);
                        m_piece_array[i] = R.drawable.triangle_blue;
                        break;
                    case "cross":
                        imageButton.setBackgroundResource(R.drawable.cross_blue);
                        m_piece_array[i] = R.drawable.cross_blue;
                        break;
                    case "star":
                        imageButton.setBackgroundResource(R.drawable.star_blue);
                        m_piece_array[i] = R.drawable.star_blue;
                        break;
                    case "clover":
                        imageButton.setBackgroundResource(R.drawable.clover_blue);
                        m_piece_array[i] = R.drawable.clover_blue;
                        break;
                    case "circle":
                        imageButton.setBackgroundResource(R.drawable.circle_blue);
                        m_piece_array[i] = R.drawable.circle_blue;
                        break;
                    case "diamond":
                        imageButton.setBackgroundResource(R.drawable.diamond_blue);
                        m_piece_array[i] = R.drawable.diamond_blue;
                        break;
                    case "moon":
                        imageButton.setBackgroundResource(R.drawable.moon_blue);
                        m_piece_array[i] = R.drawable.moon_blue;
                        break;
                    case "heart":
                        imageButton.setBackgroundResource(R.drawable.heart_blue);
                        m_piece_array[i] = R.drawable.heart_blue;
                        break;
                    case "square":
                        imageButton.setBackgroundResource(R.drawable.square_blue);
                        m_piece_array[i] = R.drawable.square_blue;
                        break;
                    case "1":
                        imageButton.setBackgroundResource(R.drawable.p1_blue);
                        m_piece_array[i] = R.drawable.p1_blue;
                        break;
                    case "2":
                        imageButton.setBackgroundResource(R.drawable.p2_blue);
                        m_piece_array[i] = R.drawable.p2_blue;
                        break;
                    case "3":
                        imageButton.setBackgroundResource(R.drawable.p3_blue);
                        m_piece_array[i] = R.drawable.p3_blue;
                        break;
                    case "4":
                        imageButton.setBackgroundResource(R.drawable.p4_blue);
                        m_piece_array[i] = R.drawable.p4_blue;
                        break;
                    case "5":
                        imageButton.setBackgroundResource(R.drawable.p5_blue);
                        m_piece_array[i] = R.drawable.p5_blue;
                        break;
                    case "6":
                        imageButton.setBackgroundResource(R.drawable.p6_blue);
                        m_piece_array[i] = R.drawable.p6_blue;
                        break;
                    case "7":
                        imageButton.setBackgroundResource(R.drawable.p7_blue);
                        m_piece_array[i] = R.drawable.p7_blue;
                        break;
                    case "8":
                        imageButton.setBackgroundResource(R.drawable.p8_blue);
                        m_piece_array[i] = R.drawable.p8_blue;
                        break;
                    case "9":
                        imageButton.setBackgroundResource(R.drawable.p9_blue);
                        m_piece_array[i] = R.drawable.p9_blue;
                        break;
                    case "A":
                        imageButton.setBackgroundResource(R.drawable.a_blue);
                        m_piece_array[i] = R.drawable.a_blue;
                        break;
                    case "B":
                        imageButton.setBackgroundResource(R.drawable.b_blue);
                        m_piece_array[i] = R.drawable.b_blue;
                        break;
                    case "C":
                        imageButton.setBackgroundResource(R.drawable.c_blue);
                        m_piece_array[i] = R.drawable.c_blue;
                        break;
                    case "D":
                        imageButton.setBackgroundResource(R.drawable.d_blue);
                        m_piece_array[i] = R.drawable.d_blue;
                        break;
                    case "E":
                        imageButton.setBackgroundResource(R.drawable.e_blue);
                        m_piece_array[i] = R.drawable.e_blue;
                        break;
                    case "F":
                        imageButton.setBackgroundResource(R.drawable.f_blue);
                        m_piece_array[i] = R.drawable.f_blue;
                        break;
                    case "G":
                        imageButton.setBackgroundResource(R.drawable.g_blue);
                        m_piece_array[i] = R.drawable.g_blue;
                        break;
                    case "H":
                        imageButton.setBackgroundResource(R.drawable.h_blue);
                        m_piece_array[i] = R.drawable.h_blue;
                        break;
                    case "I":
                        imageButton.setBackgroundResource(R.drawable.i_blue);
                        m_piece_array[i] = R.drawable.i_blue;
                        break;
                    case "wild":
                        imageButton.setBackgroundResource(R.drawable.wild_blue);
                        m_piece_array[i] = R.drawable.wild_blue;
                        break;
                    default:
                        imageButton.setBackgroundResource(R.mipmap.test_heart);
                        m_piece_array[i] = R.mipmap.test_heart;
                        break;


                }
            }
            // If the player is red, set the following
            else if (a_current_player.GetColor().equals("red")){
                switch(Piece_a_type){
                    case "triangle":
                        imageButton.setBackgroundResource(R.drawable.triangle_red);
                        m_piece_array[i] = R.drawable.triangle_red;
                        break;
                    case "cross":
                        imageButton.setBackgroundResource(R.drawable.cross_red);
                        m_piece_array[i] = R.drawable.cross_red;
                        break;
                    case "star":
                        imageButton.setBackgroundResource(R.drawable.star_red);
                        m_piece_array[i] = R.drawable.star_red;
                        break;
                    case "clover":
                        imageButton.setBackgroundResource(R.drawable.clover_red);
                        m_piece_array[i] = R.drawable.clover_red;
                        break;
                    case "circle":
                        imageButton.setBackgroundResource(R.drawable.circle_red);
                        m_piece_array[i] = R.drawable.circle_red;
                        break;
                    case "diamond":
                        imageButton.setBackgroundResource(R.drawable.diamond_red);
                        m_piece_array[i] = R.drawable.diamond_red;
                        break;
                    case "moon":
                        imageButton.setBackgroundResource(R.drawable.moon_red);
                        m_piece_array[i] = R.drawable.moon_red;
                        break;
                    case "heart":
                        imageButton.setBackgroundResource(R.drawable.heart_red);
                        m_piece_array[i] = R.drawable.heart_red;
                        break;
                    case "square":
                        imageButton.setBackgroundResource(R.drawable.square_red);
                        m_piece_array[i] = R.drawable.square_red;
                        break;
                    case "1":
                        imageButton.setBackgroundResource(R.drawable.p1_red);
                        m_piece_array[i] = R.drawable.p1_red;
                        break;
                    case "2":
                        imageButton.setBackgroundResource(R.drawable.p2_red);
                        m_piece_array[i] = R.drawable.p2_red;
                        break;
                    case "3":
                        imageButton.setBackgroundResource(R.drawable.p3_red);
                        m_piece_array[i] = R.drawable.p3_red;
                        break;
                    case "4":
                        imageButton.setBackgroundResource(R.drawable.p4_red);
                        m_piece_array[i] = R.drawable.p4_red;
                        break;
                    case "5":
                        imageButton.setBackgroundResource(R.drawable.p5_red);
                        m_piece_array[i] = R.drawable.p5_red;
                        break;
                    case "6":
                        imageButton.setBackgroundResource(R.drawable.p6_red);
                        m_piece_array[i] = R.drawable.p6_red;
                        break;
                    case "7":
                        imageButton.setBackgroundResource(R.drawable.p7_red);
                        m_piece_array[i] = R.drawable.p7_red;
                        break;
                    case "8":
                        imageButton.setBackgroundResource(R.drawable.p8_red);
                        m_piece_array[i] = R.drawable.p8_red;
                        break;
                    case "9":
                        imageButton.setBackgroundResource(R.drawable.p9_red);
                        m_piece_array[i] = R.drawable.p9_red;
                        break;
                    case "A":
                        imageButton.setBackgroundResource(R.drawable.a_red);
                        m_piece_array[i] = R.drawable.a_red;
                        break;
                    case "B":
                        imageButton.setBackgroundResource(R.drawable.b_red);
                        m_piece_array[i] = R.drawable.b_red;
                        break;
                    case "C":
                        imageButton.setBackgroundResource(R.drawable.c_red);
                        m_piece_array[i] = R.drawable.c_red;
                        break;
                    case "D":
                        imageButton.setBackgroundResource(R.drawable.d_red);
                        m_piece_array[i] = R.drawable.d_red;
                        break;
                    case "E":
                        imageButton.setBackgroundResource(R.drawable.e_red);
                        m_piece_array[i] = R.drawable.e_red;
                        break;
                    case "F":
                        imageButton.setBackgroundResource(R.drawable.f_red);
                        m_piece_array[i] = R.drawable.f_red;
                        break;
                    case "G":
                        imageButton.setBackgroundResource(R.drawable.g_red);
                        m_piece_array[i] = R.drawable.g_red;
                        break;
                    case "H":
                        imageButton.setBackgroundResource(R.drawable.h_red);
                        m_piece_array[i] = R.drawable.h_red;
                        break;
                    case "I":
                        imageButton.setBackgroundResource(R.drawable.i_red);
                        m_piece_array[i] = R.drawable.i_red;
                        break;
                    case "wild":
                        imageButton.setBackgroundResource(R.drawable.wild_red);
                        m_piece_array[i] = R.drawable.wild_red;
                        break;
                    default:
                        imageButton.setBackgroundResource(R.mipmap.test_heart);
                        m_piece_array[i] = R.mipmap.test_heart;
                        break;


                }
            }
            // If the player is yellow, set the following
            else if (a_current_player.GetColor().equals("yellow")){
                switch(Piece_a_type){
                    case "triangle":
                        imageButton.setBackgroundResource(R.drawable.triangle_yellow);
                        m_piece_array[i] = R.drawable.triangle_yellow;
                        break;
                    case "cross":
                        imageButton.setBackgroundResource(R.drawable.cross_yellow);
                        m_piece_array[i] = R.drawable.cross_yellow;
                        break;
                    case "star":
                        imageButton.setBackgroundResource(R.drawable.star_yellow);
                        m_piece_array[i] = R.drawable.star_yellow;
                        break;
                    case "clover":
                        imageButton.setBackgroundResource(R.drawable.clover_yellow);
                        m_piece_array[i] = R.drawable.clover_yellow;
                        break;
                    case "circle":
                        imageButton.setBackgroundResource(R.drawable.circle_yellow);
                        m_piece_array[i] = R.drawable.circle_yellow;
                        break;
                    case "diamond":
                        imageButton.setBackgroundResource(R.drawable.diamond_yellow);
                        m_piece_array[i] = R.drawable.diamond_yellow;
                        break;
                    case "moon":
                        imageButton.setBackgroundResource(R.drawable.moon_yellow);
                        m_piece_array[i] = R.drawable.moon_yellow;
                        break;
                    case "heart":
                        imageButton.setBackgroundResource(R.drawable.heart_yellow);
                        m_piece_array[i] = R.drawable.heart_yellow;
                        break;
                    case "square":
                        imageButton.setBackgroundResource(R.drawable.square_yellow);
                        m_piece_array[i] = R.drawable.square_yellow;
                        break;
                    case "1":
                        imageButton.setBackgroundResource(R.drawable.p1_yellow);
                        m_piece_array[i] = R.drawable.p1_yellow;
                        break;
                    case "2":
                        imageButton.setBackgroundResource(R.drawable.p2_yellow);
                        m_piece_array[i] = R.drawable.p2_yellow;
                        break;
                    case "3":
                        imageButton.setBackgroundResource(R.drawable.p3_yellow);
                        m_piece_array[i] = R.drawable.p3_yellow;
                        break;
                    case "4":
                        imageButton.setBackgroundResource(R.drawable.p4_yellow);
                        m_piece_array[i] = R.drawable.p4_yellow;
                        break;
                    case "5":
                        imageButton.setBackgroundResource(R.drawable.p5_yellow);
                        m_piece_array[i] = R.drawable.p5_yellow;
                        break;
                    case "6":
                        imageButton.setBackgroundResource(R.drawable.p6_yellow);
                        m_piece_array[i] = R.drawable.p6_yellow;
                        break;
                    case "7":
                        imageButton.setBackgroundResource(R.drawable.p7_yellow);
                        m_piece_array[i] = R.drawable.p7_yellow;
                        break;
                    case "8":
                        imageButton.setBackgroundResource(R.drawable.p8_yellow);
                        m_piece_array[i] = R.drawable.p8_yellow;
                        break;
                    case "9":
                        imageButton.setBackgroundResource(R.drawable.p9_yellow);
                        m_piece_array[i] = R.drawable.p9_yellow;
                        break;
                    case "A":
                        imageButton.setBackgroundResource(R.drawable.a_yellow);
                        m_piece_array[i] = R.drawable.a_yellow;
                        break;
                    case "B":
                        imageButton.setBackgroundResource(R.drawable.b_yellow);
                        m_piece_array[i] = R.drawable.b_yellow;
                        break;
                    case "C":
                        imageButton.setBackgroundResource(R.drawable.c_yellow);
                        m_piece_array[i] = R.drawable.c_yellow;
                        break;
                    case "D":
                        imageButton.setBackgroundResource(R.drawable.d_yellow);
                        m_piece_array[i] = R.drawable.d_yellow;
                        break;
                    case "E":
                        imageButton.setBackgroundResource(R.drawable.e_yellow);
                        m_piece_array[i] = R.drawable.e_yellow;
                        break;
                    case "F":
                        imageButton.setBackgroundResource(R.drawable.f_yellow);
                        m_piece_array[i] = R.drawable.f_yellow;
                        break;
                    case "G":
                        imageButton.setBackgroundResource(R.drawable.g_yellow);
                        m_piece_array[i] = R.drawable.g_yellow;
                        break;
                    case "H":
                        imageButton.setBackgroundResource(R.drawable.h_yellow);
                        m_piece_array[i] = R.drawable.h_yellow;
                        break;
                    case "I":
                        imageButton.setBackgroundResource(R.drawable.i_yellow);
                        m_piece_array[i] = R.drawable.i_yellow;
                        break;
                    case "wild":
                        imageButton.setBackgroundResource(R.drawable.wild_yellow);
                        m_piece_array[i] = R.drawable.wild_yellow;
                        break;
                    default:
                        imageButton.setBackgroundResource(R.mipmap.test_heart);
                        m_piece_array[i] = R.mipmap.test_heart;
                        break;


                }
            }

            // If the player is green, set the following
            else if (a_current_player.GetColor().equals("green")){
                switch(Piece_a_type){
                    case "triangle":
                        imageButton.setBackgroundResource(R.drawable.triangle_green);
                        m_piece_array[i] = R.drawable.triangle_green;
                        break;
                    case "cross":
                        imageButton.setBackgroundResource(R.drawable.cross_green);
                        m_piece_array[i] = R.drawable.cross_green;
                        break;
                    case "star":
                        imageButton.setBackgroundResource(R.drawable.star_green);
                        m_piece_array[i] = R.drawable.star_green;
                        break;
                    case "clover":
                        imageButton.setBackgroundResource(R.drawable.clover_green);
                        m_piece_array[i] = R.drawable.clover_green;
                        break;
                    case "circle":
                        imageButton.setBackgroundResource(R.drawable.circle_green);
                        m_piece_array[i] = R.drawable.circle_green;
                        break;
                    case "diamond":
                        imageButton.setBackgroundResource(R.drawable.diamond_green);
                        m_piece_array[i] = R.drawable.diamond_green;
                        break;
                    case "moon":
                        imageButton.setBackgroundResource(R.drawable.moon_green);
                        m_piece_array[i] = R.drawable.moon_green;
                        break;
                    case "heart":
                        imageButton.setBackgroundResource(R.drawable.heart_green);
                        m_piece_array[i] = R.drawable.heart_green;
                        break;
                    case "square":
                        imageButton.setBackgroundResource(R.drawable.square_green);
                        m_piece_array[i] = R.drawable.square_green;
                        break;
                    case "1":
                        imageButton.setBackgroundResource(R.drawable.p1_green);
                        m_piece_array[i] = R.drawable.p1_green;
                        break;
                    case "2":
                        imageButton.setBackgroundResource(R.drawable.p2_green);
                        m_piece_array[i] = R.drawable.p2_green;
                        break;
                    case "3":
                        imageButton.setBackgroundResource(R.drawable.p3_green);
                        m_piece_array[i] = R.drawable.p3_green;
                        break;
                    case "4":
                        imageButton.setBackgroundResource(R.drawable.p4_green);
                        m_piece_array[i] = R.drawable.p4_green;
                        break;
                    case "5":
                        imageButton.setBackgroundResource(R.drawable.p5_green);
                        m_piece_array[i] = R.drawable.p5_green;
                        break;
                    case "6":
                        imageButton.setBackgroundResource(R.drawable.p6_green);
                        m_piece_array[i] = R.drawable.p6_green;
                        break;
                    case "7":
                        imageButton.setBackgroundResource(R.drawable.p7_green);
                        m_piece_array[i] = R.drawable.p7_green;
                        break;
                    case "8":
                        imageButton.setBackgroundResource(R.drawable.p8_green);
                        m_piece_array[i] = R.drawable.p8_green;
                        break;
                    case "9":
                        imageButton.setBackgroundResource(R.drawable.p9_green);
                        m_piece_array[i] = R.drawable.p9_green;
                        break;
                    case "A":
                        imageButton.setBackgroundResource(R.drawable.a_green);
                        m_piece_array[i] = R.drawable.a_green;
                        break;
                    case "B":
                        imageButton.setBackgroundResource(R.drawable.b_green);
                        m_piece_array[i] = R.drawable.b_green;
                        break;
                    case "C":
                        imageButton.setBackgroundResource(R.drawable.c_green);
                        m_piece_array[i] = R.drawable.c_green;
                        break;
                    case "D":
                        imageButton.setBackgroundResource(R.drawable.d_green);
                        m_piece_array[i] = R.drawable.d_green;
                        break;
                    case "E":
                        imageButton.setBackgroundResource(R.drawable.e_green);
                        m_piece_array[i] = R.drawable.e_green;
                        break;
                    case "F":
                        imageButton.setBackgroundResource(R.drawable.f_green);
                        m_piece_array[i] = R.drawable.f_green;
                        break;
                    case "G":
                        imageButton.setBackgroundResource(R.drawable.g_green);
                        m_piece_array[i] = R.drawable.g_green;
                        break;
                    case "H":
                        imageButton.setBackgroundResource(R.drawable.h_green);
                        m_piece_array[i] = R.drawable.h_green;
                        break;
                    case "I":
                        imageButton.setBackgroundResource(R.drawable.i_green);
                        m_piece_array[i] = R.drawable.i_green;
                        break;
                    case "wild":
                        imageButton.setBackgroundResource(R.drawable.wild_green);
                        m_piece_array[i] = R.drawable.wild_green;
                        break;
                    default:
                        imageButton.setBackgroundResource(R.mipmap.test_heart);
                        m_piece_array[i] = R.mipmap.test_heart;
                        break;


                }
            }

            // If the player is purple, set the following
            else if (a_current_player.GetColor().equals("purple")){
                switch(Piece_a_type){
                    case "triangle":
                        imageButton.setBackgroundResource(R.drawable.triangle_purple);
                        m_piece_array[i] = R.drawable.triangle_purple;
                        break;
                    case "cross":
                        imageButton.setBackgroundResource(R.drawable.cross_purple);
                        m_piece_array[i] = R.drawable.cross_purple;
                        break;
                    case "star":
                        imageButton.setBackgroundResource(R.drawable.star_purple);
                        m_piece_array[i] = R.drawable.star_purple;
                        break;
                    case "clover":
                        imageButton.setBackgroundResource(R.drawable.clover_purple);
                        m_piece_array[i] = R.drawable.clover_purple;
                        break;
                    case "circle":
                        imageButton.setBackgroundResource(R.drawable.circle_purple);
                        m_piece_array[i] = R.drawable.circle_purple;
                        break;
                    case "diamond":
                        imageButton.setBackgroundResource(R.drawable.diamond_purple);
                        m_piece_array[i] = R.drawable.diamond_purple;
                        break;
                    case "moon":
                        imageButton.setBackgroundResource(R.drawable.moon_purple);
                        m_piece_array[i] = R.drawable.moon_purple;
                        break;
                    case "heart":
                        imageButton.setBackgroundResource(R.drawable.heart_purple);
                        m_piece_array[i] = R.drawable.heart_purple;
                        break;
                    case "square":
                        imageButton.setBackgroundResource(R.drawable.square_purple);
                        m_piece_array[i] = R.drawable.square_purple;
                        break;
                    case "1":
                        imageButton.setBackgroundResource(R.drawable.p1_purple);
                        m_piece_array[i] = R.drawable.p1_purple;
                        break;
                    case "2":
                        imageButton.setBackgroundResource(R.drawable.p2_purple);
                        m_piece_array[i] = R.drawable.p2_purple;
                        break;
                    case "3":
                        imageButton.setBackgroundResource(R.drawable.p3_purple);
                        m_piece_array[i] = R.drawable.p3_purple;
                        break;
                    case "4":
                        imageButton.setBackgroundResource(R.drawable.p4_purple);
                        m_piece_array[i] = R.drawable.p4_purple;
                        break;
                    case "5":
                        imageButton.setBackgroundResource(R.drawable.p5_purple);
                        m_piece_array[i] = R.drawable.p5_purple;
                        break;
                    case "6":
                        imageButton.setBackgroundResource(R.drawable.p6_purple);
                        m_piece_array[i] = R.drawable.p6_purple;
                        break;
                    case "7":
                        imageButton.setBackgroundResource(R.drawable.p7_purple);
                        m_piece_array[i] = R.drawable.p7_purple;
                        break;
                    case "8":
                        imageButton.setBackgroundResource(R.drawable.p8_purple);
                        m_piece_array[i] = R.drawable.p8_purple;
                        break;
                    case "9":
                        imageButton.setBackgroundResource(R.drawable.p9_purple);
                        m_piece_array[i] = R.drawable.p9_purple;
                        break;
                    case "A":
                        imageButton.setBackgroundResource(R.drawable.a_purple);
                        m_piece_array[i] = R.drawable.a_purple;
                        break;
                    case "B":
                        imageButton.setBackgroundResource(R.drawable.b_purple);
                        m_piece_array[i] = R.drawable.b_purple;
                        break;
                    case "C":
                        imageButton.setBackgroundResource(R.drawable.c_purple);
                        m_piece_array[i] = R.drawable.c_purple;
                        break;
                    case "D":
                        imageButton.setBackgroundResource(R.drawable.d_purple);
                        m_piece_array[i] = R.drawable.d_purple;
                        break;
                    case "E":
                        imageButton.setBackgroundResource(R.drawable.e_purple);
                        m_piece_array[i] = R.drawable.e_purple;
                        break;
                    case "F":
                        imageButton.setBackgroundResource(R.drawable.f_purple);
                        m_piece_array[i] = R.drawable.f_purple;
                        break;
                    case "G":
                        imageButton.setBackgroundResource(R.drawable.g_purple);
                        m_piece_array[i] = R.drawable.g_purple;
                        break;
                    case "H":
                        imageButton.setBackgroundResource(R.drawable.h_purple);
                        m_piece_array[i] = R.drawable.h_purple;
                        break;
                    case "I":
                        imageButton.setBackgroundResource(R.drawable.i_purple);
                        m_piece_array[i] = R.drawable.i_purple;
                        break;
                    case "wild":
                        imageButton.setBackgroundResource(R.drawable.wild_purple);
                        m_piece_array[i] = R.drawable.wild_purple;
                        break;
                    default:
                        imageButton.setBackgroundResource(R.mipmap.test_heart);
                        m_piece_array[i] = R.mipmap.test_heart;
                        break;


                }
            }
            // end gathering pieces
            // set the index of the button as extra information
            imageButton.setTag(i);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // when clicked, set the current chosen piece to the correct index
                    m_current_chosen_piece = (Integer)view.getTag();
                }
            });

            // if the player is human, set the on drag listener
            if (a_type=='h'){
                imageButton.setOnTouchListener(new MyTouchListener());
            }


            // set appropriate parameters
            param2.height = 110;
            param2.width = 110;

            param2.rightMargin = 10;
            param2.leftMargin = 10;
            param2.topMargin = 1;
            param2.bottomMargin = 1;

            // add parameters to the button
            imageButton.setLayoutParams(param2);


        }
    }

    // Utility function that checks if the game is over
    private boolean GameOver(){
        for (Player i : m_player_list){
            if (i.HasFourRemaining()==false){
                return false;
            }
        }
        return true;
    }

    /**/
    /*
    MyDragListenerBoard

    NAME

            MyDragListenerBoard

    SYNOPSIS

            View.OnDragListener MyDragListenerBoard

    DESCRIPTION

            This is not a function but a overwritten drag listener that interprets
            the users piece choice and attempted destination.

            This drag listener is added to the TextViews among the game board in order to
            detect a drop event. If the piece being moved is valid, the model and view for
            the board and player hand are each updated. If the move is invalid, the user
            is alerted and the hand view is reset.


    RETURNS

            returns None, inherited class

    AUTHOR

            Glenn Ryan

    DATE

            10:42pm 3/10/2018

    */
    /**/
    private View.OnDragListener MyDragListenerBoard = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {

            // holds the type of event action
            int action = event.getAction();
            // holds the coordinates of the game board view (attempted drop)
            String coordinates = "00";

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:

                    break;
                case DragEvent.ACTION_DRAG_EXITED:

                    break;
                // handles the event of a drop onto the board
                case DragEvent.ACTION_DROP:

                    //
                    LinearLayout hand_layout = (LinearLayout) findViewById(R.id.linearLayout);
                    // sets the coordinates to the current TextView the view was dropped on
                    coordinates = (String) v.getTag();

                    // if the human has moved, tell them they cannot move again
                    if (m_human_has_moved){

                        // set the chosen piece back to visible for he/she cannot move it
                        hand_layout.getChildAt(m_piece_chosen_index).setVisibility(View.VISIBLE);

                        Toast errorToast = Toast.makeText(GameActivity.this, "You cannot place another piece!",
                                    Toast.LENGTH_LONG);

                        View toastView = errorToast.getView();
                        toastView.setBackgroundColor(Color.parseColor("#eedfcb"));
                        errorToast.setGravity(Gravity.BOTTOM, 0, 50);

                        TextView toastText = (TextView) errorToast.getView().findViewById(android.R.id.message);
                        toastText.setTextColor(Color.RED);

                        errorToast.show();

                    }

                    // check if the player can play this piece, then do the following
                    else if(m_player_list.get(m_player_index).CanPlay(m_piece_chosen_index, coordinates, m_game_board)){

                        // set the correct drawable background to the target view
                        v.setBackgroundResource(m_current_chosen_piece);
                        // set the human to has moved
                        m_human_has_moved = true;
                        // set the piece to invisible
                        hand_layout.getChildAt(m_piece_chosen_index).setVisibility(View.INVISIBLE);

                        // Add pieces collected to the players total if the piece at these coordinates was from another player
                        if (m_game_board.GetPiece(coordinates).GetColor().equals(m_player_list.get(m_player_index).GetColor()) == false && m_game_board.GetPiece(coordinates).GetColor().equals("")==false){
                            m_player_list.get(m_player_index).AddPieceCollected();
                            UpdatePieces();
                        }

                        // Add the piece to the gameboard
                        m_game_board.AddPiece(m_player_list.get(m_player_index).GetHand().get(m_piece_chosen_index), coordinates);


                        // Delete the piece from the players hand
                        m_player_list.get(m_player_index).DeletePiece(m_player_list.get(m_player_index).GetHand().get(m_piece_chosen_index));

                        // Make the player draw a new piece;
                        if (m_player_list.get(m_player_index).GetDeck().IsEmpty()==false) {
                            m_player_list.get(m_player_index).DrawPiece();
                        }

                    }
                    // otherwise, tell the player they cannot move this piece here
                    else{

                        hand_layout.getChildAt(m_piece_chosen_index).setVisibility(View.VISIBLE);
                        Toast errorToast = Toast.makeText(GameActivity.this, "You cannot place this piece here!",
                                    Toast.LENGTH_LONG);
                        View toastView = errorToast.getView();
                        toastView.setBackgroundColor(Color.parseColor("#eedfcb"));
                        errorToast.setGravity(Gravity.BOTTOM, 0, 50);

                        TextView toastText = (TextView) errorToast.getView().findViewById(android.R.id.message);
                        toastText.setTextColor(Color.RED);

                        errorToast.show();
                    }


                    break;
                // the following handles a drop outside of any target area
                case DragEvent.ACTION_DRAG_ENDED:

                    // set the piece to visible
                    LinearLayout player_hand_layout = (LinearLayout) findViewById(R.id.linearLayout);
                    player_hand_layout.getChildAt(m_piece_chosen_index).setVisibility(View.VISIBLE);

                    // if the human already has moved and the move was successful, do the following
                    if(m_human_has_moved && !m_human_successful_move){

                        // set the chosen piece to invisible
                        LinearLayout hand = (LinearLayout) findViewById(R.id.linearLayout);
                        hand.getChildAt(m_piece_chosen_index).setVisibility(View.INVISIBLE);

                    }


                    break;
                default:
                    break;
            }
            return true;
        }
    };

    // This class defines/inherits the touch listener for Image Buttons in the linear hand layout
    final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                // set the dragged piece resource to the current chosen piece
                m_current_chosen_piece = m_piece_array[ (int) view.getTag()];
                // set the dragged piece index to the piece chosen index
                m_piece_chosen_index = (int) view.getTag();

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                view.startDrag(data, shadowBuilder, view, 0);
                // allow the piece to be invisible while dragged
                view.setVisibility(View.INVISIBLE);

                // set the successful move to true if the human has moved
                if (m_human_has_moved && !m_human_successful_move){
                    m_human_successful_move = true;
                }
                return true;
            } else {
                return false;
            }
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
        AlertDialog.Builder backBuilder = new AlertDialog.Builder(GameActivity.this);

        backBuilder.setTitle("Are you sure you wish to exit to the Main Menu?");
        backBuilder.setMessage("(all current progress will be lost)");
        // set the back button to go back to the start menu
        backBuilder.setPositiveButton(
                "Return",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // go back to the main
                        Intent intent = new Intent(GameActivity.this, MainActivity.class);
                        GameActivity.this.startActivity(intent);
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


