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

import powertreedesigner.device.exception.SimulatingException;
import powertreedesigner.device.Device;
import powertreedesigner.device.command.Command;
import powertreedesigner.device.components.electricComponents.parameter.EngNotation;
import powertreedesigner.device.components.electricComponents.parameter.ParameterBoolean;
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.components.electricComponents.parameter.ParameterInteger;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.components.electricComponents.parameter.waveform.Waveform;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public abstract class Simulator extends Command  {
    private final static boolean DEBUG = false;
    protected static boolean SimulatingSteadyState = false;

    public static boolean isSimulatingSteadyState() {
        return Simulator.SimulatingSteadyState;
    }
  
    
    public Simulator(String name) {   
        super ("simulate"+name, "command simulation");
        
        addSetting(new Setting("tEnd", new ParameterDouble(1.,"s"), "the simulation end time"));        
        addSetting(new Setting("dtMax", new ParameterDouble(1.e-6,"s"), "the simulation max time step"));
        addSetting(new Setting("dtBegin", new ParameterDouble(1.e-9,"s"), "the simulation initial time step"));
        addSetting(new Setting("dtMin", new ParameterDouble(1.e-12,"s"), "the simulation min time step"));
        
        addSetting(new Setting("tSave", new ParameterDouble(0.,"s"), "the time to begin data saving"));        
        addSetting(new Setting("saveTransient", new ParameterBoolean(false), "choose if save transient analisys. If the transient is not saved the source are set constant low value."));        
        addSetting(new Setting("savePoints", new ParameterInteger(1000), "how many calculated point has to be saved for a simulation. First and last are saved by default"));        
        
        addSetting(new Setting("dvMin", new ParameterDouble(0.01e-3,"V"), "the minimum node voltage variation require to increase time step"));
        addSetting(new Setting("dvMax", new ParameterDouble(1e-3,"V"), "the maximum node voltage variation require to decrease time step"));
        
        addSetting(new Setting("diSteadyMax", new ParameterDouble(1e-9,"A"), "the maximum current variation on the current at worst node to be in steady state"));        
        addSetting(new Setting("stopAtSteadyState", new ParameterBoolean(false), "choose if stop when steady state is reached"));
    }      

    @Override
    public boolean hasLabel() {
        return false;
    }
            
    protected void simulate (Device device) throws ParsingException {
        final double tEnd = getParameter("tEnd").getDouble(); 
                
        final boolean saveHistory = getParameter ("saveTransient").getBoolean();
        Waveform.setSteadyState(! saveHistory);
        double tSave = getParameter ("tSave").getDouble();
        double saveTimeStep = (tEnd-tSave)/getParameter ("savePoints").getInteger();
        
        final double dvMax = getParameter ("dvMax").getDouble();
        final double dvMin = getParameter ("dvMin").getDouble();         
        final double diSteadyMax = getParameter ("diSteadyMax").getDouble();  
        final boolean stopAtSteady = getParameter ("stopAtSteadyState").getBoolean();
        
        double lc;        
        Integrator.resetTime(getParameter ("dtMin").getDouble(), getParameter ("dtMax").getDouble(), getParameter ("dtBegin").getDouble());        

        device.setInitialCondition();
        
        System.out.println("Simulation "+getLabel()+" begin");
        
        ProgressBar pb = new ProgressBar(tEnd);
        
        long simulTime = System.nanoTime();
        
        
        if (saveHistory && Integrator.getTn() >= tSave) {
            device.saveToHistory();
            tSave += saveTimeStep;
        }
        
        do {
            
            pb.printProgress(Integrator.getTn());
            
            device.stepForward();           
            
            lc = device.getLastChange();
            if (lc > dvMax) {
                device.stepBackward();                
                if (Integrator.shorterTimeStep()) throw new SimulatingException("at t "+EngNotation.convert(Integrator.getTn())+" dt dropped below the minimum time increment "+getParameter ("dtMin").getDouble());
                Integrator.stepBackwardT();
                if (DEBUG) System.out.println("simulation step back at time "+Integrator.getTn());
                continue;
            } else if (lc < dvMin) {
                if (Integrator.longerTimeStep() && DEBUG) System.out.println("simulation goes faster at time "+Integrator.getTn());
            }
            
            Integrator.nextStep();
            if (saveHistory && Integrator.getTn() >= tSave) {
                device.saveToHistory();
                tSave += saveTimeStep;
            }
        } while (Integrator.getTn() < tEnd && (! stopAtSteady || device.getLastConvergenceBalance() >= diSteadyMax));
        
        device.saveToHistory(); //that is required to have at least 1 saved data        
        
        pb.printDone();
        
        System.out.println("Updated variable TIME");
        
        System.out.println("Simulation end at t="+EngNotation.convert(Integrator.getTn())+"s, simulation time "+EngNotation.convert((System.nanoTime()-simulTime)/1e9)+"s\n");
                
        System.out.println();
        String wrn = device.getWarning();        
        System.out.print ("Component warnings:\n");        
        if (wrn.length() == 0) System.out.println ("none\n");
        else System.out.println (wrn);        
    }

    @Override
    public int getGateNumber() {
        return 0; 
    }

    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        d.clearHistory();
        simulate(d);
    }        
}
