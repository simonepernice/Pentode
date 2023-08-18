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
package powertreedesigner.device.parser;

import powertreedesigner.device.exception.ParsingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import powertreedesigner.device.components.ConverterLDO;
import powertreedesigner.device.components.SourceVoltage;
import powertreedesigner.device.components.DrainResistor;
import powertreedesigner.device.components.electricComponents.Node;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import powertreedesigner.device.Device;
import powertreedesigner.device.exception.PTDException;
import powertreedesigner.device.commands.system.Modifier;
import powertreedesigner.device.commands.drawer.Drawer;
import powertreedesigner.device.commands.system.Quitter;
import powertreedesigner.device.commands.system.Plotter;
import powertreedesigner.device.commands.printer.Printer;
import powertreedesigner.device.commands.simulator.SimulatorSteady;
import powertreedesigner.device.commands.simulator.SimulatorTransient;
import powertreedesigner.device.commands.helper.Helper;
import powertreedesigner.device.commands.iterations.Break;
import powertreedesigner.device.commands.iterations.Continue;
import powertreedesigner.device.commands.iterations.Do;
import powertreedesigner.device.commands.iterations.For;
import powertreedesigner.device.commands.iterations.If;
import powertreedesigner.device.commands.iterations.IfEnd;
import powertreedesigner.device.commands.iterations.Loop;
import powertreedesigner.device.commands.iterations.Next;
import powertreedesigner.device.commands.iterations.While;
import powertreedesigner.device.commands.iterations.WhileEnd;
import powertreedesigner.device.commands.system.Currencier;
import powertreedesigner.device.commands.system.Definer;
import powertreedesigner.device.commands.system.Deleter;
import powertreedesigner.device.commands.system.Lister;
import powertreedesigner.device.commands.system.Loader;
import powertreedesigner.device.commands.system.Saver;
import powertreedesigner.device.commands.system.Setup;
import powertreedesigner.device.commands.system.Variable;
import powertreedesigner.device.components.ConverterBoost;
import powertreedesigner.device.components.ConverterBuck;
import powertreedesigner.device.components.ConverterControlledSwitch;
import powertreedesigner.device.components.ConverterDiode;
import powertreedesigner.device.components.ConverterInductor;
import powertreedesigner.device.components.ConverterResistor;
import powertreedesigner.device.components.ConverterSwitch;
import powertreedesigner.device.components.DrainCurrent;
import powertreedesigner.device.components.DrainPower;
import powertreedesigner.device.components.SourceBattery;
import powertreedesigner.device.components.SourceCurrent;
import powertreedesigner.device.components.electricComponents.components.Component;
import powertreedesigner.device.components.electricComponents.parameter.Parameters;
import powertreedesigner.device.parser.linesToParse.LinesToParse;
import powertreedesigner.device.parser.linesToParse.TokenList;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Parser  {
    private static final boolean DEBUG = false;
    private static final HashMap <String, Component> DICTIONARY;
    
    private final Device device;
    private final LinesToParse workingLine;
        
    static {
        DICTIONARY = new HashMap<>();
        Component[] keywords = {
            new ConverterLDO(), new ConverterBuck(), new ConverterBoost (), new ConverterResistor(),
            new ConverterDiode(), new ConverterSwitch (), new ConverterControlledSwitch(),
            new ConverterInductor(),
            new DrainResistor(), new DrainCurrent(), new DrainPower (),
            new SourceVoltage(), new SourceCurrent(), new SourceBattery (),
            new Node (), 
            new SimulatorTransient (), new SimulatorSteady(), 
            new Modifier(), new Deleter(), 
            new Quitter (), new Helper (), new Lister(),
            new Loader (), new Saver(),
            new Drawer(), new Plotter(), new Printer(), 
            new Variable(),
            new Definer(),
            new Currencier (),
            new While (), new WhileEnd(),
            new Do (), new Loop(),
            new Break(), new Continue(),
            new If(), new IfEnd (),
            new For(), new Next(),
            new Setup()
        };
        
        for (Component p : keywords)
            if (DICTIONARY.put(p.getName(), p) != null) throw new RuntimeException ("Two commands have the same name");        
    }

    public Parser(Device device) {
        this.device = device;
        workingLine = new LinesToParse(device);
    }    
    
    public static String getCommandHelp (String beginWith) {       
        StringBuilder pd = new StringBuilder();
        ArrayList<String> helps = new ArrayList<> (DICTIONARY.size());
        Collections.sort(helps);
        for (Component p : DICTIONARY.values()) {
            if (p.getName().startsWith(beginWith)) {
                helps.add(p.getHelp());
            }
        }
        
        Collections.sort(helps);
        
        pd.setLength(0);
        for (String h : helps) pd.append(h);
        
        return pd.toString();                            
    }
    
    public static String getLatexCommandHelp (String beginWith) {  
        HashMap<String, ArrayList<String>> categoryHelp = new HashMap<>();
        
        for (Component p : DICTIONARY.values()) {
            if (! p.getName().startsWith(beginWith)) continue;
            String t = p.getType();
            if (! categoryHelp.containsKey(t)) {
                categoryHelp.put(t, new ArrayList<>());
            }
            categoryHelp.get(t).add("\\subsection{"+p.getName()+"}\n"+p.getHelp().substring(1).replace("\n", "\\\\\n"));
        }                
        
        List<String> types = new ArrayList<String> (categoryHelp.keySet());
        Collections.sort(types);
        
        StringBuilder help = new StringBuilder ();
        
        for (String t : types) {
            help.append("\\section{"+t+"}\n");
            for (String h : categoryHelp.get(t)) {
                help.append(h);
            }
        }
        
        return help.toString();
    }
    
    public static   <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }
    
    public void parse (LinkedList<String> linesToParse) throws ParsingException {
        workingLine.addLines(linesToParse);
        TokenList tokens;
        String parameter;
        Parseable element;
        String command;
        String[] nodes = new String [3];
        int j;

        while (workingLine.hasNext()) {
            try {
                tokens = workingLine.getNextLineSplitted();
            } catch (PTDException e) {
                printErrorStdout(e);
                continue;
            }
            if (DEBUG) System.out.println("Tokens "+tokens);
            if (tokens == null) break;

            command = tokens.next();
            element = DICTIONARY.get(command);
            if (element == null) {
                printErrorStdout(new ParsingException ("unrecognized component/command "+command));
                continue;
            }
            element = element.makeNewParseable();

            if (element.hasLabel()) element.setLabel(tokens.next());

            for (j = 0; j < element.getGateNumber(); ++j)
                nodes[j]=tokens.next();

            while (tokens.hasNext()) {
                if (DEBUG) System.out.println("Tokens "+tokens);
                if (tokens.hasNext("#")) break;
                if (tokens.hasNext(2, "=")) {
                    parameter = tokens.next();
                    tokens.next();//skip '='                    
                } else  {                        
                    parameter = Parameters.UNLABELLED;                        
                }
                try {
                    element.setParameter(parameter, tokens, device);                     
                } catch (ParsingException nfe) {
                    printErrorStdout(nfe);
                    while (tokens.hasNext()) {
                        if (tokens.hasNext(2, "=")) break;
                        else tokens.next();
                    }
                }                   
            }
            
            String misses = element.missingRequiredParameters();
            if (misses != null) {
                printErrorStdout (new ParsingException ("the component "+command+" cannot be added because it is missing the following required parameters "+misses)) ;
                continue; 
            }

            try {
                element.addToDevice(device, nodes);
            } catch (ParsingException pe) {
                printErrorStdout(pe);
            } catch (RuntimeException re) {
                printErrorStdout(new PTDException(re.getMessage()));
            }
        }          
  
    }    
    
    private void printErrorStdout (PTDException e) {
        System.out.println(e.getTypeMessage());
        System.out.println("At line number "+workingLine.getLineNumber());
        System.out.println("On the line:\n"+workingLine.getLine());
        
    }
    
    public LinesToParse getLinesToParse () {
        return workingLine;
    }
     
}
