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
public class MFVectorStep extends MathFunction {

    public MFVectorStep() {
        super("vectorStep", 3, "create a vector from start value, with step value up to end value");
    }

    @Override
    public void calculate(Stack<VectorOfDouble> stack) throws CalculationException {
        final double end = stack.pop().getData().getFirst();        
        final double step = (stack.pop().getData().getFirst());
        double begin = stack.pop().getData().getFirst();        
        LinkedList<Double> res = new LinkedList<>();            
        res.add(begin);
        while (begin <= end) res.add(begin+=step);
        res.add(end);
        stack.push(new VectorOfDouble(res, ""));
    }

    @Override
    public double calculate(double[] inp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }        
}
