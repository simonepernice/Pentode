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
public class Quitter extends Command {

    public Quitter() {
        super("quit", "command system");
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
        System.out.println("Power Tree Designer is quitting...");
        System.exit(0);
    }

    @Override
    public Component makeNewParseable() {
        return new Quitter();
    }      
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = exits Power Tree Designer. Any command given in the interactive session is not saved \n";
    }      
    
}
