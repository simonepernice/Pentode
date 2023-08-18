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
public class ParameterElectricComponent extends Parameter {
    private final String label;
    private final String property;

    public ParameterElectricComponent(String label, String property) {
        this.label = label;
        this.property = property;
    }

    @Override
    public String getLabel() {
        return label;
    }
    
    @Override
    public String getProperty() {
        return property;
    }

    @Override
    public String getTypeName() {
        return "electricComponent"; 
    }

    @Override
    public Parameter parse(TokenList param, Device d) throws ParsingException {
        return new ParameterElectricComponent(param.next(), param.hasNext(".") ? param.next(2) : null);
    }

    @Override
    public String getPrintString() {
        return label+"."+property;
    }            
    
    @Override
    public String getHelp() {
        return " expects a component optionally followed by .property";
    }    
}
