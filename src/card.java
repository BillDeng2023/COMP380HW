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



    public Rank getRank() {
        return rank;
    }

    public Suite getSuite() {
        return suite;
    }
}
