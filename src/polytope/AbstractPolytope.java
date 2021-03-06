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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractPolytope implements Polytope{

    /**
     * Print the list of extremal point of the polytopes
     * @param c is a consumer that defines the way a point is printed.
     * @return a String containing the list of extremal points.
     */
    private String printListOfPoints(BiConsumer<Point,StringBuilder> c){
        StringBuilder sb=new StringBuilder();
        Collection<Point> points= getPoints();
        for(Point p:points)
            c.accept(p,sb);
        return sb.toString();
    }

    /**
     * Print the list of extremal points, where a point's coordinate is delimited with parenthesis.
     * @return a string containing the list of extremal points, where a point's coordinate is delimited with parenthesis.
     */
    @Override
    public String toString() {
        return printListOfPoints(Point::addToPrint);
    }

    /**
     * Print the list of extremal points, with a carriage return after each point.
     * @return a string containing the list of extremal points, with a carriage return after each point.
     */  private String toFile() {
        return printListOfPoints(Point::addToFile);
    }

    /**
     * Print the list of extremal points into a file
     * @param filename is the path of the file.
     */
    public void printToFile(String filename){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.print(toFile());
        //writer.println(getPoints().iterator().next());
        writer.close();
    }

}
