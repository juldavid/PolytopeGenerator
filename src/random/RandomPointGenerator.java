package random;

import polytope.Point;

import java.util.ArrayList;
import java.util.Random;

public class RandomPointGenerator {
    Random rn = new Random();

    public Point randomPoint(int dimension, int boxSize){
        ArrayList<Integer> coord=new ArrayList<>();
        for(int i=0;i<dimension;i++)
            coord.add(rn.nextInt(boxSize+1));
        return new Point(coord);
    }


}
