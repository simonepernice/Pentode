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
package powertreedesigner.device.commands.helper;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import powertreedesigner.PowerTreeDesigner;
import powertreedesigner.device.Device;
import powertreedesigner.device.command.Command;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.components.Component;
import powertreedesigner.device.components.electricComponents.parameter.ParameterOptions;
import powertreedesigner.device.components.electricComponents.parameter.ParameterString;
import powertreedesigner.device.components.electricComponents.parameter.Parameters;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.parser.Parser;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Helper extends Command {

    public Helper() {
        super("help", "command");
        
        addSetting(new Setting(Parameters.UNLABELLED, new ParameterString (""), "the first chars of the command requiring help"));
        addSetting(new Setting("out", new ParameterOptions ("scr", "fle"), "the output of the help goes to the screen or to a file"));
        addSetting(new Setting("frmt", new ParameterOptions ("read", "latex"), "the output format has to be read or in latex format"));
        addSetting(new Setting("fleNme", new ParameterString ("help"), "the output file name if was selected to output to a file"));
        
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
            
            if (PowerTreeDesigner.donated) {
                
                String help;
                if (getParameter("frmt").getInteger() == 0) help = (Parser.getCommandHelp(getParameter(Parameters.UNLABELLED).getString()));
                else  help = (Parser.getLatexCommandHelp(getParameter(Parameters.UNLABELLED).getString()));
                
                if (getParameter("out").getInteger() == 0) {
                    System.out.println("Helping: the list of commands/components beginning with the given word is available below: ");
                    System.out.println (help);
                } else {
                    System.out.println("Helping: the list of commands/components beginning with the given word is available on the file: "+getParameter("fleNme").getString());
                    Writer writer = null;

                    try {
                        writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(getParameter("fleNme").getString()), "utf-8"));
                        
                        writer.write(help);
                                
                    } catch (IOException ex) {
                        System.out.println("It was not possible to write the file because: "+ex.getMessage());
                    } finally {
                       try {
                           writer.close();
                       } catch (Exception ex) {/*ignore*/}
                    }                    
                    
                }
            }    
            else System.out.println("Please donate to have the manual");
            System.out.println("Help end.\n");    
    }

    @Override
    public Component makeNewParseable() {
        return new Helper();
    }      
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = returns the help of all the component beginning with the given text\n";
    }
    
}
