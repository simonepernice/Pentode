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
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class ConverterBuck extends ConverterSwitching {  
    private double iOMax, vRev, rRev;

    public ConverterBuck() {
        super("Buck");
        
        addSetting(new Setting("iOMax", new ParameterDouble(4,"A"), "the maximum output current"));
        addSetting(new Setting("rRev", new ParameterDouble(1.,"Ohm"), "the high side parassitic diode forward resistance"));
        addSetting(new Setting("vRev", new ParameterDouble(1.,"V"), "the high side parassitic diode forward voltage"));
    }

    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition(); 
        iOMax = getParameter("iOMax").getDouble();
        rRev = getParameter("rRev").getDouble();
        vRev = getParameter("vRev").getDouble();       
    }
        
    @Override
    public void stepForward() {
        final double vi = getGateVoltage(0);
        final double vo = getGateVoltage(1);
        final double dv = vi - vo;
        
        if (! enable) {
            setGateCurrent(0, vi > 0 ? iOff : 0); 
            setGateCurrent(1, 0.);
            return;              
        }
        
        if (dv < 0) {//reverse biased
            if (-dv > vRev) {
                setGateCurrent(0, (dv+vRev)/rRev); 
                setGateCurrent(1, -(dv+vRev)/rRev);
            } else {
                setGateCurrent(0, vi > 0 ? iOff : 0.); 
                setGateCurrent(1, 0.);                
            }
            return;                     
        }
        
        if (vo > vTgt) {//overvolage stop regulating
            setGateCurrent(0, iQst); 
            setGateCurrent(1, 0.);
            return;
        }        
        
        if (vi < vMin) {//insufficient input voltage to turn on              
            setGateCurrent(0, iOff); 
            setGateCurrent(1, 0.);               
            return;
        }        
        
        if (vi < vTgt) {//insufficient input voltage, 100% dc
            double i = dv/rSwt;
            if (i > iOMax) i = iOMax;            
            setGateCurrent(0, iQst+i); 
            setGateCurrent(1, -i);               
            return;
        }
        
        double io = (vTgt*(1.+(vi-vTgt)*lnRgl)-vo)/rLdRg; //io is computed using load and line regulation
        if (io > iOMax) io = iOMax;    
        // duty cycle = (vo+rSw*io)/vi;
        // switching current = io > iPfm ? iSw : iSw * io/iPfm
        double ii = io * (vo +rSwt*io)/vi + iQst + (io > iPfm ? iSwt : iSwt * io/iPfm);
        setGateCurrent(0, ii); 
        setGateCurrent(1, -io);                
    }

    @Override
    public BlackBox makeNewParseable() {
        return new ConverterBuck();
    }

    @Override
    public String getWarning() {        
        String warnings = "";
        if (-getGateCurrent(1) >= iOMax) warnings += "\n-Output current exceed maximum value ";
        if (getGateVoltage(0) < vTgt) warnings += "\n-The input voltage is too low to turn on ";
        if (getGateVoltage(1) > vTgt) warnings += "\n-The output voltage is too high to turn on ";
        if (warnings.length()==0) return null;
        return "On "+getLabel()+" were found:"+warnings;        
    }
 
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = is a switching buck converter\n";
    }  
    
}
