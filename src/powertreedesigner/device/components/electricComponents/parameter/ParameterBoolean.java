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
package powertreedesigner.device.components.electricComponents.parameter;

import powertreedesigner.device.Device;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.parser.linesToParse.TokenList;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class ParameterBoolean extends Parameter {
    private final boolean val;

    public ParameterBoolean(boolean val) {
        this.val = val;
    }

    @Override
    public String getTypeName() {
        return "boolean";
    }
    
    @Override
    public boolean getBoolean() {
        return val;
    }

    @Override
    public Parameter parse(TokenList param, Device d) throws ParsingException {
        String val = param.next();
        if ("true".equals(val)) return new ParameterBoolean(true);
        if ("false".equals(val)) return new ParameterBoolean(false);
        throw new ParsingException("it was expected a boolean while was found "+param);
    }

    @Override
    public String getPrintString() {
        return Boolean.toString(val);
    }        

    @Override
    public String getHelp() {
        return "(true|false)"; 
    }        
}
