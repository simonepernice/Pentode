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

import powertreedesigner.device.Device;
import powertreedesigner.device.calculator.Calculator;
import powertreedesigner.device.commands.simulator.history.VectorOfDouble;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.parser.linesToParse.TokenList;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class ParameterVectorOfDouble extends Parameter {
    private final VectorOfDouble vod;
    
    public ParameterVectorOfDouble() {
        this("");
    }    
    
    public ParameterVectorOfDouble(String mu) {
        this.vod = new VectorOfDouble(mu);
    }    
        
    public ParameterVectorOfDouble(VectorOfDouble value) {
        this.vod = value;
    }
    
    @Override
    public VectorOfDouble getVectorOfDouble() {
        return vod;
    }    
        
    @Override
    public String getTypeName() {
        return "simulationHistory";
    }

    @Override
    public Parameter parse(TokenList param, Device d) throws ParsingException {
        VectorOfDouble sh = parseVectorOfDouble(param, d, vod.getMeasurementUnit());

        return new ParameterVectorOfDouble(sh);
    }   
    
    public static VectorOfDouble parseVectorOfDouble (TokenList tokens, Device d, String mu) throws ParsingException {
        VectorOfDouble sh;
        String param = tokens.next();
        
        if (param.equals("[")) {//the parameter is a calculation
            sh = (new Calculator()).calculate(tokens, d);            
            sh.setMeasurementUnit(mu);
            return sh;
        } 
        
        if (Character.isLetter(param.charAt(0))) {//the parameter is a variable
            sh = d.getSimulationHistory(param, tokens.hasNext(".") ? tokens.next(2) : null);
            sh.setMeasurementUnit(mu);
            return sh;            
        }

        sh = new VectorOfDouble(mu);
        
        if (mu.length() > 0 && param.endsWith(mu)) param = param.substring(0, param.length()-mu.length()); //remove the ending measurement unit
        
        double v;
        try {            
            if (Character.isLetter(param.charAt(param.length()-1))) v = EngNotation.parse (param);
            else v = Double.parseDouble(param);
        } catch (Exception nfe) {
            throw new ParsingException("it was expected a number instead was found "+param);
        }
        return sh.addPoint(v);        
    }

    @Override
    public String getPrintString() {
        return vod.getPrintString();
    }        

}
