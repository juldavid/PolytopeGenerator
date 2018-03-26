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

import java.util.*;

public class Quickhull {
    private static ArrayDeque<Facet> facetsWithOutsideSet=new ArrayDeque<>();
    private static ArrayDeque<ArrayList<Point>> outsideSets=new ArrayDeque<>();
    private static ArrayList<Facet> visible=new ArrayList<>();
    private static ArrayList<Facet> modified=new ArrayList<>();
    private static ArrayList<Ridge> frontier=new ArrayList<>();
    private static FullDimensionPolytope res;



    private static void partition(List<Facet> facets,List<Point> points){
        for(Facet f:facets){
            ListIterator<Point> unseenPoints=points.listIterator();
            ArrayList<Point> outside=new ArrayList<>();
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
        points.clear();
    }

    private static void setVisibleAndFrontier(Facet f, Point p){
        visible.clear();
        frontier.clear();
        modified.clear();
        //---------------
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
                        //System.out.println("Ajout de la facette visible "+neighbor+" à partir de "+tmp);
                        visible.add(neighbor);
                        seen.add(neighbor);
                        queue.add(neighbor);
                    }
                }
                else frontier.add(tmp.getRidge(i));
            }
        }
    }

    private static List<Facet> setNewFacets(Point p){
        ArrayList<Point> points;
        ArrayList<Facet> res = new ArrayList<>();
        Facet v,nv;
        for(Ridge r:frontier){
            if(r.first.isAbove(p)){
                v=r.first;
                nv=r.second;
            }
            else{
                v=r.second;
                nv=r.first;
            }
            if(!nv.lastPointOnTheHyperplane()) {
                points= (ArrayList<Point>) r.getPoints().clone();
                points.add(p);
                Facet f = new Facet(points, v.getInsidePoint());
                res.add(f);
                f.addNeighbor(nv, r);
                nv.replaceNeighbor(f, r);
            }
            else{
                if(!v.lastPointOnTheHyperplane()){
                    nv.addPoint(p);
                    modified.add(nv);
                    nv.removeNeighbor(r);
                    v.removeNeighbor(r);
                }
                else
                    r.addPoint(p);
            }
        }
        return res;
    }

    private static boolean absorbedPoint(FullDimensionPolytope fdp) {
        ArrayList<Point> points=fdp.getPoints();
        List<Facet> facets= fdp.getFacets();
        for(Point p:points){
            boolean b=false;
            for(Facet f:facets){
                if(f.getPoints().contains(p) && !visible.contains(f) && !f.lastPointOnTheHyperplane()) {
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


    private static void convexHull(FullDimensionPolytope res, List<Point> points, int dimension){
        StringBuilder sb=new StringBuilder();
        /*sb.append(" Simplexe de départ ");
        for(Point p2:res.getPoints())
            sb.append(p2);
        sb.append("\n ");
        for(Facet f2:res.getFacets())
            sb.append(f2+"\n");
        sb.append(" Points à ajouter  ");
        for(Point p2:points)
            sb.append(p2);*/

        partition(res.getFacets(),points);
        while(!facetsWithOutsideSet.isEmpty()){
            Facet f=facetsWithOutsideSet.remove();
            ArrayList<Point> outside=outsideSets.remove();
            Point p=f.extractMostDistantPoint(outside);
            //System.out.println("Recalcul, Point à ajouter "+p);
            setVisibleAndFrontier(f,p);
            if(visible.size()==res.getFacets().size()) {
                throw new RuntimeException("Ici "+ p + "\n" + sb.toString());
            }
            List<Facet> newFacets=setNewFacets(p);
            res.addNeighbors(newFacets,modified);
            partition(newFacets,outside);
            res.addExtremalPointAndReplaceFacets(p,newFacets,visible);
        }
    }

    public static FullDimensionPolytope quickHull(ArrayList<Point> points, int dimension) {
        res=new FullDimensionPolytope(dimension);
        ArrayList<Point> firsts=new ArrayList<>();
        StringBuilder sb=new StringBuilder();
        int numberOfTest=points.size()-dimension-1;
        for(int i=0;i<=numberOfTest;i++) {
            if(firsts.size()==dimension+1){
                points.add(firsts.remove(0));
                firsts.add(points.remove(0));
            }
            else {
                for(int j=0;j<dimension+1;j++)
                    firsts.add(points.remove(0));
            }
            if (GaussianEliminationLite.getRank(firsts) == (dimension + 1)) {
                res.createSimplex(firsts);
                convexHull(res, points, dimension);
                return res;
            }
        }
        throw new RuntimeException("Is this ever gonna end? ");
    }

    public static boolean incrementalConvexHull(FullDimensionPolytope fdp,Point p){
        List<Point> list=new ArrayList<>();
        List<Facet> newFacets;
        list.add(p);
        partition(fdp.getFacets(),list); //Assign the point to a facet that's visible from it
        if(facetsWithOutsideSet.isEmpty())
            return false;
        setVisibleAndFrontier(facetsWithOutsideSet.remove(),outsideSets.remove().get(0)); //Compute all visibile Facets and the frontier.
        if(absorbedPoint(fdp))
            return false;
        //System.out.println("J'ajoute "+p);

        if(visible.size()==fdp.getFacets().size())
            throw new RuntimeException("Ici");

        newFacets = setNewFacets(p);
        fdp.addNeighbors(newFacets,modified); //Connect new and modified facets
        fdp.addExtremalPointAndReplaceFacets(p, newFacets, visible); //Remove visible facets.

        return true;
    }


}
