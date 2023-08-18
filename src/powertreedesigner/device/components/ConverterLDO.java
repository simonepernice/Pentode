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
public class ConverterLDO extends Converter {  
    private double vTgt, iMax, rLdRg, vDrp, iQst, vRev, rRev, rFrw, lnRgl, pMax;

    public ConverterLDO() {
        super("LDO");
        
        addSetting(new Setting("vTgt", new ParameterDouble(3.3,"V"), true, "the target output voltage"));
        addSetting(new Setting("iMax", new ParameterDouble(1,"A"), "the maximum output current"));
        addSetting(new Setting("pMax", new ParameterDouble(10,"W"), "the maximum power dissipable"));
        addSetting(new Setting("rLdRg", new ParameterDouble(0.1,"Ohm"), "the load regulation expressed as resistance"));
        addSetting(new Setting("vDrp", new ParameterDouble(0.2,"V"), "the minimum required voltage drop"));
        addSetting(new Setting("iQst", new ParameterDouble(1.e-3,"A"), "the device baseline supply current"));
        addSetting(new Setting("rFrw", new ParameterDouble(0.1,"Ohm"), "the parassitic diode forward resistance"));
        addSetting(new Setting("rRev", new ParameterDouble(1,"Ohm"), "the parassitic diode reverse resistance"));
        addSetting(new Setting("vRev", new ParameterDouble(1,"V"), "the parassitic diode forward voltage"));
        addSetting(new Setting("lnRgl", new ParameterDouble(0.01,"%"), "the line regulation dVout/dVin in percentage"));
        
        try {
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",vTgt", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in ConverterLDO class");
        } 
        
    }

    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition(); 
        vTgt = getParameter("vTgt").getDouble();
        iMax = getParameter("iMax").getDouble();
        pMax = getParameter("pMax").getDouble();
        rLdRg = getParameter("rLdRg").getDouble();
        if (rLdRg <= 0.) throw new ParsingException ("To converge the simulation required a load regulation resistance > 0. while was found "+rLdRg+" at "+getLabel());        
        vDrp = getParameter("vDrp").getDouble();
        iQst = getParameter ("iQst").getDouble();
        rRev = getParameter("rRev").getDouble();
        rFrw = getParameter("rFrw").getDouble();
        vRev = getParameter("vRev").getDouble();    
        lnRgl = getParameter("lnRgl").getDouble()/100.;     
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
                double io = (dv+vRev)/rRev;
                setGateCurrent(0, io); 
                setGateCurrent(1, -io);
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
        
        if (vi < vDrp) {//device not operative too low input voltage
            setGateCurrent(0, iOff); 
            setGateCurrent(1, 0.);
            return;            
        }
        
        if (vi < vTgt+vDrp) {//insufficient input voltage
            double io = (dv-vDrp)/rFrw;
            if (io > iMax) io = iMax;            
            setGateCurrent(0, iQst+io); 
            setGateCurrent(1, -io);               
            return;
        }
        
        double io = (vTgt*(1.+(vi-vTgt)*lnRgl)-vo)/rLdRg;   // load and line regulation
        if (io > iMax) io = iMax;                           //exceeding maximum output current
        if (io * dv > pMax) io = pMax/dv;                   //exceeding maximum dissipable power
        setGateCurrent(0, iQst+io); 
        setGateCurrent(1, -io);                
    }

    @Override
    public BlackBox makeNewParseable() {
        return new ConverterLDO();
    }

    @Override
    public String getWarning() {        
        String warnings = "";               
        if (-getGateCurrent(1) >= iMax) warnings += "\n-Output current exceed maximum value ";
        if (-getGateCurrent(1) * (getGateVoltage(0, 1)) >= pMax*0.999) warnings += "\n-Dissipated power exceed maximum value ";
        if (getGateVoltage(0) < vTgt + vDrp) warnings += "\n-The input voltage is too low to regulate outpu";
        if (getGateVoltage(1) > vTgt) warnings += "\n-The output voltage is too high to turn on ";
        if (warnings.length()==0) return null;
        return "On "+getLabel()+" were found:"+warnings;  
    }
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = is a linear (low drop out) regulator\n";
    }      
}
