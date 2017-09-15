import java.util.ArrayList;

public class bicluster {
    ArrayList<Double> count;
    bicluster left;
    bicluster right;
    String tweetUser;

    bicluster(ArrayList<Double> numbers,bicluster x,bicluster y, String User){
        count=numbers;
        left=x;
        right=y;
        tweetUser=User;
    }
}
