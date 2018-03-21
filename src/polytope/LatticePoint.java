package polytope;

import java.util.ArrayList;

public class LatticePoint {
    private final int[] coordinates;

    public LatticePoint(int [] pCoordinates){
        coordinates=pCoordinates;
    }

    public LatticePoint(int value,int dimension){
        coordinates=new int[dimension];
        for(int i=0;i<dimension;i++)
            coordinates[i]=value;
    }

    public int getDimension(){
        return coordinates.length;
    }

    public int getCoordinate(int i){
        return coordinates[i];
    }

    public void addToPrint(StringBuilder sb){
        for(int c:coordinates) {
            sb.append(c);
            sb.append(" ");
        }
        sb.append("\n");
    }

    @Override
    public boolean equals(Object o) throws RuntimeException{
        if(!(o instanceof Point))
            throw new RuntimeException("Points must be compared with other Points");
        Point p=(Point)o;
        for(int i=0;i<coordinates.length;i++)
            if(coordinates[i]!=p.getCoordinate(i))
                return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        addToPrint(sb);
        return sb.toString();
    }
}
