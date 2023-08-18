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
package powertreedesigner.device.commands.system;

import powertreedesigner.device.Device;
import powertreedesigner.device.command.Command;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.components.Component;
import powertreedesigner.device.components.electricComponents.components.ElectricComponent;
import powertreedesigner.device.components.electricComponents.parameter.ParameterOptions;
import powertreedesigner.device.components.electricComponents.parameter.ParameterString;
import powertreedesigner.device.components.electricComponents.parameter.Parameters;
import powertreedesigner.device.components.electricComponents.parameter.Parametrizable;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.parser.linesToParse.TokenList;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Modifier extends Command {
    private final Parameters params;

    public Modifier() {
        super("modify", "command modifier");
        
        params = new Parameters();
        
        addSetting(new Setting("LOOKAT",new ParameterOptions("label","name"),"the parameter that should be looked for the match"));
        addSetting(new Setting("MATCH",new ParameterOptions("exact","begin","end"),"the type of match to use in the search"));

    }

    @Override
    public int getGateNumber() {
        return 0; 
    }

    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        System.out.println("Modifing: "); 
        int la = getParameter("LOOKAT").getInteger();
        int ma = getParameter("MATCH").getInteger();
        for (ElectricComponent hc : d.getElectricComponents()) {
            String s = (la == 0 ? hc.getLabel() : hc.getName());
            switch (ma) {
                case 0:
                    if (! s.equals(getLabel())) continue;
                    break;
                case 1:
                    if (! s.startsWith(getLabel())) continue;
                    break;
                case 2:
                    if (! s.endsWith(getLabel())) continue;
                    break;
            }
            Parametrizable c = d.getHistoryComponent(hc.getLabel());
            c.setParameter(params);
            //hc.setInitialCondition(); we cannot check here for incongruence on the settings otherwise the initial parameters would be deleted
            System.out.print("On the component "+hc.getLabel()+" was modified the following parameters "+params.getParametersDefault());
        }
        System.out.println("End modifing.\n"); 
                
    }

    @Override
    public Component makeNewParseable() {
        return new Modifier();
    }
    
    @Override
    public void setParameter(String param, TokenList val, Device device) throws ParsingException {
        if (param.equals("LOOKAT") || param.equals("MATCH")) super.setParameter(param, val, device);
        else {
            if (params.getParameters().contains(param)) throw new ParsingException ("the parameter "+param+" was already added to the list of parameter to modify");
            params.addSetting(new Setting(param, new ParameterString(val.next()), ""));
        } 
    }    
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = modifies a parameter of the component already entered in the net list whose [label|name] [matches|begin|end] with given text. Modify expects a single string value for each modified parameter. Therefore if the parameter to modify is longer it needs to be written between \". For instance to modify drwClr it is required to write drwClr=\"1,2,3\" or to modify drwPrm list it requires drwPrm=\"lbl,vol\"\n";
    }
    
    
}
