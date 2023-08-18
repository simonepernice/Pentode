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
package powertreedesigner.device.components;

import powertreedesigner.device.components.electricComponents.BlackBox;
import powertreedesigner.device.components.electricComponents.Drain;
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class DrainResistor extends Drain {  
    private double res, vMin;


    public DrainResistor() {
        super("Resistor");
        
        addSetting(new Setting("res", new ParameterDouble(100.,"Ohm"), true, "the component resistance"));
        addSetting(new Setting("vMin", new ParameterDouble(0.,"V"), "the minimum operative voltage"));
        addSetting(new Setting("iMax", new ParameterDouble(10.,"A"), "maximum forward current used only for warnings (not for simulation)"));                
        
        try {
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",res", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in ConverterResistor class");
        } 
        
    }

    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition(); 
        res = getParameter("res").getDouble();
        if (res <= 0.) throw new ParsingException ("To converge the simulation requires a resistance > 0. while was found "+res+" at "+getLabel());                
        vMin = getParameter("vMin").getDouble();
    }
        
    @Override
    public void stepForward() {                
        if (! enable) {
            setGateCurrent(0, 0.);
            return;
        }
        
        double vi = getGateVoltage(0);
        
        if (vi < vMin) {
            setGateCurrent(0, 0.);
            return;
        }
        
        setGateCurrent(0, (vi-vMin) /res);
    }

    @Override
    public BlackBox makeNewParseable() {
        return new DrainResistor();
    }
    
    @Override
    public String getWarning() {
        String warnings = "";  
        if (getGateVoltage(0) < vMin) warnings += "\n-Input voltage too low to turn on ";
        if (getGateCurrent(0) > getParameter("iMax").getDouble()) warnings += "\n-The input current is exceeding the maximum allowable";
        if (warnings.length()==0) return null;
        return "On "+getLabel()+" were found:"+warnings;   
    }        
        
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = is a resistor\n";
    }      
}
