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
import powertreedesigner.device.components.electricComponents.Converter;
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class ConverterSwitch extends Converter {  
    private double res, iQst, vRev, rRev, vMin;

    public ConverterSwitch() {
        super("Switch");

        addSetting(new Setting("res", new ParameterDouble(0.05,"Ohm"), "the on resistance"));
        addSetting(new Setting("rRev", new ParameterDouble(1,"Ohm"), "the parassitic diode forward resistance"));
        addSetting(new Setting("vRev", new ParameterDouble(1,"V"), "the parassitic diode forward voltage"));
        addSetting(new Setting("vMin", new ParameterDouble(2,"V"), "the minimum operative input voltage"));
        addSetting(new Setting("iQst", new ParameterDouble(1.e-3,"A"), "the current required to supply the switch circuitry"));
        addSetting(new Setting("iMax", new ParameterDouble(10.,"A"), "switch maximum forward current used only for warnings (not for simulation)"));                
        
        try {
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",res", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in ConverterSwitch class");
        } 
        
    }

    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition(); 
        res = getParameter("res").getDouble();
        if (res <= 0.) throw new ParsingException ("To converge the simulation requires a resistance > 0. while was found "+res+" at "+getLabel());        
        rRev = getParameter("rRev").getDouble();
        vRev = getParameter("vRev").getDouble();
        vMin = getParameter("vMin").getDouble();
        iQst = getParameter ("iQst").getDouble();
    }
        
    @Override
    public void stepForward() {
        final double dv = getGateVoltage(0, 1);
        final double vi = getGateVoltage(0);
        
        if (dv < 0) {
            if (-dv > vRev) {
                setGateCurrent(0, (dv+vRev)/rRev); 
                setGateCurrent(1, -(dv+vRev)/rRev);
            } else {
                setGateCurrent(0, vi > vMin ? iOff : 0.); 
                setGateCurrent(1, 0.);                
            }
            return;
        }
        
        if (! enable) {
            setGateCurrent(0, vi > vMin ? iOff : 0.); 
            setGateCurrent(1, 0.);   
            return;
        }
        
        if (vi < vMin) {
            setGateCurrent(0, 0.); 
            setGateCurrent(1, 0.);   
            return;
        }    
        
        //direct biased
        final double i = dv/res;

        setGateCurrent(0, i+iQst); 
        setGateCurrent(1, -i);              
    }

    @Override
    public BlackBox makeNewParseable() {
        return new ConverterSwitch();
    }

    @Override
    public String getWarning() {        
        String warnings = "";        
        if (getGateVoltage(0, 1)< 0) warnings += "\n-The switch is not forward biased";
        if (getGateVoltage(0) < vMin) warnings += "\n-The input voltage is too low to turn on";
        if (getGateCurrent(0) > getParameter("iMax").getDouble())  warnings += "\n-The forward current is exceeding maximum allowable ";        
        if (warnings.length()==0) return null;
        return "On "+getLabel()+" were found:"+warnings;       
    }    
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = is a switch\n";
    }      
    
}
