import java.util.ArrayList;

public class bicluster {
    ArrayList<Double> count;
    bicluster left;
    bicluster right;
    String tweetUser;
    int id;

    bicluster(ArrayList<Double> numbers,String User,int n){
        count=numbers;
        left=null;
        right=null;
        tweetUser=User;
        id=n;
    }
}
