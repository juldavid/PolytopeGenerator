package polytope;

import java.util.ArrayList;
import java.util.Objects;

public class Point {
    private final ArrayList<Integer> coordinates;


    public Point(ArrayList<Integer> pCoordinates) {
        this.coordinates = pCoordinates;
    }

    public Point(int [] pCoordinates){
        coordinates=new ArrayList<>();
        for(int x:pCoordinates)
            coordinates.add(x);
    }

    public Point(int value,int dimension){
        coordinates=new ArrayList<>();
        for(int i=0;i<dimension;i++)
            coordinates.add(value);
    }

    public int getDimension(){
        return coordinates.size();
    }

    public int getCoordinate(int i){
        return coordinates.get(i);
    }

    private void add(StringBuilder sb,String before,String after){
        sb.append(before);
        for(int c:coordinates) {
            sb.append(c);
            sb.append(" ");
        }
        sb.append(after);

    }

    public void addToPrint(StringBuilder sb){
        add(sb,"( ",") ");
    }

    public void addToFile(StringBuilder sb){
        add(sb,"","\n");
    }


    @Override
    public int hashCode() {
        return Objects.hash(coordinates);
    }

    @Override
    public boolean equals(Object o) throws RuntimeException{
        if(!(o instanceof Point))
            throw new RuntimeException("Points must be compared with other Points");
        Point p=(Point)o;
        //System.out.println("Et moi je compare " + this + " et " + p);
        for(int i=0;i<coordinates.size();i++)
            if(coordinates.get(i)!=p.coordinates.get(i))
                return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        addToPrint(sb);
        return sb.toString();
    }

    public static int compare(Point p1,Point p2){
        int d=p1.getDimension();
        int i=0;
        while(i<d){
            if(p1.getCoordinate(i)<p2.getCoordinate(i))
                return -1;
            else if(p1.getCoordinate(i)>p2.getCoordinate(i))
                return 1;
            i++;
        }
        return 0;
    }

}
