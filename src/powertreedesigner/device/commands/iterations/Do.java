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
package powertreedesigner.device.commands.iterations;

import powertreedesigner.device.Device;
import powertreedesigner.device.command.Command;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.components.Component;
import powertreedesigner.device.parser.linesToParse.LinesToParse;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Do extends Command {
    static {
        LinesToParse.addIterationCommand("do", "loop");
    }    
    
    public Do() {
        super("do", "command iteration do");
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
    }

    @Override
    public Component makeNewParseable() {
        return new Do();
    }      
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = defines the begin of the iteration do / loop block. To insert a multi line statement in interactive mode ends the required lines by symbol +\n";
    }      
    
}
