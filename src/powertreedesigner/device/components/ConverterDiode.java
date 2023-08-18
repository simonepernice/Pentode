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
public class ConverterDiode extends Converter {  
    private double vFrw, rFrw, iRev;

    public ConverterDiode() {
        super("Diode");
        
        addSetting(new Setting("vFrw", new ParameterDouble(0.6,"V"), "diode forward voltage"));
        addSetting(new Setting("rFrw", new ParameterDouble(0.3,"Ohm"), "diode forward resistance"));
        addSetting(new Setting("iRev", new ParameterDouble(1.e-6,"A"), "diode reverse leakage current"));
        addSetting(new Setting("iMax", new ParameterDouble(10.,"A"), "diode maximum forward current used only for warnings (not for simulation)"));
        
        try {
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",vFrw", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in ConverterDiode class");
        } 
        
    }

    @Override
    public void setInitialCondition() throws ParsingException  {
        super.setInitialCondition(); 
        vFrw = getParameter("vFrw").getDouble();
        rFrw = getParameter("rFrw").getDouble();
        if (rFrw <= 0.) throw new ParsingException ("To converge the simulation requires a forward resistance > 0. while was found "+rFrw+" at "+getLabel());                
        if (! getParameter("enb").getBoolean()) throw new ParsingException ("A diode cannot be disabled in "+getLabel());                
        iRev = getParameter ("iRev").getDouble();
    }
    
    

    @Override
    public void stepForward() {
        final double dv = getGateVoltage(0)-getGateVoltage(1);
        
        if (dv < 0) {//reverse biased
            setGateCurrent(0, -iRev); 
            setGateCurrent(1, iRev);
            return;            
        }
        
        //direct biased
        
        if (dv < vFrw) {//interdiction
            setGateCurrent(0, 0.); 
            setGateCurrent(1, 0.);
            return;
        }         
                
        final double i = (dv - vFrw)/rFrw;
        
        setGateCurrent(0, i); 
        setGateCurrent(1, -i);                

    }

    @Override
    public BlackBox makeNewParseable() {
        return new ConverterDiode();
    }

    @Override
    public String getWarning() {     
        String warnings = "";            
        if (getGateVoltage(0)-getGateVoltage(1) < vFrw)  warnings += "\n-The diode is not forward biased ";
        if (getGateCurrent(0) > getParameter("iMax").getDouble())  warnings += "\n-The forward current is exceeding maximum allowable ";
        if (warnings.length()==0) return null;
        return "On "+getLabel()+" were found:"+warnings;     
    }

    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = is a diode\n";
    }      
    
}
