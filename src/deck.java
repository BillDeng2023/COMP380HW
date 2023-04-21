import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class deck {

    private ArrayList<card> cards;

    public deck() {
        this.cards = new ArrayList<>();
        for (Suite suit : Suite.values()) {
            for (Rank rank : Rank.values()) {
                this.cards.add(new card(rank, suit));
            }
        }
        shuffle();
    }

    public deck(ArrayList<card> cards){
        this.cards = cards;
    }

    public ArrayList<card> getDeckContent(){
        return this.cards;
    }

    public void removeCards(ArrayList<card> cardsToRemove) {
        this.cards.removeAll(cardsToRemove);
    }



    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty");
        }
        return cards.remove(cards.size() - 1);
    }

    // Removes a specific card from the deck
    public void removeCard(card cardToRemove) {
        cards.removeIf(card -> card.equals(cardToRemove));
    }

    // Returns a deep copy of the deck object
    public deck copy() {
        deck newDeck = new deck(new ArrayList<card>(this.cards));
        return newDeck;
    }

}
