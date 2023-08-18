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
package powertreedesigner.device.components.electricComponents;

import powertreedesigner.device.components.electricComponents.components.ElectricComponent;
import java.awt.Color;
import powertreedesigner.device.components.electricComponents.components.Component;
import java.util.LinkedList;
import powertreedesigner.device.Device;
import powertreedesigner.device.commands.drawer.Drawable;
import powertreedesigner.device.commands.drawer.Drawer;
import powertreedesigner.device.components.electricComponents.parameter.EngNotation;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.commands.drawer.Place;
import powertreedesigner.device.commands.simulator.Integrator;
import powertreedesigner.device.commands.simulator.history.VectorOfDouble;
import powertreedesigner.device.components.electricComponents.parameter.ParameterColor;
import powertreedesigner.device.components.electricComponents.parameter.ParameterReadOnly;
import powertreedesigner.device.components.electricComponents.parameter.Setting;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Node extends ElectricComponent {
    private final LinkedList<Gate> gates;
    private double capacitance, deltaCurrent;
    private final VectorOfDouble history;
    private final Integrator voltIntgr;
               
    public Node() {
        super("node", "component node", true);
        
        gates = new LinkedList<>();
        
        addSetting(new Setting("vBgn", new ParameterDouble (0.,"V"),"the initial voltage used to begin simulation"));        
        addSetting(new Setting("cap",new ParameterDouble (100.e-6,"F"), "the capacity linked to the node"));        
        addSetting(new Setting("vol", new ParameterReadOnly("vol", this), "the node voltage"));
        addSetting(new Setting("drwClr", new ParameterColor(Color.gray), "the fill color used for drawing "));
        history = new VectorOfDouble("V");
        
        try {
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",vol,cap", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in Node class");
        }        
        
        voltIntgr = new Integrator ();
    }
    
   @Override
    public String getReadOnlyParameter(String paramLabel) {
        if (paramLabel.equals("vol")) return EngNotation.convert(getValue())+"V";      
        if (paramLabel.equals("lnks")) {
            StringBuilder links = new StringBuilder();
            for (Gate g : gates) {                                
                links.append(g.getOwner().getLabel()).append(", ");                                
            }
            links.setLength(links.length()-2);
            return links.toString();
        }        
        return super.getReadOnlyParameter(paramLabel);        
    }       
    
    
    public void addGate (Gate gate) {
        for (Gate cg : gates) 
            if (cg == gate) throw new RuntimeException ("The same gate was added more than one at node "+getLabel());
        gates.add(gate);
    }
    
    public void removeGate (Gate gate) {
        gates.remove(gate);
    }
    
    public boolean hasGates () {
        return gates.size() != 0;
    }
    
    public LinkedList<Gate> getGates () {
        return gates;
    }

    @Override
    public final void setLabel(String label) {
        super.setLabel(label); 
        history.setLabel(label);
    }

    @Override
    public void setInitialCondition() throws ParsingException {
        voltIntgr.setY0(getParameter("vBgn").getDouble()); 
        capacitance = getParameter("cap").getDouble(); 
        if (capacitance <= 0.) throw new ParsingException ("To converge it is required a capacitance > 0. at every node while it was found "+capacitance+" at node "+getLabel());
    }

    @Override
    public void stepForward() {
        deltaCurrent = 0.;
        for (Gate g : gates) deltaCurrent -= g.getValue();
        voltIntgr.addDyDt(deltaCurrent/capacitance);
    }

    @Override
    public double getLastChange() {
        return voltIntgr.getLastChange();
    }

    @Override
    public double getLastConvergenceBalance() {
        return Math.abs(deltaCurrent);
    }
        
    @Override
    public void stepBackward() {
        voltIntgr.stepBackwardY();
    }

    @Override
    public double getValue() {
        return voltIntgr.getYnp1();
    }

    @Override
    public int getGateNumber() {
        return 0;
    }

    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        d.addNode(this);
    }

    @Override
    public Component makeNewParseable() {
        return new Node(); 
    }

    @Override
    public String getPrintString() {
        return "Node "+getLabel()+" Voltage: "+EngNotation.convert(voltIntgr.getYnp1())+'V';
    }      

    @Override
    public String getWarning() {
        return null;
    }

    @Override
    public void saveToHistory() {
        history.addPoint(voltIntgr.getYnp1());
    }    

    @Override
    public VectorOfDouble getHistory() {
        return history;
    }

    @Override
    public void clearHistory() {
        history.clear();
    }
            
    @Override
    public final void drawChildLinkedToFather(Drawable father) {    
        canvas.setPaint(getDrawFillColor());
        canvas.fillRoundRect(place.getXCorner(), place.getYCorner(), Place.getWidth(), Place.getHeight(), Place.getArcWidth(), Place.getArcHeight());
        canvas.setPaint(Drawer.getFOREGROUNDCOLOR());
        canvas.drawRoundRect(place.getXCorner(), place.getYCorner(), Place.getWidth(), Place.getHeight(), Place.getArcWidth(), Place.getArcHeight());
        
        super.drawChildLinkedToFather (father);
    }     

    @Override
    public String getHelp() {
        return super.getHelp()+"-Class = voltage node\n-Function = is a node of the power tree. It is intrinsecally defined when a component is liked to one ore more nodes. So usually it is not required to define it. It can be defined to set its capacity and initial voltage. If defined it has to be declared before its usage.\n";
    }
}
