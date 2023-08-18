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
import java.util.LinkedList;
import powertreedesigner.device.commands.helper.Helpable;
import powertreedesigner.device.commands.printer.Printable;
import powertreedesigner.device.components.electricComponents.parameter.ParameterBoolean;
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.components.electricComponents.parameter.ParameterInteger;
import powertreedesigner.device.components.electricComponents.parameter.ParameterOptions;
import powertreedesigner.device.components.electricComponents.parameter.ParameterVectorOfDouble;
import powertreedesigner.device.components.electricComponents.parameter.Parametrizable;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Waveform implements Printable, Helpable  {
    private final static Waveform[] DICTIONARY = {new WaveformDC (), new WaveformPWL(), new WaveformTrapezoidal(), new WaveformSaw(), new WaveformTriangular(), new WaveformSinusoidal()};
    private static boolean steadyState = false;
    
    
    private final static String HELP;    
    private final static String[] WAVESOPT;
    static {
        int i = 0;
        WAVESOPT = new String[DICTIONARY.length];
        String[] opt = new String [DICTIONARY.length];
        StringBuilder sbHelp = new StringBuilder ();
        for (Waveform w : DICTIONARY) {
            WAVESOPT[i]=w.getName();
            sbHelp.setLength(0);
            opt[i++] = sbHelp.append("\n---").append(w.getName()).append(" ").append(w.getHelp()).toString();
        }      

        sbHelp.setLength(0);
        Arrays.sort(opt);
        for (String h : opt) sbHelp.append(h);
        HELP = sbHelp.toString();        
    }
    

    public static void setSteadyState(boolean steadyState) {
        Waveform.steadyState = steadyState;
    }        
        
    private final String name, mu;    
    private boolean wPrdc;
    private int nPrds;
    
    protected double low, hgh, tPrd, tDly, tPls, tRse, tFll;    
    protected double[] tVct, vct;
    
    public Waveform (String mu) {
        this ("", mu);
    }
            
    protected Waveform (String name, String mu) {       
        this.name = name;  
        this.mu = mu;        
    }
    
    public Waveform (Waveform wv) {
        name = wv.name;
        mu = wv.mu;
        wPrdc = wv.wPrdc;
        nPrds = wv.nPrds;
        
        low = wv.low;
        hgh = wv.hgh;
        tPrd = wv.tPrd;
        tDly = wv.tDly;
        tPls = wv.tPls;
        tRse = wv.tRse;
        tFll = wv.tFll;
        tVct = wv.tVct.clone();
        vct = wv.vct.clone();
    }

    public final String getName() {
        return name;
    }        
    
    public final double getY (double t) {
        if (steadyState) return low;
        if (!wPrdc) return low;
        if (nPrds>0 && t/tPrd > nPrds) return low;
        return _getY(t%tPrd);
    }
    
    public double _getY (double t) {
        throw new RuntimeException ("This method should not be call from base class");
    }
    
    protected Waveform buildWaveform (Waveform likeThis) throws ParsingException {
        throw new RuntimeException ("This method should not be call from base class");        
    }    

    public final void addParameters (Parametrizable p) {
        String mulc = mu.toLowerCase();
        p.addSetting(new Setting(mulc+"Low", new ParameterDouble(0., mu), "the voltage value for dc, the low peak for other waveforms, the value used for steady state simulation"));
        p.addSetting(new Setting(mulc+"Hgh", new ParameterDouble(10., mu), "the high peak for the waveform"));
        p.addSetting(new Setting("tPrd", new ParameterDouble(1., "s"), "the period of the waveform, use wPrdc if the waveform has to repeat aftera 1 period"));
        p.addSetting(new Setting("tDly", new ParameterDouble(0., "s"), "the delay of the trapezoidal or phase for sin waveform"));
        p.addSetting(new Setting("tPls", new ParameterDouble(0.5, "s"), "the pulse duration for trapezoidal wave"));
        p.addSetting(new Setting("tRse", new ParameterDouble(0.01, "s"), "the rise time for trapezoidal wave"));
        p.addSetting(new Setting("tFll", new ParameterDouble(0.01, "s"), "the fall time for trapezoidal wave"));
        p.addSetting(new Setting("tVct", new ParameterVectorOfDouble("s"), "the list of time points to use for a pwl waveform"));
        p.addSetting(new Setting(mulc+"Vct", new ParameterVectorOfDouble(mu), "the list of "+mu+" points to use for a pwl waveform"));        
        p.addSetting(new Setting("wPrdc", new ParameterBoolean(false), "is used to set if the wavefor is periodic, in case tPrd is used for period"));
        p.addSetting(new Setting("nPrds", new ParameterInteger(0), "is used to set the number of periods if the wavefor is periodic, <= 0 means infinity"));
        p.addSetting(new Setting("wTyp", new ParameterOptions(WAVESOPT), "is used to set the waveform type: "+HELP));        
    }
    
    public final Waveform buildFromParameters (Parametrizable p) throws ParsingException  {
        String mulc = mu.toLowerCase();
        low = p.getParameter(mulc+"Low").getDouble();
        hgh = p.getParameter(mulc+"Hgh").getDouble();
        tPrd = p.getParameter("tPrd").getDouble();
        tDly = p.getParameter("tDly").getDouble();
        tPls = p.getParameter("tPls").getDouble();
        tRse = p.getParameter("tRse").getDouble();
        tFll = p.getParameter("tFll").getDouble();
        
        LinkedList<Double> lTVct = p.getParameter("tVct").getVectorOfDouble().getData();
        tVct = new double[lTVct.size()];
        int i = 0;
        for (double d : lTVct) tVct[i++]=d;        
        
        LinkedList<Double> lVct = p.getParameter(mulc+"Vct").getVectorOfDouble().getData();
        vct = new double [lVct.size()];
        i =0;
        for (double d : lVct) vct[i++]=d;
        
        wPrdc = p.getParameter("wPrdc").getBoolean();
        nPrds = p.getParameter("nPrds").getInteger();
        
        return DICTIONARY[p.getParameter("wTyp").getInteger()].buildWaveform(this);
    }    

    @Override
    public String getPrintString() {
        throw new UnsupportedOperationException("This method should not be call from base class");        
    }

    @Override
    public String getHelp() {
        return HELP;
    }
    
}
