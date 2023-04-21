import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Position {
    private deck deck;
    private List<hand> playerHands;
    private hand dealerHand;

    public Position(deck deck, List<hand> playerHands, hand dealerHand) {
        this.deck = deck;
        this.playerHands = playerHands;
        this.dealerHand = dealerHand;
    }

    private List<Integer> getNormalizedRanks(hand hand) {
        List<Integer> ranks = new ArrayList<>();

        for (card card : hand.getHandContent()) {
            int rankValue = card.getRank().getValue();

            if (rankValue >= 10 && !hand.isPair()) {
                rankValue = 10;
            }
            ranks.add(rankValue);
        }

        Collections.sort(ranks);
        return ranks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position) o;

        if (!deck.equals(position.deck)) {
            return false;
        }

        if (playerHands.size() != position.playerHands.size()) {
            return false;
        }

        for (int i = 0; i < playerHands.size(); i++) {
            hand thisHand = playerHands.get(i);
            hand otherHand = position.playerHands.get(i);

            List<Integer> thisRanks = getNormalizedRanks(thisHand);
            List<Integer> otherRanks = getNormalizedRanks(otherHand);

            if (!thisRanks.equals(otherRanks)) {
                return false;
            }
        }

        List<Integer> dealerThisRanks = getNormalizedRanks(dealerHand);
        List<Integer> dealerOtherRanks = getNormalizedRanks(position.dealerHand);

        return dealerThisRanks.equals(dealerOtherRanks);
    }

    @Override
    public int hashCode() {
        int result = deck.hashCode();
        result = 31 * result + dealerHand.hashCode();

        List<List<Integer>> playerHandRanks = new ArrayList<>();
        for (hand hand : playerHands) {
            playerHandRanks.add(getNormalizedRanks(hand));
        }
        playerHandRanks.sort((o1, o2) -> {
            for (int i = 0; i < o1.size(); i++) {
                int cmp = Integer.compare(o1.get(i), o2.get(i));
                if (cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        });

        result = 31 * result + playerHandRanks.hashCode();
        return result;
    }
}
