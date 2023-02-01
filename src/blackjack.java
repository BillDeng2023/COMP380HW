//import com.google.common.base.Preconditions;
//import com.google.common.collect.ImmutableMap;

//import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.io.*;

public class blackjack {
    /**
     * The main function reads in data from the given blackjack situation samples and writes
     * an output document containing the situations and next step for the player based
     * on a simple algorithm.
     * @param args
     * @throws IOException throws exception when there is an invalid card
     */
    public static void main(String[] args) throws IOException {
        //read in strategies
        ArrayList<ArrayList<String>> hardstrat = null;
        ArrayList<ArrayList<String>> softstrat = null;
        ArrayList<ArrayList<String>> pairstrat = null;
        try {
            hardstrat = readStrategy("src/Wiki Strategy (Modified) - hard.csv");
            softstrat = readStrategy("src/Wiki Strategy (Modified) - soft.csv");
            pairstrat = readStrategy("src/Wiki Strategy (Modified) - pairs.csv");
        } catch (Exception e) {
            System.out.println("read strategies error");
        }

        BufferedReader bufferedReader = new BufferedReader(new FileReader(
            "src/blackjack_table_samples-V3.csv"));
        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter("src/blackjack_output.csv")));
        String line = "";
        //Note that in HW1 description the starting index should be 9, but it's different for
        //the sample csv file for some reason
        int startingIndex = 8;

        while((line = bufferedReader.readLine()) != null){
            //skip description line in sample csv file
            if(line.charAt(0) == '=') continue;
            String[] str = line.split(",");

            //dealer's revealed card
            card dealerCard = null;
            try{
                dealerCard = new card(str[1]);
            } catch (Exception e) {
                System.out.println(str[1]);
            }
            //create hand
            hand h = new hand();
            for(int i = startingIndex; i < str.length; i++){
                //the encoding of the sample csv starts at 0 instead of 1 for some reason
                try{
                    h.addCard(new card(str[i]));
                } catch (Exception e) {
                    System.out.println(str[i]);
                }
            }

            //Simple strategy
            //A -> 0, 2 -> 1, etc.
            int dealerIndex = card.rankToValue(dealerCard.getRank()) - 1;
            //if has pairs
            if(h.isPair()){
                int value = h.getHardValue()/2;
                String strategy = pairstrat.get(10-value).get(dealerIndex);
                printWriter.println(strategy + line);
            }
            //if hand is soft
            else if(h.hasAce() && h.getSoftValue() <= 21){
                int value = h.getSoftValue();
                String strategy = softstrat.get(21 - value).get(dealerIndex);
                if(strategy == "DOUBLE/STAY"){
                    if(h.handSize() == 2) strategy = "DOUBLE";
                    else strategy = "STAY";
                }
                else if(strategy == "DOUBLE/HIT"){
                    if(h.handSize() == 2) strategy = "DOUBLE";
                    else strategy = "HIT";
                }
                printWriter.println(strategy + line);
            }
            //if hand is hard
            else{
                int value = h.getHardValue();
                String strategy = hardstrat.get(21-value).get(dealerIndex);
                if(strategy == "SURRENDER/STAY"){
                    if(h.handSize() == 2) strategy = "SURRENDER";
                    else strategy = "STAY";
                }
                else if(strategy == "SURRENDER/HIT"){
                    if(h.handSize() == 2) strategy = "SURRENDER";
                    else strategy = "HIT";
                }
                else if(strategy == "DOUBLE/HIT"){
                    if(h.handSize() == 2) strategy = "DOUBLE";
                    else strategy = "HIT";
                }
                printWriter.println(strategy + line);
            }
        }

        printWriter.close();
        bufferedReader.close();

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


