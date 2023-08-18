/*
 * Copyright (C) 2016 Simone Pernice pernice@libero.it
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
public final class ProgressBar {
    private final int BARS = 20;
    private final double timeStep;
    private double tStamp;
    private int bar;    

    public ProgressBar(double tEnd) {
        timeStep = tEnd / BARS;
        tStamp = 0.;
        bar = 0;
        System.out.println();
    }
    
    public void printProgress (double t) {
        if (t > tStamp) {
            ++bar;

            printBars ();
            
            tStamp += timeStep;
        }        
    }
    
    private void printBars () {
        System.out.print ("[");
        int i;
        for (i=0; i< bar;  ++i) System.out.print ("=");
        for (   ; i< BARS; ++i) System.out.print (" ");
        System.out.print ("]\r");        
    }
    
    public void printDone () {
        bar = BARS;
        printBars();
        System.out.println();
        System.out.println();
    }
    
}
