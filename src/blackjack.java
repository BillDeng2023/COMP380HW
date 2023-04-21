//import com.google.common.base.Preconditions;
//import com.google.common.collect.ImmutableMap;

//import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.io.*;

public class blackjack {
    private static ArrayList<ArrayList<String>> hardstrat = null;
    private static ArrayList<ArrayList<String>> softstrat = null;
    private static ArrayList<ArrayList<String>> pairstrat = null;
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
            if(presetLn.length() < 1 || presetLn.charAt(0) != ',') continue;
            String[] instance = presetLn.split(",");
            presets.add(instance);
            presetLn = presetReader.readLine();
        }

        presetReader.close();

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

            if(dealerCard.equals(new card("1f0d9"))){
                deck deckNaive = new deck();
                deckNaive.removeCards(drawnCards);
                hand dealerHand = new hand();
                dealerHand.addCard(dealerCard);
                System.out.println(makeIdealStrat(deckNaive, playerHand, dealerHand));
            }

            double naive_average = 0;
            double advanced_average = 0;
            //System.out.println(dealerCard.getRank() + " " + dealerCard.getSuite());
            int iteration = 2000;
            for(int i = 0; i < iteration; i++){
                deck deckNaive = new deck();
                deckNaive.removeCards(drawnCards);
                deck deckAdvanced = deckNaive.copy();

//                double naive = play("naive", deckNaive, playerHand, dealerCard);
//                naive_average += naive;

                double advanced = play("advanced", deckAdvanced, playerHand, dealerCard);
                advanced_average += advanced;
            }
//            naive_average = naive_average/iteration;
            advanced_average = advanced_average/(double) iteration;
            StringBuilder line = new StringBuilder();
            line.append(naive_average + "," + advanced_average);
            for (int j = 2; j < preset.length; j++) {
                line.append("," + preset[j]);
            }
            printWriter.println(line.toString());
        }


        printWriter.close();
    }

    //calculate score with one hand
    public static double calculateScore(hand hand, hand dealerHand) {
        double outcome = 0;
        if(hand.isBlackJack()){
            if(dealerHand.isBlackJack()){
                outcome += 0;
            }
            else{
                if(hand.isDoubled()) outcome += 3;
                else outcome += 1.5;
            }
        }
        else if(dealerHand.isBlackJack() || hand.isBust()){
            if(hand.isDoubled()) outcome -= 2;
            else outcome -= 1;
        }
        else if(hand.isSurrendered()){
            outcome -= 0.5;
        }
        else{
            if(dealerHand.highestValue() == hand.highestValue()){
                outcome += 0;
            }
            else if(dealerHand.highestValue() > hand.highestValue()){
                if(hand.isDoubled()) outcome -= 2;
                else outcome -= 1;
            }
            else if(dealerHand.highestValue() < hand.highestValue()){
                if(hand.isDoubled()) outcome += 2;
                else outcome += 1;
            }
        }
        return outcome;
    }

    //calculate score for one hand for all possible dealerCards
    public static double calculateScore(deck deck, hand hand, hand dealerHand) {
        double outcome = 0;
        List<card> remainingCards = deck.getDeckContent();
        int numRemainingCards = remainingCards.size();
        deck newDeck;
        if (dealerHand.isBust()) {
            return calculateScore(hand, dealerHand);
        }
        else{
            String action = naiveStrategy(dealerHand);
            switch (action) {
                case "STAY":
                    return calculateScore(hand, dealerHand);
                case "HIT":
                    for (card card : remainingCards) {
                        newDeck = deck.copy();
                        newDeck.removeCard(card);

                        hand newDealerHand = dealerHand.copy();
                        newDealerHand.addCard(card);

                        outcome += calculateScore(newDeck, hand, newDealerHand);
                    }

            }
        }
        outcome /= numRemainingCards;
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
        double totalScore = 0;

        if (hand.isBust()) {
            deck newDeck = deck.copy();
            hand newPlayerHand = hand.copy();
            newPlayerHand.makeFinal();
            return calculateScore(newDeck, newPlayerHand, dealerHand);
        }
        else{
            //calculate score for different outcomes
            double hitScore = 0;
            double stayScore = 0;
            double doubleScore = 0;
            double splitScore = 0;
            double surrenderScore = 0;

            List<card> remainingCards = deck.getDeckContent();
            int numRemainingCards = remainingCards.size();
            deck newDeck;

            // HIT
            for (card card : remainingCards) {
                newDeck = deck.copy();
                newDeck.removeCard(card);

                hand newPlayerHand = hand.copy();
                newPlayerHand.addCard(card);

                hitScore += makeIdealStrat(newDeck, newPlayerHand, dealerHand);
            }
            hitScore /= numRemainingCards;



            // SPLIT
            if (hand.isPair()) {
                hand hand1 = hand.copy();
                hand hand2 = hand1.split();
                double splitScore1 = 0;
                double splitScore2 = 0;
                //Needs more work
                for (card card : remainingCards) {
                    newDeck = deck.copy();
                    newDeck.removeCard(card);
                    hand newPlayerHand1 = hand1.copy();
                    hand newPlayerHand2 = hand2.copy();

                    newPlayerHand1.addCard(card);
                    splitScore1 += makeIdealStrat(newDeck, newPlayerHand1, dealerHand);

                    newPlayerHand2.addCard(card);
                    splitScore2 += makeIdealStrat(newDeck, newPlayerHand2, dealerHand);
                }

                splitScore1 /= numRemainingCards;
                splitScore2 /= numRemainingCards;
                splitScore = splitScore1 + splitScore2;
            }
            else {
                splitScore = -8;
            }

            //Double
            for (card card : remainingCards) {
                newDeck = deck.copy();
                newDeck.removeCard(card);

                hand newPlayerHand = hand.copy();
                newPlayerHand.addCard(card);
                newPlayerHand.makeDouble();
                newPlayerHand.makeFinal();

                doubleScore += calculateScore(newDeck, hand, dealerHand);
            }
            doubleScore /= numRemainingCards;

            // STAY
            newDeck = deck.copy();
            hand newPlayerHand = hand.copy();
            newPlayerHand.makeFinal();
            stayScore = calculateScore(newDeck, newPlayerHand, dealerHand);

            //Surrender
            newPlayerHand.makeSurrender();
            surrenderScore = calculateScore(newDeck, newPlayerHand, dealerHand);

            double MaxScore = Math.max(hitScore, Math.max(stayScore, Math.max(splitScore, Math.max(surrenderScore, doubleScore))));
            return MaxScore;
        }

    }

    public static double makeIdealStrat(deck deck, List<hand> playerHands,  hand dealerHand, PositionCache pCache){
        double maxScore = -8;
        double totalScore = 0;

        for (int i = 0; i < playerHands.size(); i++){
            hand hand = playerHands.get(i);

            // check if the position is already in the cache
            Position position = new Position(deck, hand, dealerHand);
            PositionCache.Pair<String, Double> actScore = pCache.getValue(position.hashCode());
            if (actScore != null){
                totalScore += actScore.getScore();
                continue;
            }

            if (hand.isBust()) {
                deck newDeck = deck.copy();
                hand newPlayerHand = hand.copy();
                newPlayerHand.makeFinal();
                totalScore += calculateScore(newDeck, newPlayerHand, dealerHand);
            }
            else {
                //calculate score for different outcomes
                double hitScore = 0;
                double stayScore = 0;
                double doubleScore = 0;
                double splitScore = 0;
                double surrenderScore = 0;

                List<card> remainingCards = deck.getDeckContent();
                int numRemainingCards = remainingCards.size();
                deck newDeck;

                // HIT
                for (card card : remainingCards) {
                    newDeck = deck.copy();
                    newDeck.removeCard(card);

                    List<hand> newPlayerHands = new ArrayList<>();
                    for (int j = 0; j < playerHands.size(); j++){
                        hand newHand = playerHands.get(j).copy();
                        if (i == j){
                            newHand.addCard(card);
                        }
                        newPlayerHands.add(newHand);
                    }

                    hitScore += makeIdealStrat(newDeck, newPlayerHands, dealerHand, pCache);
                }
                hitScore /= numRemainingCards;

                // SPLIT
                if (hand.isPair()) {
                    for (int x = 0; x < numRemainingCards - 1; x++){
                        for (int y = x + 1; y < numRemainingCards; y++){
                            List<hand> newPlayerHands = new ArrayList<>();
                            newDeck = deck.copy();

                            card draw1 = remainingCards.get(x);
                            card draw2 = remainingCards.get(y);
                            newDeck.removeCard(draw1);
                            newDeck.removeCard(draw2);

                            for (int j = 0; j < playerHands.size(); j++){
                                hand newHand = playerHands.get(j).copy();
                                if (i == j){
                                    hand splitHand = newHand.split();
                                    newHand.addCard(draw1);
                                    splitHand.addCard(draw2);
                                    newPlayerHands.add(splitHand);
                                }
                                newPlayerHands.add(newHand);
                            }
                            splitScore += makeIdealStrat(newDeck, newPlayerHands, dealerHand, pCache);
                        }
                    }
                    splitScore /= (double) (numRemainingCards - 1) * numRemainingCards / 2;
                } else {
                    splitScore = -8;
                }

                //Double
                for (card card : remainingCards) {
                    newDeck = deck.copy();
                    newDeck.removeCard(card);

                    List<hand> newPlayerHands = new ArrayList<>();
                    for (int j = 0; j < playerHands.size(); j++){
                        hand newHand = playerHands.get(j).copy();
                        if (i == j){
                            newHand.addCard(card);
                            newHand.makeDouble();
                            newHand.makeFinal();
                        }
                        newPlayerHands.add(newHand);
                    }
                    doubleScore += calculateScore(newDeck, hand, dealerHand);
                }
                doubleScore /= numRemainingCards;

                // STAY
                newDeck = deck.copy();
                List<hand> newPlayerHands = new ArrayList<>();
                for (int j = 0; j < playerHands.size(); j++){
                    hand newHand = playerHands.get(j).copy();
                    if (i == j){
                        newHand.makeFinal();
                    }
                    newPlayerHands.add(newHand);
                }
                stayScore = calculateScore(newDeck, hand, dealerHand);

                //Surrender
                newPlayerHands.get(i).makeSurrender();
                surrenderScore = calculateScore(newDeck, hand, dealerHand);

                double MaxScore = Math.max(hitScore, Math.max(stayScore,
                    Math.max(splitScore, Math.max(surrenderScore, doubleScore))));
                totalScore += MaxScore;

                String action;

                if (MaxScore == hitScore) action = "HIT";
                else if (MaxScore == stayScore) action = "STAY";
                else if (MaxScore == doubleScore) action = "DOUBLE";
                else if (MaxScore == splitScore) action = "SPLIT";
                else action = "SURRENDER";

                pCache.putValue(position.hashCode(), action, MaxScore);
            }
        }
        return totalScore;
    }

    private static double play(String strategy, deck deck, hand playerHand, card dealerCard){
        ArrayList<hand> hands = new ArrayList<>();
        hands.add(playerHand);

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