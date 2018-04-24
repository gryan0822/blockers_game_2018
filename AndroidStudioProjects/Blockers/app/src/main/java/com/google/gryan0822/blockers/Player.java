package com.google.gryan0822.blockers;

/**
 * Created by gryan on 2/20/2018.
 */

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Player implements Serializable {

    // VARIABLES
    // holds the players hand
    private List<Piece> m_player_hand = new ArrayList<Piece>();
    // holds the players name
    private String m_player_name;
    // holds the players color
    private String m_player_color;
    // holds the players number of collected pieces
    private int m_player_collected_pieces;
    // holds the players deck
    private Deck m_player_deck = new Deck();
    // holds the players style
    private String m_player_style = "Balanced";
    // holds the players number of groups
    private int m_player_groups;
    // holds true if the player is human
    private boolean m_human = false;
    // holds the number of points the player has earned for each round
    private ArrayList<Integer> m_points_earned = new ArrayList<Integer>();

    
    // CONSTRUCTOR
    // Initializes a blank player
    public Player(){
        m_player_name="null";
        m_player_color="null";
        m_player_collected_pieces=0;
    }


    // SELECTORS
    // returns players current number of pieces collected
    public int GetPiecesCollected(){
        return this.m_player_collected_pieces;
    }

    // returns true if the player is human
    public boolean IsHuman(){
        return this.m_human;
    }

    // returns the players style of play
    public String GetStyle(){
        return this.m_player_style;
    }

    // returns the players hand
    public List<Piece> GetHand(){
        return this.m_player_hand;
    }

    // returns the players deck
    public Deck GetDeck(){
        return this.m_player_deck;
    }

    // returns the players name
    public String GetName() {return this.m_player_name;}

    // returns the total number of points the player has earned
    public int GetTotal(){
        Integer total = 0;
        for (Integer i : this.m_points_earned){
            total = total + i;
        }
        return total;
    }

    // returns the players color
    public String GetColor() {return this.m_player_color;}

    // returns the number of groups the player has on the board
    public int GetGroups(){return this.m_player_groups;}

    // returns the array of points the player has scored per round
    public ArrayList<Integer> GetPoints(){return this.m_points_earned;}

    // returns true if the player has 4 pieces in his/her deck
    public boolean HasFourRemaining(){
        if (this.m_player_hand.size()<=4){
            return true;
        }
        else{
            return false;
        }
    }


    // MUTATORS
    // sets the players hand_Total
    public void SetStyle(String a_style){
        this.m_player_style = a_style;
    }

    // sets the player as human
    public void SetHuman(){
        this.m_human = true;
    }

    // adds one to the amount of pieces the player has collected
    public void AddPieceCollected(){
        this.m_player_collected_pieces++;
    }

    // Takes an integer and determines how many points the player has earned by his/her placement
    public void AddPoints(Integer a_placement){
        if (a_placement==1){
            this.m_points_earned.add(new Integer(4));
        }
        else if (a_placement==2){
            this.m_points_earned.add(new Integer(3));
        }
        else if (a_placement==3){
            this.m_points_earned.add(new Integer(2));
        }
        else if (a_placement==4){
            this.m_points_earned.add(new Integer(1));
        }
        else{
            this.m_points_earned.add(new Integer(0));
        }
    }

    // adds a round result to the players points earned array
    public void AddResult(Integer a_round_results){
        this.m_points_earned.add(a_round_results);
    }

    // sets the players name
    public void SetName(String name) { this.m_player_name = name;}

    // sets the players color
    public void SetColor(String a_color) {
        this.m_player_color = a_color;
        this.m_player_deck.SetColor(a_color);
    }

    // sets the players group number
    public void SetGroups(int a_group_num){
        this.m_player_groups = a_group_num;
    }

    // UTILITY
    // shuffles the players deck
    public void ShuffleDeck(){
        this.m_player_deck.ShuffleDeck();
    }

    // deals the players hand
    public void GetDealt(){
        this.m_player_hand = this.m_player_deck.TakeHand();
    }

    // adds piece to the players hand
    public void DrawPiece(){
        this.m_player_hand.add(this.m_player_deck.DrawPiece());
    }

    // clears the players hand
    public void ClearHand(){
        this.m_player_hand.clear();
    }

    // removes piece from the players hand
    public void DeletePiece(Piece token){
        this.m_player_hand.remove(token);
    }

    // resets the total of pieces in hand
    public void ClearPieceTotal(){
        this.m_player_collected_pieces = 0;
    }

    // displays the pieces in the hand (used for debugging)
    public void DisplayHand(){
        for (Piece pieces : this.m_player_hand){
            System.out.println(pieces.ToString() + " ");
        }
    }

    /**/
    /*
    CanPlay()

    NAME

            CanPlay()

    SYNOPSIS

            boolean CanPlay(int a_piece_attempt, String a_coordinates, GameBoard a_board)
            a_coordinates -> coordinates the player is trying to place the piece on
            a_piece_attempt -> the index of the piece in the players hand
            a_board -> the game board the player is playing the piece on



    DESCRIPTION

            This function checks if the piece at the given index of the plaers hand can be placed
            at the given coordinates on the given board.

            This function is used every time a player of any type attempts to place a piece on the
            board. This must return true for a valid play.

    RETURNS

            returns boolean
             -> Returns true if the move is valid

    AUTHOR

            Glenn Ryan

    DATE

            1:22pm 3/18/2018

    */
    /**/
    
    public boolean CanPlay(int a_piece_attempt, String a_coordinates, GameBoard a_board){

        // represents the target piece
        Piece target = new Piece(a_board.GetPiece(a_coordinates));
        // represents the destination piece
        Piece token = m_player_hand.get(a_piece_attempt);

        // holds the old number of groups of the target piece
        int old_number_of_groups = 0;

        // check if the piece is not empty
        if (!target.GetColor().equals("")){
            // get the amount of groups of the target color
            old_number_of_groups = a_board.GetGroups(target.GetColor());
        }

        // holds the new number of groups after the move
        int new_number_of_groups = 0;

        // get the token type
        String token_type = token.GetType();

        // do the following if the piece is not the players color
        if(!target.GetColor().equals(m_player_color)) {

            // check if the piece can be played here
            if (token_type.equals(target.GetDefaultLetter()) || token_type.equals(target.GetDefaultNumber()) || token_type.equals(target.GetDefaultShape())) {

                // if not occupied the player can play
                if (target.GetColor().equals("")){
                    return true;
                }

                // otherwise, check the following
                else {
                    // add the piece temporarily
                    a_board.AddPiece(token, a_coordinates);
                    // calculate new groups
                    new_number_of_groups = a_board.GetGroups(target.GetColor());

                    // if the new number of groups is the same, the move is valid
                    if (new_number_of_groups == old_number_of_groups) {
                        a_board.AddPiece(target, a_coordinates);
                        return true;
                    }

                    // put the board back to normal
                    a_board.AddPiece(target, a_coordinates);
                }


            }
            // if the piece is wild, do the following
            else if (token_type.equals("wild")) {

                // if empty, return true
                if (target.GetColor().equals("")){
                    return true;
                }

                // otherwise, do the following
                else {
                    // temporarily add the piece
                    a_board.AddPiece(token, a_coordinates);
                    // calculate the new groups
                    new_number_of_groups = a_board.GetGroups(target.GetColor());

                    // if the group numbers are the same, the move is valid
                    if (new_number_of_groups == old_number_of_groups) {
                        a_board.AddPiece(target, a_coordinates);
                        return true;
                    }

                    // put the board back to normal
                    a_board.AddPiece(target, a_coordinates);
                }


            }
            // otherwise, no good
            else{
                return false;
            }
        }

        // move is no good
        return false;
    }

    /**/
    /*
    Play

    NAME

            Play()

    SYNOPSIS

            ArrayList<String> Play(GameBoard a_board)
            a_board -> board the player is going to play on


    DESCRIPTION

            This function checks the type of player and returns an array list of the
            appropriate move based upon the players style.

            If the player is human, this function is used to obtain a suggestion rather than
            dictate a move.

    RETURNS

            returns ArrayList<String>
             -> Returns a list containing the move and where to play it

    AUTHOR

            Glenn Ryan

    DATE

            2:23pm 3/24/2018

    */
    /**/
    
    public ArrayList<String> Play(GameBoard a_board){

        ArrayList players_move = new ArrayList<String>();

        // The following code handles BALANCED player computer moves
        if (this.m_player_style.equals("Balanced")){

            players_move = AdjacentEmptyPlay(a_board);
            if (!players_move.get(0).equals("INVALID")){
                return players_move;
            }
            players_move = AdjacentCapturePlay(a_board);
            if (!players_move.get(0).equals("INVALID")){
                return players_move;
            }
            players_move = DefaultEmptyPlay(a_board);
            if (!players_move.get(0).equals("INVALID")){
                return players_move;
            }
            players_move = DefaultCapturePlay(a_board);
            if (!players_move.get(0).equals("INVALID")){
                return players_move;
            }


            return players_move;

        }
        // The following code handles AGGRESSIVE player computer moves
        else if (this.m_player_style.equals("Aggressive")){

            players_move = AdjacentCapturePlay(a_board);
            if (!players_move.get(0).equals("INVALID")){
                return players_move;
            }
            players_move = AdjacentEmptyPlay(a_board);
            if (!players_move.get(0).equals("INVALID")){
                return players_move;
            }
            players_move = DefaultCapturePlay(a_board);
            if (!players_move.get(0).equals("INVALID")){
                return players_move;
            }
            players_move = DefaultEmptyPlay(a_board);
            if (!players_move.get(0).equals("INVALID")){
                return players_move;
            }


            return players_move;
        }
        // The following code handles CONSERVATIVE player computer moves
        else if (this.m_player_style.equals("Conservative")){

            players_move = AdjacentEmptyPlay(a_board);
            if (!players_move.get(0).equals("INVALID")){
                return players_move;
            }
            players_move = DefaultEmptyPlay(a_board);
            if (!players_move.get(0).equals("INVALID")){
                return players_move;
            }
            players_move = AdjacentCapturePlay(a_board);
            if (!players_move.get(0).equals("INVALID")){
                return players_move;
            }
            players_move = DefaultCapturePlay(a_board);
            if (!players_move.get(0).equals("INVALID")){
                return players_move;
            }


            return players_move;
        }

        return players_move;

    }

    /**/
    /*
    AdjacentEmptyPlay

    NAME

            AdjacentEmptyPlay()

    SYNOPSIS

            ArrayList<String> AdjacentEmptyPlay(GameBoard a_board)
            a_board -> board the player is going to play on


    DESCRIPTION

            This function checks if the player can play a piece on an empty spot adjacent to
            once of the player's other pieces. If they can, the move is returned but if not,
            an ArrayList<String> will contain one string of "INVALID" to indicate there are no
            moves.

    RETURNS

            returns ArrayList<String>
             -> Returns a list containing the move and where to play it

    AUTHOR

            Glenn Ryan

    DATE

            12:23pm 3/29/2018

    */
    /**/
    
    public ArrayList<String> AdjacentEmptyPlay(GameBoard a_board) {

        // Iterate through the board
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // Iterate through the players hand
                for (int piece_index = 0; piece_index < m_player_hand.size(); piece_index++) {
                    // find each piece matching the players color
                    if (a_board.GetPiece(i, j).GetColor().equals(m_player_color)) {

                        // check if any adjacent pieces (that are not occupied) can be played upon, return the move
                        if (i + 1 < 9) {
                            if (a_board.GetPiece("" + (i + 1) + "" + j).GetColor().equals("")) {
                                if (CanPlay(piece_index, "" + (i + 1) + "" + j, a_board)) {
                                    return ReturnPlay("" + (i + 1) + "" + j, piece_index);
                                }
                            }
                        }

                        if (i - 1 >= 0) {
                            if (a_board.GetPiece("" + (i - 1) + "" + j).GetColor().equals("")) {
                                if (CanPlay(piece_index, "" + (i - 1) + "" + j, a_board)) {
                                    return ReturnPlay("" + (i - 1) + "" + j, piece_index);
                                }
                            }
                        }
                        if (j + 1 < 9) {
                            if (a_board.GetPiece("" + i + "" + (j + 1)).GetColor().equals("")) {
                                if (CanPlay(piece_index, "" + i + "" + (j + 1), a_board)) {
                                    return ReturnPlay("" + i + "" + (j + 1), piece_index);
                                }
                            }
                        }
                        if (j - 1 >= 0) {
                            if (a_board.GetPiece("" + i + "" + (j - 1)).GetColor().equals("")) {
                                if (CanPlay(piece_index, "" + i + "" + (j - 1), a_board)) {
                                    return ReturnPlay("" + i + "" + (j - 1), piece_index);
                                }
                            }
                        }

                    }
                }


            }
        }

        // there are no possible moves so return an invalid list
        ArrayList<String> invalid = new ArrayList<String>();
        invalid.add("INVALID");
        return invalid;
    }

    /**/
    /*
    AdjacentCapturePlay

    NAME

            AdjacentCapturePlay()

    SYNOPSIS

            ArrayList<String> AdjacentCapturePlay(GameBoard a_board)
            a_board -> board the player is going to play on


    DESCRIPTION

            This function checks if the player can play a piece on an occupied spot adjacent to
            once of the player's other pieces. If they can, the move is returned but if not,
            an ArrayList<String> will contain one string of "INVALID" to indicate there are no
            moves.

    RETURNS

            returns ArrayList<String>
             -> Returns a list containing the move and where to play it

    AUTHOR

            Glenn Ryan

    DATE

            12:23pm 3/29/2018

    */
    /**/

    public ArrayList<String> AdjacentCapturePlay(GameBoard a_board){
        // Iterate through the board
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                // Iterate through the players hand
                for (int piece_index = 0; piece_index < m_player_hand.size(); piece_index++) {

                    // check if any adjacent pieces (that are indeed occupied) can be played upon, return the move
                    if (a_board.GetPiece(i, j).GetColor().equals(m_player_color)) {

                        if (i + 1 < 9) {
                            if (CanPlay(piece_index, "" + (i + 1) + "" + j, a_board)) {
                                return ReturnPlay("" + (i + 1) + "" + j, piece_index);
                            }
                        }

                        if (i - 1 >= 0) {
                            if (CanPlay(piece_index, "" + (i - 1) + "" + j, a_board)) {
                                return ReturnPlay("" + (i - 1) + "" + j, piece_index);
                            }
                        }
                        if (j + 1 < 9) {
                            if (CanPlay(piece_index, "" + i + "" + (j + 1), a_board)) {
                                return ReturnPlay("" + i + "" + (j + 1), piece_index);
                            }
                        }
                        if (j - 1 >= 0) {
                            if (CanPlay(piece_index, "" + i + "" + (j - 1), a_board)) {
                                return ReturnPlay("" + i + "" + (j - 1), piece_index);
                            }
                        }

                    }
                }


            }
        }

        // there are no moves, return invalid
        ArrayList<String> invalid = new ArrayList<String>();
        invalid.add("INVALID");
        return invalid;

    }

    /**/
    /*
    DefaultEmptyPlay

    NAME

            DefaultEmptyPlay()

    SYNOPSIS

            ArrayList<String> DefaultEmptyPlay(GameBoard a_board)
            a_board -> board the player is going to play on


    DESCRIPTION

            This function checks if the player can play a piece on an empty spot anywhere
            on the board. If they can, the move is returned but if not,
            an ArrayList<String> will contain one string of "INVALID" to indicate there are no
            moves.

    RETURNS

            returns ArrayList<String>
             -> Returns a list containing the move and where to play it

    AUTHOR

            Glenn Ryan

    DATE

            12:23pm 3/29/2018

    */
    /**/

    public ArrayList<String> DefaultEmptyPlay(GameBoard a_board) {


        // Iterate through the players hand
        for (int piece_index = 0; piece_index < m_player_hand.size(); piece_index++) {

            // iterate through the board
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    // if the spot is empty and the piece can be played return it
                    if (a_board.GetPiece("" + i + "" + j).GetColor().equals("")) {
                        if (CanPlay(piece_index, "" + i + "" + j, a_board)) {

                            return ReturnPlay("" + i + "" + j, piece_index);
                        }
                    }
                }
            }

        }

        // return invalid because there were no mores
        ArrayList<String> invalid = new ArrayList<String>();
        invalid.add("INVALID");
        return invalid;
    }

    /**/
    /*
    DefaultCapturePlay

    NAME

            DefaultCapturePlay()

    SYNOPSIS

            ArrayList<String> DefaultCapturePlay(GameBoard a_board)
            a_board -> board the player is going to play on


    DESCRIPTION

            This function checks if the player can play a piece on an occupied spot anywhere
            on the board. If they can, the move is returned but if not,
            an ArrayList<String> will contain one string of "INVALID" to indicate there are no
            moves.

    RETURNS

            returns ArrayList<String>
             -> Returns a list containing the move and where to play it

    AUTHOR

            Glenn Ryan

    DATE

            12:23pm 3/29/2018

    */
    /**/

    public ArrayList<String> DefaultCapturePlay(GameBoard a_board){

        // Iterate through the players hand
        for (int piece_index = 0; piece_index < m_player_hand.size(); piece_index++){

            // Iterate through the board
            for (int i = 0; i < 9; i++){
                for (int j = 0; j < 9; j++){

                    // if the piece on the board is not the same color (and not blank)
                    // and the piece can be played, return the move
                    if (a_board.GetPiece("" + i + "" + j).GetColor().equals("")==false) {
                        if (CanPlay(piece_index, "" + i + "" + j, a_board)) {

                            return ReturnPlay("" + i + "" + j, piece_index);
                        }
                    }
                }
            }

        }

        // there were no moves, return invalid
        ArrayList<String> invalid = new ArrayList<String>();
        invalid.add("INVALID");
        return invalid;
    }

    /**/
    /*
    ReturnPlay

    NAME

            ReturnPlay()

    SYNOPSIS

            ArrayList<String> ReturnPlay(String a_coordinates, int a_index)
            a_coordinates -> coordinates the play will be on
            a_index -> the index of the piece in the players hand to play


    DESCRIPTION

            This function constructs an array list dictating the players move and
            all the necessary information involved with placing the piece
    RETURNS

            returns ArrayList<String>
             -> Returns a list containing the move, player color, piece type, piece index

    AUTHOR

            Glenn Ryan

    DATE

            12:23pm 3/29/2018

    */
    /**/
    
    public ArrayList<String> ReturnPlay(String a_coordinates, int a_index){

        // list holding the move
        ArrayList<String> move_result = new ArrayList<String>();

        // add players color
        move_result.add(m_player_color);
        // add piece type
        move_result.add(m_player_hand.get(a_index).GetType());
        // add coordinates
        move_result.add(a_coordinates);
        // add piece hand index
        move_result.add(""+a_index);

        // if the player is computer, delete the piece
        // if human, this is only a suggestion, do not delete piece
        if(!this.IsHuman()){
            DeletePiece(m_player_hand.get(a_index));
        }

        return move_result;

    }

    // overridden operator that compares if the player has more groups than another player
    @Override
    public boolean equals(Object o){
        Player obj = (Player) o;
        if (this.m_player_groups == obj.GetGroups() && this.m_player_collected_pieces == obj.GetPiecesCollected()){
            return true;
        }
        else {
            return false;
        }
    }

}
