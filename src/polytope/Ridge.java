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

public class Ridge extends LatticePolytope{
    Facet first;
    Facet second;


    /**
     * Returns one of the two facets connected by the current ridge.
     * @return one of the two facets connected by the current ridge.
     */
    public Facet getFirst() {
        return first;
    }

    /**
     * Returns one of the two facets connected by the current ridge.
     * @return one of the two facets connected by the current ridge.
     */
    public Facet getSecond() {
        return second;
    }

    /**
     * Returns the Facet that's different from the one given as a parameter.
     * Not safe since it doesn't check if f is actually one of the two facets.
     * Should only be called from Facet and stay package-private.
     * @param f one of the facet that the ridge connects.
     * @return the facet different from f.
     */
    public Facet getNeighbor(Facet f){
        return (first==f)?second:first;
    }

    /**
     * Replace the Facet oldF by the Facet newF.
     * Time and Space complexity: O(1)
     * @param oldF the Facet we want to replace
     * @param newF the new Facet.
     * @return true if oldF has been replaced, false otherwise.
     */
    boolean replaceNeighbor(Facet oldF, Facet newF){
        if(first==oldF)
            first=newF;
        else if(second==oldF)
            second=newF;
        else
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Construct a ridge, which connects facets f1 and f2, whose list of points pPoints was computed previously
     * and is equal to the intersection of f1.points and f2.points.
     * @param pPoints list of points, computed previously and equal to the intersection of f1.points and f2.points.
     * @param f1 a facet of a d-dimensional polytope.
     * @param f2 a facet of a d-dimensional polytope.
     */
    public Ridge(ArrayList<Point> pPoints, Facet f1, Facet f2) {
        first=f1;
        second=f2;
        points=pPoints;
    }
}
