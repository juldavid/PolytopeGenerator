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

package experiment;

import polytope.*;
import random.FirstMarkovChain;

import java.util.*;

public class AverageVolume {

    /**
     * Return the point in the segment source that is different from the point 'common'
     * @param source a segment
     * @param common a point in two dimensions
     * @return the other point in the segment.
     */
    private static Point getNotCommonPoint(Facet source,Point common){
        if (source.getPoints().get(0).equals(common))
            return source.getPoints().get(1);
        return source.getPoints().get(0);
    }

    /**
     * Returns the perimeter of the triangle defined by the points given as parameters.
     * @param p1 the first point in the triangle
     * @param p2 the second point in the triangle
     * @param p3 the third point in the triangle
     * @return the perimeter of the triangle
     */
    private static double trianglePerimeter(Point p1,Point p2,Point p3){
        double res=0;
        res+=Point.distance(p1,p2);
        res+=Point.distance(p1,p3);
        res+=Point.distance(p3,p2);
        return res;
    }

    /**
     * Compute the volume of a triangle with Heron Formula.
     * @param p1 the first point in the triangle
     * @param p2 the second point in the triangle
     * @param p3 the third point in the triangle
     * @return the volume of the triangle
     */
    private static double triangleVolume(Point p1,Point p2,Point p3){
        double p=trianglePerimeter(p1,p2,p3)/2.;
        return Math.sqrt(p*(p-Point.distance(p1,p2))*(p-Point.distance(p1,p3))*
                (p-Point.distance(p2,p3)));
    }

    /**
     * The principe of the algorithm is the following: we decompose the n-vertices polygon into a range of n-3 triangles,
     * rooted in a fixed pointn then sum the volume of the triangles.
     * Time Complexity: O(n), Space Complexity: O(1)
     * @param fdp a polygon
     * @return the volume of the polygon given as an argument
     */
    private static double volume2D(FullDimensionPolytope fdp){
        Facet f=fdp.getFacets().get(0);
        Point fixed=f.getPoints().get(0);
        Point middle=f.getPoints().get(1);
        f=f.getNeighbor2D(middle); //Get the other facet adjacent to middle.
        Point newP=getNotCommonPoint(f,middle);
        double volume=triangleVolume(fixed,middle,newP);
        for(int i=0;i<(fdp.getPoints().size()-3);i++){
            f=f.getNeighbor2D(newP);
            middle=newP;
            newP=getNotCommonPoint(f,middle);
            volume+=triangleVolume(fixed,middle,newP);
        }
        return volume;
    }

    /**
     * Compute an approximation of the average volume of a polygon in a box of side k, for 2<=k<=100
     * @param arg is useless.
     */
    public static void main(String [] arg){
        FullDimensionPolytope lp;
        int numberOfSizes=100;
        int numberOfSteps=10000000;

        for(int k=2;k<=numberOfSizes;k++){
            FirstMarkovChain fmc=new FirstMarkovChain(2,k);
            fmc.initialize();
            double acc=0;
            for(int j=0;j<numberOfSteps;j++) {
                lp = fmc.nextStep();
                acc+=volume2D(lp);
            }
            System.out.println(k+" "+acc/(double)numberOfSteps);

        }
    }

}
