package experiment;

import polytope.AbstractPolytope;
import random.FirstMarkovChain;

public class AdditionAndDeletion {
    public static void main(String [] args){
        AbstractPolytope lp;
        int numberOfExperiments=1000;
        int numberOfSizes=100;
        int numberOfSteps=100000;
        for(int k=2;k<numberOfSizes;k++){
            long add=0;
            long sub=0;

            for(int i=0;i<numberOfExperiments;i++){
                FirstMarkovChain fmc=new FirstMarkovChain(2,k);
                lp=fmc.initialize();
                int tmp,size=lp.getPoints().size();
                for(int j=0;j<numberOfSteps;j++) {
                    lp = fmc.nextStep();
                    tmp=lp.getPoints().size();
                    if(tmp==size+1)
                        add++;
                    else if(tmp==size-1)
                        sub++;
                    size=tmp;
                }
            }
            System.out.println(k+" "+add/(double)numberOfExperiments+" "+sub/(double)numberOfExperiments);
        }
    }
}
