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


package toolkit;

import polytope.Point;

import java.math.BigInteger;
import java.util.List;

public class GaussianEliminationLite {
    private int columns;
    private int lines;
    private BigInteger [] x=null;
    private int lastDependantColumn;
    private int rank;

    private GaussianEliminationLite(List<Point> points) {
        if(points.size()==1)
            rank=1;
        else {
            columns = points.size();
            lines = points.get(0).getDimension() + 1;
            BigInteger[][] pA = MatrixFactory.getMatrix(lines, columns);
            for (int i = 0; i < (lines - 1); i++)
                for (int j = 0; j < columns; j++)
                    pA[i][j] = BigInteger.valueOf(points.get(j).getCoordinate(i));
            for (int j = 0; j < columns; j++)
                pA[lines - 1][j] = BigInteger.valueOf(1);
            GaussJordan(pA, true);
        }
    }

    public GaussianEliminationLite(List<Point> points,int dim) {
        int i,j;
        columns = dim+1;
        lines= dim;
        BigInteger [][]pA=MatrixFactory.getMatrix(lines,columns);
        for(i=0;i<lines;i++) {
            for (j = 0; j < (columns-1); j++)
                pA[i][j] = BigInteger.valueOf(points.get(i).getCoordinate(j));
            pA[i][j]=BigInteger.valueOf(-1);
        }
        GaussJordan(pA,false);
        divideLinesByPgcd(pA);

        solve(pA);
    }

    private int getRank(){
        return rank;
    }


    private BigInteger pgcdVector(BigInteger [] line)
    {
        BigInteger res=line[0].abs();
        for(int i=1;i<line.length;i++)
            res = res.gcd(line[i]); //The absolute value of line[i] is already computed by gcd.
        return res;
    }

    private void divideVectorByPgcd(BigInteger [] line,BigInteger pgcd)
    {
        for(int i=0;i<line.length;i++)
            line[i].divide(pgcd);
    }

    private void divideLinesByPgcd(BigInteger[][] pA){
        BigInteger pgcd;
        for(BigInteger [] line:pA){
            pgcd= pgcdVector(line);
            if(!pgcd.equals(BigInteger.ZERO))
                divideVectorByPgcd(line,pgcd);
        }
    }


    private void GaussJordan(BigInteger[][] pA,boolean computeDim) {
        lastDependantColumn=columns-1;
        int j;
        BigInteger t;
        for (j = 0,rank=0; j < columns; j++) {

            int i = rank;
            while(i<lines && pA[i][j].equals(BigInteger.ZERO)) i++;
            if(i<lines) {
                swap(pA, rank, i);
                for (i = (computeDim)?rank + 1:0; i < lines; i++) {
                    if(computeDim || i!=rank) {
                        t=pA[i][j];
                        for (int m = 0; m < columns; m++)
                            pA[i][m] = pA[i][m].multiply(pA[rank][j]).subtract(t.multiply(pA[rank][m]));
                    }
                }
                rank++;
            }
            else
                lastDependantColumn=j;
        }

    }



    private BigInteger diagProduct(BigInteger[][] pA,int l){
        BigInteger prod=BigInteger.ONE;
        for(int i=0;i<l;i++)
            prod=prod.multiply(pA[i][i]);
        return prod;
    }

    public BigInteger getBeta() {
        return x[columns-1];
    }

    public BigInteger [] getSolution(){
        return x;
    }

    private void solve(BigInteger[][] pA){
        int i,l=lastDependantColumn;
        if(x==null) {
            x = new BigInteger[columns];
            x[l] = diagProduct(pA, l);
            for (i = l - 1; i >= 0; i--)
                x[i] = pA[i][l].negate().multiply(x[l]).divide(pA[i][i]);
            for (i = l + 1; i < columns; i++)
                x[i] = BigInteger.ZERO;
            BigInteger pgcd = pgcdVector(x);
            if (!pgcd.equals(BigInteger.ZERO))
                divideVectorByPgcd(x, pgcd);
        }
    }

    public void inverse() {
        for(int i=0;i<x.length;i++)
            x[i]=x[i].negate();
    }


    public static int getRank(List<Point> points){
        GaussianEliminationLite gel=new GaussianEliminationLite(points);
        return gel.getRank();
    }

    // swap rows A[i][] and A[j][] in 2D array A[][]
    private static void swap(BigInteger[][] A, int i, int j) {
        BigInteger[] temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }


}
