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
public class ParameterOptions extends Parameter {
    private final String[] options;
    private int val = 0;

    public ParameterOptions(String... options) {
        this.options = options;
        val = 0;
    }
    
    private ParameterOptions (int val, String... options) {
        this(options);
        this.val = val;
    }
    
    @Override
    public String getTypeName() {
        return "options";
    }
        
    @Override
    public int getInteger() {
        return val;
    }

    @Override
    public String getString() {
        if (val < 0 ) return "undefined value";
        return options[val];
    }

    @Override
    public Parameter parse(TokenList tokens, Device d) throws ParsingException {
        int v=0;
        String param = tokens.next();
        
        for (String s : options) {
            if (s.equals(param)) break;
            ++v;
        }
        if (v == options.length) {
            v = -1;
            throw new ParsingException("the given option is not allowed: "+param);
        }
        
        return new ParameterOptions(v, options);
    }

    @Override
    public String getPrintString() {
        return options[val];

    }     

    @Override
    public String getHelp() {
        StringBuilder opts = new StringBuilder();
        opts.append('(');
        for (String op : options) opts.append(op).append('|');
        opts.setLength(opts.length()-1);
        opts.append(')');
        opts.append(super.getHelp());
        return opts.toString();
    }
    
    
}
