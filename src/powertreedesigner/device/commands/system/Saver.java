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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import powertreedesigner.device.Device;
import powertreedesigner.device.command.Command;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.components.Component;
import powertreedesigner.device.components.electricComponents.parameter.ParameterString;
import powertreedesigner.device.components.electricComponents.parameter.Parameters;
import powertreedesigner.device.components.electricComponents.parameter.Setting;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Saver extends Command {

    public Saver() {
        super("save", "command system");
        
        addSetting(new Setting(Parameters.UNLABELLED, new ParameterString (""), "the file name where the current parsed lines are saved"));
    }

    @Override
    public boolean hasLabel() {
        return false;
    }
        
    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        String fileName = getParameter(Parameters.UNLABELLED).getString();
        if (fileName.length() == 0) fileName = d.getLabel();
        fileName += ".ptd";
        d.getParser().getLinesToParse().discardLastParsedLine();
        save (fileName, d.getParser().getLinesToParse().getInputLines().iterator());             
    }
    
    protected void save (String fileName, Iterator<String> lines) throws ParsingException {
        System.out.println("Saving parsed lines to file "+fileName);
        try {
            BufferedWriter  bw = new BufferedWriter(new FileWriter(fileName));
            while (lines.hasNext()) {
                bw.write(lines.next());
                bw.write('\n');
            }
            bw.close();              
        } catch (Exception e) {
            throw new ParsingException("error saving file "+fileName+" "+e.getMessage());
        }  
        System.out.println("Saving file as "+fileName+" end");
    }
    

    @Override
    public Component makeNewParseable() {
        return new Saver();
    }      

    @Override
    public int getGateNumber() {
        return 0;
    }
        
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = saves the current parsed lines (which contains the interactive session input). It is possible to provide an optional fileName otherwise the device fileName is used.\n";
    }      
}
