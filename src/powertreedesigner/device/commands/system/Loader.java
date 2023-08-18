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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import powertreedesigner.device.Device;
import powertreedesigner.device.command.Command;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.components.Component;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Loader extends Command {

    public Loader() {
        super("load", "command system");
    }

    @Override
    public int getGateNumber() {
        return 0; 
    }
        
    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        String fileName = getLabel()+".ptd";
        System.out.println("Loading file "+fileName);
        LinkedList<String> lines = new LinkedList<>();
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine()) != null) lines.add(line);
            br.close();              
        } catch (Exception e) {
            throw new ParsingException("error loading file "+fileName+" "+e.getMessage());
        }
        d.getParser().parse(lines);        
        System.out.println("Loading file end");
    }

    @Override
    public Component makeNewParseable() {
        return new Loader();
    }      
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = a command to load  and parse the lines found in the given fileName\n";
    }      
    
}
