import java.util.*;

public class hand {
    /**
     * This class represents the players' hand in blackjack. It may be used to obtain the
     * soft and hard values of the hand.
     */

    private ArrayList<card> cards;
    private boolean isfinal;
    private boolean surrendered;
    private boolean doubled;
    /**
     * Initialize hand.
     */
    public hand(){
        this.cards = new ArrayList<>();
        isfinal = false;
        surrendered = false;
    }

    public hand(ArrayList<card> cards){
        this.cards = cards;
        isfinal = false;
        surrendered = false;
    }

    public ArrayList<card> getHandContent(){
        return this.cards;
    }

    public hand split(){
        hand other = new hand();
        other.addCard(cards.remove(cards.size() - 1));
        return other;
    }

    /**
     * This method adds a singular card to the hand.
     * @param card a card object
     */
    public void addCard(card card){
        cards.add(card);
    }

    public boolean isPair(){
        if(cards.size() == 2){
            return cards.get(0).getRank() == cards.get(1).getRank();
        }
        return false;
    }

    public void makeFinal(){
        isfinal = true;
    }

    public void makeSurrender(){
        surrendered = true;
    }

    public void makeDouble(){
        doubled = true;
    }

    public boolean isFinal(){
        return isfinal;
    }

    public boolean isSurrendered(){
        return surrendered;
    }

    public boolean isDoubled(){
        return doubled;
    }

    public boolean isBlackJack(){
        return (handSize() == 2) && hasAce() && (getSoftValue() == 21);
    }

    public boolean isBust(){
        return getHardValue() > 21;
    }

    /**
     * This method adds multiple cards to the hand at once.
     * @param card a hashset containing the card objects to be added
     */
    public void addCards(ArrayList<card> card){
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
            sum += card.rankToValue(rank);
        }
        return sum;
    }

    public int handSize(){
        return cards.size();
    }

    public boolean hasAce(){
        boolean hasAce = false;
        // iterate through all cards in the hand to determine if there is an ace
        for(card c : cards){
            if(c.getRank() == Rank.ACE){
                hasAce = true;
                break;
            }
        }
        return hasAce;
    }
    /**
     * Calculates the soft value of the hand based on hard value.
     * @return an integer representing the soft value of the hand
     */
    public int getSoftValue(){
        if(this.hasAce()) return getHardValue() + 10;
        else return getHardValue();
    }

    public int highestValue(){
        int soft = getSoftValue();
        if(soft > 21) return getHardValue();
        else return soft;
    }
}
