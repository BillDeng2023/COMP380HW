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

    public hand copy() {
        hand newHand = new hand(new ArrayList<card>(this.cards));
        newHand.isfinal = this.isfinal;
        newHand.surrendered = this.surrendered;
        newHand.doubled = this.doubled;
        return newHand;
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

    public void unFinal(){
        isfinal = false;
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

    public card getDealerCard(){
        return cards.get(0);
    }

    public int getPairValue() {
        // get the rank of the first card in the hand
        Rank rank = cards.get(0).getRank();
        // return the pair value based on the rank of the card
        switch(rank) {
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            case FIVE:
                return 5;
            case SIX:
                return 6;
            case SEVEN:
                return 7;
            case EIGHT:
                return 8;
            case NINE:
                return 9;
            case TEN:
            case JACK:
            case QUEEN:
            case KING:
                return 10;
            case ACE:
                // return a special value for pairs of aces
                return 12;
            default:
                return 0;
        }
    }

    public boolean canSurrender() {
        // Check if the hand is eligible for surrender based on its value
        // and the rules of the game being played.
        if (this.cards.size() == 2 && this.getHardValue() == 16) {
            // The hand is eligible for surrender if it has a value of 16 and
            // the dealer's up card is 9, 10, or Ace.
            return true;
        } else {
            return false;
        }
    }

    public boolean isSoft() {
        return hasAce() && getSoftValue() <= 21;
    }

    @Override
    public int hashCode(){
        int[] values = new int[cards.size()+2];
        for (int i = 0; i < cards.size(); i++) {
            int rankValue = cards.get(i).getRank().getValue();
            if (rankValue >= 10) {
                rankValue = 10;
            }
            values[i] = rankValue;
        }
        Arrays.sort(values);
        if(this.isSurrendered()){
            values[0] = 1;
        }
        if(this.isDoubled()){
            values[1] = 1;
        }
        return Arrays.hashCode(values);
    }
}
