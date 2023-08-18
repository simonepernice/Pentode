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
package powertreedesigner.device.commands.printer;

import powertreedesigner.device.Device;
import powertreedesigner.device.command.Command;
import powertreedesigner.device.components.electricComponents.components.Component;
import powertreedesigner.device.components.electricComponents.parameter.Parameter;
import powertreedesigner.device.components.electricComponents.parameter.ParameterElectricComponent;
import powertreedesigner.device.components.electricComponents.parameter.ParameterList;
import powertreedesigner.device.components.electricComponents.parameter.Parameters;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Printer extends Command{

    public Printer() {
        super ("print", "command work output");
        
        addSetting(new Setting(Parameters.UNLABELLED, new ParameterList (new ParameterElectricComponent(null, null)), true,"the list electric component of parameters to print"));
    }

    @Override
    public int getGateNumber() {
        return 0;
    }

    @Override
    public boolean hasLabel() {
        return false;
    }
        
    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        for (Parameter cn : getParameter(Parameters.UNLABELLED).getList()) {
            System.out.println("\nPrinting "+cn.getLabel()+ (cn.getProperty() != null ? "."+cn.getProperty() : "" ));
            System.out.println(d.getPrintString(cn.getLabel(), cn.getProperty()));
        }

        System.out.println("End print\n");
    }

    @Override
    public Component makeNewParseable() {
        return new Printer();
    }    
    
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = prints on screen a parameter of the given node or blackBox with syntax label.parameter. If it is used an integer: label.0|1 the history of last simulation on that port will be printed\n";
    }
    
    
}
