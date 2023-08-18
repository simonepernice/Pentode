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
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.components.electricComponents.parameter.Parameters;
import powertreedesigner.device.components.electricComponents.parameter.Setting;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Break extends Command {
    public Break() {
        super("break", "command iteration");
        
        addSetting(new Setting(Parameters.UNLABELLED, new ParameterDouble(1., ""), "the condition checked not-equal to 0. will break the current loop going to its end"));                
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
        if (getParameter(Parameters.UNLABELLED).getDouble()!=0.) d.getParser().getLinesToParse().goToEndLoop();
    }

    @Override
    public Component makeNewParseable() {
        return new Break();
    }      
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = ends the current iteration blok (do-loop, if-iEnd, while-wEnd, for-next)\n";
    }      
    
}
