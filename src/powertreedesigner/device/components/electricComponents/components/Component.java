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
package powertreedesigner.device.components.electricComponents.components;

import java.util.Set;
import powertreedesigner.device.Device;
import powertreedesigner.device.commands.helper.Helpable;
import powertreedesigner.device.components.electricComponents.parameter.Parameter;
import powertreedesigner.device.components.electricComponents.parameter.Parameters;
import powertreedesigner.device.components.electricComponents.parameter.Parametrizable;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.parser.Parseable;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.parser.linesToParse.TokenList;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public abstract class Component implements Helpable, Parseable  {

    private final Parameters parameters;
    private final String name;
    private String label;
    private String type;
    
    public Component (String name, String type) {
        this.name = name;
        this.type = type;
                
        parameters = new Parameters();
    }
    
    public String getType () {
        return type;
    }

    @Override
    public final String getName() {
        return name;
    }     

    @Override
    public void setLabel(String label) {
        this.label = label;
    }     

    @Override
    public final String getLabel() {
        return label;
    }     

    @Override
    public boolean hasLabel() {
        return true;
    }
                    
    @Override
    public final void addSetting(Setting s) {
        parameters.addSetting(s);
    }

    @Override
    public final Parameter getParameter(String param) {
        return parameters.getParameter(param);
    }

    @Override
    public boolean hasParameter(String param) {
        return parameters.hasParameter(param);
    }
        
    @Override
    public void setParameter(String param, Parameter val) throws ParsingException {
        try {
            parameters.setParameter(param, val);
        } catch (ParsingException pe) {
            throw new ParsingException(pe.getMessage()+ " on component/command "+(hasLabel()?getLabel():getName()));
        }        
    }    

    @Override
    public void setParameter(String param, TokenList val, Device device) throws ParsingException {
        try {
            parameters.setParameter(param, val, device);
        } catch (ParsingException pe) {
            throw new ParsingException(pe.getMessage()+ " on component/command "+(hasLabel()?getLabel():getName()));
        }        
    }
        
    @Override
    public void setParameter(Parametrizable p) throws ParsingException {
        try {
            parameters.setParameter(p);
        } catch (ParsingException pe) {
            throw new ParsingException(pe.getMessage()+ " on component/command "+(hasLabel()?getLabel():getName()));
        }            
    }

    @Override
    public Set<String> getParameters() {
        return parameters.getParameters();
    }            
    
    @Override
    public String getParametersDefault() {
        return parameters.getParametersDefault();
    }    
        
    public String getParametersHelp() {
        return parameters.getHelp();
    }    
            
    @Override
    public String getParameterString (String param) {
        return parameters.getParameterString(param);
    }

    @Override
    public String missingRequiredParameters(){
        return parameters.missingRequiredParameters();
    }
    

    @Override
    public String getParameterHelp(String param) {
        return parameters.getParameterHelp(param);
    }

    @Override
    public String getHelp() {
        return "\n-Name = "+name+"\n-Type = "+type+"\n-Label = "+(hasLabel()?"REQUIRED":"NOT REQUIRED")+"\n-Parameters ="+parameters.getHelp();
    }            
}
