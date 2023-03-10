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
    public static void main(String[] args) throws IOException {
        //read in strategies
        try {
            hardstrat = readStrategy("src/Wiki Strategy (Modified) - hard.csv");
            softstrat = readStrategy("src/Wiki Strategy (Modified) - soft.csv");
            pairstrat = readStrategy("src/Wiki Strategy (Modified) - pairs.csv");
        } catch (Exception e) {
            System.out.println("read strategies error");
        }

        ArrayList<String[]> presets = new ArrayList<>();

        BufferedReader presetReader = new BufferedReader(new FileReader("hw4.csv"));
        String presetLn = "";
        while((presetLn = presetReader.readLine()) != null) {
            String[] instance = presetLn.split(",");
            presets.add(instance);
            presetLn = presetReader.readLine();
        }

        presetReader.close();

        for (String[] preset : presets) {
            ArrayList<card> drawnCards = new ArrayList<>();
            hand playerHand = new hand();
            card dealerCard;
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

        }

        double naive_average = 0;
        double naive_max_gain = 0;
        double naive_max_lost = 0;

        double advanced_average = 0;
        double advanced_max_gain = 0;
        double advanced_max_lost = 0;

        int iteration = 100000000;
        for(int i = 0; i < iteration; i++){
            double naive = play("naive");
            naive_average += naive;
            naive_max_gain = Math.max(naive_max_gain, naive);
            naive_max_lost = Math.min(naive_max_lost, naive);

            double advanced = play("advanced");
            advanced_average += advanced;
            advanced_max_gain = Math.max(advanced_max_gain, advanced);
            advanced_max_lost = Math.min(advanced_max_lost, advanced);
        }

        naive_average = naive_average/iteration;
        advanced_average = advanced_average/iteration;

        System.out.println(naive_average);
        System.out.println(naive_max_gain);
        System.out.println(naive_max_lost);

        System.out.println(advanced_average);
        System.out.println(advanced_max_gain);
        System.out.println(advanced_max_lost);
    }

    private static double play(String strategy){
        deck deck = new deck();
        ArrayList<hand> hands = new ArrayList<>();
        //create dealer's hand
        hand dealerHand = new hand();
        card dealerCard = deck.draw();
        dealerHand.addCard(dealerCard);
        //create player's hand
        hand playerHand = new hand();
        playerHand.addCard(deck.draw());
        playerHand.addCard(deck.draw());
        hands.add(playerHand);

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
        double outcome = 0;
        for(hand hand: hands){
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
        }

        return outcome;
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
        double outcome = 0;
        for(hand hand: hands){
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
        }

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


