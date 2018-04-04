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

import java.util.ArrayList;
import java.util.Objects;

public class Point {
    private final ArrayList<Integer> coordinates;

    /**
     * Construct a d-dimensional point whose coordinates are given by an ArrayList of d integers.
     * @param pCoordinates is a list of integers, the coordinates of the point.
     */
    public Point(ArrayList<Integer> pCoordinates) {
        this.coordinates = pCoordinates;
    }


    /**
     * Construct a d-dimensional point whose coordinates are given by an tabular of d integers.
     * @param pCoordinates is a tabular of integers, the coordinates of the point.
     */
    public Point(int [] pCoordinates){
        coordinates=new ArrayList<>();
        for(int x:pCoordinates)
            coordinates.add(x);
    }

    /**
     * Construct a d-dimensional point in which each coordinate is set to 'value'
     * @param value is the value assigned to each coordinate.
     * @param dimension is the dimension of the point.
     */
    public Point(int value,int dimension){
        coordinates=new ArrayList<>();
        for(int i=0;i<dimension;i++)
            coordinates.add(value);
    }

    /**
     * Returns the number of coordinates.
     * @return the number of coordinates.
     */
    public int getDimension(){
        return coordinates.size();
    }

    /**
     * Get the i-th coordinate of a point.
     * @param i the index of the coordinate we want to obtain.
     * @return the i-th coordinate of a point.
     */
    public int getCoordinate(int i){
        return coordinates.get(i);
    }

    /**
     * Add the coordinates of the point to a StringBuilder, using special delimiters 'before' and after.
     * For instance if before="( " and after=") ", the String ( 0 1 3 ) will be added to the Stringbuilder.
     * @param sb the StringBuilder we want to modify
     * @param before the delimiter we add before the coordinates
     * @param after the delimiter we add after the coordinates
     */
    private void add(StringBuilder sb,String before,String after){
        sb.append(before);
        for(int c:coordinates) {
            sb.append(c);
            sb.append(" ");
        }
        sb.append(after);

    }

    /**
     * Add the coordinates of the point to a StringBuilder, using parenthesis as delimiters.
     * For instance, if p={0,1,3}, the String ( 0 1 3 ) will be added to the Stringbuilder
     * @param p the point we want to print
     * @param sb the StringBuilder we want to modify
     */
    public static void addToPrint(Point p,StringBuilder sb){
        p.add(sb,"( ",") ");
    }

    /**
     * Add the coordinates of the point and a carriage return to a StringBuilder.
     * @param p the point we want to print
     * @param sb the StringBuilder we want to modify
     */
    public static void addToFile(Point p,StringBuilder sb){
        p.add(sb,"","\n");
    }

    /**
     * Computes the hashcode of a Point, which is equal to the hashcode of its coordinates.
     * Calls List.hashcode()
     * @return the hashcode of a Point, which is equal to the hashcode of its coordinates.
     */
    @Override
    public int hashCode() {
        return Objects.hash(coordinates);
    }

    /**
     * Compares two points
     * @param o must be an instance of a Point
     * @return true if all coordinates are equal, false otherwise.
     * @throws RuntimeException
     */
    @Override
    public boolean equals(Object o) throws RuntimeException{
        if(!(o instanceof Point))
            throw new RuntimeException("Points must be compared with other Points");
        Point p=(Point)o;
        //System.out.println("Et moi je compare " + this + " et " + p);
        /*for(int i=0;i<coordinates.size();i++)
            if(!coordinates.get(i).equals(p.coordinates.get(i)))
                return false;
        return true;*/
        return coordinates.equals(p.coordinates);
    }

    /**
     * Print a point using parenthesis as delimiters.
     * @return a string  containing a point using parenthesis as delimiters.
     */
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        addToPrint(this,sb);
        return sb.toString();
    }

    /**
     * Compare two points according to the lexicographical order of their coordinates.
     * @param p1 a d-dimensionnal point.
     * @param p2 a d-dimensionnal point.
     * @return 1 p1>p2 in the lexicographical order, -1 if p1<p2, 0 if p1=p2
     */
    public static int compare(Point p1,Point p2){
        int d=p1.getDimension();
        int i=0;
        while(i<d){
            if(p1.getCoordinate(i)<p2.getCoordinate(i))
                return -1;
            else if(p1.getCoordinate(i)>p2.getCoordinate(i))
                return 1;
            i++;
        }
        return 0;
    }

    /**
     * Returns the distance between two d-dimensional points
     * @param p1 a d-dimensionnal point.
     * @param p2 a d-dimensionnal point.
     * @return the distance between two d-dimensional points
     */
    public static double distance(Point p1,Point p2){
        double dist=0.;
        for(int i=0;i<p1.getDimension();i++)
            dist+=(p1.getCoordinate(i)-p2.getCoordinate(i))*(p1.getCoordinate(i)-p2.getCoordinate(i));
        return Math.sqrt(dist);
    }

}
