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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import powertreedesigner.device.Device;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.commands.simulator.history.VectorOfDouble;
import powertreedesigner.device.components.electricComponents.parameter.ParameterVectorOfDouble;
import powertreedesigner.device.parser.linesToParse.TokenList;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Calculator {
    private final static HashMap<String, MathFunction> FUNCTIONS;    
    
    static {
        FUNCTIONS = new HashMap<>();
        MathFunction[] keywords = {
            new MFDrop(), new MFSwap(), new MFDuplicate(),
            new MFNegate(), new MFInvert(),
            new MFPlus(), new MFMinus(), 
            new MFTimes(), new MFDivide(), new MFReminder(),
            new MFIncrement(), new MFDecrement(),
            new MFEqual(), new MFNotEqual(), new MFLessThan(), new MFLessOrEqual(), new MFGreaterThan(), new MFGreaterThanOrEqual(),
            new MFAnd(), new MFOr(), new MFNot(), new MFXOr(),
            new MFJoin(), new MFJoinAll(), new MFGet(),
            new MFSquare(), new MFSquareRoot(), new MFPower(),
            new MFAbs(), new MFSign(),
            new MFSumUp(), new MFSize(),
            new MFVectorLinear(), new MFVectorStep(),
            new MFFloor(), new MFCeil()
        };
        
        for (MathFunction f : keywords)
            if (FUNCTIONS.put(f.getName(), f) != null) throw new RuntimeException ("Two commands have the same name");             
    }
    
    private final Stack<VectorOfDouble> stack ;
    
    public Calculator () {
        stack = new Stack<>();    
    }

    public VectorOfDouble calculate(TokenList prog, Device d) throws ParsingException {        
        String command ;
        while (prog.hasNext()) {            
            command = prog.peekNext();
            if (command.equals("]")) {
                prog.next();
                break;
            }
            if (command.endsWith("(") || !(command.equals("[") || Character.isLetterOrDigit(command.charAt(command.length()-1)))) {
                prog.next();
                executeFunction (command);
                continue;
            }            

            stack.push(ParameterVectorOfDouble.parseVectorOfDouble(prog, d, ""));
        }
        if (stack.isEmpty()) throw new CalculationException ("the stack is empy, cannot save variable");
        if (stack.size() > 1) System.out.println("Warning some values are left unused on the stack ");

        return stack.pop();
    }

    private void executeFunction(String fname) throws ParsingException {
        if (! FUNCTIONS.containsKey(fname)) throw new ParsingException ("unrecognized function "+fname+" on calculator program");
        FUNCTIONS.get(fname).calculate(stack);
    }

    public static String getHelp() {
        LinkedList<String> help = new LinkedList<>();
        for (MathFunction mf : FUNCTIONS.values()) help.add(mf.getHelp());
        Collections.sort(help);
        StringBuilder hlp = new StringBuilder();
        for (String s : help) hlp.append(s).append('\n');
        return hlp.toString();
    }
    
}
