//import com.google.common.base.Preconditions;
//import com.google.common.collect.ImmutableMap;

//import javax.annotation.concurrent.ThreadSafe;
import java.util.Map;
import java.util.Objects;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Scanner;

public class blackjack {
    /**
     * The main function reads in data from the given blackjack situation samples and writes
     * an output document containing the situations and next step for the player based
     * on a simple algorithm.
     * @param args
     * @throws IOException throws exception when there is an invalid card
     */
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("src/blackjack_table_samples-V3.csv"));
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
                    h.addCard(createCard(str[i]));
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
    }

    /**
     * This method parses a unicode string representation for a poker card into a card
     * object.
     * @param hexcoding a string containing the unicode representation of the card
     * @return  a card object
     * @throws Exception when the string is not a valid unicode representation of a
     *         poker card used in blackjack.
     */
    public static card createCard(String hexcoding) throws Exception{
        //need to confirm capitalization
        Rank rank = null;
        Suite suite = null;

        switch (hexcoding.charAt(3)){
            case 'a' : suite = Suite.SPADES;
                break;
            case 'b' : suite = Suite.HEARTS;
                break;
            case 'c' : suite = Suite.DIAMONDS;
                break;
            case 'd' : suite = Suite.CLUBS;
                break;
            default  : throw new Exception("illegal suite");
        }

        switch (hexcoding.charAt(4)){
            case '1' : rank = Rank.ACE;
                break;
            case '2' : rank = Rank.TWO;
                break;
            case '3' : rank = Rank.THREE;
                break;
            case '4' : rank = Rank.FOUR;
                break;
            case '5' : rank = Rank.FIVE;
                break;
            case '6' : rank = Rank.SIX;
                break;
            case '7' : rank = Rank.SEVEN;
                break;
            case '8' : rank = Rank.EIGHT;
                break;
            case '9' : rank = Rank.NINE;
                break;
            case 'a' : rank = Rank.TEN;
                break;
            case 'b' : rank = Rank.JACK;
                break;
            case 'd' : rank = Rank.QUEEN;
                break;
            case 'e' : rank = Rank.KING;
                break;
            default  : throw new Exception("illegal rank");
        }

        return new card(rank, suite);
    }
}


