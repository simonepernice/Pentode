/*
 * Copyright (C) 2017 Simone Pernice pernice@libero.it
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package powertreedesigner.device.commands.simulator;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Integrator {
    public static final String[] METHOD = {"rungekutta3", "euler", "midpoint", "rungekutta4bis3/8", "rungeKutta4", "ralston", "heun"};
    private final static double TSTEP = 10;
    
    private static double[][] dvodtCoeff;
    private static double[]   dtCoeff;

    private static double tn;               //current point
    private static double tnp1;             //point at the next step/substep 
    private static double dt, subDt;        //time step and sub time step to use for sub step    
    private static int subStep, subStepNm1;             //current substep    
    private static double dtMin, dtMax;
    
    static {
        setIntegrationAlgorithm(4);
    }
    
    public static void setIntegrationAlgorithm (int alg) {
        switch (alg) {
            case 1: //euler
                dvodtCoeff = new double[][] {
                    {1.}
                };
                dtCoeff = new double[] {1.};
                break;
            case 6: //heun
                dvodtCoeff = new double[][] {
                    {1.},
                    {1./2.,  1./2.}
                };
                dtCoeff = new double[] {1., 1.};
                break;
            case 2: //midpoint
                dvodtCoeff = new double[][] {
                    {1./2.},
                    {0.,     1.}
                };
                dtCoeff = new double[] {1./2., 1.};
                break;           
             case 5: //ralston
                dvodtCoeff = new double[][] {
                    {2./3.},
                    {1./4.,     3./4.}
                };
                dtCoeff = new double[] {2./3., 1.};
                break;           
            case 0: //rungekutta3
                dvodtCoeff = new double[][] {
                    {0.5},
                    {-1.,     2.},
                    {1./6., 2./3.,  1./6.}
                };
                dtCoeff = new double[] {1./2., 1., 1.};
                break;                    
            case 4: //rungekutta4
                dvodtCoeff = new double[][] {
                    {0.5},
                    {0.,      0.5},
                    {0.,         0.,     1.},
                    {1./6., 1./3.,  1./3.,  1./6.}
                };
                dtCoeff = new double[] {1./2., 1./2., 1., 1.};
                break;                    
            case 3: //rungekutta4 3/8
                dvodtCoeff = new double[][] {
                    {1./3.},
                    {-1./3.,    1.},
                    {1.,        -1.,     1.},
                    {1./8., 3./8.,  3./8.,  1./8.}
                };
                dtCoeff = new double[] {1./3., 2./3., 1., 1.};
                break;                    
        }
        if (dtCoeff.length != dvodtCoeff.length) throw new RuntimeException ("The coefficient definition is wrong for the integrator");
        if (dtCoeff[dtCoeff.length-1] != 1.) throw new RuntimeException ("The last time coefficient definition is wrong for the integrator");
        for (int i=0;i<dtCoeff.length;++i) {
            double s=0.;
            for(double e : dvodtCoeff[i]) s += e; 
            if (Math.abs(s-dtCoeff[i])>0.0001) throw new RuntimeException ("The time coefficient "+i+" does not match to the dv/dt coefficients");
        }
        subStepNm1 = dtCoeff.length-1;        
    }    
        
    public static void resetTime (double dtMin, double dtMax, double dt) {        
        tn = tnp1 = 0.;
        Integrator.dtMin = dtMin;
        Integrator.dtMax = dtMax;      
        Integrator.dt = dt;
        resetSubStep();                  
    }        
    
    public static void nextStep() {
        tn=tnp1;         
        resetSubStep ();                                            
    }  
    
    public static boolean shorterTimeStep () {
        if (dt < dtMin) return true;
        dt /= TSTEP;        
        return false;
    }
    
    public static boolean longerTimeStep () {
        if (dt >= dtMax) return true;
        dt *= TSTEP;                
        return false;
    }
    
    public static void stepBackwardT () {
        tnp1=tn;
        resetSubStep ();
    }
    
    private static void resetSubStep () {        
        subStep = 0;
        subDt = fastMultiply(dtCoeff[subStep], dt);           
    }  
    
    public static double getTnp1 () {
        return tnp1;
    }    
    
    public static double getTn () {
        return tn;
    }    
    
    public static boolean hasMoreSubStep () {
        return subStep < dtCoeff.length;
    }
    
    public static void nextSubStep () {        
        tnp1=tn+subDt;      
        ++subStep; 
        if (subStep<dtCoeff.length) subDt = fastMultiply(dtCoeff[subStep], dt);                               
    }
        
    private double[] dYdT;
    private double yn;               //current point
    private double ynp1;              //point at the next step/substep 
    private double lastChange;
    
    public Integrator () {
        dYdT = new double[dtCoeff.length];
    }
    
    public double getLastChange () {
        return lastChange;
    }
    
    public void setY0 (double y) {    
        yn = ynp1 = y;        
        if (dYdT.length != dtCoeff.length) dYdT = new double[dtCoeff.length];  //if the integration algorithm was changed may need different size of stored derivatives
    }
    
    public void stepBackwardY () {
        ynp1 = yn;
    }
    
    public void addDyDt (double dydt) {        
        dYdT[subStep]=dydt;
        double dy = 0.;
        int i=0;
        for (double c : dvodtCoeff[subStep]) dy += fastMultiply(c, dYdT[i++]);
        ynp1=yn+dy*dt;     
        if (subStep == subStepNm1) {
            lastChange = Math.abs(yn-ynp1);
            yn = ynp1;
        }
    }
    
    public double getYnp1 () {
        return ynp1;
    }
    
    public double getYn() {
        return yn;
    }    
    
    private static double fastMultiply (double coeff, double val) {
        if (coeff == 0.) return 0.;
        if (coeff == 1.) return val;
        return coeff * val;        
    }
    
}
