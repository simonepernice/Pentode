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
public class DrainCurrent extends Drain {  
    private double iTgt, vMin;

    public DrainCurrent() {
        super("Current");
        
        addSetting(new Setting("iTgt", new ParameterDouble(0.1,"A"), true, "the target constant current drained"));
        addSetting(new Setting("vMin", new ParameterDouble(1.,"V"), "the minimum operative voltage, below this level the curred linearly reduce to zero"));
        addSetting(new Setting("vMax", new ParameterDouble(50.,"V"), "maximum applicable voltage used only for warnings (not for simulation)"));                        
        
        try {
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",iTgt", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in ConverterBoost class");
        } 
        
    }

    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition(); 
        iTgt = getParameter("iTgt").getDouble();
        vMin = getParameter("vMin").getDouble();
    }
        
    @Override
    public void stepForward() {
        final double vi = getGateVoltage(0);
        
        if (! enable) {
            setGateCurrent(0, vi > vMin ? iOff : 0);
            return;
        }
        
        if (vi < 0.)  {//reverse polarity
            setGateCurrent(0, 0.);
            return;
        }        
                
        if (vi < vMin) {//not enough voltage
            setGateCurrent(0, iTgt*vi/vMin);
            return;
        }        
        
        setGateCurrent(0, iTgt);
    }

    @Override
    public BlackBox makeNewParseable() {
        return new DrainCurrent();
    }
    
    @Override
    public String getWarning() {
        String warnings = "";           
        if (getGateVoltage(0) <= vMin) warnings += "\n-Voltage too low to turn on ";
        if (getGateVoltage(0) > getParameter("vMax").getDouble()) warnings += "\n-Voltage too high";
        if (warnings.length()==0) return null;
        return "On "+getLabel()+" were found:"+warnings;       
    }       
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = is constant current drain\n";
    }      
}
