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

    public static int rankToValue(Rank r){
        switch (r) {
            case ACE:
                return 1;
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            case FIVE:
                return 5;
            case SIX:
                return 6;
            case SEVEN:
                return 7;
            case EIGHT:
                return 8;
            case NINE:
                return 9;
            case TEN:
                return 10;
            case JACK:
                return 10;
            case QUEEN:
                return 10;
            case KING:
                return 10;
            default:
                return 0;
        }
    }


    public Rank getRank() {
        return rank;
    }

    public Suite getSuite() {
        return suite;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof card)) {
            return false;
        }
        card otherCard = (card) obj;
        return this.rank == otherCard.getRank() && this.suite == otherCard.getSuite();
    }
}
