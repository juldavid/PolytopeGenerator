package random;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import polytope.*;
import polytope.Point;

import java.awt.*;
import java.util.ArrayList;

public class FirstMarkovChain extends MarkovChain {
    private FullDimensionPolytope state;

    public FirstMarkovChain(int pDim, int bSize) {
        super(pDim, bSize);
    }

    @Override
    public FullDimensionPolytope initialize() {
        state=new FullDimensionPolytope(dimension);
        ArrayList<Point> points = new ArrayList<>();
        do {
            points.clear();
            for (int i = 0; i <= dimension; i++) {
                Point p=rpg.randomPoint(dimension, boxSize);
                if(!points.contains(p))
                    points.add(p);
                else
                    i--;
            }
        }while (!state.createSimplex(points));
        System.gc();
        return state;
    }

    @Override
    public FullDimensionPolytope nextStep() {
        Point p=rpg.randomPoint(dimension,boxSize);

        if(state.containsExtremalPoint(p)) {
            if(state.staysFullDimensionAfterDeletion(p)){
                state.getPoints().remove(p);
                state = Quickhull.quickHull(state.getPoints(),dimension);
            }
        }
        else Quickhull.incrementalConvexHull(state,p);
        return state;
    }
}
