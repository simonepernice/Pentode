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
package powertreedesigner.device.commands.simulator;

import java.util.Iterator;
import java.util.LinkedList;
import powertreedesigner.device.Device;
import powertreedesigner.device.components.electricComponents.components.Component;
import powertreedesigner.device.components.electricComponents.parameter.Parameter;
import powertreedesigner.device.components.electricComponents.parameter.ParameterBoolean;
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.components.electricComponents.parameter.ParameterList;
import powertreedesigner.device.components.electricComponents.parameter.ParameterString;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.components.HistoryComponent;
import powertreedesigner.device.commands.system.Variable;
import powertreedesigner.device.components.electricComponents.parameter.EngNotation;
import powertreedesigner.device.components.electricComponents.parameter.ParameterElectricComponent;
import powertreedesigner.device.components.electricComponents.parameter.ParameterVectorOfDouble;
import powertreedesigner.device.components.electricComponents.parameter.Setting;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public final class SimulatorSteady extends Simulator {
    
    public SimulatorSteady() {
        super("Steady");
        setLabel("Steady");
        addSetting(new Setting("values", new ParameterList(new ParameterVectorOfDouble()),"the vector with values to simulate"));
        addSetting(new Setting("parameter", new ParameterList(new ParameterElectricComponent("", "")), "the parameter to step"));
        addSetting(new Setting("measurementUnit", new ParameterList(new ParameterString("")),"the measurement unit to use for each parameter stepped"));
        try {
            setParameter("stopAtSteadyState", new ParameterBoolean(true));                
        } catch (ParsingException pe) {
            throw new RuntimeException("Internal error at SimulatorTransient");
        }
    }

    @Override
    protected void simulate(Device device) throws ParsingException {
        Parameter command = getParameter("parameter");
        LinkedList<Parameter> commands = command.getList();
        if (commands.isEmpty()) {
            super.simulate(device); 

            final double diSteadyMax = getParameter ("diSteadyMax").getDouble(); 
            if (device.getLastConvergenceBalance() < diSteadyMax) System.out.println("The simulation reached steady state");
            else System.out.println("Simulation warning: the simulation does not reached the steady state, max delta current at node is "+EngNotation.convert(device.getLastConvergenceBalance())+"A"+ "\nThe exceeding nodes are: "+device.getOutOfConvergenceBalance(diSteadyMax));                    
                        
            return;
        }   
        
        if (commands.size() != getParameter("values").getList().size()) throw new ParsingException ("the value and parameter lists should be the same size");        
        
        Iterator<Parameter> imu = getParameter("measurementUnit").getList().iterator();
        Iterator<Parameter> iva = getParameter("values").getList().iterator();
        Iterator<Parameter> ipa = command.getList().iterator();        
                
        final int steppers = commands.size();
                
        HistoryComponent[] hs = new HistoryComponent[steppers];
        String[] prmLbl = new String[steppers];
        double[][] values = new double[steppers][];        
        
        Variable[] ps = new Variable[steppers];
                
        int j = 0;
        Parameter cmpr;        
        while (ipa.hasNext()) {
            cmpr = ipa.next();
            if (cmpr.getProperty() == null) throw new ParsingException ("it is expected component.parameter instead it was found "+cmpr);
            hs[j] = device.getHistoryComponent(cmpr.getLabel());
            prmLbl[j] = cmpr.getProperty();
            Parameter p = hs[j].getParameter(prmLbl[j]);
            if (! (p instanceof ParameterDouble)) throw new ParsingException ("nnly real value parameter can be stepped instead it was find "+prmLbl[j]+" of type "+p.getTypeName());
            
            LinkedList<Double> sh = iva.next().getVectorOfDouble().getData();
            Iterator<Double> ish = sh.iterator();
            values[j] = new double[sh.size()];
            for (int i=0;i<sh.size();++i) values[j][i] = ish.next();
            ps[j] = new Variable(hs[j].getLabel().toUpperCase()+prmLbl[j].toUpperCase(), imu.hasNext() ? imu.next().getString() : "");
            ++j;
        }
                
        int[] index = new int[steppers];
        
        do {
            System.out.print("Step simulation called with parameters: \n");
            for (j = 0; j<steppers; ++j) {
                hs[j].setParameter(prmLbl[j], new ParameterDouble(values[j][index[j]], ""));
                ps[j].storeValues(values[j][index[j]]);
                System.out.println("Component "+hs[j].getLabel()+" parameter "+prmLbl[j]+"="+values[j][index[j]]);
            }
            
            super.simulate(device); 
            
            final double diSteadyMax = getParameter ("diSteadyMax").getDouble(); 
            if (device.getLastConvergenceBalance() < diSteadyMax) System.out.println("The simulation reached steady state");
            else System.out.println("Simulation warning: the simulation does not reached the steady state, max delta current at node is "+EngNotation.convert(device.getLastConvergenceBalance())+"A"+ "\nThe exceeding nodes are: "+device.getOutOfConvergenceBalance(diSteadyMax));                    
            
            for (j = 0; j<steppers; ++j) {
                index[j]+=1; 
                if (index[j] < values[j].length) break;
                else index[j] = 0;
            }                        
        } while (j<steppers);
        
        for (j=0; j<steppers; ++j) {
            device.setVariable(ps[j]);
            System.out.println("Created variable "+ps[j].getLabel());
        }
    }
            
    @Override
    public Component makeNewParseable() {
        return new SimulatorSteady ();
    }  

    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = runs the transient simulation stopping as soon as a steady state is reached only the last point is saved on history\n";
    }
           
}
