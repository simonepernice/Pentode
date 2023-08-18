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
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class ConverterBoost extends ConverterSwitching {  
    private double iIMax, vFrw, iRev;
    private final static boolean DEBUG = false;

    public ConverterBoost() {
        super("Boost");
        
        addSetting(new Setting("iIMax", new ParameterDouble(10,"A"), "the maximum input current"));
        addSetting(new Setting("iRev", new ParameterDouble(1.e-6,"A"), "the diode reverse current"));
        addSetting(new Setting("vFrw", new ParameterDouble(0.5,"V"), "the diode forward voltage"));
    }

    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition(); 
        iIMax = getParameter("iIMax").getDouble();
        iRev = getParameter("iRev").getDouble();
        vFrw = getParameter("vFrw").getDouble();        
    }
        
    @Override
    public void stepForward() {
        final double vi = getGateVoltage(0);
        final double vo = getGateVoltage(1);
        final double dv = vi - vo;

        if (! enable || vi < vMin ||  //off -> works as diode 
           vo > vTgt || vi > vTgt)   {//stopped -> works as diode 
            double iGnd = vi < vMin  ? 0. : (enable ? iQst : iOff );
            if (dv > vFrw) {
                final double i = (dv-vFrw)/rSwt;
                setGateCurrent(0, i+iGnd); 
                setGateCurrent(1, -i);
                if (DEBUG) System.out.println("work as diode at t="+Integrator.getTn());
                return;
            }
            
            if (dv > 0) {
                setGateCurrent(0, iGnd); 
                setGateCurrent(1, 0.);
                if (DEBUG) System.out.println("is off at t="+Integrator.getTn());
                return;                              
            }
            
            if (DEBUG) System.out.println("reverse biased at t="+Integrator.getTn());
            setGateCurrent(0, -iRev-iGnd); 
            setGateCurrent(1, iRev);
            return;              
        }
        
        if (DEBUG) System.out.println("work boosting at t="+Integrator.getTn());
                
        double io = (vTgt*(1.+(vi-vMin)*lnRgl)-vo)/rLdRg;        
        double iSwPfm = (io > iPfm ? iSwt : iSwt * io/iPfm);
        double ii = io * (vo +rSwt*io)/vi + iQst + iSwPfm;
        
        if (ii > iIMax) {
            ii = iIMax;
            io = (-vo +Math.sqrt(vo*vo-4*rSwt*(iQst+iSwPfm-ii)*vi))/2/rSwt;//previous ii equation solved on io 
        }    
        
        setGateCurrent(0, ii); 
        setGateCurrent(1, -io);                
    }

    @Override
    public BlackBox makeNewParseable() {
        return new ConverterBoost();
    }

    @Override
    public String getWarning() {    
        String warnings = "";
        if (-getGateCurrent(0) >= iIMax) warnings += "\n-Input current exceed maximum value";
        if (getGateVoltage(0) < vMin) warnings += "\n-The input voltage is too low to turn on ";
        if (getGateVoltage(1) > vTgt) warnings += "\n-The output voltage is too high to turn on ";
        if (warnings.length()==0) return null;
        return "On "+getLabel()+" were found:"+warnings;
    }    
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = is a switching boost converter\n";
    }    
}
