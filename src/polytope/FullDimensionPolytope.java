package polytope;

import toolkit.GaussianEliminationLite;

import java.util.ArrayList;
import java.util.List;

public class FullDimensionPolytope extends LatticePolytope {
    protected int dimension;
    private ArrayList<Facet> facets=new ArrayList<>();

    public FullDimensionPolytope(int pDimension) {
        dimension=pDimension;
    }

    public void addExtremalPointAndReplaceFacets(Point p,List<Facet> newF,List<Facet> oldF){
        points.add(p);
        for(Facet f:oldF)
            facets.remove(f);
        //facets.removeAll(oldF);
        for(Facet f:newF)
            facets.add(f);
        //facets.addAll(newF);
    }

    private static void intersectAndAdd(Facet f1,Facet f2){
        //double [] solution=null;
        Ridge r=f1.intersection(f2);
        if(r.getPoints().size() == (r.getPoints().get(0).getDimension() -1)) {
                    /*if (solution == null || solution.length < r.getPoints().size()) {
                        solution = new double[r.getPoints().size()];
                        for (int k = 0; k < solution.length; k++)
                            solution[k] = 1;
                    }
                    GaussianEliminationLite dimension = new GaussianEliminationLite(r.getPoints(), solution);*/
            //if(dimension.getRank()==r.getPoints().size()){
            f1.addNeighbor(f2,r);
            f2.addNeighbor(f1,r);
            //}
        }
    }

    public static void addNeighbors(List<Facet> newF,List<Facet> modified){
        Facet f1;
        for(int i=0;i<newF.size();i++){
            f1=newF.get(i);
            for(int j=i+1;j<newF.size();j++)
                intersectAndAdd(f1,newF.get(j));
            if(modified!=null)
               for(Facet f:modified)
                    intersectAndAdd(f1,f);
        }
        if(modified!=null) {
            for (int i = 0; i < modified.size(); i++) {
                f1 = modified.get(i);
                for (int j = i + 1; j < modified.size(); j++)
                    intersectAndAdd(f1, modified.get(j));
            }
        }
    }

    public boolean createSimplex(List<Point> pPoints) throws RuntimeException{
        if(pPoints.size() != dimension+1)
            throw new RuntimeException("A simplex must have d+1 points");
        points=new ArrayList<>(pPoints);
        if(!isSimplex(points))
            return false;
        for(int i=0;i<pPoints.size();i++){
            Point p=points.remove(0);
            Facet f=new Facet((ArrayList<Point>) points.clone(),p);
            facets.add(f);
            points.add(p);

        }
        addNeighbors(facets,null);
        return true;
    }

    private boolean isSimplex(ArrayList<Point> points){
        //Facet f=new Facet(points,new Point(0,dimension));
        GaussianEliminationLite simplex=new GaussianEliminationLite(points);
        return simplex.getAffinelyIndependant().size()==dimension+1;
    }

    public boolean staysFullDimensionAfterDeletion(Point p){
        points.remove(p);
        GaussianEliminationLite simplex=new GaussianEliminationLite(points);
        points.add(p);
        return simplex.getAffinelyIndependant().size()==dimension+1;
    }

    public boolean isSimplex(){
        return  points.size()==dimension+1;
    }

    public List<Facet> getFacets() {
        return facets;
    }
}
