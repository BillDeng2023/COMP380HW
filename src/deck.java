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
    }

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public card draw() {
        if (this.cards.isEmpty()) {
            throw new IllegalStateException("The deck is empty");
        }
        return this.cards.remove(0);
    }
}
