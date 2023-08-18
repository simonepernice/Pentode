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
package powertreedesigner.device.components.electricComponents.parameter;

import java.awt.Color;
import java.util.LinkedList;
import powertreedesigner.device.Device;
import powertreedesigner.device.commands.helper.Helpable;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.commands.printer.Printable;
import powertreedesigner.device.commands.simulator.history.VectorOfDouble;
import powertreedesigner.device.components.electricComponents.parameter.currency.Money;
import powertreedesigner.device.components.electricComponents.parameter.waveform.Waveform;
import powertreedesigner.device.parser.linesToParse.TokenList;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public abstract class Parameter implements Printable, Helpable {    
    abstract public Parameter parse (TokenList param, Device d) throws ParsingException;         

    public double getDouble() {
        throwRuntimeException ();
        return 0.;
    }

    public int getInteger() {
        throwRuntimeException ();
        return 0;
    }

    public String getString() {
        throwRuntimeException ();
        return null;
    }
    
    public String getLabel() {
        throwRuntimeException ();
        return null;
    }
    
    public String getProperty() {
        throwRuntimeException ();
        return null;
    }
    
    public Waveform getWaveform () {
        throwRuntimeException ();
        return null;
    }
    
    public String getMeasurementUnit() {
        throwRuntimeException ();
        return null;
    }    

    public boolean getBoolean() {
        throwRuntimeException ();
        return false;
    }

    public LinkedList<Parameter> getList() {
        throwRuntimeException ();
        return null;
    }        
    
    public VectorOfDouble getVectorOfDouble () {
        throwRuntimeException ();
        return null;
    }
    
    public Color getColor() {
        throwRuntimeException ();
        return null;
    }
    
    public Money  getMoney() {
        throwRuntimeException ();
        return null;
    }    
    
    private void throwRuntimeException () {
        throw new RuntimeException ("Internal error trying to extract the double for a "+getTypeName()+" parameter");
    }

    abstract public String getTypeName ();

    @Override
    public String getHelp() {
        return "";
    }
    
    public boolean isReadOnly () {
        return false;
    }
            
}
