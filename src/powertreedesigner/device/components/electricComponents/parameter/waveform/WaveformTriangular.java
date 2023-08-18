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
public class WaveformTriangular extends WaveformPWL { 
    public WaveformTriangular()  {
        super ("tri");
    }
    
    
    protected WaveformTriangular (Waveform wv)  {  
        super (wv);
    }
    
    @Override
    public Waveform buildWaveform (Waveform likeThis) throws ParsingException {    
        WaveformTriangular wv = new WaveformTriangular(likeThis);
        wv.vct = new double[3];
        wv.vct[0] = wv.low;
        wv.vct[1] = wv.hgh;
        wv.vct[2] = wv.low;
        
        wv.tVct = new double[3];
        wv.tVct[0] = 0.;
        wv.tVct[1] = wv.tPrd/2.;
        wv.tVct[2] = wv.tPrd;
        
        wv.vectorsCheck();
        
        return wv;
    }    
    
    @Override
    public String getHelp() {
        return "triangulare wave requires to define: (v/i)Low the low voltage, (v/i)Hgh the high voltage, and tPrd the period";
    }      
                       
}
