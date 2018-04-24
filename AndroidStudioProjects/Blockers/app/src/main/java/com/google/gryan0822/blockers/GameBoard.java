package com.google.gryan0822.blockers;

/**
 * Created by gryan on 2/28/2018.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// This class is responsible for holding the information for the model game board
public class GameBoard {

    // VARIABLES
    // holds the multi-dimensional array of pieces that is the board
    private Piece[][] m_game_board = new Piece[9][9];

    // CONSTRUCTOR
    // initializes the board
    public GameBoard(){
        for (int i = 0; i < 9; i++){
            for (int k = 0; k < 9; k++){
                m_game_board[i][k] = new Piece();
            }
        }
        // set default values in the board
        Initialize();
    }

    /**/
    /*
    Initialize()

    NAME

            Initialize()

    SYNOPSIS

            void Initialize()


    DESCRIPTION

            This function is responsible for initializing the default values of the
            game board before a round of play. Each piece is given a default shape,
            letter, and type

    RETURNS

            returns void

    AUTHOR

            Glenn Ryan

    DATE

            3:12pm 2/10/2018

    */
    /**/
    private void Initialize(){
        // initialize shapes
        // triangle
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                m_game_board[i][j].SetDefaultShape("triangle");
            }
        }

        // cross
        for (int i = 0; i < 3; i++){
            for (int j = 3; j < 6; j++){
                m_game_board[i][j].SetDefaultShape("cross");
            }
        }

        // star
        for (int i = 0; i < 3; i++){
            for (int j = 6; j < 9; j++){
                m_game_board[i][j].SetDefaultShape("star");
            }
        }

        // clover
        for (int i = 3; i < 6; i++){
            for (int j = 0; j < 3; j++){
                m_game_board[i][j].SetDefaultShape("clover");
            }
        }

        // circle
        for (int i = 3; i < 6; i++){
            for (int j = 3; j < 6; j++){
                m_game_board[i][j].SetDefaultShape("circle");
            }
        }

        // diamond
        for (int i = 3; i < 6; i++){
            for (int j = 6; j < 9; j++){
                m_game_board[i][j].SetDefaultShape("diamond");
            }
        }

        // moon
        for (int i = 6; i < 9; i++){
            for (int j = 0; j < 3; j++){
                m_game_board[i][j].SetDefaultShape("moon");
            }
        }

        // heart
        for (int i = 6; i < 9; i++){
            for (int j = 3; j < 6; j++){
                m_game_board[i][j].SetDefaultShape("heart");
            }
        }

        // square
        for (int i = 6; i < 9; i++){
            for (int j = 6; j < 9; j++){
                m_game_board[i][j].SetDefaultShape("square");
            }
        }

        // 1
        for (int i = 0; i < 9; i++){
            m_game_board[i][0].SetDefaultNumber("1");
        }

        // 2
        for (int i = 0; i < 9; i++){
            m_game_board[i][1].SetDefaultNumber("2");
        }

        // 3
        for (int i = 0; i < 9; i++){
            m_game_board[i][2].SetDefaultNumber("3");
        }

        // 4
        for (int i = 0; i < 9; i++){
            m_game_board[i][3].SetDefaultNumber("4");
        }

        // 5
        for (int i = 0; i < 9; i++){
            m_game_board[i][4].SetDefaultNumber("5");
        }

        // 6
        for (int i = 0; i < 9; i++){
            m_game_board[i][5].SetDefaultNumber("6");
        }

        // 7
        for (int i = 0; i < 9; i++){
            m_game_board[i][6].SetDefaultNumber("7");
        }

        // 8
        for (int i = 0; i < 9; i++){
            m_game_board[i][7].SetDefaultNumber("8");
        }

        // 9
        for (int i = 0; i < 9; i++){
            m_game_board[i][8].SetDefaultNumber("9");
        }

        // A
        for (int i = 0; i < 9; i++){
            m_game_board[0][i].SetDefaultLetter("A");
        }

        // B
        for (int i = 0; i < 9; i++){
            m_game_board[1][i].SetDefaultLetter("B");
        }

        // C
        for (int i = 0; i < 9; i++){
            m_game_board[2][i].SetDefaultLetter("C");
        }

        // D
        for (int i = 0; i < 9; i++){
            m_game_board[3][i].SetDefaultLetter("D");
        }

        // E
        for (int i = 0; i < 9; i++){
            m_game_board[4][i].SetDefaultLetter("E");
        }

        // F
        for (int i = 0; i < 9; i++){
            m_game_board[5][i].SetDefaultLetter("F");
        }

        // G
        for (int i = 0; i < 9; i++){
            m_game_board[6][i].SetDefaultLetter("G");
        }

        // H
        for (int i = 0; i < 9; i++){
            m_game_board[7][i].SetDefaultLetter("H");
        }

        // I
        for (int i = 0; i < 9; i++){
            m_game_board[8][i].SetDefaultLetter("I");
        }
    }

    // returns the piece at the given coordinates of the board
    public Piece GetPiece(String a_coordinates){
        char Srow = a_coordinates.charAt(0);
        char Scol = a_coordinates.charAt(1);

        int row = Character.getNumericValue(Srow);
        int col = Character.getNumericValue(Scol);

        Piece target = m_game_board[row][col];
        return target;
    }

    // returns the piece at the given x,y coordinates on the board
    public Piece GetPiece(int a_x, int a_y){

        Piece target = m_game_board[a_x][a_y];
        return target;
    }

    // displays the board (used in debugging)
    public void DisplayBoard(){
        System.out.println("----------GameBoard----------");
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                if(this.m_game_board[i][j].GetColor().equals("")){
                    System.out.print("null-");
                }
                else{
                    System.out.print(this.m_game_board[i][j].GetColor() + "-");
                }
            }
            System.out.println("");
        }
    }

    // adds the given piece to the board at the given coordinates
    public void AddPiece(Piece a_token, String a_coordinates){
        char sx_coor = a_coordinates.charAt(0);
        char sy_coor = a_coordinates.charAt(1);

        int x_coordinate = Character.getNumericValue(sx_coor);
        int y_coordinate = Character.getNumericValue(sy_coor);

        this.m_game_board[x_coordinate][y_coordinate].SetType(a_token.GetType());
        this.m_game_board[x_coordinate][y_coordinate].SetColor(a_token.GetColor());
    }




    /**/
    /*
    GetGroups()

    NAME

            GetGroups()

    SYNOPSIS

            int GetNewGroups(String a_color)
            a_color -> used to search the board for pieces matching this color


    DESCRIPTION

            This function returns the number of groups of a particular color found on the
            game board. This function is essentially the most essential part of the game
            considering it is used to determine a winner of the game as well as if a player
            is allowed to play at a certain spot.

            The function will take the color given and find each piece on the board that
            matches this color then calculate and find how many groups there are of this
            color on the board.

    RETURNS

            returns int
             -> The number of groups of the particular color on the board

    AUTHOR

            Glenn Ryan

    DATE

            7:12pm 3/25/2018

    */
    /**/
    public int GetGroups(String a_color){

        // list of lists of groups found with this color
        List<List<Integer>> listed_groupings = new ArrayList<List<Integer>>();
        // hold each piece on the board of the selected color
        ArrayList<Integer> piece_array = new ArrayList<Integer>();


        // iterate through the board and add pieces of selected color to list
        for (int i = 0; i < 9; i++){
            for (int j = 0; j<9; j++){
                if (this.m_game_board[i][j].GetColor().equals(a_color)){
                    piece_array.add((i*10)+j);
                }
            }
        }

        // Recursively find new groups until there are no more to choose from
        while(piece_array.size()!=0){

            List<Integer> new_group = GetNewGroup(piece_array.get(0), piece_array);
            // add the new group to the list of groups
            listed_groupings.add(new_group);

        }

        // Connect the remaining groups recursivley
        while(Connect(listed_groupings)==false){
            Connect(listed_groupings);
        }

        return listed_groupings.size();


    }

    /**/
    /*
    GetNewGroup()

    NAME

            GetNewGroup()

    SYNOPSIS

            ArrayList<Integer> GetNewGroup(int a_piece, ArrayList<Integer> a_piece_array)
            a_piece -> piece to search for adjacent pieces
            a_piece_array -> Array to be searched for the adjacent pieces


    DESCRIPTION

            This function returns a new group regarding the given piece and any adjacent
            pieces in the array given as well. It will then delete repeated pieces and
            erase the used pieces by reference from the given array

            This function is used recursively in the GetGroups function and finds all
            adjacent pieces (groups of pieces) of the color

            When all groups are obtained, this function uses the Connect() function in order
            to connect any remaining groups that can still be connected

    RETURNS

            returns ArrayList<Integer>
             -> This array is a grouping of pieces that are adjacent to the given piece

    AUTHOR

            Glenn Ryan

    DATE

            7:12pm 3/20/2018

    */
    /**/
    public ArrayList<Integer> GetNewGroup(int a_piece, ArrayList<Integer> a_piece_array){

        // Grouping to hold the current group
        ArrayList<Integer> new_grouping = new ArrayList<Integer>();

        // Add the a_piece to the group
        new_grouping.add(new Integer(a_piece));
        if(a_piece_array.contains(a_piece)){
            a_piece_array.remove(Integer.valueOf(a_piece));
        }

        for(int i = 0; i < a_piece_array.size(); i++){
            //if the piece is adjacent, add it to the new grouping
            if (a_piece == (a_piece_array.get(i)+10) || a_piece == (a_piece_array.get(i)-10) || a_piece == (a_piece_array.get(i)+1) || a_piece == (a_piece_array.get(i)-1)){

                // use recursion to find all adjacent pieces to any pieces found
                new_grouping.addAll(GetNewGroup(new Integer(a_piece_array.get(i)),a_piece_array));


            }
        }

        // return the group
        return new_grouping;


    }

    /**/
    /*
    Connect()

    NAME

            Connect()

    SYNOPSIS

            boolean Connect(List<List<Integer>> a_group_list)
            a_group_list -> list of lists given to be connected if possible (if pieces
            of two lists in this list are adjacent, the function will connect groups)



    DESCRIPTION

            This function joins and lists within the given multi-dimensional if any
            two elements in the list are adjacent to one another on the board.

    RETURNS

            returns boolean
             -> Returns true if the group has been fully connected

    AUTHOR

            Glenn Ryan

    DATE

            2:22pm 3/23/2018

    */
    /**/
    private boolean Connect(List<List<Integer>> a_group_list){

        // iterate through the multi-dimensional array
        for (List<Integer> lists : a_group_list){
            for (int num : lists){
                // iterate through the array again (join upon itself)
                for (List<Integer> lists2 : a_group_list){
                    // if the list is not the same list in the array, check the following
                    if (!(lists2.equals(lists))){
                        // if any pieces are adjacent to one another, do the following
                        if (lists2.contains(num+1) || lists2.contains(num-1) || lists2.contains(num+10) || lists2.contains(num-10)){
                            // combine the lists
                            List<Integer> combined_list = new ArrayList<Integer>(lists2);
                            combined_list.addAll(lists);

                            // remove both lists then add the combined list in their place
                            a_group_list.remove(lists);
                            a_group_list.remove(lists2);
                            a_group_list.add(combined_list);

                            // return false, the groups are not yet proven fully combined
                            return false;
                        }

                    }

                }
            }
        }
        // the lists are all combine, we are done
        return true;

    }

}
