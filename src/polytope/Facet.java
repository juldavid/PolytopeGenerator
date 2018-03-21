package polytope;

import toolkit.GaussianEliminationLite;

import java.util.ArrayList;

public class Facet extends LatticePolytope{
    private ArrayList<Facet> neighbors=new ArrayList<>();
    private ArrayList<Ridge> ridges=new ArrayList<>();

    private Point insidePoint;
    GaussianEliminationLite linearlyIndependantTest;
    GaussianEliminationLite normal;
    private boolean inside;

    public int getVisibilityFromLastPoint() {
        return visibilityFromLastPoint;
    }

    private int visibilityFromLastPoint;



    public void printNormal(){
        for(double x:normal.solve())
            System.out.print(x+ " ");
        System.out.println("Beta :"+normal.getBeta());
    }

    public int getDimension(){
        return linearlyIndependantTest.getRank();
    }

    public Point getInsidePoint() {
        return insidePoint;
    }

    public Facet(ArrayList<Point> points, Point pOutside) {
        insidePoint=pOutside;
        this.points = points;
        computeLinearIndependancy();
        try {
            NormalVector();
        }catch(RuntimeException r){
            System.out.println("Liste des Points: "+points.size());
            linearlyIndependantTest.printMatrix();
            throw new RuntimeException(r.getMessage());
        }
        setDirection(pOutside);
    }



    public void computeLinearIndependancy(){
        linearlyIndependantTest=new GaussianEliminationLite(points);
    }

    private void NormalVector(){
        normal= new GaussianEliminationLite(points,linearlyIndependantTest.getAffinelyIndependant());
    }

    private double direction(Point p){
        double sum=0;
        double [] alpha=normal.solve();
        if(alpha==null)
            throw new RuntimeException("Erreur solution");
        for(int i=0;i<p.getDimension();i++) {
            sum += p.getCoordinate(i) * alpha[i];
        }
        return sum;
    }

    public boolean isAbove(Point p) {
        double dir=direction(p);
        double beta=normal.getBeta();
        visibilityFromLastPoint=(int)(dir-beta);
        return (visibilityFromLastPoint!=0 &&(visibilityFromLastPoint>0)!=inside );
    }

    public boolean isOnTheHyperplane(Point p){
        return visibilityFromLastPoint==0;
    }

    private void setDirection(Point pInside){
        inside=(direction(pInside)> normal.getBeta());
    }

    public int getNumberOfNeighbors(){
        return neighbors.size();
    }

    public Facet getNeighbor(int i){
        return neighbors.get(i);
    }

    public Ridge getRidge(int i){
        return ridges.get(i);
    }

    void addNeighbor(Facet neighbor, Ridge ridge){
        if(!ridges.contains(ridge)) {
            neighbors.add(neighbor);
            ridges.add(ridge);
        }

    }

    void removeNeighbor(Ridge r){
        int i=ridges.indexOf(r);
        //if(i<0||i>=ridges.size())
            //throw new RuntimeException("Impossible de supprimer "+r+" dans "+ridges);
        if(i>0) {
            neighbors.remove(i);
            ridges.remove(i);
        }
    }

    public void replaceNeighbor(Facet neighbor, Ridge ridge) {
        int i=ridges.indexOf(ridge);
        if(i>=0)
            neighbors.set(i,neighbor);
        else {
            neighbors.add(neighbor);
            ridges.add(ridge);
        }
    }

    public double distance(Point point){
        double res=0.;
        double [] alpha=normal.solve();
        for(int i=0;i<point.getDimension();i++)
           res+= (point.getCoordinate(i)*alpha[i])-(points.get(0).getCoordinate(i)*alpha[i]);
        return res;
    }

    public int mostDistantPoint(ArrayList<Point> outside){
        int posMin=0;
        double distMin=distance(outside.get(0));
        double dist;
        for(int i=1;i<outside.size();i++) {
            dist=distance(outside.get(i));
            if(distMin>dist){
                distMin=dist;
                posMin=i;
            }
        }

        return posMin;
    }

    public Ridge intersection(Facet f){
        ArrayList<Point> intersection=new ArrayList<>();
        for(Point p:points){
            if(f.getPoints().contains(p))
                intersection.add(p);
        }
        return new Ridge(intersection,this,f);
    }

    public Point extractMostDistantPoint(ArrayList<Point> outside){
        int pos=mostDistantPoint(outside);
        Point res=outside.get(pos);
        outside.remove(pos);
        return res;
    }

    public Ridge isNeighbor(Facet facet) {
        int pos=neighbors.indexOf(facet);
        return (pos>0)?ridges.get(pos):null;
    }
}
