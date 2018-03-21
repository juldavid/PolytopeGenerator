package polytope;

import java.util.Collection;
import java.util.List;

public interface Polytope {
    Collection<Point> getPoints();
    boolean containsExtremalPoint(Point p);
}
