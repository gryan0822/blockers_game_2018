package com.google.gryan0822.blockers;

import java.io.Serializable;

/**
 * Created by gryan on 2/17/2018.
 */

// Class responsible for holding the functionality of a game piece
public class Piece implements Serializable{

    // VARIABLES
    // holds default shape of a piece
    private String m_default_shape;
    // holds default letter of a piece
    private String m_default_letter;
    // holds default number of a piece
    private String default_number;

    // holds true if the piece is a wild
    private boolean wild;
    // holds the pieces color
    private String m_color;
    // holds the pieces type
    private String m_type;

    // CONSTRUCTORS
    // Initializes an empty piece
    public Piece(){

        wild = false;
        m_color = "";
        m_type="";
    }

    // Copy Constructor that copies each attribute of another piece into the current piece
    public Piece(Piece a_another){

        this.m_color = a_another.m_color;
        this.wild = a_another.wild;
        this.m_type = a_another.m_type;
        this.m_default_letter = a_another.m_default_letter;
        this.default_number = a_another.default_number;
        this.m_default_shape = a_another.m_default_shape;
    }

    // Overloaded constructor that sets a piece to wild
    public Piece(String a_w){
        m_type = a_w;
        wild = true;
    }

    // Overloaded Constructor that sets default values according to what it is given
    public Piece(boolean a_shape, boolean a_number, boolean a_letter, String a_type){

        // set the piece type if a shape
        if (a_shape && !(a_number && a_letter)){

            wild = false;
            m_type = a_type;
        }
        // set the piece type if a number
        else if (a_number && !(a_letter && a_shape)){

            wild = false;
            m_type = a_type;
        }
        // set the piece type if a letter
        else if (a_letter && !(a_shape && a_number)){

            wild = false;
            m_type = a_type;
        }
        // set the piece to a blank piece
        else{

            wild = false;
            m_type="";
        }

    }

    // SELECTORS
    // gets the default shape of a piece
    public String GetDefaultShape(){
        return m_default_shape;
    }

    // gets the default letter of a piece
    public String GetDefaultLetter(){
        return m_default_letter;
    }

    // gets the default number of a piece
    public String GetDefaultNumber(){
        return default_number;
    }

    // gets the type of a piece
    public String GetType(){
        return m_type;
    }

    // gets the color of a piece
    public String GetColor(){
        return m_color;
    }

    // MUTATORS
    // sets the default shape of a piece
    public void SetDefaultShape(String a_shape){
        m_default_shape = a_shape;
    }

    // sets the default letter of a piece
    public void SetDefaultLetter(String a_letter){
        m_default_letter = a_letter;
    }

    // sets the default number of a piece
    public void SetDefaultNumber(String a_number){
        default_number = a_number;
    }

    // sets the color of a piece
    public void SetColor(String a_color){
        this.m_color = a_color;
    }

    // sets the type of a piece
    public void SetType(String a_type){
        this.m_type = a_type;
    }

    // returns string type of piece
    public String ToString(){
        return m_type;
    }
}

