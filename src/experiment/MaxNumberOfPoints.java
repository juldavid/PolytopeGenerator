package experiment;

import polytope.AbstractPolytope;
import random.FirstMarkovChain;

public class MaxNumberOfPoints {
    public static void main(String [] args){
        AbstractPolytope lp;
        int numberOfExperiments=100;
        int numberOfSizes=100;
        int numberOfSteps=100000;
        double [][] result=new double[numberOfSizes][3];
        for(int k=2;k<numberOfSizes;k++){
            for(int i=0;i<numberOfExperiments;i++){
                FirstMarkovChain fmc=new FirstMarkovChain(2,k);
                lp=fmc.initialize();
                int max=lp.getPoints().size();
                for(int j=0;j<numberOfSteps;j++) {
                    lp = fmc.nextStep();
                    if(lp.getPoints().size()>max)
                        max=lp.getPoints().size();
                    if(j==1000) result[k][0]+=max;
                    if(j==10000) result[k][1]+=max;
                }
                result[k][2]+=max;
            }
            for(int i=0;i<3;i++) {
                result[k][i] /= numberOfExperiments;
                System.out.println(k+" "+1000*Math.pow(10,i)+" "+result[k][i]);
            }
        }
    }
}
