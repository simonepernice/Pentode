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

import java.util.LinkedList;
import powertreedesigner.device.Device;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.parser.linesToParse.TokenList;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class ParameterList extends Parameter {
    private final LinkedList<Parameter> val;
    private final Parameter builder;
    
    public ParameterList(Parameter builder) {
        this.builder = builder;
        val = new LinkedList<>();
    }
    
    public ParameterList(Parameter builder, String list) {
        this(builder);
        try {
            fillValues(TokenList.splitInTokens(list), val);
        } catch (ParsingException pe) {
            throw new RuntimeException ("Internal error initialiting a parameter list "+pe.getMessage());
        }
    }

    @Override
    public String getTypeName() {
        return "list";
    }
    
    @Override
    public LinkedList<Parameter> getList() {
        return val;
    }

    @Override
    public Parameter parse(TokenList tokens, Device d) throws ParsingException {
        ParameterList pl = new ParameterList(builder);
        fillValues(tokens, pl.val);
        return pl;
    }
    
    private void fillValues (TokenList tokens, LinkedList<Parameter> v) throws ParsingException {
        v.add(builder.parse(tokens, null)); //trim is important if getPrintString is used to build a new object
        while (tokens.hasNext(",")){
            tokens.next();
            v.add(builder.parse(tokens, null)); //trim is important if getPrintString is used to build a new object
        }            
    }

    @Override
    public String getPrintString() {//It is important to keep the list syntax correct because it is parsed internally to build the parameter draw list
        StringBuilder out = new StringBuilder();
        for (Parameter p : val) out.append(p.getPrintString()).append(", ");
        if (out.length()>2) out.setLength(out.length()-2);
        return out.toString();
    }      
  
}
