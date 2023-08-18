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
public class ConverterResistor extends Converter {  
    private double iRes, oRes, ioRes;

    public ConverterResistor() {
        super("Resistor");

        addSetting(new Setting("iRes", new ParameterDouble(1.e6,"Ohm"), "the input resistance: from input to GND"));
        addSetting(new Setting("oRes", new ParameterDouble(1.e6,"Ohm"), "the output resistance: from output to GND"));
        addSetting(new Setting("ioRes", new ParameterDouble(1,"Ohm"), "the resistance from input to output"));
        
        try {
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+", ioRes, oRes", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in ConverterSwitch class");
        } 
        
    }

    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition(); 
        iRes = getParameter("iRes").getDouble();
        oRes = getParameter("oRes").getDouble();
        ioRes = getParameter("ioRes").getDouble();
        if (iRes <= 0. ) throw new ParsingException ("To converge the simulation requires input resistances > 0"+iRes+" at "+getLabel());        
        if (oRes <= 0. ) throw new ParsingException ("To converge the simulation requires output resistances > 0"+ oRes+" at "+getLabel());        
        if (ioRes <= 0.) throw new ParsingException ("To converge the simulation requires input to output resistances > 0"+ioRes+" at "+getLabel());        
        if (! getParameter("enb").getBoolean()) throw new ParsingException ("A resistor cannot be disabled in "+getLabel());                     
    }
        
    @Override
    public void stepForward() {
        final double vo = getGateVoltage(1);
        final double vi = getGateVoltage(0);
        final double dv = vi-vo;
        
        setGateCurrent(0, vi/iRes+dv/ioRes);
        setGateCurrent(1, vo/oRes-dv/ioRes);
    }

    @Override
    public BlackBox makeNewParseable() {
        return new ConverterResistor();
    }

    @Override
    public String getWarning() {        
        return null;       
    }    
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = is a Greek-Pi resistor partition network. It is made by three resistors: input resistor, output resistor and input-output resistor. \n";
    }      
    
}
