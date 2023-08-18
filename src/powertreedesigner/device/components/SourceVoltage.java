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
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.components.electricComponents.parameter.ParameterReadOnly;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.components.electricComponents.parameter.waveform.Waveform;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class SourceVoltage extends Source {  
    private double iMax, res, vTgt;
    private Waveform vTgtWve;

    public SourceVoltage() {
        super("Voltage");
        
        vTgtWve = new Waveform("V");
        vTgtWve.addParameters(this);
//        addSetting(new Setting("vTgtWve", new ParameterWaveform("s", "V"), true, "Insert time[s], voltage[V] of the waveform to use instead of constant target"));
        addSetting(new Setting("iMax", new ParameterDouble(5.,"A"), "the maximum output current"));
        addSetting(new Setting("res",  new ParameterDouble(0.1,"Ohm"), "the output resistance"));
        addSetting(new Setting("vTgt", new ParameterReadOnly("vTgt", this), "the last voltage value during simulation "));
        
        try {
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",vTgt", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in SourceVoltage class");
        } 
        
    }    

    @Override
    public String getReadOnlyParameter(String paramLabel) {
        if (paramLabel.equals("vTgt")) return EngNotation.convert(vTgt)+"V";
        return super.getReadOnlyParameter(paramLabel); //To change body of generated methods, choose Tools | Templates.
    }    
    
    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition(); 
        vTgtWve = vTgtWve.buildFromParameters(this);
        res = getParameter("res").getDouble();
        if (res <= 0.) throw new ParsingException ("To converge the simulation requires a resistance > 0. while was found "+res+" at "+getLabel());                        
        iMax = getParameter("iMax").getDouble();
    }    

    @Override
    public void stepForward() {
        if (! enable) {
            setGateCurrent(0, 0.);
            return;
        }
        
        vTgt = vTgtWve.getY(Integrator.getTnp1());
//        System.out.println("vTgt= "+vTgt+" at t= "+Integrator.getTnp1());
        
        final double i = (getGateVoltage(0) - vTgt)/res;        
        
        if (-i> iMax) {//over current triggered
            setGateCurrent(0, -iMax);
            return;
        } 
        
        setGateCurrent(0, i);
    }

    @Override
    public BlackBox makeNewParseable() {
        return new SourceVoltage();
    }
    
    @Override
    public String getWarning() {
        String warnings ="";
        if (-getGateCurrent(0) >= iMax) return "Current exceed maximum value on "+getLabel();
        if (warnings.length()==0) return null;
        return "On "+getLabel()+" were found:"+warnings; 
    }               
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = is a constant voltage source\n";
    }      
    
}
