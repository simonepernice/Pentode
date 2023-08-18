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
public class SourceCurrent extends Source {  
    private double vMax, res, iTgt;
    private Waveform iTgtWve;

    public SourceCurrent() {
        super("Current");

        iTgtWve = new Waveform("A");
//        addSetting(new Setting("iTgtWve", new ParameterWaveform("s", "A"), true, "Insert time[s], current[A] of the waveform to use instead of constant target"));        
        iTgtWve.addParameters(this);
        addSetting(new Setting("vMax", new ParameterDouble(5.,"V"), "the maximum output voltage"));
        addSetting(new Setting("res",  new ParameterDouble(1000.,"Ohm"), "the output resistance"));
        addSetting(new Setting("iTgt", new ParameterReadOnly("iTgt", this), "the last current value during simulation "));
        
        try {
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",iTgt", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in ConverterBoost class");
        } 
        
    }

    @Override
    public String getReadOnlyParameter(String paramLabel) {
        if (paramLabel.equals("iTgt")) return EngNotation.convert(iTgt)+"A";
        return super.getReadOnlyParameter(paramLabel);  
    }
        
    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition(); 
 
        iTgtWve = iTgtWve.buildFromParameters(this);
        res = getParameter("res").getDouble();
        if (res <= 0.) throw new ParsingException ("To converge the simulation requires a resistance > 0. while was found "+res+" at "+getLabel());                        
//        vMax = Math.min(getParameter("vMax").getDouble(), iTgt*res);
        vMax = getParameter("vMax").getDouble(); 
    }    

    @Override
    public void stepForward() {
        if (! enable) {
            setGateCurrent(0, 0.);
            return;
        }
        
        iTgt = iTgtWve.getY(Integrator.getTnp1());        
        
        final double vi = getGateVoltage(0);
        
        if (vi > vMax) {//over voltage triggered
            setGateCurrent(0, -iTgt*vMax/vi);
            return;
        }        
        
        setGateCurrent(0, (vi-iTgt*res)/res);
    }

    @Override
    public BlackBox makeNewParseable() {
        return new SourceCurrent();
    }
    
    @Override
    public String getWarning() {
        String warnings ="";
        if (getGateVoltage(0) >= vMax) warnings += "\n-Voltage exceeds maximum value on ";
        if (warnings.length()==0) return null;
        return "On "+getLabel()+" were found:"+warnings;   
    }              
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = is a constant current source\n";
    }      
}
