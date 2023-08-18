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
package powertreedesigner.device.commands.iterations;

import java.util.Iterator;
import java.util.List;
import powertreedesigner.device.Device;
import powertreedesigner.device.command.Command;
import powertreedesigner.device.commands.simulator.history.VectorOfDouble;
import powertreedesigner.device.commands.system.Variable;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.components.Component;
import powertreedesigner.device.components.electricComponents.components.HistoryComponent;
import powertreedesigner.device.components.electricComponents.parameter.ParameterVectorOfDouble;
import powertreedesigner.device.components.electricComponents.parameter.Parameters;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.parser.linesToParse.LinesToParse;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class For extends Command {
    static {
        LinesToParse.addIterationCommand("for", "next");
    }    

    public For() {
        super("for", "command iteration for");
        addSetting(new Setting(Parameters.UNLABELLED, new ParameterVectorOfDouble(), true, "the array of elements to go through"));
    }

    @Override
    public int getGateNumber() {
        return 0; 
    }
        
    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        HistoryComponent v = d.getHistoryComponent(getLabel());
        if (! (v instanceof Variable)) throw new ParsingException ("the first element of a for/next has to be a variable");
        VectorOfDouble sh = v.getHistory();
        
        List<Double> list = getParameter(Parameters.UNLABELLED).getVectorOfDouble().getData();
        Iterator<Double> iList = list.iterator();
        
        if (sh.getData().size() != 1) {
            sh.clear();
            sh.addPoint(iList.next());
        } else {
            double cd = sh.getData().getFirst();
            while (iList.hasNext()) {
                if (iList.next() == cd) break;
            }
            if (! iList.hasNext()) {
                d.getParser().getLinesToParse().goToEndLoop();
                return;
            }
            sh.clear();
            sh.addPoint(iList.next());
        }
    }

    @Override
    public Component makeNewParseable() {
        return new For();
    }      
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = This command defines the begin of the iteration for vector / next which go through all the vector elements. The variable should not be changed within the cycle and should not begin with a value of the vector. To enter a multi line statement in interactive mode complete the lines with symbol +\n";
    }      
    
}
