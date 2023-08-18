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
public class ParameterReadOnly extends Parameter {
    private final String label;
    private final ReadOnlyParametrizable source;

    public ParameterReadOnly(String label, ReadOnlyParametrizable source) {
        this.label = label;
        this.source = source;
    }

    @Override
    public String getTypeName() {
        return "parameterReadOnly"; 
    }

    @Override
    public Parameter parse(TokenList param, Device d) throws ParsingException {
        throw new ParsingException ("the parameter "+label+" is read only");
    }

    @Override
    public String getPrintString() {
        return source.getReadOnlyParameter(label);
    }      

//    @Override
//    public String getHelp() {
//        return "(read only parameter)";
//    }
    
    @Override
    public boolean isReadOnly () {
        return true;
    }
  
}
