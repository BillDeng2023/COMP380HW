//import com.google.common.base.Preconditions;
//import com.google.common.collect.ImmutableMap;

public class card {
    private final Rank rank;
    private final Suite suite;


    public card(Rank rank, Suite suite){
//        this.rank = Preconditions.checkNotNull(rank);
//        this.suit = Preconditions.checkNotNull(suit);
        this.rank = rank;
        this.suite = suite;
    }

    /**
     * This constructor is used when creating a card from a unicode string.
     *
     * @param hexcoding a string denoting the unicode representation of the card
     * @throws Exception when the unicode representation is an invalid blackjack card
     */
    public card(String hexcoding) throws Exception{

        switch (hexcoding.charAt(3)){
            case 'a' : this.suite = Suite.SPADES;
                break;
            case 'b' : this.suite = Suite.HEARTS;
                break;
            case 'c' : this.suite = Suite.DIAMONDS;
                break;
            case 'd' : this.suite = Suite.CLUBS;
                break;
            default  : throw new Exception("illegal suite");
        }

        switch (hexcoding.charAt(4)) {
            case '1':
                this.rank = Rank.ACE;
                break;
            case '2':
                this.rank = Rank.TWO;
                break;
            case '3':
                this.rank = Rank.THREE;
                break;
            case '4':
                this.rank = Rank.FOUR;
                break;
            case '5':
                this.rank = Rank.FIVE;
                break;
            case '6':
                this.rank = Rank.SIX;
                break;
            case '7':
                this.rank = Rank.SEVEN;
                break;
            case '8':
                this.rank = Rank.EIGHT;
                break;
            case '9':
                this.rank = Rank.NINE;
                break;
            case 'a':
                this.rank = Rank.TEN;
                break;
            case 'b':
                this.rank = Rank.JACK;
                break;
            case 'd':
                this.rank = Rank.QUEEN;
                break;
            case 'e':
                this.rank = Rank.KING;
                break;
            default:
                throw new Exception("illegal rank");
        }
    }



    public Rank getRank() {
        return rank;
    }

    public Suite getSuite() {
        return suite;
    }
}
