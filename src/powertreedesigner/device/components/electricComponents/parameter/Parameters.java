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

import java.util.ArrayList;
import java.util.Collections;
import powertreedesigner.device.exception.ParsingException;
import java.util.HashMap;
import java.util.Set;
import powertreedesigner.device.Device;
import powertreedesigner.device.commands.helper.Helpable;
import powertreedesigner.device.parser.linesToParse.TokenList;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public final class Parameters implements Parametrizable, Helpable {
    public final static String UNLABELLED = "UNLABELLEDPARAMETER";
    
    private final HashMap<String, Setting> settings;

    public Parameters() {
        settings = new HashMap<>();
    }
    
    @Override
    public void addSetting(Setting param) {
        if (settings.containsKey(param.getLabel())) throw new RuntimeException ("The parameter "+param.getLabel()+" was already added.");
        settings.put(param.getLabel(), param);
    }

    @Override
    public Parameter getParameter(String param) {
        if (! settings.containsKey(param)) {
            throw new RuntimeException ("the parameter "+param+" does not exist. The available parameters are "+settings.keySet().toString());
        }
        return settings.get(param).getValue();
    }

    @Override
    public void setParameter(String param, Parameter val) throws ParsingException {
        if (! settings.containsKey(param)) throw new ParsingException ("the parameter "+param+" does not exists. The available parameters are "+settings.keySet().toString());
        settings.get(param).setValue(val);        
    }      
    
    @Override
    public void setParameter(String param, TokenList val, Device device) throws ParsingException {
        if (! settings.containsKey(param)) throw new ParsingException ("the parameter "+param+" does not exists. The available parameters are "+settings.keySet().toString());
        Setting s = settings.get(param);
        s.setValue(s.getValue().parse(val, device));
    }        
    
    @Override
    public void setParameter (Parametrizable p) throws ParsingException {
        for (String key : p.getParameters()) {
            setParameter(key, getParameter(key).parse(TokenList.splitInTokens(p.getParameter(key).getString()), null));
        }
    }
    
    @Override
    public Set<String> getParameters () {
        return settings.keySet();
    }

    @Override
    public String getParametersDefault() {
        StringBuilder pd = new StringBuilder();
        for (String p : getParameters()) 
            pd.append(p).append('=').append(getParameter(p).getPrintString()).append(", ");
        if (pd.length() > 0) {
            pd.setLength(pd.length()-2);            
        }
        pd.append('\n');
        return pd.toString();            
    }        
    
    @Override
    public String getHelp() {
        StringBuilder pd = new StringBuilder();
        ArrayList<String> prm = new ArrayList<> (getParameters());
        Collections.sort(prm);
        for (String p : prm) 
            pd.append("\n").append(getParameterHelp(p));//.append(",\n");
        if (pd.length() == 0) pd.append (" (none)");        
        pd.append('\n');
        return pd.toString();            
    }        

    @Override
    public String getLabel() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    @Override
    public String getParameterString (String param)  {
        if (param.equals(UNLABELLED)) return getParameter(param).getPrintString();
        return param+"="+getParameter(param).getPrintString();
    }

    @Override
    public boolean hasParameter(String param) {
        return settings.containsKey(param);
    }
    
    @Override
    public String missingRequiredParameters () {
        StringBuilder misses = new StringBuilder ();
        for (Setting s : settings.values())
            if (s.isRequired()) misses.append(s.getLabel()).append(", ");
        if (misses.length()==0) return null;
        misses.setLength(misses.length()-2);
        return misses.toString();
    }

    @Override
    public String getParameterHelp(String param) {
        Setting s=settings.get(param);
        Parameter p=s.getValue();
        return "--"+param+" = "+(s.isRequired()?" (must be defined) ": (p.isReadOnly() ? "(read only) " : ("(default ")+s.getValue().getPrintString()+" ) "))+s.getHelp();
        
    }
    
    
}
