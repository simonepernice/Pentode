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
import powertreedesigner.device.Device;
import powertreedesigner.device.commands.drawer.Drawable;
import powertreedesigner.device.commands.drawer.Drawer;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.parameter.ParameterBoolean;
import powertreedesigner.device.commands.drawer.Place;
import powertreedesigner.device.commands.simulator.history.VectorOfDouble;
import powertreedesigner.device.components.electricComponents.parameter.ParameterReadOnly;
import powertreedesigner.device.components.electricComponents.parameter.Setting;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public abstract class BlackBox extends ElectricComponent {    
    private final int gateNumber;
    private final Gate[] gates;  
    protected boolean enable;
    
    public BlackBox(String name, String type, int gateNumber) {
        super(name, type, false);
        
        this.gateNumber = gateNumber;
        
        gates = new Gate[gateNumber];
        for (int i=0; i<gateNumber; ++i) {
            gates[i] = new Gate (this);
        }

        addSetting(new Setting("enb", new ParameterBoolean(true), "to enable or disable the device"));
        addSetting(new Setting("pLos", new ParameterReadOnly("pLos", this), "the power loss computed with efficiency"));
        try {
            setParameter("drwPrm", getParameter("drwPrm").getPrintString()+",enb", null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error setting drwPrm in BlackBox class");
        }
                
        resetDrawed();
    }        
    
    @Override
    public String getReadOnlyParameter(String paramLabel) {        
        if (paramLabel.equals("lnks")) {
            StringBuilder links = new StringBuilder();
            for (int i=0;i<gateNumber; ++i) {                                
                links.append(" Gate").append(i).append(" linked to ").append(getGateNode(i).getLabel()).append(", ");                                
            }
            links.setLength(links.length()-2);
            return links.toString();
        }
        return super.getReadOnlyParameter(paramLabel);
    }
    
    @Override
    public final void setLabel(String label) {
        super.setLabel(label);
        for (int i=0; i<gateNumber; ++i) gates[i].setHistoryLabel (i, label);
    }
        
    @Override
    public int getGateNumber() {
        return gateNumber;
    }
                    
    public void setGateNode (int gate, Node n) {
        gates[gate].setLinkToNode (n);
    }
    
    @Override
    public Gate getGate (int gate) {
        return gates[gate];
    }
    
    public Node getGateNode (int gate) {
        return gates[gate].getLinkedNode();
    }
    
    public double getGateVoltage (int gate) {
        return gates[gate].getLinkedNodeVoltage();
    }
    
    public double getGateVoltage (int g0, int g1) {
        return gates[g0].getLinkedNodeVoltage()-gates[g1].getLinkedNodeVoltage();
    }
    
    public double getGateCurrent (int gate) {
        return gates[gate].getValue();
    }
    
    public void setGateCurrent (int gate, double current) {
        gates[gate].setCurrent(current);
    }

    @Override
    public void setInitialCondition() throws ParsingException {
        enable = getParameter("enb").getBoolean();
        for (Gate g : gates) g.setInitialCondition();
    }

    @Override
    public void stepBackward() {
        //To be overridden by components with memory like inductors
    }

    @Override
    public final double getLastChange() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getLastConvergenceBalance() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }        

    @Override
    public final double getValue() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        d.addBlackBox(this, nodes);        
    }    

    @Override
    public String getPrintString() {
        return getName()+" "+getLabel()+" Gate0 Current: "+gates[0].getPrintString(); 
    }
    
    @Override
    public final void drawChildLinkedToFather(Drawable father) {     
        canvas.setPaint(getDrawFillColor());
        canvas.fillRect(place.getXCorner(), place.getYCorner(), Place.getWidth(), Place.getHeight());
        canvas.setPaint(Drawer.getFOREGROUNDCOLOR());
        canvas.drawRect(place.getXCorner(), place.getYCorner(), Place.getWidth(), Place.getHeight());
        
        super.drawChildLinkedToFather (father);
    }     

    @Override
    public VectorOfDouble getHistory() {
        return gates[0].getHistory();
    }
    
    @Override
    public void saveToHistory() {
        for (int i=0; i<gateNumber; ++i) {
            gates[i].saveToHistory();
        }
    }

    @Override
    public void clearHistory() {
        for (int i=0; i<gateNumber; ++i) {
            gates[i].clearHistory();
        }
    }   
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Number of gates = "+gateNumber+"\n";
    }     

}
