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

import polytope.AbstractPolytope;
import random.FirstMarkovChain;

public class AverageNumberOfPoints {
    public static void main(String [] args){
        AbstractPolytope lp;
        int numberOfExperiments=1;
        int numberOfSizes=100;
        int numberOfSteps=1000000;
        double [][] result=new double[numberOfSizes+1][4];
        for(int k=2;k<=numberOfSizes;k++){
            FirstMarkovChain fmc=new FirstMarkovChain(3,k);
            fmc.initialize();
            long acc=0;
            for(int j=0;j<numberOfSteps;j++) {
                lp = fmc.nextStep();
                acc+=lp.getPoints().size();
                if(j==1000) result[k][0]=acc/1000.;
                else if(j==10000) result[k][1]=acc/10000.;
                else if(j==100000) result[k][2]=acc/100000.;
            }
            result[k][3]=acc/1000000.;

            for(int i=0;i<4;i++)
                System.out.println(k+" "+1000*Math.pow(10,i)+" "+result[k][i]);
            //System.out.println(k+" "+acc/(double)numberOfSteps);

        }
    }
}
