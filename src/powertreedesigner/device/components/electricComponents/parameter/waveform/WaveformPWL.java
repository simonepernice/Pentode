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

import java.util.Arrays;
import powertreedesigner.device.components.electricComponents.parameter.EngNotation;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class WaveformPWL extends Waveform  {
    private double prevT;
    private int prevI;
    
    protected WaveformPWL () {
        this("pwl"); 
        prevT = 10.;
    }
    
    protected WaveformPWL (String name) {
        super (name, "");        
    }
    
    protected WaveformPWL (Waveform wv)  {  
        super (wv);
    }

    @Override
    public String getHelp() {
        return "piece wise linear waveform requires to define tVct=[t0 t1 t2 .. tn |] (v/i)Vct=[y0 y1 y2 .. yn |] were ti are absolute time instant if positive, if negative it is considered time increment respect to the previous step";
    }       
    
    public double _getY (double t) {
        if (t < prevT) prevI = Arrays.binarySearch(tVct, t);
        else prevI = Arrays.binarySearch(tVct, prevI, tVct.length, t);
        prevT = t;
        if (prevI >= 0) return vct[prevI];
        prevI = -prevI-1;
        if (prevI == 0) return vct[0];
        if (prevI == tVct.length) return vct[vct.length-1];
        final int i1 = prevI -1;
        return vct[i1]+(vct[prevI]-vct[i1])/(tVct[prevI]-tVct[i1])*(t-tVct[i1]);
        
    }

    @Override
    public String getPrintString() {
        StringBuilder out = new StringBuilder ();
        out.append("\nx\t\ty\n");
        for (int i=0;i<tVct.length;++i) out.append(EngNotation.convert(tVct[i])).append("\t\t").append(EngNotation.convert(vct[i])).append('\n');
        return out.toString();
    }

    @Override
    public Waveform buildWaveform (Waveform likeThis) throws ParsingException {  
//        System.out.println("Building a PWL waveform");
        WaveformPWL wv = new WaveformPWL(likeThis);       
        wv.vectorsCheck();
        return wv;
    } 
    
    protected void vectorsCheck () throws ParsingException {
        if (tVct == null) throw new ParsingException ("the time vector was not defined");
        if (vct == null) throw new ParsingException ("the Y vector was not defined");
        final int s = vct.length;
        if (s != tVct.length) throw new ParsingException ("the size of time and Y vectors for PWL should be the same");
//        System.out.println("tVct bevore = "+Arrays.toString(tVct));
        for (int i=1;i<s;++i) {
            if (tVct[i]<0) tVct[i]=-tVct[i]+tVct[i-1];
        }
        for (int i=1;i<s;++i) {
            double a = tVct[i], b = tVct[i-1];
            if (a<b) throw new ParsingException ("the time event is not monotonic, found at: "+a+" "+b+" on the list "+Arrays.toString(tVct));
            if (a==b && vct[i]!=vct[i-1]) throw new ParsingException ("the y event is a step at: "+a+" values "+vct[i]+" and "+vct[i-1]);
        }           
//        System.out.println("tVct after = "+Arrays.toString(tVct));
    }
    
}
