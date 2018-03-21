package polytope;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

public abstract class AbstractPolytope implements Polytope{
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        Collection<Point> points= getPoints();
        for(Point p:points)
            p.addToFile(sb);
        return sb.toString();
    }

    public void printToFile(String filename){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.print(this.toString());
        writer.println(getPoints().iterator().next());
        writer.close();
    }

}
