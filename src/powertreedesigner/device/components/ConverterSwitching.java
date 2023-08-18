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

import powertreedesigner.device.components.electricComponents.Converter;
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public abstract class ConverterSwitching extends Converter {  
    protected double vTgt, rLdRg, rSwt, iSwt, iPfm, iQst, vMin, lnRgl;

    public ConverterSwitching(String name) {
        super(name);
        
        addSetting(new Setting("vTgt", new ParameterDouble(10,"V"), true, "the output target voltage"));
        addSetting(new Setting("rLdRg", new ParameterDouble(0.1,"Ohm"), "the load regulation expressed as resistance"));
        addSetting(new Setting("rSwt", new ParameterDouble(200.e-3,"Ohm"), "the switching circuit resistance"));
        addSetting(new Setting("iSwt", new ParameterDouble(2.5e-3,"A"), "the current required for switching "));
        addSetting(new Setting("iPfm", new ParameterDouble(10.e-3,"A"), "the minimum current below which the frequency is reduced and the switching current decreases proportionally"));
        addSetting(new Setting("iQst", new ParameterDouble(1.e-3,"A"), "the baseline current to supply the converter circuitry"));
        addSetting(new Setting("vMin", new ParameterDouble(1.5,"V"), "the minimum startup voltage"));
        addSetting(new Setting("lnRgl", new ParameterDouble(0.01,"%"), "the line regulation dVout/dVin in percentage"));
        
        try {
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",vTgt", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in ConverterBoost class");
        } 
    }

    @Override
    public void setInitialCondition() throws ParsingException {
        super.setInitialCondition(); 
        vTgt = getParameter("vTgt").getDouble();
        rLdRg = getParameter("rLdRg").getDouble();
        if (rLdRg <= 0.) throw new ParsingException ("To converge the simulation required a load regulation resistance > 0. while was found "+rLdRg+" at "+getLabel());
        rSwt = getParameter("rSwt").getDouble();
        iSwt = getParameter("iSwt").getDouble();
        iPfm = getParameter("iPfm").getDouble();
        iQst = getParameter ("iQst").getDouble();       
        vMin = getParameter("vMin").getDouble();     
        lnRgl = getParameter("lnRgl").getDouble()/100.;     
    }

}
