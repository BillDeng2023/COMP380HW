import java.util.HashSet;

public class hand {
    /**
     * This class represents the players' hand in blackjack. It may be used to obtain the
     * soft and hard values of the hand.
     */

    private HashSet<card> cards;

    /**
     * Initialize hand.
     */
    public hand(){
        cards = new HashSet<>();
    }

    /**
     * This method adds a singular card to the hand.
     * @param card a card object
     */
    public void addCard(card card){
        cards.add(card);
    }

    /**
     * This method adds multiple cards to the hand at once.
     * @param card a hashset containing the card objects to be added
     */
    public void addCards(HashSet<card> card){
        cards.addAll(card);
    }

    /**
     * This method calculates the hard value of the hand.
     * @return an integer representing the value of the hand
     */
    public int getHardValue(){
        int sum = 0;
        for(card c : cards){
            Rank rank = c.getRank();
            switch (rank){
                case ACE: sum += 1;
                    break;
                case TWO : sum += 2;
                    break;
                case THREE: sum += 3;
                    break;
                case FOUR: sum += 4;
                    break;
                case FIVE: sum += 5;
                    break;
                case SIX: sum += 6;
                    break;
                case SEVEN: sum += 7;
                    break;
                case EIGHT: sum += 8;
                    break;
                case NINE: sum += 9;
                    break;
                case TEN: sum += 10;
                    break;
                case JACK : sum += 10;
                    break;
                case QUEEN : sum += 10;
                    break;
                case KING : sum += 10;
                    break;
                default  : sum += 0;
                    break;
            }
        }
        return sum;
    }

    /**
     * Calculates the soft value of the hand based on hard value.
     * @return an integer representing the soft value of the hand
     */
    public int getSoftValue(){
        boolean hasAce = false;
        // iterate through all cards in the hand to determine if there is an ace
        for(card c : cards){
            if(c.getRank() == Rank.ACE){
                hasAce = true;
                break;
            }
        }

        if(hasAce) return getHardValue() + 10;
        else return getHardValue();
    }
}
