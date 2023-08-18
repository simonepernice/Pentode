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

import powertreedesigner.device.commands.printer.Printable;
import powertreedesigner.device.commands.simulator.Simulable;
import powertreedesigner.device.commands.simulator.history.Historeable;
import powertreedesigner.device.commands.simulator.history.VectorOfDouble;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Gate implements Simulable, Historeable, Printable {   
    private double current;
    private final BlackBox owner;
    private Node node;
    private final VectorOfDouble history;

    public Gate(BlackBox owner) {
        this.owner = owner;
        history = new VectorOfDouble("A");
        setInitialCondition();        
    }

    public BlackBox getOwner() {
        return owner;
    }        
    
    public void setLinkToNode (Node n) {
        node = n;
    }
    
    public Node getLinkedNode () {
        return node;
    }    
    
    public double getLinkedNodeVoltage () {
        return node.getValue();
    }

    public void setCurrent(double cur) {
        current = cur;
    }
        
    @Override
    public final void setInitialCondition() {
        current = 0.;
    }

    @Override
    public void stepForward() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void stepBackward() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public double getLastChange() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }      

    @Override
    public double getLastConvergenceBalance() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
        
    @Override
    public double getValue() {
        return current;
    }

    @Override
    public String getWarning() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void saveToHistory() {
        history.addPoint(current);
    }

    @Override
    public void clearHistory() {
        history.clear();
    }        

    @Override
    public VectorOfDouble getHistory() {
        return history;
    }
        
    void setHistoryLabel(int gate, String label) {
        history.setLabel(label+" gate"+gate);
    }

    @Override
    public String getPrintString() {
        return history.getPrintString();
    }
    
}
