/*
 * Copyright (C) 2017 Simone Pernice pernice@libero.it
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
package powertreedesigner.device.components.electricComponents.parameter.waveform;

import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class WaveformTrapezoidal extends WaveformPWL { 
    public WaveformTrapezoidal()  {
        super ("trp");
    }
    
    
    protected WaveformTrapezoidal (Waveform wv)  {  
        super (wv);
    }
    
    @Override
    public Waveform buildWaveform (Waveform likeThis) throws ParsingException {    
        WaveformTrapezoidal wv = new WaveformTrapezoidal(likeThis);
        wv.vct = new double[6];
        wv.vct[0] = wv.low;
        wv.vct[1] = wv.low;
        wv.vct[2] = wv.hgh;
        wv.vct[3] = wv.hgh;
        wv.vct[4] = wv.low;
        wv.vct[5] = wv.low;
        
        wv.tVct = new double[6];
        wv.tVct[0] = 0.;
        wv.tVct[1] = -wv.tDly;
        wv.tVct[2] = -wv.tRse;
        wv.tVct[3] = -wv.tPls;
        wv.tVct[4] = -wv.tFll;
        wv.tVct[5] = wv.tPrd; 
        
        wv.vectorsCheck();
        
        return wv;
    }    

//    @Override
//    public double _getY(double x) {        
//        return super.getY(x%period); 
//    }
    
    @Override
    public String getHelp() {
        return "trapezoidal wave requires to define: (v/i)Low the low voltage, (v/i)Hgh the high voltage, tDly the high pulse delay, tRse the rise time, tPls the high pulse time, tFll the fall time, tPrd the period";
    }      
                       
}
