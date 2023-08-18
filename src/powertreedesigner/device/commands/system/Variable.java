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
package powertreedesigner.device.commands.system;

import powertreedesigner.device.components.electricComponents.components.HistoryComponent;
import powertreedesigner.device.Device;
import powertreedesigner.device.calculator.Calculator;
import powertreedesigner.device.parser.Parseable;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.commands.simulator.history.VectorOfDouble;
import powertreedesigner.device.components.electricComponents.parameter.ParameterVectorOfDouble;
import powertreedesigner.device.components.electricComponents.parameter.ParameterString;
import powertreedesigner.device.components.electricComponents.parameter.Parameters;
import powertreedesigner.device.components.electricComponents.parameter.Setting;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public final class Variable extends HistoryComponent {
    private VectorOfDouble history;

    public Variable() {
        super("set", "command variable");
        this.history = new VectorOfDouble("");//history;
        addSetting(new Setting(Parameters.UNLABELLED, new ParameterVectorOfDouble(), "the value, the variable to copy or the calculation to set in the new variable"));
        addSetting(new Setting("measurementUnit", new ParameterString(""), "the measurement unit assigned to the variable"));        
    }
    
    public Variable(String label, String mu) {//this is not parsed so there is no reason to set parameters
        super("variable", "command variable");        
        history = new VectorOfDouble(mu);
        setLabel(label); //that has to go after history instantiation
        addSetting(new Setting(Parameters.UNLABELLED, new ParameterVectorOfDouble(history), ""));
        addSetting(new Setting("measurementUnit", new ParameterString(mu), ""));  
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
        history.setLabel(label);
    }
        
    @Override
    public int getGateNumber() {
        return 0;
    }

    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        history = getParameter(Parameters.UNLABELLED).getVectorOfDouble();
        history.setMeasurementUnit(getParameter("measurementUnit").getString());
        history.setLabel(getLabel());
        d.setVariable(this);
    }

    @Override
    public Parseable makeNewParseable() {
        return new Variable();
    }

    @Override
    public void clearHistory() {
        history.clear();
    }

    @Override
    public void saveToHistory() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    public void storeValues(double value) {
        history.addPoint(value);
    }

    @Override
    public VectorOfDouble getHistory() {
        return history;
    }

    @Override
    public String getPrintString() {
        return history.getPrintString();
    }
        
    @Override
    public String getHelp() {
        return super.getHelp()+"-Type = Command\n"+"-Function = defines a new variable or modify an existing one. The first field is the variable name. It requires a value and a vector. Whenever a vector is required it is possible to use the calculator. The calculator is a simple program working in reverse polish notation between square brackets [ ... ]. The following calculator commands are available:\n"+Calculator.getHelp()+'\n';
    }     

    @Override
    public boolean equals(Object obj) {
        return history.getLabel().equals (((Variable) obj).getLabel());
    }
    
    
}
