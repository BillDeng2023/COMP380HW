import java.util.ArrayList;
import java.util.Collections;

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

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty");
        }
        return cards.remove(cards.size() - 1);
    }
}
