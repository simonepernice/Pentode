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
package powertreedesigner.device.exception;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class PlottingException extends ParsingException {

    public PlottingException(String message) {
        super(message);
    }        
    
    @Override
    public String getType() {
        return "Plotting exception:"; 
    }    
}
