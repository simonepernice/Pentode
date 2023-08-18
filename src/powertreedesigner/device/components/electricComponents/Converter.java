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
import powertreedesigner.device.components.electricComponents.parameter.EngNotation;
import powertreedesigner.device.components.electricComponents.parameter.ParameterColor;
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.parameter.ParameterInteger;
import powertreedesigner.device.components.electricComponents.parameter.ParameterReadOnly;
import powertreedesigner.device.components.electricComponents.parameter.Setting;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public abstract class Converter extends BlackBox {    
    protected double iOff;
    
    protected Converter(String name, int gateNumber) {
        super("cnv"+name, "component converter", gateNumber);
        addSetting(new Setting("iOff", new ParameterDouble(1e-6,"A"), "the input current when the device is disabled"));
        addSetting(new Setting("pIn", new ParameterReadOnly("pIn", this), "the input power"));
        addSetting(new Setting("pOut", new ParameterReadOnly("pOut", this), "the output power"));
        addSetting(new Setting("iIn", new ParameterReadOnly("iIn", this), "the input current"));
        addSetting(new Setting("iOut", new ParameterReadOnly("iOut", this), "the output current"));
        addSetting(new Setting("drwClr", new ParameterColor(Color.green), "the fill color used for drawing "));        
        try {
            setParameter("pltPr", new ParameterInteger(2000));
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",pIn,pOut,iIn,iOut", null);
            
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in Converter class");
        }
    }
    
    public Converter(String name) {
        this(name, 2);
    }
    

    @Override
    public String getReadOnlyParameter(String paramLabel) {
        double v;
        String mu;
        switch (paramLabel) {
            case "pIn":
                v= getGateCurrent(0)*getGateVoltage(0);
                mu="W";
                break;
            case "pOut":
                v= -getGateCurrent(1)*getGateVoltage(1);
                mu="W";
                break; 
            case "pLos":
                v= getGateCurrent(1)*getGateVoltage(1)+getGateCurrent(0)*getGateVoltage(0);
                mu="W";
                break;                 
            case "iIn":
                v= getGateCurrent(0);
                mu="A";
                break;
            case "iOut":  
                v= -getGateCurrent(1);
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
        switch (gate) {
            case 0:
                n.addChild(this);
                break;
            case 1:
                addChild(n);
                break;
            default:
                //if it has more than 2 gates they are not showed                
        }
    }
    
    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition();
        iOff = getParameter("iOff").getDouble();
    }   
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Class = converter\n";
    }    
}
