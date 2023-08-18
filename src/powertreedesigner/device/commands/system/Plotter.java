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

import powertreedesigner.device.exception.PlottingException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import powertreedesigner.device.Device;
import powertreedesigner.device.command.Command;
import powertreedesigner.device.components.electricComponents.components.Component;
import powertreedesigner.device.components.electricComponents.parameter.Parameter;
import powertreedesigner.device.components.electricComponents.parameter.ParameterList;
import powertreedesigner.device.components.electricComponents.parameter.ParameterOptions;
import powertreedesigner.device.components.electricComponents.parameter.ParameterString;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.commands.simulator.history.SimulationHistories;
import powertreedesigner.device.components.electricComponents.parameter.ParameterElectricComponent;
import powertreedesigner.device.components.electricComponents.parameter.Parameters;
import powertreedesigner.device.components.electricComponents.parameter.Setting;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Plotter extends Command {

    public Plotter() {
        super ("plot", "command work output");
        
        addSetting(new Setting(Parameters.UNLABELLED, new ParameterList (new ParameterElectricComponent(null, null)), "the list of parameters to show"));
        addSetting(new Setting("outputFormat", new ParameterOptions ("png", "jpeg", "bmp", "wbmp", "gif", "csv", "screen"), "the output file format"));   
        addSetting(new Setting("fieldsSeparator", new ParameterString (";"), "the chars used as field separator"));   
        addSetting(new Setting("decimalPoint", new ParameterString ("."), "the chars used as decimal point"));   
    }

    @Override
    public int getGateNumber() {
        return 0;
    }

    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        System.out.println("Plotting "+getLabel()+"."+getParameter("outputFormat").getPrintString());
        
        SimulationHistories sh = new SimulationHistories();
        for (Parameter cn : getParameter(Parameters.UNLABELLED).getList()) {
            sh.addHistory(d.getSimulationHistory(cn.getLabel(), cn.getProperty()));
        }
        
        Parameter p = getParameter("outputFormat");
        
        if (sh.getSize()<2 && p.getInteger() < 5) throw new ParsingException ("It is required to provide at least 2 variables to plot a diagram ");
        
        try {
            
            switch (p.getInteger()) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    sh.savePlotDrawing(getLabel(), p.getString());
                    break;
                case 5:
                    sh.savePlotData(getLabel(), getParameter("decimalPoint").getString(), getParameter("fieldsSeparator").getString());                    
                    break;
                case 6:
                    sh.savePlotData(new OutputStreamWriter(System.out), getParameter("decimalPoint").getString(), getParameter("fieldsSeparator").getString());                    
                    break;
                default:
                    
            }
        } catch (IOException ioe) {
            throw new PlottingException("error tring to save the file "+ioe.getMessage());
        }
        

        System.out.println("Plotting end\n");
    }

    @Override
    public Component makeNewParseable() {
        return new Plotter();
    }

    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = saves on a file csv, a graphic plot or on the screen the history of the given list of parameters. On graph the first parameter is used for X axis, the outhers are plotted as Ys. If measurement units are defined they are visible on the axis.\n";
    }
    
}
