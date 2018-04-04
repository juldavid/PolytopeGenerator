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

package random;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import polytope.*;
import polytope.Point;

import java.awt.*;
import java.util.ArrayList;

public class FirstMarkovChain extends MarkovChain {
    private FullDimensionPolytope state;

    public FirstMarkovChain(int pDim, int bSize) {
        super(pDim, bSize);
    }

    @Override
    public FullDimensionPolytope initialize() {
        state=new FullDimensionPolytope(dimension);
        ArrayList<Point> points = new ArrayList<>();
        do {
            points.clear();
            for (int i = 0; i <= dimension; i++) {
                Point p=rpg.randomPoint(dimension, boxSize);
                if(!points.contains(p))
                    points.add(p);
                else
                    i--;
            }
        }while (!state.createSimplex(points));
        System.gc();
        return state;
    }

    @Override
    public FullDimensionPolytope nextStep() {
        Point p=rpg.randomPoint(dimension,boxSize);
        if(state.containsExtremalPoint(p)) {
            if(state.staysFullDimensionAfterDeletion(p)){
                //System.out.println("Je supprime " +p+ " "+state.getPoints().size()+" "+state.getFacets().size());
                state.getPoints().remove(p);
                int size=state.getPoints().size();
                String s=state.toString();
                state = Quickhull.quickHull(state.getPoints(),dimension);
                if(state.getPoints().size()!=size) {
                    StringBuilder sb=new StringBuilder();
                    for (Facet f2 : state.getFacets()) {
                        sb.append(f2 + " et ses ridges ");
                        for (Ridge r : f2.getRidges()) {
                            sb.append(r + " - ");
                        }
                        sb.append("\n");
                    }
                    throw new RuntimeException("Convex hull should contains exactly " + size + " points after removing " + p + "but instead contains " + state.getPoints().size() + "\n" + s + "\n" + state+"\n"+sb.toString());
                }
            }
        }
        else
            Quickhull.incrementalConvexHull(state, p);
        return state;
    }
}
