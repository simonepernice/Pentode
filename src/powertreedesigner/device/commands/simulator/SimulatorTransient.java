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
package powertreedesigner.device.commands.simulator;

import powertreedesigner.device.components.electricComponents.components.Component;
import powertreedesigner.device.components.electricComponents.parameter.ParameterBoolean;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class SimulatorTransient extends Simulator {
    
    public SimulatorTransient() {
        super("Transient");
        setLabel("Transient");
        try {
            setParameter("saveTransient", new ParameterBoolean(true));                  
        } catch (ParsingException pe) {
            throw new RuntimeException("Internal error at SimulatorTransient");
        }
    }
    
    @Override
    public Component makeNewParseable() {
        return new SimulatorTransient ();
    }     
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = runs the transient simulation up to endTime saving the history. It is useful to verify turn on and off sequence and over shoots.\n";
    }
    
}
