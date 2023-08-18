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

import powertreedesigner.device.components.electricComponents.parameter.EngNotation;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class WaveformDC extends Waveform {

    protected WaveformDC()  {
        super("dc", "");
    }
    
    protected WaveformDC (Waveform wv)  {  
        super (wv);
    }    

    @Override
    public double _getY(double x) {        
        return low; 
    }

    @Override
    public Waveform buildWaveform (Waveform likeThis) throws ParsingException {                            
        return new WaveformDC(likeThis);       
    }         

    @Override
    public String getPrintString() {
        return "y = "+EngNotation.convert(low);
    }

    
    @Override
    public String getHelp() {
        return "constant waveform requires (v/i)low to be defined";
    }   
                       
}
