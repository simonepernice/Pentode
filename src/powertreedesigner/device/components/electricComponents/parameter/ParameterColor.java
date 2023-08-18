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

import java.awt.Color;
import powertreedesigner.device.Device;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.parser.linesToParse.TokenList;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class ParameterColor extends Parameter {
    private final Color color;
    
    public ParameterColor(Color c) {
        this.color = c;
    }
    
    public ParameterColor(int r, int g, int b) {
        this(new Color(r, g, b));
    }
    
    @Override
    public String getTypeName() {
        return "color";
    }
    
    @Override
    public Color getColor() {
        return color;
    }
    
    @Override
    public Parameter parse(TokenList tokens, Device d) throws ParsingException {
        int[] base = new int[3];
        
        String param;
        for (int i=0; i<3; ++i) {
            param = tokens.next();
            try {
                base[i] = Integer.parseInt(param);
            } catch (NumberFormatException nfe) {
                throw new ParsingException("it was expected an integer while was found "+param);
            }       
            if (i==2) break;
            param = tokens.next();
            if (! param.equals(",")) throw new ParsingException("it were expected 3 integers divided by ',' while it was found "+param);
        }
        
        return new ParameterColor(base[0], base[1], base[2]);
    }

    @Override
    public String getPrintString() {        
        return "color RGB components: "+color.getRed()+", "+color.getGreen()+", "+color.getBlue();
    }      
  
}
