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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FullDimensionPolytope extends LatticePolytope {
    private ArrayList<Facet> facets=new ArrayList<>();

    /**
     * Creates a new instance of a pDimension-dimensional FullDimensionPolytope
     * @param pDimension is the dimension of the polytope.
     */
    public FullDimensionPolytope(int pDimension) {
        dimension=pDimension;
    }

    /**
     * Add p to the list of points, remove the list of facets oldF, and add the list of facets newF.
     * @param p a point to add
     * @param newF a list of facets to add
     * @param oldF a list of points to remove
     */
    public void addExtremalPointAndReplaceFacets(Point p,List<Facet> newF,List<Facet> oldF){
        points.add(p);
        for(Facet f:oldF) {
            //System.out.println("Supprimé "+f +"Point opposé " +f.getInsidePoint());
            if(!facets.remove(f)) {
                StringBuilder sb=new StringBuilder();
                for(Facet f2:oldF) {
                    sb.append(f2);
                    sb.append("\n");
                }
                sb.append("-----------\n");
                for(Facet f2:facets) {
                    sb.append(f2);
                    sb.append("\n");
                }
                throw new RuntimeException("Unable to remove facet " + f+"\n"+sb.toString());
            }
        }
        for(Facet f:newF)
            facets.add(f);

        //facets.addAll(newF);
    }

    /**
     * Computes the intersection between two facets and connects them if the intersection
     * Time Complexity: O(|this.points|*|f.points|), Amortized Worst Space Complexity: O(1)
     * contains (dimension-1) affinely independant points.
     * @param f1 a facet
     * @param f2 a facet
     */
    private void intersectAndAdd(Facet f1,Facet f2){
        Ridge r=f1.intersection(f2);
        if(r.getPoints().size() == (dimension -1)) {
            if(GaussianEliminationLite.getRank(r.points)==(dimension-1)){
                f1.addNeighbor(f2, r);
                f2.addNeighbor(f1, r);
                //System.out.println("Ajout des voisins "+f1 +" et "+f2+"par le ridge "+r);
            }
        }
    }

    /**
     * Takes every facet created or modified by the QuickHull algorithm and connects the adjacent ones.
     * @param newF list of facets created by the Quickhull algorithm
     * @param modified list of facets modified by the Quickhull algorithm
     */
    public void addNeighbors(List<Facet> newF,List<Facet> modified){
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

    /**
     *
     * @param pPoints a list of d+1 d-dimensional points
     * @return true if pPoints contains (d+1) affinely independant points, false otherwise.
     */
    public boolean createSimplex(List<Point> pPoints){
        if(!isSimplex(pPoints))
            return false;
        points=new ArrayList<>(pPoints);
        for(int i=0;i<pPoints.size();i++){
            Point p=points.remove(0);
            Facet f=new Facet((ArrayList<Point>) points.clone(),p);
            facets.add(f);
            points.add(p);

        }
        addNeighbors(facets,null);
        return true;
    }

    /**
     * Test whether a list of points contains d+1 affinely independant d-dimensional points
     * Time and Space Complexity: O(|points|*d)
     * @param points a list of d-dimensional points.
     * @return true if the list of points contains d+1 affinely independant d-dimensional points, false otherwise.
     */
    private boolean isSimplex(List<Point> points){
        return GaussianEliminationLite.getRank(points)==dimension+1;
    }

    /**
     * Checks if a polytope would this have d+1 affinely independent points if we where to remove the point p.
     * @param p a d-dimensional point
     * @return true if a polytope still have d+1 affinely independent points after we remove p.
     */
    public boolean staysFullDimensionAfterDeletion(Point p){
        points.remove(p);
        boolean res=GaussianEliminationLite.getRank(points)==dimension+1;
        points.add(p);
        return res;
    }


    /**
     * Returns the polytopes list of facets.
     * @return the polytopes list of facets.
     */
    public List<Facet> getFacets() {
        return facets;
    }

}
