package toolkit;


/******************************************************************************
 *  Compilation:  javac GaussianEliminationLite.java
 *  Execution:    java GaussianEliminationLite
 *  Dependencies: StdOut.java
 *
 *  Gaussian elimination with partial pivoting.
 *
 *  % java GaussianEliminationLite
 *  -1.0
 *  2.0
 *  2.0
 *
 ******************************************************************************/

import polytope.Point;

import java.util.ArrayList;

/**
 *  The {@code GaussianEliminationLite} is a modification from a code from Robert Sedgewick Kevin Wayne
 *  The original code could only perform the gauss elimination for square matrices.
 *  For additional documentation, see
 *  <a href="https://algs4.cs.princeton.edu/99scientific">Section 9.9</a>
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Julien David
 */
public class GaussianEliminationLite {
    private static final double EPSILON = 1e-10;
    private ArrayList<Integer> affinelyIndependant=null;
    private int columns;
    private int lines;
    private double [][] A;
    //private double [] b;
    private double [] x=null;
    private double beta;
    int lastDependantColumn;

    public void printMatrix(){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<lines;i++) {
            for (int j = 0; j < columns; j++) {
                sb.append(A[i][j]);
                sb.append(" ");
            }
            sb.append("\n");
        }
    }

    /**
     * Returns the unique solution <em>x</em> to the specified linear system of
     * equation <em>Ax</em> = <em>b</em>, where <em>A</em> is a nonsingular matrix.
     * Warning: this implementation mutates the arguments <em>A</em> and <em>b</em>.
     *
     * @param  pA the nonsingular <em>n</em>-by-<em>n</em> constraint matrix
     * @return the unique solution <em>x</em> to the linear system of equations
     *         <em>Ax</em> = <em>b</em>
     * @throws ArithmeticException if the matrix is singular or nearly singular
     */
    public void GaussJordan(double[][] pA) {
        A=pA;
//        columns = A[0].length;
//        lines=A.length;
        affinelyIndependant=new ArrayList<>();
        lastDependantColumn=columns-1;

        for (int p = 0; p < columns && affinelyIndependant.size()<lines; p++) {

            // find pivot row and swap
            int line=affinelyIndependant.size();
            int max = line;
            for (int i = line + 1; i < lines; i++) {
                if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                    max = i;
                }
            }
            swap(A, line, max);

            // singular or nearly singular

            if(Math.abs(A[line][p])>0) {
                if (Math.abs(A[line][p]) <= EPSILON) {
                    System.out.println("Matrix contains values whose precision we cannot handle");

                    //throw new ArithmeticException("Matrix contains values whose precision we cannot handle");
                }
                affinelyIndependant.add(p);

                // pivot within A and b
                for (int i = 0; i < lines; i++) {
                    if(i!=line) {
                        double alpha = A[i][p] / A[line][p];
                        for (int j = p; j < columns; j++) {
                            A[i][j] -= alpha * A[line][j];
                        }
                    }
                }
            }
            else
                lastDependantColumn=p;
        }


    }

    /*public GaussianEliminationLite(double[][] pA, double[] pb) {
        GaussJordan(pA,pb);
    }*/

    public GaussianEliminationLite(ArrayList<Point> points) {
        columns = points.size();
        lines=points.get(0).getDimension()+1;
        double [][]pA=new double[lines][columns];
        for(int i=0;i<(lines-1);i++)
            for(int j=0;j<columns;j++)
                pA[i][j]=points.get(j).getCoordinate(i);
        for(int j=0;j<columns;j++)
            pA[lines-1][j]=1;

        GaussJordan(pA);
    }
    public GaussianEliminationLite(ArrayList<Point> points,ArrayList<Integer> selection) {
        int i,j;
        columns = points.get(0).getDimension()+1;
        lines=Math.min(selection.size(),columns-1);
        if(lines<columns-1) {
            StringBuilder sb=new StringBuilder();
            sb.append("Nombre de points "+points.size()+" et de sélectionnés "+selection.size()+"\n");
            for(Point p2:points)
                sb.append(p2+ "\n");
            for(i=0;i<points.get(0).getDimension();i++) {
                for (j = 0; j < points.size(); j++)
                    sb.append(points.get(j).getCoordinate(i) + " ");
                sb.append("\n");
            }
            sb.append("Insuffisant number of points" + columns + " " + lines+"\n");
            throw new RuntimeException(sb.toString());
        }
        double [][]pA=new double[lines][columns];
        for(i=0;i<lines;i++) {
            for (j = 0; j < (columns-1); j++)
                pA[i][j] = points.get(selection.get(i)).getCoordinate(j);
            pA[i][j]=-1;
        }
        GaussJordan(pA);
    }

    private double diagProduct(int l){
        double prod=1.;
        for(int i=0;i<l;i++)
            prod*=A[i][i];
        return prod;
    }

    public double getBeta() {
        if(lastDependantColumn==columns-1)
            return beta;
        return 0;
    }


    public double[] solve() {
        // back substitution
        int i,l=lastDependantColumn;
        beta=diagProduct(l);
        if(x==null) {
            x = new double[lines];
            if(lastDependantColumn!=lines)
                x[l]=beta;
            for (i = l-1; i >=0; i--)
                x[i] =-A[i][l] * beta / A[i][i];
            for(i=l+1;i<lines;i++)
                x[i]=0;
        }
        /*System.out.print(" alpha : ");
        for(double y:x)
            System.out.print(y+ " ");
        System.out.println("");*/
        return x;
    }

    public ArrayList<Integer> getAffinelyIndependant() {
        return affinelyIndependant;
    }

    public int getRank(){
        return affinelyIndependant.size();
    }

    // swap entries a[i] and a[j] in array a[]
    private static void swap(double[] a, int i, int j) {
        double temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    // swap rows A[i][] and A[j][] in 2D array A[][]
    private static void swap(double[][] A, int i, int j) {
        double[] temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }


}
