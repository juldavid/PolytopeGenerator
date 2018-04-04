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
import java.util.List;

public class LatticePolytope  extends AbstractPolytope {
    int dimension;
    protected ArrayList<Point> points=new ArrayList<>();

    @Override
    public boolean containsExtremalPoint(Point p) {
        return points.contains(p);
    }

    @Override
    public ArrayList<Point> getPoints() {
        return points;
    }

    public void addPoint(Point p){
        points.add(p);
    }

    @Override
    public String toString() {
        points.sort(Point::compare);
        return super.toString();
    }

    @Override
    public boolean equals(Object o) throws RuntimeException{
        if(!(o instanceof LatticePolytope))
            throw new RuntimeException("Ridges must be compared with other Ridges");
        LatticePolytope r=(LatticePolytope) o;
        //if(o instanceof Facet)
            //System.out.println("Test d'égalité de la facette "+o+" avec "+this);
        if(r.points.size()!=points.size())
            return false;
        for(int i=0;i<points.size();i++)
            if(!r.points.contains(points.get(i)))
                return false;
        return true;
    }


}
