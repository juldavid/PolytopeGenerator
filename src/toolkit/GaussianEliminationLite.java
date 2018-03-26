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

/*
 *  The {@code GaussianEliminationLite} is a modification from a code from Robert Sedgewick Kevin Wayne
 *  The original code could only perform the gauss elimination for square matrices.
 *  For additional documentation, see
 *  <a href="https://algs4.cs.princeton.edu/99scientific">Section 9.9</a>
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick (Original author)
 *  @author Kevin Wayne (Original author)
 *  @author Julien David (Modification)
 */
package toolkit;

import polytope.Point;

import java.util.List;

public class GaussianEliminationLite {
    private static final double EPSILON = 1e-10;
    private int columns;
    private int lines;
    //private long [][] A;
    private long [] x=null;
    private int lastDependantColumn;
    private int rank;

    private GaussianEliminationLite(List<Point> points) {
        if(points.size()==1)
            rank=1;
        else {
            columns = points.size();
            lines = points.get(0).getDimension() + 1;
            long[][] pA = MatrixFactory.getMatrix(lines, columns);
            for (int i = 0; i < (lines - 1); i++)
                for (int j = 0; j < columns; j++)
                    pA[i][j] = points.get(j).getCoordinate(i);
            for (int j = 0; j < columns; j++)
                pA[lines - 1][j] = 1;
            //printMatrix(pA, lines, columns);
            GaussJordan(pA, true);
            //printMatrix(pA, lines, columns);
        }
    }

    public GaussianEliminationLite(List<Point> points,int dim) {
        int i,j;
        columns = dim+1;
        lines= dim;
        long [][]pA=MatrixFactory.getMatrix(lines,columns);
        for(i=0;i<lines;i++) {
            for (j = 0; j < (columns-1); j++)
                pA[i][j] = points.get(i).getCoordinate(j);
            pA[i][j]=-1;
        }
        //printMatrix(pA,lines,columns);
        GaussJordan(pA,false);
        //printMatrix(pA,lines,columns);
        divideLinesByPgcd(pA);
        //printMatrix(pA,lines,columns);

        solve(pA);

    }

    private int getRank(){
        return rank;
    }

    private long pgcd(long a,long b)
    {
        long tmp;
        while(b>0){
            tmp=b;
            b=a%b;
            a=tmp;
        }
        return a;
    }

    private long pgcdVector(long [] line)
    {
        long res=Math.abs(line[0]);
        for(int i=1;i<line.length;i++)
            res=pgcd(res,Math.abs(line[i]));
        return res;
    }

    private void divideVectorByPgcd(long [] line,long pgcd)
    {
        for(int i=0;i<line.length;i++)
            line[i]/=pgcd;
    }

    private void divideLinesByPgcd(long[][] pA){
        long pgcd;
        for(long [] line:pA){
            pgcd= pgcdVector(line);
            if(pgcd!=0)
                divideVectorByPgcd(line,pgcd);
        }
    }

    private void printMatrix(long[][] pA,int l,int c){
        for(int i=0;i<l;i++){
            for(int j=0;j<c;j++)
                System.out.print(pA[i][j]+ " ");
            System.out.println("");
        }System.out.println("------------------");
    }

    private void GaussJordan(long[][] pA,boolean computeDim) {
        lastDependantColumn=columns-1;
        int j;
        long t;
        for (j = 0,rank=0; j < columns; j++) {

            int i = rank;
            while(i<lines && pA[i][j]==0) i++;
            if(i<lines) {
                swap(pA, rank, i);
                for (i = (computeDim)?rank + 1:0; i < lines; i++) {
                    if(computeDim || i!=rank) {
                        t=pA[i][j];
                        for (int m = 0; m < columns; m++) {
                            pA[i][m] = pA[i][m] * pA[rank][j] - t * pA[rank][m];
                        }
                    }
                }
                rank++;
            }
            else
                lastDependantColumn=j;
        }

    }



    private long diagProduct(long[][] pA,int l){
        long prod=1;
        for(int i=0;i<l;i++)
            prod*=pA[i][i];
        return prod;
    }

    public long getBeta() {
        return x[columns-1];
    }

    public long [] getSolution(){
        return x;
    }

    private void solve(long[][] pA){
        int i,l=lastDependantColumn;
        if(x==null) {
            x = new long[columns];
            x[l] = diagProduct(pA, l);
            for (i = l - 1; i >= 0; i--)
                x[i] = -pA[i][l] * x[l] / pA[i][i];
            for (i = l + 1; i < lines; i++)
                x[i] = 0;
            /*System.out.print("Solution : ");
            for (i = 0; i < columns; i++)
                System.out.print(x[i]+" ");
            System.out.println("");*/
            long pgcd = pgcdVector(x);
            if (pgcd != 0)
                divideVectorByPgcd(x, pgcd);
            /*for (i = 0; i < columns; i++)
                System.out.print(x[i]+" ");
            System.out.println("");*/
        }
    }
    public void inverse() {
        for(int i=0;i<x.length;i++)
            x[i]=-x[i];
    }


    public static int getRank(List<Point> points){
        GaussianEliminationLite gel=new GaussianEliminationLite(points);
        return gel.getRank();
    }

    // swap rows A[i][] and A[j][] in 2D array A[][]
    private static void swap(long[][] A, int i, int j) {
        long[] temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }


}
