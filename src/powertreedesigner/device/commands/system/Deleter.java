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
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.components.Component;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Deleter extends Command {

    public Deleter() {
        super("delete", "command modifier");
    }

    @Override
    public int getGateNumber() {
        return 0; 
    }
        
    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        String etd = getLabel();
        System.out.println("Loading file "+etd);
        d.delete(etd);
        System.out.println("Element deleted.\n");
    }

    @Override
    public Component makeNewParseable() {
        return new Deleter();
    }      
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = a command to delete a blackbox, a node (without any gate linked), variable or currency. It it useful when a power tree is build interatively. \n";
    }      
    
}
