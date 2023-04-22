import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        hand dealerHand = new hand();
        dealerHand.addCard(dealerCard);

        ArrayList<card> testCards = new ArrayList<card>();
        String[] testHex = {"1f0a7", "1f0c3"};
        for (String hex: testHex){
            card newCard = new card(hex);
            testCards.add(newCard);
        }
        deck deckTest = new deck(testCards);

        System.out.println(makeIdealStrat(deckTest, playerHand, dealerHand));

    }


    public static double calculateScore(hand hand, hand dealerHand) {
        if(hand.isSurrendered()){
            return -0.5;
        }
        else if(hand.isBlackJack()){
            if(dealerHand.isBlackJack()){
                return 0;
            }
            else{
                if(hand.isDoubled()) return 3;
                else return 1.5;
            }
        }
        else if(dealerHand.isBlackJack() || hand.isBust()){
            if(hand.isDoubled()) return -2;
            else return -1;
        }
        else{
            if(dealerHand.highestValue() == hand.highestValue()){
                return 0;
            }
            else if(dealerHand.highestValue() > hand.highestValue()){
                if(hand.isDoubled()) return -2;
                else return -1;
            }
            else if(dealerHand.highestValue() < hand.highestValue()){
                if(hand.isDoubled()) return 2;
                else return 1;
            }
        }
        return 1000000;
    }

    public static double calculateScore(deck deck, hand hand, hand dealerHand) {
        //if(hand.isSurrendered()) return -0.5;
        //checks cache
//        Position position = new Position (deck, hand, dealerHand);
//        int key = position.hashCode();
//        if(dCache.hasKey(key)){
//            return dCache.getValue(key).getScore();
//        }

        double outcome = 0;
        String action = "";
        List<card> remainingCards = deck.getDeckContent();
        int numRemainingCards = remainingCards.size();
        deck newDeck;
        if (dealerHand.isBust()) {
            outcome = calculateScore(hand, dealerHand);
        }
        else{
            if (remainingCards.size() == 1){
                action = "STAY";
                outcome = calculateScore(hand, dealerHand);
                System.out.println(outcome);
            } else {
                action = "HIT";
                System.out.println("Hitting");
                for (card card : remainingCards) {
                    newDeck = deck.copy();
                    hand newPlayerHand = hand.copy();
                    newDeck.removeCard(card);
                    System.out.println(newDeck.getDeckContent());

                    hand newDealerHand = dealerHand.copy();
                    newDealerHand.addCard(card);

                    double temp = calculateScore(newDeck, newPlayerHand, newDealerHand);
                    outcome += temp;
                }

                outcome /= numRemainingCards;
            }
        }
        //dCache.putValue(position.hashCode(), action, outcome);
        System.out.println(outcome);
        return outcome;
    }

    public static double makeIdealStrat(deck deck, hand hand, hand dealerHand) {
        double maxScore = -8;

        deck newDeck;
        hand newPlayerHand;
        hand newDealerHand;
        if (hand.isBust()) {
            newDeck = deck.copy();
            newPlayerHand = hand.copy();
            newDealerHand = dealerHand.copy();
            newPlayerHand.makeFinal();
            maxScore = calculateScore(newPlayerHand, newDealerHand);
        } else {
            //calculate score for different outcomes
            double hitScore = -8;
            double stayScore = -8;
            double doubleScore = -8;
            double splitScore = -8;
            double surrenderScore = -8;

            List<card> remainingCards = deck.getDeckContent();
            int numRemainingCards = remainingCards.size();

//            // HIT
//            for (card card : remainingCards) {
//                newDeck = deck.copy();
//                newPlayerHand = hand.copy();
//                newDealerHand = dealerHand.copy();
//                newDeck.removeCard(card);
//                newPlayerHand.addCard(card);
//
//                hitScore += makeIdealStrat(newDeck, newPlayerHand, newDealerHand);
//            }
//            hitScore /= numRemainingCards;
//
//            splitScore = -8;

//            //Double
//            for (card card : remainingCards) {
//                newDeck = deck.copy();
//                newPlayerHand = hand.copy();
//                newDealerHand = dealerHand.copy();
//                newDeck.removeCard(card);
//                newPlayerHand.addCard(card);
//                newPlayerHand.makeDouble();
//                newPlayerHand.makeFinal();
//
//                doubleScore += calculateScore(newDeck, newPlayerHand, newDealerHand);
//            }
//            doubleScore /= numRemainingCards;

//            // STAY
//            newDeck = deck.copy();
//            newPlayerHand = hand.copy();
//            newDealerHand = dealerHand.copy();
//            newPlayerHand.makeFinal();
//            stayScore = calculateScore(newDeck, newPlayerHand, newDealerHand);

            //Surrender
            newDeck = deck.copy();
            newPlayerHand = hand.copy();
            newDealerHand = dealerHand.copy();
            newPlayerHand.makeSurrender();
            newPlayerHand.makeFinal();
            surrenderScore = calculateScore(newDeck, newPlayerHand, newDealerHand);

            List<Double> scores = Arrays.asList(hitScore, stayScore, splitScore, surrenderScore, doubleScore);

            maxScore = Collections.max(scores);

            String action;

            if (maxScore == hitScore)
                action = "HIT";
            else if (maxScore == stayScore)
                action = "STAY";
            else if (maxScore == doubleScore)
                action = "DOUBLE";
            else if (maxScore == splitScore)
                action = "SPLIT";
            else
                action = "SURRENDER";

            System.out.println(action);

        }
        return maxScore;
    }
}
