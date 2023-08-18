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
package powertreedesigner.device.components.electricComponents;

import java.awt.Color;
import powertreedesigner.device.Device;
import powertreedesigner.device.components.electricComponents.parameter.EngNotation;
import powertreedesigner.device.components.electricComponents.parameter.ParameterColor;
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.parameter.ParameterInteger;
import powertreedesigner.device.components.electricComponents.parameter.ParameterReadOnly;
import powertreedesigner.device.components.electricComponents.parameter.ReadOnlyParametrizable;
import powertreedesigner.device.components.electricComponents.parameter.Setting;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public abstract class Source extends BlackBox implements ReadOnlyParametrizable {  
    public Source(String name) {
        super("src"+name, "component source", 1);
        addSetting(new Setting("eff", new ParameterDouble(0,"%"), "the efficiency used to compute the power loss pLos"));        
        addSetting(new Setting("pOut", new ParameterReadOnly("pOut", this), "the output power"));
        addSetting(new Setting("iOut", new ParameterReadOnly("iOut", this), "the output current"));   
        addSetting(new Setting("drwClr", new ParameterColor(Color.cyan), "the fill color used for drawing "));
        try {
            setParameter("pltPr", new ParameterInteger(1000));
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",pOut,iOut", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in Converter class");
        }
        
    }
    
    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        setInitialCondition(); //to throw exception if any waveform parameter is not set properly
        d.addBlackBox(this, nodes); 
        d.addChild(this);
    }   
    
    @Override
    public String getReadOnlyParameter(String paramLabel) {
        double v=0.;
        String mu=null;
        switch (paramLabel) {
            case "pOut":
                v= -getGateCurrent(0)*getGateVoltage(0);
                mu="W";
                break; 
            case "pLos":
                v= -getGateCurrent(0)*getGateVoltage(0)*(1-getParameter("eff").getDouble()/100.);
                mu="W";
                break; 
            case "iOut":  
                v= -getGateCurrent(0);
                mu="A";
                break;   
            default :
                return super.getReadOnlyParameter(paramLabel);
        }
        return EngNotation.convert(v)+mu;        
    }   
    
    @Override
    public void setGateNode(int gate, Node n) {
        super.setGateNode(gate, n); 
        addChild(n);
    } 
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Class = sourcer\n";
    }    
        
}
