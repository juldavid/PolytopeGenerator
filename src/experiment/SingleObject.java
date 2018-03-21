package experiment;

import polytope.*;
import random.FirstMarkovChain;

public class SingleObject {
    public static void main(String [] args){

        FirstMarkovChain fmc=new FirstMarkovChain(3,10);
        FullDimensionPolytope lp=fmc.initialize();
        System.out.println(lp);

        for(int i=0;i<1000;i++) {
            lp = fmc.nextStep();
            //System.out.println(i+" "+lp.getPoints().size());
        }
        lp.printToFile("test.plot");
        System.out.println(lp);
        System.out.println("Taille: "+lp.getPoints().size());

    }
}
