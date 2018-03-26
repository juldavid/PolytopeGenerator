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


    public Point(ArrayList<Integer> pCoordinates) {
        this.coordinates = pCoordinates;
    }

    public Point(int [] pCoordinates){
        coordinates=new ArrayList<>();
        for(int x:pCoordinates)
            coordinates.add(x);
    }

    public Point(int value,int dimension){
        coordinates=new ArrayList<>();
        for(int i=0;i<dimension;i++)
            coordinates.add(value);
    }

    public int getDimension(){
        return coordinates.size();
    }

    public int getCoordinate(int i){
        return coordinates.get(i);
    }

    private void add(StringBuilder sb,String before,String after){
        sb.append(before);
        for(int c:coordinates) {
            sb.append(c);
            sb.append(" ");
        }
        sb.append(after);

    }

    public static void addToPrint(Point p,StringBuilder sb){
        p.add(sb,"( ",") ");
    }


    public static void addToFile(Point p,StringBuilder sb){
        p.add(sb,"","\n");
    }


    @Override
    public int hashCode() {
        return Objects.hash(coordinates);
    }

    @Override
    public boolean equals(Object o) throws RuntimeException{
        if(!(o instanceof Point))
            throw new RuntimeException("Points must be compared with other Points");
        Point p=(Point)o;
        //System.out.println("Et moi je compare " + this + " et " + p);
        for(int i=0;i<coordinates.size();i++)
            if(!coordinates.get(i).equals(p.coordinates.get(i)))
                return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        addToPrint(this,sb);
        return sb.toString();
    }

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

    public static double distance(Point p1,Point p2){
        double dist=0.;
        for(int i=0;i<p1.getDimension();i++)
            dist+=(p1.getCoordinate(i)-p2.getCoordinate(i))*(p1.getCoordinate(i)-p2.getCoordinate(i));
        return Math.sqrt(dist);
    }

}
