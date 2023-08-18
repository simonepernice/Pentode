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
package powertreedesigner.device.components.electricComponents.parameter;

import java.util.LinkedList;
import powertreedesigner.device.Device;
import powertreedesigner.device.calculator.Calculator;
import powertreedesigner.device.commands.simulator.history.VectorOfDouble;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.parser.linesToParse.TokenList;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class ParameterDouble extends Parameter {
    private final double val;
    private final String mu;
        
    public ParameterDouble(double value, String measurementUnit) {
        this.val = value;
        this.mu = measurementUnit.trim();
    }

    @Override
    public double getDouble() {
        return val;
    }

    @Override
    public String getMeasurementUnit() {
        return mu;
    }
        
    @Override
    public String getTypeName() {
        return "double";
    }

    @Override
    public Parameter parse(TokenList tokens, Device d) throws ParsingException {
        VectorOfDouble sh = null;
        String value = tokens.next();
        if (value.equals("[")) {//the parameter is a calculation
            sh = (new Calculator()).calculate(tokens, d);            
        } else if (Character.isLetter(value.charAt(0))) {//the parameter is a variable
            sh = d.getHistoryComponent(value).getHistory();
        }
        
        if (sh != null) {
            LinkedList<Double> hc = sh.getData();
            if (hc.size() > 1) System.out.println("Waring the element "+tokens+" is a vector only the last value is used");
            return new ParameterDouble(hc.getLast(), mu);            
        }
        
        if (value.endsWith(mu)) value = value.substring(0, value.length()-mu.length()); //remove the ending measurement unit
        
        double v;
        try {            
            if (Character.isLetter(value.charAt(value.length()-1))) v = EngNotation.parse (value);
            else v = Double.parseDouble(value);
        } catch (Exception nfe) {
            throw new ParsingException("it was expected a real number with option measurement unit "+mu+" instead was found "+value);
        }
        return new ParameterDouble(v, mu);
    }   

    @Override
    public String getPrintString() {
        return EngNotation.convert(val)+mu;
    }        
}
