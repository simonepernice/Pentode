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
import powertreedesigner.device.components.electricComponents.Source;
import powertreedesigner.device.components.electricComponents.parameter.EngNotation;
import powertreedesigner.device.components.electricComponents.parameter.ParameterBoolean;
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.components.electricComponents.parameter.ParameterReadOnly;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class SourceBattery extends Source {  
    private double v100, v90, v10, v0, iRes, iCap, eRes;
    private boolean rchrgbl;
    
    private double cpty, cpactyOvchg;
    private double k1, k2, k3;                      //battery vs. capacity linear coefficients
    private final Integrator charge, capCharge;     //battery charge and internal capacitor charge
    private double vCap, iBat, iOut, chrg;
    private boolean rechargeable;

    public SourceBattery() {
        super("Battery");

        addSetting(new Setting("cpty", new ParameterDouble(3600.,"C"),      "The battery capacity expressed in Culomb, it is usually provided in Ah: 1Ah=3600C"));
        
        addSetting(new Setting("v100", new ParameterDouble(4.2,"V"),       "The battery voltage at 100% maximum (full charge) capacity"));
        addSetting(new Setting("v90", new ParameterDouble(3.9,"V"),        "The battery voltage at 90% capacity"));
        addSetting(new Setting("v10", new ParameterDouble(3.5,"V"),        "The battery voltage at 10% capacity"));
        addSetting(new Setting("v0", new ParameterDouble(3.2,"V"),         "The battery voltage at 0% (minimum) capacity"));
        
        addSetting(new Setting("iRes", new ParameterDouble(80.e-3,"Ohm"),   "The battery internal resistance used to charge its internal capacitor.It models the long DC current capability of the battery"));
        addSetting(new Setting("iCap", new ParameterDouble(.1,"F"),         "The battery internal capacitor, together to the external resistance it models the short DC pulse current capability than the DC current"));
        addSetting(new Setting("eRes", new ParameterDouble(20.e-3,"Ohm"),   "The battery external resistance, together to the external resistance it models the short DC pulse current capability than the DC current"));
        
        addSetting(new Setting("rchrgbl", new ParameterBoolean(true),       "An input current increases the internal capacity, if the battery is not rechargeable that rises a warning"));
        
        addSetting(new Setting("vBtt", new ParameterReadOnly("vBtt", this),         "the internal battery voltage value during simulation "));
        addSetting(new Setting("aCpty", new ParameterReadOnly("aCpty", this),    "the available capacity after simulation "));
        
        try {
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+", vBtt, cpty", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in SourceBattery class");
        } 
        
        charge = new Integrator();
        capCharge = new Integrator();
        
    }    

    @Override
    public String getReadOnlyParameter(String paramLabel) {
        if (paramLabel.equals("vBtt")) return EngNotation.convert(getInternalVoltage(chrg))+"V";
        if (paramLabel.equals("aCpty")) return EngNotation.convert(chrg)+"C";
        return super.getReadOnlyParameter(paramLabel); 
    }    
    
    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition(); 

        v100 = getParameter("v100").getDouble();
        v90 = getParameter("v90").getDouble();
        v10 = getParameter("v10").getDouble();
        v0 = getParameter("v0").getDouble();        
        if (v100 <= v90 || v90 <= v10 || v10 <= v0) throw new ParsingException ("The battery voltage has to decrease while the battery discharge: v100 > v90 > v10 > v0 at "+getLabel());                        
        k1 = (v100-v90)/(1.-.9);
        k2 = (v90-v10)/(.9-.1);
        k3 = (v10-v0)/(.1-0.);
        
        iRes = getParameter("iRes").getDouble();
        if (iRes <= 0.) throw new ParsingException ("The internal resistance has to be higher then 0. to converge: "+iRes+" at "+getLabel());                        
        
        iCap = getParameter("iCap").getDouble();
        if (iCap <= 0.) throw new ParsingException ("The internal capacity has to be higher then 0. to converge: "+iCap+" at "+getLabel());                        
        
        eRes = getParameter("eRes").getDouble();
        if (eRes <= 0.) throw new ParsingException ("The external resistance has to be higher then 0. to converge: "+eRes+" at "+getLabel());                        
        
        rchrgbl = getParameter("rchrgbl").getBoolean();
        
        cpty = getParameter("cpty").getDouble();
        cpactyOvchg = 1.1 * cpty; //maximum overcharge limited to 110%
        
        charge.setY0(cpty);
        capCharge.setY0(v100);        
        
        rechargeable = getParameter ("rchrgbl").getBoolean();
    }    

    @Override
    public void stepForward() {
        if (! enable) {
            setGateCurrent(0, 0.);
            return;
        }
        
        vCap = capCharge.getYn();
        
        chrg = charge.getYn();
        
        iBat = (getInternalVoltage(chrg) - vCap)/iRes;
        iOut = (getGateVoltage(0) - vCap)/eRes;
        
        capCharge.addDyDt((iBat+iOut)/iCap);

        if (iBat < 0.) {
            if (rechargeable) {//only rechargeable batery increase their internal charge
                if (chrg > cpactyOvchg) charge.setY0(cpactyOvchg);   //the internal charge increases only if the battery is not overloaded
                else charge.addDyDt(-iBat);                
            }
        } else {
            charge.addDyDt(-iBat);
        }
        
        
        setGateCurrent(0, iOut);
    }

    @Override
    public BlackBox makeNewParseable() {
        return new SourceBattery();
    }
    
    @Override
    public String getWarning() {
        String warnings ="";
        if (!rchrgbl && iBat < 0.) warnings += "Charging current was injected on a not rechargeable battery "+getLabel();
        if (charge.getYn()>cpty) warnings += "The battery was overcharged exceeding its capacity "+getLabel();
        if (warnings.length()==0) return null;
        return "On "+getLabel()+" were found: "+warnings; 
    }               
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = is a battery\n";
    }     
    
    private double getInternalVoltage (double c) {
        c = c/cpty;                        
        if (c < 0.0) return 0.;
        if (c > 0.9) return v100+k1*(c-1.);
        if (c < 0.1) return v10+k3*(c-0.1);        
        return v90+k2*(c-0.9);
    }
    
}
