/*
 * Copyright (C) 2017 Simone Pernice pernice@libero.it
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
package powertreedesigner.device.parser.linesToParse;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class LocatedIterationCommand {
    private final IterationCommand ic;
    private final int location;

    public LocatedIterationCommand(IterationCommand ic, int location) {
        this.ic = ic;
        this.location = location;
    }

    public int getLocation() {
        return location;
    }
    
    public String getBegin () {
        return ic.getBegin();
    }
    
    public String getEnd () {
        return ic.getEnd();
    }
    
}
