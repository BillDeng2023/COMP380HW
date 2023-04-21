import java.util.ArrayList;
import java.util.List;

public class blackjacktest {
    public static void main(String[] args) throws Exception {
        String presetLn = ",,1f0d9,1f0d5,1f0a8,1f0d7,1f0db,1f0c1,1f0cd,1f0d3,1f0de,1f0a2,1f0c5";

        String[] preset = presetLn.split(",");

        ArrayList<card> drawnCards = new ArrayList<>();
        hand playerHand = new hand();
        card dealerCard = null;
        for (int j = 2; j < preset.length; j++) {
            try {
                switch (j) {
                    case 2:
                        dealerCard = new card(preset[j]);
                        drawnCards.add(dealerCard);
                        break;
                    case 11:
                    case 12:
                        card playerCard = new card(preset[j]);
                        playerHand.addCard(playerCard);
                        drawnCards.add(playerCard);
                        break;
                    default:
                        if (!preset[j].equals("")) {
                            drawnCards.add(new card(preset[j]));
                        }
                }
            } catch (Exception e) {
                System.out.println("invalid input preset");
            }
        }

        deck deckNaive = new deck();
        deckNaive.removeCards(drawnCards);
        deck deckTest = new deck();
        deckTest.removeCards(drawnCards);
        hand dealerHand = new hand();
        dealerHand.addCard(dealerCard);
        hand dealerTestHand = new hand();
        dealerTestHand.addCard(new card("1f0c5"));


        hand playerHand2 = new hand();
        playerHand2.addCard(new card("1f0db"));
        playerHand2.addCard(new card("1f0a2"));

        List<hand> playerHands1 = new ArrayList<>();
        playerHands1.add(playerHand);
        playerHands1.add(playerHand2);

        List<hand> playerHands2 = new ArrayList<>();
        playerHands2.add(playerHand2);
        playerHands2.add(playerHand);
        Position positionNaive = new Position(deckNaive, playerHands1, dealerHand);
        Position positionTest = new Position(deckNaive, playerHands2, dealerHand);

        System.out.println(positionNaive.equals(positionTest));
    }
}
