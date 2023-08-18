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

import powertreedesigner.device.commands.simulator.Integrator;
import powertreedesigner.device.components.electricComponents.BlackBox;
import powertreedesigner.device.components.electricComponents.Converter;
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class ConverterInductor extends Converter {  
    private double res, ind;
    private final Integrator currInteg;

    public ConverterInductor() {
        super("Inductor");

        addSetting(new Setting("res", new ParameterDouble(0.,"Ohm"), "inductance series resistance"));
        addSetting(new Setting("ind", new ParameterDouble(100e-6,"H"), true, "inductance value"));
        addSetting(new Setting("iBgn", new ParameterDouble(0.,"A"), "simulation starting current"));
        addSetting(new Setting("iMax", new ParameterDouble(10.,"A"), "inductor maximum forward current used only for warnings (not for simulation)"));
        
        try {
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",ind", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in ConverterInductor class");
        } 
        
        currInteg = new Integrator ();
    }

    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition(); 
        res = getParameter("res").getDouble();
        ind = getParameter ("ind").getDouble();
        currInteg.setY0(getParameter ("iBgn").getDouble());
    }
        
    @Override
    public void stepForward() {
        final double vio = getGateVoltage(0, 1);
        
        currInteg.addDyDt((vio-currInteg.getYn()*res)/ind);        
        setGateCurrent(0, currInteg.getYnp1());
        setGateCurrent(1, -currInteg.getYnp1());
    }

    @Override
    public void stepBackward() {
        currInteg.stepBackwardY();
    }
        
    @Override
    public BlackBox makeNewParseable() {
        return new ConverterInductor();
    }

    @Override
    public String getWarning() {  
        String warnings = "";                    
        if (getGateCurrent(0) > getParameter("iMax").getDouble())  warnings += "\n-The forward current is exceeding maximum allowable ";
        if (warnings.length()==0) return null;
        return "On "+getLabel()+" were found:"+warnings;     
    }
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = is an inductor modeled by an ideal inductor in series to a resistor.\n";
    }      
    
}
