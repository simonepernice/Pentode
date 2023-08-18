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
package powertreedesigner.device.commands.simulator.history;

import java.util.LinkedList;
import powertreedesigner.device.components.electricComponents.parameter.EngNotation;
import powertreedesigner.device.commands.printer.Printable;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class VectorOfDouble implements Printable {
    private final LinkedList<Double> data;
    private String label;
    private String measurementUnit;
    
    public VectorOfDouble(String measurementUnit) {
        data = new LinkedList<> ();
        this.measurementUnit = measurementUnit;
    }
    
    public VectorOfDouble(LinkedList<Double> history, String measurementUnit) {
        this.data = history;
        this.measurementUnit = measurementUnit;
    }
    
    public VectorOfDouble addPoint ( double y) {
        data.add(y);
        return this;
    }
    
    public LinkedList<Double> getData () {
        return data;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }        

    public void clear  () {
        data.clear();
    }
    
    public int getSize () {
        return data.size();
    }
    
    public double getDouble () throws ParsingException {
        if (data.size()!=1) throw new ParsingException("it was required a single real value while it was found a vector "+getPrintString());
        return data.getFirst();
    }

    @Override
    public String getPrintString() {
        StringBuilder out = new StringBuilder();
        out.append("{");
        for (double d : data) out.append(EngNotation.convert(d)).append(measurementUnit).append(", ");
        if (out.length()>2) out.setLength(out.length()-2);
        out.append('}');
        return out.toString();
    }
    
}
