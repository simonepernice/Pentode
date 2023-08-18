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

import java.util.LinkedList;
import powertreedesigner.device.Device;
import powertreedesigner.device.command.Command;
import powertreedesigner.device.components.electricComponents.BlackBox;
import powertreedesigner.device.components.electricComponents.Node;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.components.Component;
import powertreedesigner.device.components.electricComponents.parameter.Parameter;
import powertreedesigner.device.components.electricComponents.parameter.ParameterList;
import powertreedesigner.device.components.electricComponents.parameter.ParameterOptions;
import powertreedesigner.device.components.electricComponents.parameter.ParameterString;
import powertreedesigner.device.components.electricComponents.parameter.Parameters;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.components.electricComponents.parameter.currency.Currency;
import powertreedesigner.device.components.electricComponents.parameter.currency.Money;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Lister extends Command {

    public Lister() {
        super("list", "command work output");
        
        addSetting(new Setting(Parameters.UNLABELLED, new ParameterOptions ("blackboxes", "variables", "parsedLines", "nodes", "currencies", "definitions"), "the kind of element to list"));
        addSetting(new Setting("show", new ParameterList (new ParameterString (""),"name,lbl,lnks"), "the list of parameter to show for black boxes"));
    }

    @Override
    public int getGateNumber() {
        return 0; 
    }

    @Override
    public boolean hasLabel() {
        return false;
    }
        
    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
            System.out.println("Listing "+getParameter(Parameters.UNLABELLED).getString());
            switch (getParameter(Parameters.UNLABELLED).getInteger()) {
                case 2:
                    int ln = 0;
                    for (String line : d.getParser().getLinesToParse().getInputLines()) {
                        System.out.println(String.format("%04d", ln)+": "+line);    
                        ++ln;
                    }
                    break;
                case 1:
                    for (Variable v : d.getVariables()) {
                        System.out.println("Variable "+v.getLabel()+", size "+v.getHistory().getData().size());
                    }
                    for (BlackBox b : d.getBlackBoxes()) {
                        for (int i=0;i<b.getGateNumber(); ++i) 
                            System.out.println("BlackBox "+b.getLabel()+"."+i+", size "+b.getGate(i).getHistory().getData().size());
                    }                    
                    for (Node n: d.getNodes()) {
                        System.out.println("Node "+n.getLabel()+", size "+n.getHistory().getData().size());
                    }                                        
                    break;
                case 0:
                    for (BlackBox b : d.getBlackBoxes ()) {
                        LinkedList<Parameter> pts = getParameter("show").getList();
                        for (Parameter p : pts) {
                            if (b.hasParameter(p.getString())) System.out.print(b.getParameterString(p.getString()));
                            else System.out.print(p.getString()+ " is not available for this component");
                            System.out.print("; ");
                        }
                        System.out.println();                                                   
                    }                                            
                    break;
                case 3:
                    for (Node n : d.getNodes ()) {
                        LinkedList<Parameter> pts = getParameter("show").getList();
                        for (Parameter p : pts) {
                            if (n.hasParameter(p.getString())) System.out.print(n.getParameterString(p.getString()));
                            else System.out.print(p.getString()+ " is not available for this component");
                            System.out.print("; ");
                        }                        
                        System.out.println();                                                   
                    }
                    break; 
                case 4:
                    for (Currency c : Money.CURRENCIES.getValues()) {
                        System.out.println("Currency "+c.getSymbol()+", conversion ratio "+c.getConversionRatio()+", decimal digits "+c.getDecDigits()); 
                    }
                    break;
                case 5:
                    for (Definer de : d.getDefinitions ()) {
                        System.out.println("Definition "+de.getLabel()+" is set equivalent to '"+de.getParameter(Parameters.UNLABELLED)+"'");                         
                    }
                    break;
                default:
                    System.out.println("Not recognized option");
            }

            System.out.println("End listing.\n");    
    }

    @Override
    public Component makeNewParseable() {
        return new Lister();
    }      
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = lists the variable or blackBox (which is the netlist) or node or parsedLine\n";
    }      
    
}
