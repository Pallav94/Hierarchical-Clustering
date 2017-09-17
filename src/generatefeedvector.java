import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class generatefeedvector {
    ArrayList<HashMap<String, HashMap<String, Integer>>> wc = new ArrayList<HashMap<String, HashMap<String, Integer>>>();
    HashMap<String, Integer> apcount = new HashMap<String, Integer>();
    ArrayList<String> wordlist = new ArrayList<String>();
    ArrayList<String> tweetUsername = new ArrayList<String>();
    ArrayList<String> lineData=new ArrayList<String>();
    ArrayList<bicluster> clust=new ArrayList<bicluster>();

    int tweetCounts = 0;


    //Function to get word count of each blog and number of blog each word appeared in....
    void getWordCounts(ArrayList<String> tweetD) {
        for (int count = 0; count < tweetD.size(); count++) {
            String word[] = tweetD.get(count).toLowerCase().split("[^a-zA-Z']+");

            HashMap<String, Integer> tempWords = new HashMap<String, Integer>();
            for (int j = 0; j < word.length; j++) {
                tempWords.put(word[j], 0);
            }
            ArrayList<String> wordsArray = new ArrayList<String>();
            for (Map.Entry z : tempWords.entrySet()) {
                wordsArray.add((String) z.getKey());
            }
            HashMap<String, Integer> x = new HashMap<String, Integer>();
            HashMap<String, HashMap<String, Integer>> y = new HashMap<String, HashMap<String, Integer>>();
            for (int i = 0; i < word.length; i++) {
                if (x.containsKey(word[i])) {
                    int value = x.get(word[i]);
                    x.put(word[i], value + 1);
                } else {
                    x.put(word[i], 1);
                }

            }
            y.put(tweetUsername.get(count), x);
            wc.add(y);
            for (int j = 0; j < wordsArray.size(); j++) {
                if (apcount.containsKey(wordsArray.get(j))) {
                    int value = apcount.get(wordsArray.get(j));
                    apcount.put(wordsArray.get(j), value + 1);
                } else {
                    apcount.put(wordsArray.get(j), 1);
                }
            }
        }
    }

    //Function to obtain tweets from each user
    public ArrayList<String> hashtags(String hashtag) {
        ConfigurationBuilder cf = new ConfigurationBuilder();
        cf.setDebugEnabled(true).setOAuthConsumerKey("                    ")
                .setOAuthConsumerSecret("                                 ")
                .setOAuthAccessToken("                                    ")
                .setOAuthAccessTokenSecret("                              ");
        TwitterFactory tf = new TwitterFactory(cf.build());
        int i = 0;
        Twitter twitter = tf.getInstance();
        ArrayList<String> tweetD = new ArrayList<String>();
        try {
            Query query = new Query(hashtag);
            query.setCount(100);
            QueryResult result;
            result = twitter.search(query);
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                tweetUsername.add(tweet.getUser().getScreenName());
                tweetD.add(tweet.getText());
                tweetCounts = tweetCounts + 1;
            }
            System.out.println("Tweet:" + tweetCounts);
        } catch (TwitterException te) {
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
        return tweetD;
    }

    //function to store valid words
    void validWords() {
        System.out.println("++++++++++++++++++++++++++++");
        for (Map.Entry e : apcount.entrySet()) {
            int x = apcount.get(e.getKey());
            double frac = (double) x / (double) tweetCounts;
            if (frac > 0.1 && frac < 0.5) {
                wordlist.add((String) e.getKey());
            }
        }
    }

    //Function to create a big matrix of words user and word frequencies
    void bigMatrixFile() {
        /*for (int k = 0; k < wc.size(); k++) {
            HashMap<String, HashMap<String, Integer>> temp = wc.get(k);
            for (Map.Entry e : temp.entrySet()) {
                System.out.print("User:" + e.getKey() + "--->\n");

                HashMap<String, Integer> tempValues = (HashMap<String, Integer>) e.getValue();
                for (Map.Entry f : tempValues.entrySet()) {
                    System.out.println(f.getKey() + " "+f.getValue());
                }
                System.out.println("\n-----------------------------");
            }
        }*/
        try {
            FileWriter fout = new FileWriter("/Users/pallavsaxena/Desktop/matrix.csv");
            int i;
            for (i = 0; i < wordlist.size(); i++) {
                fout.write("\t" + wordlist.get(i));
            }
            fout.write("\n");
            System.out.println("WC:"+wc.size());
            for (int k = 0; k < wc.size(); k++) {
                HashMap<String, HashMap<String, Integer>> temp = wc.get(k);
                for (Map.Entry e : temp.entrySet()) {
                    fout.write((String) e.getKey());
                    HashMap<String, Integer> tempValues = (HashMap<String, Integer>) e.getValue();
                    for (i = 0; i < wordlist.size(); i++) {

                        if (tempValues.containsKey(wordlist.get(i))) {
                            int num = tempValues.get(wordlist.get(i));
                            fout.write("\t" + num);
                        } else {
                            fout.write("\t0");
                        }
                    }
                    fout.write("\n");
                }
            }
            fout.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    //Function to access matrix.csv
    void accessFile(){
        try {
            File f = new File("/Users/pallavsaxena/Desktop/matrix.csv");
            BufferedReader b = new BufferedReader(new FileReader(f));
            String readLine = "";
            System.out.println("Reading file using Buffered Reader");
            while ((readLine = b.readLine()) != null) {
                lineData.add(readLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void clusterNodes(){
        int i;
       for(i=1;i<lineData.size();i++) {
           String[] row= lineData.get(i).split("\\s");
           ArrayList<Double> numbers=new ArrayList<Double>();
           for(int j=1;j<row.length;j++){
               numbers.add(Double.parseDouble(row[j]));
           }
           bicluster b=new bicluster(numbers,row[0],0);
           clust.add(b);
       }
    }
    //Initial List of users
    void list(){
        for(int i=0;i<clust.size();i++){
            System.out.println(clust.get(i).tweetUser);
        }
    }
    //Function to create cluster
    bicluster treeCluster(){

        while(clust.size()>1){
            int lowesti=0;
            int lowestj=1;
            double closest=clusters.pearson(clust.get(lowesti),clust.get(lowestj));
            for(int i=0;i<clust.size();i++){
                for(int j=i+1;j<clust.size();j++){
                    double d=clusters.pearson(clust.get(i),clust.get(j));
                    if(d<closest){
                        closest=d;
                        lowesti=i;
                        lowestj=j;
                    }
                }
            }
            double sum=0;
            System.out.println("Closest:"+closest);
            System.out.println("i:"+lowesti+" "+"j:"+lowestj);
            ArrayList<Double> mergevec=new ArrayList<Double>();
            for (int k=0;k<clust.get(0).count.size();k++){
                sum=(clust.get(lowesti).count.get(k)+clust.get(lowestj).count.get(k))/2;
                mergevec.add(sum);
            }
            System.out.println("MergeSum:"+sum);
            bicluster b=new bicluster(mergevec, clust.get(lowesti).tweetUser+" "+clust.get(lowestj).tweetUser,1);
            b.left=clust.get(lowesti);
            b.right=clust.get(lowestj);
            clust.remove(lowesti);
            clust.remove(lowestj-1);
            System.out.println("Removed:"+lowesti+"____"+lowestj);
            clust.add(b);
            for(int h=0;h<clust.size();h++){
                System.out.println("User:"+clust.get(h).tweetUser);
            }
        }
        return clust.get(0);
    }
    //Printing Cluster Tree
    void printClust(bicluster root){
        if(root==null){
            return;
        }
        if(root.id==0){
            System.out.println(root.tweetUser);
        }
        else if(root.id==1){
            System.out.println("   -----");
        }

        printClust(root.left);
        printClust(root.right);
    }

    public static void main(String[] args) {
        generatefeedvector gfv = new generatefeedvector();
        ArrayList<String> tweetsData = gfv.hashtags("apple");
        //Tweets from users
        System.out.println("___________________________");
        for (int i = 0; i < tweetsData.size(); i++) {
            System.out.println(tweetsData.get(i) + "\n");
        }
        gfv.getWordCounts(tweetsData);
        gfv.validWords();
        gfv.bigMatrixFile();
        gfv.accessFile();
        gfv.clusterNodes();
        gfv.list();
        bicluster root=gfv.treeCluster();
        gfv.printClust(root);
    }
}