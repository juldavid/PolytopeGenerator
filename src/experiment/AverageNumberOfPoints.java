package experiment;

import polytope.AbstractPolytope;
import random.FirstMarkovChain;

public class AverageNumberOfPoints {
    public static void main(String [] args){
        AbstractPolytope lp;
        int numberOfExperiments=1;
        int numberOfSizes=100;
        int numberOfSteps=100000;
        double [][] result=new double[numberOfSizes+1][3];
        for(int k=2;k<=numberOfSizes;k++){
            FirstMarkovChain fmc=new FirstMarkovChain(2,k);
            lp=fmc.initialize();
            long acc=0;
            for(int j=0;j<numberOfSteps;j++) {
                lp = fmc.nextStep();
                acc+=lp.getPoints().size();
                if(j==1000) result[k][0]=acc/1000.;
                if(j==10000) result[k][1]=acc/10000.;
            }
            result[k][2]=acc/100000.;

            for(int i=0;i<3;i++)
                System.out.println(k+" "+1000*Math.pow(10,i)+" "+result[k][i]);

        }
    }
}
