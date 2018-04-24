package com.google.gryan0822.blockers;

/**
 * Created by gryan on 2/17/2018.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// The following class is responsible for holding the model deck of pieces the user will use in the round
public class Deck implements Serializable {

    // VARIABLES
    // holds the stock (unshuffled) pieces of the deck
    private List<Piece> m_stock = new ArrayList<Piece>();
    // holds the shuffled pieces of the deck
    private List<Piece> m_deck = new ArrayList<Piece>();

    // Default constructor initializes the stock
    public Deck(){
        // add the shapes
        m_stock.add(new Piece(true, false, false, "triangle")); m_stock.add(new Piece(true, false, false, "cross")); m_stock.add(new Piece(true, false, false, "star"));
        m_stock.add(new Piece(true, false, false, "clover")); m_stock.add(new Piece(true, false, false, "circle")); m_stock.add(new Piece(true, false, false, "diamond"));
        m_stock.add(new Piece(true, false, false, "moon")); m_stock.add(new Piece(true, false, false, "heart")); m_stock.add(new Piece(true, false, false, "square"));

        // add the numbers
        m_stock.add(new Piece(false, true, false, "1")); m_stock.add(new Piece(false, true, false, "2")); m_stock.add(new Piece(false, true, false, "3"));
        m_stock.add(new Piece(false, true, false, "4")); m_stock.add(new Piece(false, true, false, "5")); m_stock.add(new Piece(false, true, false, "6"));
        m_stock.add(new Piece(false, true, false, "7")); m_stock.add(new Piece(false, true, false, "8")); m_stock.add(new Piece(false, true, false, "9"));

        // add the letters
        m_stock.add(new Piece(false, false, true, "A")); m_stock.add(new Piece(false, false, true, "B")); m_stock.add(new Piece(false, false, true, "C"));
        m_stock.add(new Piece(false, false, true, "D")); m_stock.add(new Piece(false, false, true, "E")); m_stock.add(new Piece(false, false, true, "F"));
        m_stock.add(new Piece(false, false, true, "G")); m_stock.add(new Piece(false, false, true, "H")); m_stock.add(new Piece(false, false, true, "I"));

        // add the wild
        m_stock.add(new Piece("wild"));
    }

    // SELECTORS
    // prints the elements inside the m_deck (used for debugging)
    public void DisplayDeck(){
        for (Piece element : this.m_deck) {
            System.out.println(element.GetType() + " ");
        }
    }

    // returns the size of the deck
    public int GetSize(){
        return m_deck.size();
    }


    // returns true if the m_deck is empty
    public boolean IsEmpty(){
        if (this.m_deck.size() == 0){
            return true;
        }
        else{
            return false;
        }
    }

    // MUTATORS
    // copies the elements in the m_stock into the m_deck and shuffles
    public void ShuffleDeck(){
        this.m_deck.addAll(this.m_stock);
        Collections.shuffle(this.m_deck);
    }

    // sets the new color as the color of pieces in the m_stock
    public void SetColor(String a_Ncolor){

        for (Piece i : this.m_stock){
            i.SetColor(a_Ncolor);
        }
    }

    // returns and deletes the first 8 elements in the m_deck
    public List<Piece> TakeHand(){
        List<Piece> hand = new ArrayList<Piece>();
        for (int i = 0; i < 5; i++){
            hand.add(this.m_deck.get(0));
            this.m_deck.remove(0);
        }
        return hand;
    }


    // returns first pip from the m_deck
    public Piece DrawPiece(){
        Piece piece = this.m_deck.get(0);
        this.m_deck.remove(0);
        return piece;
    }

}