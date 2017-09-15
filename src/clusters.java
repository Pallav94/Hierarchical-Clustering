public class clusters {
    static double pearson(bicluster x, bicluster y){
        int i;
        double sum1=0,sum2=0;
        for(i=0;i<x.count.size();i++){
            sum1=sum1+x.count.get(i);
        }
        for(i=0;i<x.count.size();i++){
            sum2=sum2+y.count.get(i);
        }
        double sumsq1=0;
        double sumsq2=0;
        for(i=0;i<x.count.size();i++){
            sumsq1=sumsq1+(x.count.get(i)*x.count.get(i));
        }
        for(i=0;i<y.count.size();i++){
            sumsq2=sumsq2+(y.count.get(i)*y.count.get(i));
        }
        double pSum=0;
        for(i=0;i<x.count.size();i++){
            pSum=pSum+(x.count.get(i) * y.count.get(i));
        }
        double num=pSum-(sum1*sum2/x.count.size());

        double den=Math.sqrt((sumsq1-Math.pow(sum1,2)/x.count.size())*(sumsq2-Math.pow(sum2,2)/x.count.size()));
        if(den==0){
            return 0;
        }
        return 1-num/den;
    }

}
