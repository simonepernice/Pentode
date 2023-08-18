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
package powertreedesigner.device;

import java.awt.Color;
import java.util.ArrayList;
import powertreedesigner.device.commands.simulator.Simulable;
import powertreedesigner.device.commands.drawer.Place;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.Node;
import powertreedesigner.device.components.electricComponents.BlackBox;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import powertreedesigner.device.commands.system.Definer;
import powertreedesigner.device.commands.drawer.Children;
import powertreedesigner.device.commands.simulator.history.VectorOfDouble;
import powertreedesigner.device.commands.drawer.Drawable;
import powertreedesigner.device.commands.simulator.Integrator;
import powertreedesigner.device.commands.simulator.history.Historeable;
import powertreedesigner.device.components.electricComponents.components.HistoryComponent;
import powertreedesigner.device.commands.system.Variable;
import powertreedesigner.device.components.electricComponents.Gate;
import powertreedesigner.device.components.electricComponents.components.ElectricComponent;
import powertreedesigner.device.components.electricComponents.parameter.EngNotation;
import powertreedesigner.device.components.electricComponents.parameter.currency.Currency;
import powertreedesigner.device.components.electricComponents.parameter.currency.Money;
import powertreedesigner.device.parser.Parser;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public final class Device implements Simulable, Drawable, Historeable {    
    private final String label;
    private final Parser parser;
    private final HashMap<String, BlackBox> blackBoxes;
    private final HashMap<String, Node> nodes;
    private final HashMap<String, Variable> variables;
    private final HashMap<String, Definer> definitions;
    private final Children sources;
    private final Variable time;
    private Variable cost;

    public Device(String label) throws ParsingException {
        this.label = label;
        blackBoxes = new HashMap<>();
        nodes = new HashMap<>();
        variables = new HashMap<>();
        definitions = new HashMap<>();
        sources = new Children();
        
        checkLabel(label);
        
        time = new Variable("TIME", "s");
        time.setLabel("TIME");                
        try {
            time.addToDevice(this, null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in Device constructor");
        }
        
        updateCost ();
        
        parser = new Parser(this);
    }
    
    public void updateCost () {
        cost = new Variable("COST", Money.CURRENCIES.getBaseCurrency().getSymbol());
        cost.setLabel("COST");                
        try {
            cost.addToDevice(this, null);
        } catch (ParsingException ex) {
            throw new RuntimeException("Error in Device constructor");
        }  
        
        for (BlackBox  b  : blackBoxes.values()) {
            cost.storeValues (b.getParameter("cst").getMoney().toDoubleInBaseCurrency());
        }
        
        for (Node  n  : nodes.values()) {
            cost.storeValues (n.getParameter("cst").getMoney().toDoubleInBaseCurrency());
        }
    }

    public Parser getParser() {
        return parser;
    }

    public Collection<BlackBox> getBlackBoxes() {
        return blackBoxes.values();
    }

    public Collection<Node> getNodes() {
        return nodes.values();
    }

    public Collection<Variable> getVariables() {
        return variables.values();
    }       
    
    public Collection<Definer> getDefinitions() {
        return definitions.values();
    }       
    
    public Collection<ElectricComponent> getElectricComponents () {
        LinkedList<ElectricComponent> result = new LinkedList<> ();
        result.addAll(blackBoxes.values());
        result.addAll(nodes.values());
        return result;
    }    
    
    public void addDefinition (Definer d) throws ParsingException {
        checkLabel(d.getLabel());
        
        definitions.put(d.getLabel(), d);
    }
    
    public String replaceDefinition (String s) {
        for (String lbl : definitions.keySet())
            s = s.replace(lbl, definitions.get(lbl).getPrintString()); 
        
        return s;
    }
                
    public void addBlackBox (BlackBox b, String[] nodeNames) throws ParsingException {
        checkLabel(b.getLabel());
                
        final int si = b.getGateNumber();
        Node n;
        for (int i=0; i<si; ++i) {
            n = getAndAddNode(nodeNames[i]);
            
            b.setGateNode(i, n);
            
            n.addGate(b.getGate(i));            
        }
        
        blackBoxes.put(b.getLabel(), b);
        
    }
    
    private Node getAndAddNode (String name) {
        if (nodes.containsKey(name)) return nodes.get(name);
        Node n = new Node ();
        n.setLabel(name);
        nodes.put(name, n);
        return n;
    }    
    
    public void addNode (Node n) throws ParsingException {
        checkLabel(n.getLabel());
        nodes.put(n.getLabel(), n);
    }
    
    public void delete (String label) throws ParsingException {
        {
            BlackBox bb = blackBoxes.get(label);        
            if (bb != null) {
                for (int i=0; i<bb.getGateNumber(); i++) {
                    bb.getGateNode(i).removeGate(bb.getGate(i));
                }
                blackBoxes.remove(label);
                return;
            }
        }
         
        {
            Node n = nodes.get(label);
            if (n != null) {
                if (n.hasGates ()) {
                    StringBuilder gates = new StringBuilder();
                    for (Gate g : n.getGates()) gates.append(g.getOwner().getLabel()).append(", "); 
                    throw new ParsingException ("It is not possible to delete the required node because it has some blackbox gate alreay linked to it: "+gates.toString());
                }
                nodes.remove(label);
                return;
            }
            
        }
        
        {
            Variable v = variables.get(label);
            if (v != null) {   
                boolean isUC = true;
                for (char c : label.toCharArray()) 
                    if (Character.isLowerCase (c)) {
                        isUC= false; 
                        break;
                    }
                if (isUC) {
                    throw new ParsingException ("It is not possible to delete upper case variables because they are used internally");
                }
                variables.remove(label);
                return;
            }
            
        }  
        
        {
            Definer d = definitions.get(label);
            if (d != null) {
                definitions.remove(label);
                return;
            }
         
        }
        
        {
            Currency cu = Money.CURRENCIES.getCurrency(label);
            if (cu != null) {
                if (Money.CURRENCIES.getBaseCurrency().equals(cu)) throw new ParsingException ("It is not possible to delete the base currency");
                Money.CURRENCIES.deleteCurrency(label);
                return;
            }
            
        }        
        
        throw new ParsingException ("It was not possible to find any element to delete matching the given label");
    }
    public void setVariable (Variable v) throws ParsingException {
        String lbl = v.getLabel();
        if (variables.containsKey(lbl)) {
            variables.put(lbl, v);
            return;
        }
        checkLabel(lbl);
        variables.put(lbl, v);
    }
    
    private void checkLabel (String label) throws ParsingException {
        if (label.length() == 0) throw new RuntimeException ("internal error in checkLabel");
        if (! label.matches("[a-zA-Z_]+[a-zA-Z_0-9]*"))  throw new ParsingException ("the label "+label+" is not appropriate syntax: labels have to begin by [a-zA-Z_] and can be optionally followed by [a-zA-Z_0-9]");
        
        if (blackBoxes.containsKey(label)) throw new ParsingException ("the label "+label+" was already used for a blackBox ");
        if (nodes.containsKey(label)) throw new ParsingException ("the label "+label+" was already used for a node");
        if (variables.containsKey(label)) throw new ParsingException ("the label "+label+" was already used for a variable");
        if (definitions.containsKey(label)) throw new ParsingException ("the label "+label+" was already used for a definition");
    }
    
    public HistoryComponent getHistoryComponent (String label) throws ParsingException {
        HistoryComponent c = blackBoxes.get(label);
        if (c != null) return c;
        c = nodes.get(label);
        if (c != null) return c;
        c = variables.get(label);
        if (c != null) return c;
        throw new ParsingException ("the component "+label+" is not defined");
    }     
    
    public VectorOfDouble getSimulationHistory (String label, String property) throws ParsingException {
        HistoryComponent c = getHistoryComponent(label) ;

         if (property == null) return c.getHistory();            
         return c.getGate(Integer.parseInt(property)).getHistory();

    }  

    public String getPrintString (String label, String property) throws ParsingException {
        HistoryComponent c = getHistoryComponent(label) ;

         if (property == null) {
                 StringBuilder allP = new StringBuilder();
                 ArrayList<String> prm = new ArrayList<>(c.getParameters());
                 Collections.sort(prm);
                 for (String p : prm) allP.append(c.getParameterString(p)).append(",\n");
                 if (allP.length()>2) allP.setLength(allP.length()-2);
                 return allP.toString();
         }
         
        if (property.length()>0 && Character.isLetter(property.charAt(0))) return c.getParameterString(property);

        int gate;
        try {
          gate = Integer.parseInt(property);
        } catch (NumberFormatException nfe) {
           throw new ParsingException("it was expected a gate number while was found "+property);
        }
        if (gate>=c.getGateNumber()) return c.getHistory().getPrintString();
        return c.getGate(gate).getPrintString();
    }     

    public String getLabel() {
        return label;
    }

    @Override
    public void saveToHistory() {
        time.storeValues(Integrator.getTn());
        for (BlackBox b : blackBoxes.values()) b.saveToHistory();
        for (Node n : nodes.values()) n.saveToHistory();
    }

    @Override
    public VectorOfDouble getHistory() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void clearHistory() {
        time.clearHistory();
        for (BlackBox b : blackBoxes.values()) b.clearHistory();
        for (Node n : nodes.values()) n.clearHistory();
    }
            
    @Override
    public void resetDrawed  () {
        for (BlackBox b : blackBoxes.values()) b.resetDrawed();
        for (Node n : nodes.values()) n.resetDrawed();
        sources.resetDrawed();
    }    

    @Override
    public void setInitialCondition() throws ParsingException {
        for (BlackBox b : blackBoxes.values()) b.setInitialCondition(); 
        for (Node n : nodes.values()) n.setInitialCondition(); 
    }

    @Override
    public void stepForward() {
        while (Integrator.hasMoreSubStep()) {
            for (BlackBox b : blackBoxes.values()) b.stepForward();         
            for (Node n : nodes.values()) n.stepForward(); 
            Integrator.nextSubStep();
        }
    }

    @Override
    public void stepBackward() {
        for (BlackBox b : blackBoxes.values()) b.stepBackward(); 
        for (Node n : nodes.values()) n.stepBackward(); 
    }

    @Override
    public double getLastChange() {
        return getMaximumChange();
    }
    
    private double getMaximumChange () {
        double v = 0.;
        Collection<Node> ln = nodes.values();
        for (Node n : ln) if (n.getLastChange()>v) v = n.getLastChange();
        return v;        
    }
    
    private double getAverageChange () {
        double v = 0.;
        Collection<Node> ln = nodes.values();
        for (Node n : ln) v += n.getLastChange();
        return v/ln.size();        
    }

    @Override
    public double getLastConvergenceBalance() {
        double i = 0.;
        Collection<Node> ln = nodes.values();
        for (Node n : ln) if (n.getLastConvergenceBalance()>i) i = n.getLastConvergenceBalance();
        return i;
    }
    
    public String getOutOfConvergenceBalance(double maxI) {
        StringBuilder out  = new StringBuilder();
        Collection<Node> ln = nodes.values();
        double i;
        for (Node n : ln) if ((i=n.getLastConvergenceBalance())>maxI) out.append(n.getLabel()).append(" (").append(EngNotation.convert(i)). append("A), "); 
        return out.toString();
    }
        
    @Override
    public double getValue() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getWarning() {
        StringBuilder warnings = new StringBuilder();
        String w;
        for (BlackBox b : blackBoxes.values()) {
            w = b.getWarning();
            if (w != null) warnings.append(w).append('\n');
        } 
        return warnings.toString();
    }

    @Override
    public Drawable nextChild() {
        return sources.nextChild();
    }

    @Override
    public Place getDrawPlace() {
        return null;
    }

    @Override
    public int getDrawPriority() {
        return 0;
    }

    @Override
    public void addChild(Drawable child) {
        sources.addChild(child);
    }

    @Override
    public void setDrawPlace(Place childPlace) {
        //nothing to do when called by drawer because it is not visible
    }
    
    public void resetPlotPlace() {
        for (BlackBox b : blackBoxes.values()) b.setDrawPlace(null);
        for (Node n : nodes.values()) n.setDrawPlace(null);
    }

    @Override
    public void drawChildLinkedToFather(Drawable father) {
        //nothing to do when called by drawer because it is not visible
    }

    @Override
    public Color getDrawFillColor() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public byte howToDrawLinkToFather() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
            
}
