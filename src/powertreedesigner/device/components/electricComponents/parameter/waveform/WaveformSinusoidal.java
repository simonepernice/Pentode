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
public class WaveformSinusoidal extends Waveform {
    private double amp, avg, ph, T;

    protected WaveformSinusoidal()  {
        super("sin", "");
    }
    
    protected WaveformSinusoidal (Waveform wv)  {  
        super (wv);
    }    

    @Override
    public double _getY(double t) {        
        return avg+amp*Math.sin(t*T+ph); 
    }

    @Override
    public Waveform buildWaveform (Waveform likeThis) throws ParsingException {                            
        WaveformSinusoidal wv = new WaveformSinusoidal(likeThis);       
        wv.amp = (wv.hgh-wv.low)/2.;
        wv.avg = (wv.hgh+wv.low)/2.;
        wv.T =  2.*Math.PI/wv.tPrd;
        wv.ph = wv.T*wv.tDly;
        return wv;
    }         

    @Override
    public String getPrintString() {
        return "sinusoidal wave";
    }

    
    @Override
    public String getHelp() {
        return "constant waveform requires (v/i)low to be defined";
    }   
                       
}
