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
package powertreedesigner.device.calculator;

import powertreedesigner.device.exception.CalculationException;
import java.util.LinkedList;
import java.util.Stack;
import powertreedesigner.device.commands.simulator.history.VectorOfDouble;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class MFVectorLinear extends MathFunction {

    public MFVectorLinear() {
        super("vectorLinear", 3, "create a vector from start value, number of points, end value");
    }

    @Override
    public void calculate(Stack<VectorOfDouble> stack) throws CalculationException {
        final double end = stack.pop().getData().getFirst();        
        int n = (int) (stack.pop().getData().getFirst()-1.);
        double begin = stack.pop().getData().getFirst();        
        final double step = (end-begin)/n;
        LinkedList<Double> res = new LinkedList<>();            
        res.add(begin);
        --n;
        for (int i=0; i<n; ++i) res.add(begin+=step);
        res.add(end);
        stack.push(new VectorOfDouble(res, ""));
    }

    @Override
    public double calculate(double[] inp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }        
}
