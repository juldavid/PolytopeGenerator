/*
 * Copyright (c) 2018. Julien David.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the <organization>.
 * 4. Neither the name of the <organization> nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package polytope;

import toolkit.GaussianEliminationLite;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class Facet extends LatticePolytope{

    private HashSet<Ridge> ridges=new HashSet<>();


    private Point insidePoint;
    private GaussianEliminationLite normal;
    private int visibilityFromLastPoint;

    /**
     * Returns an extremal point of the polytope that doesn't belong to the facet.
     * @return an extremal point of the polytope that doesn't belong to the facet.
     */
    public Point getInsidePoint() {
        return insidePoint;
    }

    /**
     * Defines a d-dimensional facet using a list of points. Generates a exception if the list of points does not contain at least
     * d affinely independent points. Also computes its normal vector and uses the pOutside point to decide in which direction
     * is the polytope's interior.
     * @param points list of points from which the facet will be created
     * @param pOutside an extremal point of the polytope that doesn't belong to the facet.
     */
    Facet(ArrayList<Point> points, Point pOutside) {
        insidePoint=pOutside;
        this.points = points;
        this.dimension=pOutside.getDimension();
        if(points.contains(pOutside))
            throw new RuntimeException("Non mais sérieux");
        if(points.size()<dimension)
            throw new RuntimeException("This list of points should contain at least "+dimension+ "points but instead contains "+points.size());
        NormalVector();
        setDirection(pOutside);
    }

    /**
     * Computes the normal vector of the facet.
     * Time complexity: O(d^2), Space Complexity: O(d^2) in the worst case and O(1) in the best case.
     */
    private void NormalVector(){
        normal= new GaussianEliminationLite(points,points.get(0).getDimension());
    }

    /**
     * Computes the cross product of a d-dimensional point p and the normal vector of the facet.
     * Time Complexity: O(d), Space Commplexity: O(1).
     * @param p a point
     * @return the cross product of a point p and the normal vector.
     */
    private BigInteger direction(Point p){
        BigInteger sum=BigInteger.ZERO;
        BigInteger [] alpha=normal.getSolution();
        for(int i=0;i<p.getDimension();i++)
            sum =  sum.add(alpha[i].multiply(BigInteger.valueOf(p.getCoordinate(i))));
        return sum;
    }

    private void computeVisibility(Point p){
        BigInteger dir=direction(p);
        BigInteger beta=normal.getBeta();
        visibilityFromLastPoint=dir.compareTo(beta);
    }

    /**
     * Determines if the point p is above the facet.
     * Time Complexity: O(d), Space Commplexity: O(1).
     * @param p a point.
     * @return true if p is above the facet, false otherwise.
     */
    public boolean isAbove(Point p) {
        computeVisibility(p);
        boolean res=(visibilityFromLastPoint>0);
        /*if(visibilityFromLastPoint>0)
            System.out.println("La facette "+this +"est visible depuis "+p +" "+dir+" "+beta+" "+ insidePoint + direction(insidePoint));
        else if(visibilityFromLastPoint<0)
            System.out.println("La facette "+this +"n'est  pas visible depuis "+p +" "+dir+" "+beta+" "+ insidePoint + direction(insidePoint));
        else
            System.out.println("Le point "+p+" est sur l'hyperplan de "+this+" "+dir+" "+beta+" "+ insidePoint + direction(insidePoint));*/
        return res;
        //return  (visibilityFromLastPoint>0);
    }

    public boolean isPointOnTheHyperplane(Point p){
        computeVisibility(p);
        return (visibilityFromLastPoint==0);
    }

    /**
     * Returns true if the last tested point is on the facet's hyperplan.
     * @return true if the last tested point is on the facet's hyperplan, false otherwise.
     */
    public boolean lastPointOnTheHyperplane(){
        return visibilityFromLastPoint==0;
    }

    /**
     * Inverse the normal vector if the direction of the interior d-dimensional point in greater than beta. This way, in future tests, one will only need
     * to check if the direction of a point is above beta to determine whether it is above the facet or not.
     * Time Complexity: O(d), Space complexity: O(1)
     * @param pInside a d-dimensional point.
     */
    private void setDirection(Point pInside){
        computeVisibility(pInside);
        if(visibilityFromLastPoint>0)
            normal.inverse();
    }

    /**
     * Method used to compute the area of a Polygon.
     * Returns the neighbor connected with the current facet by the point p.
     * Time and Space Complexity: O(1)
     * @param p 2-dimensional point which connects the current facet and the solution.
     * @return the neighbor connected with the current facet by the point p
     */
    public Facet getNeighbor2D(Point p) {
        Ridge res = null;
        for(Ridge r:ridges){
            if((r.getPoints().contains(p))) {
                res = r;
                break;
            }
        }
        if(res==null) throw new RuntimeException("Point is not in the neighborhood");
        if(this==res.getFirst())
            return res.getSecond();
        return res.getFirst();
    }

    /**
     * Returns the facet's list of ridges.
     * @return the facet's list of ridges.
     */
    public Collection<Ridge> getRidges() {
        return ridges;
    }

    /**
     * Connects the facet 'neighbor' to the current one using the ridge 'ridge'
     * TODO: check if the test "contains" is needed in order to improve the complexity
     * Amortized time and space complexity: O(1). If the test if removed.
     * @param ridge the new ridge that connects the current facet and "neighbor"
     */
    void addNeighbor(Ridge ridge){
        if(!ridges.add(ridge))
            throw new RuntimeException("The neighbor already exists");
    }

    /**
     * Remove the adjacent facet connected to the current one by the ridge r
     * Time Complexity: O(|this+ridges|)
     * @param r the ridge that identifies the adjacent facet where' going to remove.
     */
    void removeNeighbor(Ridge r) {
        if(!ridges.remove(r)){
            StringBuilder sb=new StringBuilder();
            for(Ridge r2:ridges) {
                sb.append(r2);
                sb.append("\n");
            }
            throw new RuntimeException("Facet :" + this + "Can't remove a neighbor connected with ridge" + r+ "\n" +sb.toString());
        }
    }

    /**
     * Computes the distance between a d-dimensional point and the current facet.
     * Time Complexity: O(d), Space complexity: O(1)
     * @param point a d-dimensional point
     * @return the distance between a d-dimensional point and the current facet.
     */
    private BigInteger distance(Point point){
        BigInteger res=BigInteger.ZERO;
        BigInteger [] alpha=normal.getSolution();
        for(int i=0;i<point.getDimension();i++)
           res= res.add(alpha[i].multiply(BigInteger.valueOf(point.getCoordinate(i))).subtract(alpha[i].multiply(BigInteger.valueOf(points.get(0).getCoordinate(i)))));
        return res;
    }

    /**
     * Computes the most distant d-dimensional point to the current facet amongst a given list.
     * Time Complexity: O(|outside|*d), Space Complexity: O(1)
     * @param outside a list of d-dimensional points.
     * @return the position of the most distant d-dimensional point to the current facet amongst a given list.
     */
    private int mostDistantPoint(ArrayList<Point> outside){
        int posMin=0;
        BigInteger distMin=distance(outside.get(0));
        BigInteger dist;
        for(int i=1;i<outside.size();i++) {
            dist=distance(outside.get(i));
            if(distMin.compareTo(dist)<0){
                distMin=dist;
                posMin=i;
            }
        }

        return posMin;
    }

    /**
     * Computes the intersection between the set of points of the current facet and the one given as a parameter
     * and returns it a a ridge.
     * Time Complexity: O(|this.points|*|f.points|), Space complexity: O(|this.points|)
     * @param f a facet.
     * @return a ridge containing the intersection between the set of points of the current facet and the one given as a parameter
     */
    public Ridge intersection(Facet f){
        ArrayList<Point> intersection=new ArrayList<>();
        for(Point p:points){
            if(f.getPoints().contains(p))
                intersection.add(p);
        }
        return new Ridge(intersection,this,f);
    }

    /**
     * Extract the most distant point to the current facet amongst the list and returns it.
     * Time Complexity: O(|outside|*d), Space Complexity: O(1)
     * @param outside a list of d-dimensional points
     * @return the most distant point to the current facet amongst the list
     */
    public Point extractMostDistantPoint(ArrayList<Point> outside){
        int pos=mostDistantPoint(outside);
        Point res=outside.get(pos);
        outside.remove(pos);
        return res;
    }


}
