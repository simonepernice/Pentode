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
public class ParameterInteger extends Parameter {
    private final int val;

    public ParameterInteger(int val) {
        this.val = val;
    }
    
    @Override
    public String getTypeName() {
        return "integer";
    }
        
    @Override
    public int getInteger() {
        return val;
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
            if (hc.size() > 1) System.out.println("Warning the element "+tokens+" is a vector only the last value is used");
            return new ParameterInteger((int) hc.getLast().doubleValue());            
        }
        
        int v;

        try {
            v = Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            throw new ParsingException("it was expected integer while was found "+value);
        }
        return new ParameterInteger(v);
    }

    @Override
    public String getPrintString() {
        return Integer.toString(val);
    }            
}
