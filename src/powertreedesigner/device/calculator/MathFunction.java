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
package powertreedesigner.device.calculator;

import powertreedesigner.device.exception.CalculationException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import powertreedesigner.device.commands.helper.Helpable;
import powertreedesigner.device.commands.simulator.history.VectorOfDouble;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public abstract class MathFunction implements Helpable {
    private final String name, help;
    private final int args;

    public MathFunction(String name, int args, String help) {
        this.name = name+(Character.isLetter(name.charAt(0)) ? "(" : "");
        this.help = help;
        this.args = args;
    }

    public String getName() {
        return name;
    }    

    @Override
    public String getHelp() {
        return "-Command "+name+(args>0?" takes "+args+" arguments from the stack to ":" ")+help;
    }
    
    public abstract double calculate (double[] inp);
    
    public void calculate (Stack<VectorOfDouble> stack) throws CalculationException {
        if (stack.size() < args) throw new CalculationException("the stack does not have enough elements for input of the function "+name+" reguiring "+args+" arguments");
        VectorOfDouble res = new VectorOfDouble("");
        
        ArrayList<LinkedList<Double>> v = new ArrayList<>(args);
        for (int i=0; i<args; ++i) v.add(null);                                 //set the initial capacity
        for (int i = args-1 ; i >= 0; --i) v.set(i, stack.pop().getData());     //load in the reverse order
        
        ArrayList<Iterator<Double>> iv = new ArrayList<>(args);
        
        for (int i = 0 ; i < args; ++i) iv.add(v.get(i).iterator());

       
        boolean[] vHasNext = new boolean[args];
        for (int i = 0 ; i < args; ++i) vHasNext[i] = iv.get(i).hasNext();
        
        double[] inp = new double[args];
        
        while (hasNextOr(vHasNext)) {
            
            while (hasNextAnd(iv)) {
                for (int i = 0 ; i < args; ++i) inp[i] = iv.get(i).next();
                res.addPoint(calculate(inp));
            }            
            
            for (int i = 0 ; i < args; ++i) 
                if (! iv.get(i).hasNext()) {
                    iv.set(i, v.get(i).iterator());
                    vHasNext[i]= false;
                }            
        }
        
        stack.push(res);
        
    }       
    
    private boolean hasNextOr (boolean[] data) {
        for (boolean d : data) if (d) return true;
        return false;
    }
    
    private boolean hasNextAnd (ArrayList<Iterator<Double>> data) {
        for (Iterator<Double> id : data) if (! id.hasNext()) return false;
        return true;
    }    
    
}
