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
public class DrainPower extends Drain {  
    private double pTgt, vMin, iMax;

    public DrainPower() {
        super("Power");
        
        addSetting(new Setting("pTgt", new ParameterDouble(0.1,"W"), true, "the target drained power"));
        addSetting(new Setting("iMax", new ParameterDouble(1.,"A"), "the maximum drained current"));
        addSetting(new Setting("vMin", new ParameterDouble(2.,"V"), "the minimum operative voltage"));
        try {
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",pTgt", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in ConverterBoost class");
        } 
        
    }

    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition(); 
        pTgt = getParameter("pTgt").getDouble();
        iMax = getParameter("iMax").getDouble();
        vMin = getParameter("vMin").getDouble();
    }
        
    @Override
    public void stepForward() {
        double vi = getGateVoltage(0);
        
        if (! enable)  {
            setGateCurrent(0, vi > vMin ? iOff : 0.);
            return;
        }
                                        
        if (vi < 0.)  {//reverse polarity
            setGateCurrent(0, 0.);
            return;
        }        
        
        double i = pTgt/vi;        
        
        if (i > iMax) {//over current protection
            i = iMax;
        }
        
        if (vi < vMin) {//not enough voltage
            i *= vi/vMin;
        }
        
        setGateCurrent(0, i);
    }

    @Override
    public BlackBox makeNewParseable() {
        return new DrainPower();
    }
    
    @Override
    public String getWarning() {
        String warnings="";
        if (getGateVoltage(0) <= vMin) warnings += "\n-Input Voltage too low to turn on";
        if (getGateCurrent(0) >= iMax) warnings += "\n-Input current to high, switched to current protection mode";
        if (warnings.length()==0) return null;
        return "On "+getLabel()+" were found:"+warnings;      
    }       
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = is a constant power drain\n";
    }      
    
}
