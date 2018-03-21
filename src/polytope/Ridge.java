package polytope;

import java.util.ArrayList;

public class Ridge extends LatticePolytope{
    //ajouter voisinage
    Facet first;
    Facet second;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) throws RuntimeException{
        if(!(o instanceof Ridge))
            throw new RuntimeException("Ridges must be compared with other Ridges");
        Ridge r=(Ridge) o;
        if(r.points.size()!=points.size())
            return false;
        for(int i=0;i<points.size();i++)
            if(!r.points.contains(points.get(i)))
                return false;
        return true;
    }

    public Ridge(ArrayList<Point> pPoints,Facet f1,Facet f2) {
        first=f1;
        second=f2;
        points=pPoints;
    }
}
