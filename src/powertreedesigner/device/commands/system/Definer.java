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

import powertreedesigner.device.Device;
import powertreedesigner.device.command.Command;
import powertreedesigner.device.commands.printer.Printable;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.components.Component;
import powertreedesigner.device.components.electricComponents.parameter.ParameterString;
import powertreedesigner.device.components.electricComponents.parameter.Parameters;
import powertreedesigner.device.components.electricComponents.parameter.Setting;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Definer extends Command implements Printable {
    private String value;

    public Definer() {
        super("define", "command modifier");
        
        addSetting(new Setting(Parameters.UNLABELLED, new ParameterString (""), true, "A definition to be used as shortcut for write more times the same device requiring a lot of parameter, write the parameters between apex"));
    }

    @Override
    public int getGateNumber() {
        return 0; 
    }
        
    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        value = getParameter(Parameters.UNLABELLED).getString();//.replace("_", " ").replace(":", "=");
        System.out.println("Defined "+getLabel()+" with value "+value);
        d.addDefinition (this);
    }

    @Override
    public Component makeNewParseable() {
        return new Definer();
    }              
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = defines a string to be used as replacement in the following lines. It can be used to define a long set of parameters to be used several times later. Write the string between \" \n";
    }

    @Override
    public String getPrintString() {
        return value;
    }
    
}
