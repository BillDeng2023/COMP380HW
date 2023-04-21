import java.util.*;
import java.util.stream.Collectors;

public class Position {
    private deck deck;
    private hand playerHand;
    private hand dealerHand;

    public Position(deck deck, hand playerHand, hand dealerHand) {
        this.deck = deck;
        this.playerHand = playerHand;
        this.dealerHand = dealerHand;
    }

    public hand getHand() { return this.playerHand; }

    public deck getDeck(){
        return this.deck;
    }

    public hand getDealerHand(){
        return this.dealerHand;
    }

//    private List<Integer> getNormalizedRanks(hand hand) {
//        List<Integer> ranks = new ArrayList<>();
//
//        for (card card : hand.getHandContent()) {
//            int rankValue = card.getRank().getValue();
//
//            if (rankValue >= 10 && !hand.isPair()) {
//                rankValue = 10;
//            }
//            ranks.add(rankValue);
//        }
//
//        Collections.sort(ranks);
//        return ranks;
//    }

    @Override
    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//
//        Position position = (Position) o;

//        if (!deck.equals(position.deck)) {
//            return false;
//        }
//
//        if (playerHands.size() != position.playerHands.size()) {
//            return false;
//        }
//
//        for (int i = 0; i < playerHands.size(); i++) {
//            hand thisHand = playerHands.get(i);
//            hand otherHand = position.playerHands.get(i);
//
//            List<Integer> thisRanks = getNormalizedRanks(thisHand);
//            List<Integer> otherRanks = getNormalizedRanks(otherHand);
//
//            if (!thisRanks.equals(otherRanks)) {
//                return false;
//            }
//        }
//
//        List<Integer> dealerThisRanks = getNormalizedRanks(dealerHand);
//        List<Integer> dealerOtherRanks = getNormalizedRanks(position.dealerHand);


        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {

        String concatenatedHash = playerHand.hashCode() + "," + dealerHand.hashCode() + ","
            + deck.hashCode();
        return concatenatedHash.hashCode();
    }
}
