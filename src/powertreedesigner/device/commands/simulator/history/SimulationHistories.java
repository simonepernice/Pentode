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

import java.util.Iterator;
import java.util.LinkedList;
import com.github.plot.Plot;
import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class SimulationHistories  {
    private final static Color[] COLORS = {Color.black, Color.blue, Color.cyan, Color.green, Color.magenta, Color.orange, Color.pink, Color.red, Color.yellow};
    private final LinkedList<VectorOfDouble> histories;

    public SimulationHistories() {
        histories = new LinkedList<> ();
    }
    
    public int getSize () {
        return histories.size();
    }
    
    public SimulationHistories addHistory (VectorOfDouble simulationHistory) {
        histories.add(simulationHistory);
        return this;
    }    
    
    public SimulationHistories insertHistory (VectorOfDouble simulationHistory) {
        histories.addFirst(simulationHistory);
        return this;
    }

    public void savePlotData (String label, String dp, String fs) throws IOException {
        if (histories.isEmpty()) return;                
    
        FileWriter fw = new FileWriter(label+".csv"); 
        savePlotData(fw, dp, fs);
        fw.close();
    }    
    
    public void savePlotData (OutputStreamWriter fw, String dp, String fs) throws IOException {
        if (histories.isEmpty()) return;                      

        LinkedList<Iterator<Double>> iterators = new LinkedList<>();        
        for (VectorOfDouble sh : histories) iterators.add(sh.getData().iterator());
        
        for (VectorOfDouble sh : histories) fw.write(sh.getLabel()+fs);
        fw.write('\n');
        
        boolean hn = true;
        
        for (Iterator i : iterators) if (! i.hasNext()) {hn = false; break;}
        
        while (hn) {
            if (! dp.equals("."))  for (Iterator<Double> i : iterators) fw.write(i.next().toString().replace(".", dp)+fs);
            else for (Iterator<Double> i : iterators) fw.write(i.next().toString()+fs);
            fw.write('\n');
            
            for (Iterator<Double> i : iterators) if (! i.hasNext()) {hn = false; break;}
        }
        
        fw.flush();
    }    
    
    public void savePlotDrawing (String label, String format) throws IOException {
        if (histories.size()<2) return;        
        Plot plot = Plot.plot(Plot.plotOpts().title(label).legend(Plot.LegendFormat.BOTTOM));
        
        Iterator<VectorOfDouble> i = histories.iterator();
        
        VectorOfDouble x = i.next();
        plot.xAxis(x.getMeasurementUnit(), Plot.axisOpts().format(Plot.AxisFormat.NUMBER_KGM));
        
        while (i.hasNext())
            plot.yAxis(i.next().getMeasurementUnit(), Plot.axisOpts().format(Plot.AxisFormat.NUMBER_KGM));

        i = histories.iterator();
        i.next(); //skip x
        
        int col = 0;
        while (i.hasNext()) {
            VectorOfDouble y = i.next();
            plot.series(y.getLabel(), Plot.data().xy(x.getData(), y.getData()), Plot.seriesOpts().color(COLORS[col]).xAxis(x.getMeasurementUnit()).yAxis(y.getMeasurementUnit()));
            ++col;
            if (col >= COLORS.length) col = 0;
        }                

        plot.save(label, format);    
    }

}
