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
        BufferedReader bufferedReader = new BufferedReader(new FileReader(
            "src/blackjack_table_samples-V3.csv"));
        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter("src/blackjack_output.csv")));
        String line = "";
        //Note that in HW1 description the starting index should be 9, but it's different for
        //the sample csv file for some reason
        int startingIndex = 8;

        while((line = bufferedReader.readLine()) != null){
            String[] str = line.split(",");
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

            //Naive strategy
            if(h.getHardValue() > 11) {
                printWriter.println("STAY" + line);
            }
            else{
                if(h.getSoftValue() > 17){
                    printWriter.println("STAY" + line);
                }
                else{
                    printWriter.println("HIT" + line);
                }
            }
            //skip description line in sample csv file
            line = bufferedReader.readLine();
        }

        printWriter.close();
        bufferedReader.close();
        try {
            ArrayList<ArrayList<String>> strat = readStrategy("src/Wiki Strategy (Modified) - hard.csv");
            System.out.print(strat.toString());
        } catch (Exception e) {
            System.out.println("Error");
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


