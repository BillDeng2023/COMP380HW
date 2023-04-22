//import com.google.common.base.Preconditions;
//import com.google.common.collect.ImmutableMap;

//import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.io.*;

public class blackjack {
    private static ArrayList<ArrayList<String>> hardstrat = null;
    private static ArrayList<ArrayList<String>> softstrat = null;
    private static ArrayList<ArrayList<String>> pairstrat = null;

    private static PositionCache pCache = new PositionCache();
    private static PositionCache dCache = new PositionCache();
    /**
     * The main function reads in data from the given blackjack situation samples and writes
     * an output document containing the situations and next step for the player based
     * on a simple algorithm.
     * @param args
     * @throws IOException throws exception when there is an invalid card
     */
    public static void main(String[] args) throws Exception {
        //read in strategies
        try {
            hardstrat = readStrategy("src/Wiki Strategy (Modified) - hard.csv");
            softstrat = readStrategy("src/Wiki Strategy (Modified) - soft.csv");
            pairstrat = readStrategy("src/Wiki Strategy (Modified) - pairs.csv");
        } catch (Exception e) {
            System.out.println("read strategies error");
        }

        ArrayList<String[]> presets = new ArrayList<>();

        BufferedReader presetReader = new BufferedReader(new FileReader("src/hw5.csv"));
        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter("src/blackjack_output.csv")));
        String presetLn = "";
        while((presetLn = presetReader.readLine()) != null) {
            if(presetLn.length() < 1 || presetLn.charAt(0) != ','){
                continue;
            }
            String[] instance = presetLn.split(",");
            presets.add(instance);
        }


        for (String[] preset : presets) {
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
            hand dealerHand = new hand();
            hand newPlayerHand = playerHand.copy();
            deckNaive.removeCards(drawnCards);
            dealerHand.addCard(dealerCard);
            makeIdealStrat(deckNaive, newPlayerHand, dealerHand);

            double naive_average = 0;
            double advanced_average = 0;
            //System.out.println(dealerCard.getRank() + " " + dealerCard.getSuite());
            int iteration = 100;
            for(int i = 0; i < iteration; i++){
                deckNaive = new deck();
                deckNaive.removeCards(drawnCards);
                deck deckAdvanced = deckNaive.copy();

                double naive = playIdeal(deckNaive, playerHand, dealerCard);
                naive_average += naive;
                double advanced = play("advanced", deckAdvanced, playerHand, dealerCard);
                advanced_average += advanced;
            }
            naive_average = naive_average/iteration;
            advanced_average = advanced_average/(double) iteration;
            StringBuilder line = new StringBuilder();
            line.append(Math.round(naive_average * 1e5) / 1e5 + "," + advanced_average);
            for (int j = 2; j < preset.length; j++) {
                line.append("," + preset[j]);
            }
            printWriter.println(line.toString());
        }


        printWriter.close();
    }

    //calculate score with one hand
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

    //calculate score for one hand for all possible dealerCards
    public static double calculateScore(deck deck, hand hand, hand dealerHand) {
        //if(hand.isSurrendered()) return -0.5;
        //checks cache
        Position position = new Position (deck, hand, dealerHand);
        int key = position.hashCode();
        if(dCache.hasKey(key)){
            return dCache.getValue(key).getScore();
        }

        double outcome = 0;
        String action = "";
        List<card> remainingCards = deck.getDeckContent();
        int numRemainingCards = remainingCards.size();
        deck newDeck;
        if (dealerHand.isBust()) {
            outcome = calculateScore(hand, dealerHand);
        }
        else{
            action = naiveStrategy(dealerHand);
            switch (action) {
                case "STAY":
                    outcome = calculateScore(hand, dealerHand);
                    break;
                case "HIT":
                    for (card card : remainingCards) {
                        newDeck = deck.copy();
                        hand newPlayerHand = hand.copy();
                        newDeck.removeCard(card);

                        hand newDealerHand = dealerHand.copy();
                        newDealerHand.addCard(card);

                        double temp = calculateScore(newDeck, newPlayerHand, newDealerHand);
                        outcome += temp;
                    }

                    outcome /= numRemainingCards;
                    break;
            }
        }
        dCache.putValue(position.hashCode(), action, outcome);
        return outcome;
    }

    //calculate aggregate score for list of hands
    public static double calculateScore(List<hand> hands, hand dealerHand) {
        double outcome = 0;
        for(hand hand: hands){
            outcome += calculateScore(hand, dealerHand);
        }
        return outcome;
    }

    public static double makeIdealStrat(deck deck, hand hand, hand dealerHand) {
        double maxScore = -8;
        Position position = new Position (deck, hand, dealerHand);
        int key = position.hashCode();
        if(pCache.hasKey(key)){
            return pCache.getValue(key).getScore();
        }

        deck newDeck;
        hand newPlayerHand;
        hand newDealerHand;
        if (hand.isBust()) {
            newDeck = deck.copy();
            newPlayerHand = hand.copy();
            newDealerHand = dealerHand.copy();
            newPlayerHand.makeFinal();
            maxScore =  calculateScore(newDeck, newPlayerHand, newDealerHand);
            return maxScore;
        }
        else{
            //calculate score for different outcomes
            double hitScore = -8;
            double stayScore = -8;
            double doubleScore = -8;
            double splitScore = -8;
            double surrenderScore = -8;

            List<card> remainingCards = deck.getDeckContent();
            int numRemainingCards = remainingCards.size();

            // HIT
            for (card card : remainingCards) {
                newDeck = deck.copy();
                newPlayerHand = hand.copy();
                newDealerHand = dealerHand.copy();
                newDeck.removeCard(card);
                newPlayerHand.addCard(card);

                hitScore += makeIdealStrat(newDeck, newPlayerHand, newDealerHand);
            }
            hitScore /= numRemainingCards;

            splitScore = -8;

            //Double
            for (card card : remainingCards) {
                newDeck = deck.copy();
                newPlayerHand = hand.copy();
                newDealerHand = dealerHand.copy();
                newDeck.removeCard(card);
                newPlayerHand.addCard(card);
                newPlayerHand.makeDouble();
                newPlayerHand.makeFinal();

                doubleScore += calculateScore(newDeck, newPlayerHand, newDealerHand);
            }
            doubleScore /= numRemainingCards;

            // STAY
            newDeck = deck.copy();
            newPlayerHand = hand.copy();
            newDealerHand = dealerHand.copy();
            newPlayerHand.makeFinal();
            stayScore = calculateScore(newDeck, newPlayerHand, newDealerHand);

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

            if (maxScore == hitScore) action = "HIT";
            else if (maxScore == stayScore) action = "STAY";
            else if (maxScore == doubleScore) action = "DOUBLE";
            else if (maxScore == splitScore) action = "SPLIT";
            else action = "SURRENDER";

            pCache.putValue(position.hashCode(), action, maxScore);
            //System.out.println(maxScore);
            return pCache.getValue(position.hashCode()).getScore();
        }

    }

    public static double playIdeal(deck deck, hand playerHand, card dealerCard){
        hand newPlayerHand = playerHand.copy();
        hand dealerHand = new hand();
        dealerHand.addCard(dealerCard);
        deck newDeck = deck.copy();

        Position position = new Position (newDeck, newPlayerHand, dealerHand);
        int key = position.hashCode();
        if(pCache.hasKey(key)){
            return pCache.getValue(key).getScore();
        }
        else {
            System.out.println("no key");
            return 0;
        }
    }
//    public static double makeIdealStrat(deck deck, hand hand, hand dealerHand) {
//        double maxScore = -8;
//        Position position = new Position (deck, hand, dealerHand);
//        int key = position.hashCode();
//        if(pCache.hasKey(key)){
//            return pCache.getValue(key).getScore();
//        }
//
//        deck newDeck;
//        hand newPlayerHand;
//        hand newDealerHand;
//        if (hand.isBust()) {
//            newDeck = deck.copy();
//            newPlayerHand = hand.copy();
//            newDealerHand = dealerHand.copy();
//            newPlayerHand.makeFinal();
//            maxScore =  calculateScore(newDeck, newPlayerHand, newDealerHand);
//            return maxScore;
//        }
//        else{
//            //calculate score for different outcomes
//            double hitScore = 0;
//            double stayScore = 0;
//            double doubleScore = 0;
//            double splitScore = 0;
//            double surrenderScore = 0;
//
//            List<card> remainingCards = deck.getDeckContent();
//            int numRemainingCards = remainingCards.size();
//
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
//
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
//
//            // STAY
//            newDeck = deck.copy();
//            newPlayerHand = hand.copy();
//            newDealerHand = dealerHand.copy();
//            newPlayerHand.makeFinal();
//            stayScore = calculateScore(newDeck, newPlayerHand, newDealerHand);
//
//            //Surrender
//            newDeck = deck.copy();
//            newPlayerHand = hand.copy();
//            newDealerHand = dealerHand.copy();
//            newPlayerHand.makeSurrender();
//            newPlayerHand.makeFinal();
//            surrenderScore = calculateScore(newDeck, newPlayerHand, newDealerHand);
//
//            maxScore = Math.max(hitScore, Math.max(stayScore, Math.max(splitScore, Math.max(surrenderScore, doubleScore))));
//
//            String action;
//
//            if (maxScore == hitScore) action = "HIT";
//            else if (maxScore == stayScore) action = "STAY";
//            else if (maxScore == doubleScore) action = "DOUBLE";
//            else if (maxScore == splitScore) action = "SPLIT";
//            else action = "SURRENDER";
//
//            pCache.putValue(position.hashCode(), action, maxScore);
//            //System.out.println(maxScore);
//            return pCache.getValue(position.hashCode()).getScore();
//        }
//
//    }


    private static double play(String strategy, deck deck, hand playerHand, card dealerCard){
        ArrayList<hand> hands = new ArrayList<>();
        hands.add(playerHand.copy());

        hand dealerHand = new hand();
        dealerHand.addCard(dealerCard);

        boolean allFinal = false;
        //player draws cards and plays
        while(allFinal == false){
            allFinal = true;
            ListIterator<hand> handIterator = hands.listIterator();
            while (handIterator.hasNext()){
                hand hand = handIterator.next();
                if(hand.isFinal()){
                    continue;
                }
                else{
                    if(hand.getHardValue() >= 21){
                        hand.makeFinal();
                        continue;
                    }
                    allFinal = false;
                    //determine action according to strategy
                    String action = null;
                    if(strategy.equals("naive")){
                        action = naiveStrategy(hand);
                    }
                    else if(strategy.equals("advanced")){
                        action = advancedStrategy(dealerCard, hand);
                    }
                    //apply action
                    switch (action) {
                        case "STAY":
                            hand.makeFinal();
                            break;
                        case "HIT":
                            hand.addCard(deck.draw());
                            break;
                        case "SURRENDER":
                            hand.makeSurrender();
                            hand.makeFinal();
                            break;
                        case "SPLIT":
                            hand other = hand.split();
                            hand.addCard(deck.draw());
                            other.addCard(deck.draw());
                            handIterator.add(other);
                            break;
                        case "DOUBLE":
                            hand.makeDouble();
                            hand.makeFinal();
                            hand.addCard(deck.draw());
                            break;
                    }
                }
            }
        }

        //dealer draws cards and plays
        while(dealerHand.isFinal() == false){
            if(dealerHand.getHardValue() >= 21){
                dealerHand.makeFinal();
                continue;
            }
            //determine action according to strategy
            String action = naiveStrategy(dealerHand);
            switch (action) {
                case "STAY":
                    dealerHand.makeFinal();
                    break;
                case "HIT":
                    dealerHand.addCard(deck.draw());
                    break;
            }
        }


        //calculate sum of outcome
        double outcome = calculateScore(hands, dealerHand);

        return outcome;
    }

    private static String naiveStrategy(hand h){
        if(h.getHardValue() > 11) {
            return "STAY";
        }
        else{
            if(h.getSoftValue() > 17){
                return "STAY";
            }
            else{
                return "HIT";
            }
        }
    }

    private static String advancedStrategy(card dealerCard, hand h){
        //Simple strategy
        //A -> 0, 2 -> 1, etc.
        int dealerIndex = card.rankToValue(dealerCard.getRank()) - 1;
        //if has pairs
        if(h.isPair()){
            int value = h.getHardValue()/2;
            String strategy = pairstrat.get(10-value).get(dealerIndex);
            return strategy;
        }
        //if hand is soft
        else if(h.hasAce() && h.getSoftValue() <= 21){
            int value = h.getSoftValue();
            String strategy = softstrat.get(21 - value).get(dealerIndex);
            if(strategy.equals("DOUBLE/STAY")){
                if(h.handSize() == 2) strategy = "DOUBLE";
                else strategy = "STAY";
            }
            else if(strategy.equals("DOUBLE/HIT")){
                if(h.handSize() == 2) strategy = "DOUBLE";
                else strategy = "HIT";
            }
            return strategy;
        }
        //if hand is hard
        else{
            int value = h.getHardValue();
            String strategy = hardstrat.get(21-value).get(dealerIndex);
            if(strategy.equals("SURRENDER/STAY")){
                if(h.handSize() == 2) strategy = "SURRENDER";
                else strategy = "STAY";
            }
            else if(strategy.equals("SURRENDER/HIT")){
                if(h.handSize() == 2) strategy = "SURRENDER";
                else strategy = "HIT";
            }
            else if(strategy.equals("DOUBLE/HIT")){
                if(h.handSize() == 2) strategy = "DOUBLE";
                else strategy = "HIT";
            }
            return strategy;
        }
    }

    private static ArrayList<ArrayList<String>> readStrategy (String filename) throws Exception {
        ArrayList<ArrayList<String>> strategy = new ArrayList<>();

        BufferedReader stratReader = new BufferedReader(new FileReader(filename));
        String stratline = "";
        stratline = stratReader.readLine();
        while((stratline = stratReader.readLine()) != null) {
            ArrayList<String> actions =
                new ArrayList<>(Arrays.asList(stratline.split(",")));
            actions.remove(0);
            strategy.add(actions);
        }

        stratReader.close();
        return strategy;
    }
}