package polytope;

import java.util.ArrayList;
import java.util.List;

public class LatticePolytope  extends AbstractPolytope {
    protected ArrayList<Point> points=new ArrayList<>();

    @Override
    public boolean containsExtremalPoint(Point p) {
        return points.contains(p);
    }

    @Override
    public ArrayList<Point> getPoints() {
        return points;
    }

    public void addPoint(Point p){
        points.add(p);
    }

    @Override
    public String toString() {
        points.sort(Point::compare);
        return super.toString();
    }

}
