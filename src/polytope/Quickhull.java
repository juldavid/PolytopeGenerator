package polytope;

import java.util.*;

public class Quickhull {
    private static ArrayDeque<Facet> facetsWithOutsideSet=new ArrayDeque<>();
    private static ArrayDeque<ArrayList<Point>> outsideSets=new ArrayDeque<>();
    private static ArrayList<Facet> visible=new ArrayList<>();
    private static ArrayList<Facet> modified=new ArrayList<>();
    private static ArrayList<Ridge> frontier=new ArrayList<>();
    private static FullDimensionPolytope res;



    private static void partition(List<Facet> facets,List<Point> points){
        outsideSets.clear();
        facetsWithOutsideSet.clear();
        for(Facet f:facets){
            ListIterator<Point> unseenPoints=points.listIterator();
            ArrayList outside=new ArrayList();
            while(unseenPoints.hasNext()){
                Point p=unseenPoints.next();
                if(f.isAbove(p)) {
                    unseenPoints.remove();
                    outside.add(p);
                }
            }
            if(!outside.isEmpty()){
                facetsWithOutsideSet.add(f);
                outsideSets.add(outside);
            }
        }
    }

    private static void setVisibleAndFrontier(Facet f, Point p){
        visible.clear();
        frontier.clear();
        modified.clear();
        visible.add(f);
        HashSet<Facet> seen = new HashSet<>();
        seen.add(f);
        ArrayDeque<Facet> queue=new ArrayDeque<>();
        queue.add(f);
        Facet tmp,neighbor;
        while (!queue.isEmpty()){
            tmp=queue.remove();
            for(int i=0;i<tmp.getNumberOfNeighbors();i++){
                neighbor=tmp.getNeighbor(i);
                if (neighbor.isAbove(p)) {
                    if (!seen.contains(neighbor)) {
                        visible.add(neighbor);
                        //System.out.println("La facette est visible"+ neighbor);
                        seen.add(neighbor);
                        queue.add(neighbor);
                    }
                }
                else frontier.add(tmp.getRidge(i));
            }
        }
    }

    private static List<Facet> setNewFacets(Point p,Point inside){
        ArrayList<Point> points;
        ArrayList<Facet> res = new ArrayList<>();
        Facet v,nv;
        Ridge r;
        for(int i=0;i<frontier.size();i++){
            r=frontier.get(i);
            if(r.first.isAbove(p)){
                v=r.first;
                nv=r.second;
            }
            else{
                v=r.second;
                nv=r.first;
            }
            if(!nv.isOnTheHyperplane(p)) {
                points= (ArrayList<Point>) frontier.get(i).getPoints().clone();
                if(points.contains(p))
                    throw new RuntimeException("Le point apparaît deux fois" + p);
                points.add(p);
                Facet f = new Facet(points, inside);
                res.add(f);
                f.addNeighbor(nv, frontier.get(i));
                nv.replaceNeighbor(f, frontier.get(i));
            }
            else{
                if(!v.isOnTheHyperplane(p)){
                    nv.addPoint(p);
                    modified.add(nv);
                    try {
                        nv.removeNeighbor(r);
                    }catch(RuntimeException e){
                        throw new RuntimeException("Point ajouté "+p+" "+e.getMessage());
                    }
                    v.removeNeighbor(r);
                }
                else
                    r.addPoint(p);
            }
        }
        return res;
    }

    private static boolean absorbedPoint(FullDimensionPolytope fdp,Point newP) {
        ArrayList<Point> points=fdp.getPoints();
        List<Facet> facets= fdp.getFacets();
        for(Point p:points){
            boolean b=false;
            for(Facet f:facets){
                if(f.getPoints().contains(p) && !visible.contains(f) && !f.isOnTheHyperplane(newP)) {
                    b = true;
                    break;
                }
            }
            if(!b)
                return true;
        }
        return false;
    }

    private static void removeRidgesAndNeighbors(){
        for(int i=0;i<visible.size();i++){
            Facet f1=visible.get(i);
            for(int j=i+1;j<visible.size();j++){
                Ridge ridge=f1.isNeighbor(visible.get(j));
                if(ridge!=null) {
                    f1.removeNeighbor(ridge);
                    visible.get(j).removeNeighbor(ridge);
                }
            }
        }
    }


    public static void convexHull(FullDimensionPolytope res, List<Point> points, int dimension){
        partition(res.getFacets(),points);
        while(!facetsWithOutsideSet.isEmpty()){
            Facet f=facetsWithOutsideSet.remove();
            ArrayList<Point> outside=outsideSets.remove();
            Point p=f.extractMostDistantPoint(outside);
            setVisibleAndFrontier(f,p);
            List<Facet> newFacets=setNewFacets(p,f.getInsidePoint());
            res.addNeighbors(newFacets,modified);
            partition(newFacets,outside);
            res.addExtremalPointAndReplaceFacets(p,newFacets,visible);
        }
    }

    public static FullDimensionPolytope quickHull(ArrayList<Point> points, int dimension) {
        res=new FullDimensionPolytope(dimension);
        List<Point> firsts=points.subList(0,dimension+1);
        List<Point> rest=points.subList(dimension+1,points.size());
        res.createSimplex(firsts);
        convexHull(res,rest,dimension);
        return res;
    }

    public static boolean incrementalConvexHull(FullDimensionPolytope fdp,Point p){
        List<Point> list=new ArrayList<>();
        List<Facet> newFacets=null;
        list.add(p);
        partition(fdp.getFacets(),list); //Assign the point to a facet that's visible from it
        if(facetsWithOutsideSet.isEmpty())
            return false;
        setVisibleAndFrontier(facetsWithOutsideSet.remove(),outsideSets.remove().get(0)); //Compute all visibile Facets and the frontier.
        if(absorbedPoint(fdp,p))
            return false;

        newFacets = setNewFacets(p, visible.get(0).getInsidePoint());
        fdp.addNeighbors(newFacets,modified); //Connect new and modified facets
        fdp.addExtremalPointAndReplaceFacets(p, newFacets, visible); //Remove visible facets.

        return true;
    }



    public static void decrementalConvexHull(FullDimensionPolytope fdp,Point p){
        fdp.getPoints().remove(p);
        ArrayList<Point> points=fdp.getPoints();
        fdp=quickHull(points,fdp.dimension);
    }

}
