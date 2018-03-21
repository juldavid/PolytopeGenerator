package random;

import polytope.AbstractPolytope;
import polytope.FullDimensionPolytope;
import polytope.Polytope;

public abstract class MarkovChain {
    protected int dimension;
    protected int boxSize;
    protected RandomPointGenerator rpg=new RandomPointGenerator();;


    public MarkovChain(int pDim, int bSize) {
        dimension=pDim;
        boxSize=bSize;
    }


    public abstract Polytope initialize();
    public abstract Polytope nextStep();
}
