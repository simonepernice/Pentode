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
public abstract class Drain extends BlackBox {
    protected double iOff;
    
    public Drain(String name) {
        super("drn"+name, "component drain", 1);
        addSetting(new Setting("iOff", new ParameterDouble(1e-6,"A"), "the current drained when the device is disabled"));
        addSetting(new Setting("eff", new ParameterDouble(0,"%"), "the efficiency used to compute the poewr loss pLos"));
        addSetting(new Setting("pIn", new ParameterReadOnly("pIn", this), "the input power"));
        addSetting(new Setting("iIn", new ParameterReadOnly("iIn", this), "the input current"));   
        addSetting(new Setting("drwClr", new ParameterColor(Color.yellow), "the fill color used for drawing "));
        try {
            setParameter("pltPr", new ParameterInteger(3000));
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",pIn,iIn", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in Drain class");
        }     
    }
    
    @Override
    public String getReadOnlyParameter(String paramLabel) {
        double v=0.;
        String mu=null;
        switch (paramLabel) {
            case "pIn":
                v= getGateCurrent(0)*getGateVoltage(0);
                mu="W";
                break;
            case "pLos":
                v= getGateCurrent(0)*getGateVoltage(0)*(1-getParameter("eff").getDouble()/100.);
                mu="W";
                break;                
            case "iIn":
                v= getGateCurrent(0);
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
        n.addChild(this);
    }    

    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition();
        iOff = getParameter("iOff").getDouble();
    }
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Class = drainer\n";
    }    
    
      
}
