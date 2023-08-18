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
package powertreedesigner.device.components.electricComponents.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.LinkedList;
import powertreedesigner.device.commands.drawer.Place;
import powertreedesigner.device.commands.simulator.Simulable;
import powertreedesigner.device.components.electricComponents.parameter.ParameterInteger;
import powertreedesigner.device.commands.drawer.Children;
import powertreedesigner.device.commands.drawer.Drawable;
import powertreedesigner.device.commands.drawer.Drawer;
import powertreedesigner.device.components.electricComponents.parameter.Parameter;
import powertreedesigner.device.components.electricComponents.parameter.ParameterMoney;
import powertreedesigner.device.components.electricComponents.parameter.ParameterList;
import powertreedesigner.device.components.electricComponents.parameter.ParameterOptions;
import powertreedesigner.device.components.electricComponents.parameter.ParameterReadOnly;
import powertreedesigner.device.components.electricComponents.parameter.ParameterString;
import powertreedesigner.device.components.electricComponents.parameter.ReadOnlyParametrizable;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.components.electricComponents.parameter.currency.Money;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public abstract class ElectricComponent extends HistoryComponent implements  Simulable, Drawable, ReadOnlyParametrizable {
    private final static Color STRINGCOLOR = Color.black;
    private final static int MININODEHALFHEIGHT = Place.getYDisplacement()/6*2;
    private static Stroke STDSTROKE;
    private static final Stroke DSHSTROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2,10}, 0);
    private static final Stroke DSHLNGSTROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5,5}, 0);    
    
    protected static Graphics2D canvas;
    private static FontMetrics fontMetrics;
    
    private int priority;
    protected Place place;    
    private final Children children;   
    private byte drwLnk;
    private final boolean centerText;
    
    public static void setCanvas(Graphics2D canvas) {
        ElectricComponent.canvas = canvas;
        STDSTROKE = canvas.getStroke();
        ElectricComponent.fontMetrics = canvas.getFontMetrics();
    }
    
    public static void drawStringOnCanvas (LinkedList<String> ltext, Place p, boolean center) {
        canvas.setPaint(STRINGCOLOR);
                
        final int dy = fontMetrics.getHeight();
        int y = p.getYCorner();
        if (center) y+= (Place.getHeight()-ltext.size()*dy)/2;
        int x = p.getXCorner() + fontMetrics.charWidth(' ');
        for (String text : ltext) {
            y += dy;
            if (y > p.getYCorner()+Place.getHeight()) break;
            while (fontMetrics.stringWidth(text)>Place.getWidth()) text = text.substring(0, text.length()-1);  
            if (center) x = p.getXCorner() + (Place.getWidth()-fontMetrics.stringWidth(text))/2;
            canvas.drawString(text, x, y);
        }
    }
    
    public static void drawTitleOnCanvas (String text, int width) {
        canvas.setPaint(STRINGCOLOR);
                
        final int y = fontMetrics.getHeight();//*3/2;
        
        final int x = (width - fontMetrics.stringWidth(text))/2; 
        
        canvas.drawString(text, x, y);        
    }    

    public ElectricComponent(String name, String type, boolean centerText) {
        super(name, type);
        priority = 0;
        drwLnk = 0;
        this.centerText  = centerText;
        addSetting(new Setting("pltPr", new ParameterInteger(0),"the draw priority, the lower more on the left is drawed. By default source have 1000, converter 2000 and drain 3000."));
        addSetting(new Setting("lbl", new ParameterReadOnly("lbl", this), "the component label"));
        addSetting(new Setting("name", new ParameterReadOnly("name", this), "the component name"));
        addSetting(new Setting("lnks", new ParameterReadOnly("lnks", this), "the component links"));
        addSetting(new Setting("drwLnk", new ParameterOptions("full","dash","cpChild","void"), "how should be draw the link from child to his father"));
        addSetting(new Setting("drwPrm", new ParameterList(new ParameterString(""), "lbl"), "the list of parameter to show in the power tree drawing"));
        addSetting(new Setting("cst", new ParameterMoney(new Money(0.)), "the component cost"));
        place = null;     
        children = new Children();

    }

    @Override
    public String getReadOnlyParameter(String paramLabel) {
        if (paramLabel.equals("lbl")) return getLabel();
        if (paramLabel.equals("name")) return getName();        
        throw new RuntimeException ("Called with not existing read only parameter: "+paramLabel);
    }

    
    @Override
    public byte howToDrawLinkToFather() {
        return drwLnk;
    }
        
    @Override
    public void setLabel(String label) {
        super.setLabel(label);
    }
                
    @Override
    public final void resetDrawed() {
        priority  = getParameter("pltPr").getInteger();
        drwLnk = (byte) getParameter("drwLnk").getInteger();
        children.resetDrawed();
    }    

    @Override
    public final Place getDrawPlace() {
        return place;
    }

    @Override
    public final Drawable nextChild(){
        return children.nextChild();
    }

    @Override
    public final int getDrawPriority() {
        return priority;
    }   

    @Override
    public final void addChild(Drawable child) {
        children.addChild(child);
    }
           
    @Override
    public void drawChildLinkedToFather(Drawable father) {
        LinkedList<String> stp = new LinkedList<>();
        for (Parameter param : getParameter("drwPrm").getList()) stp.add(getParameterString(param.getString()));

        drawStringOnCanvas(stp, place, centerText);

        Place fatherPlace;
        if (father == null || (fatherPlace = father.getDrawPlace()) == null) return;
        canvas.setPaint(Drawer.getFOREGROUNDCOLOR());


        switch (father.howToDrawLinkToFather()) {
            case 0: // full
                canvas.drawLine(place.getXMid(), place.getYCorner(), place.getXMid(), place.getYAbove());
                canvas.drawLine(place.getXMid(), place.getYAbove(), fatherPlace.getXMid(), place.getYAbove());
                canvas.drawLine(fatherPlace.getXMid(), place.getYAbove(), fatherPlace.getXMid(), fatherPlace.getYBottom());                       
                break;
                
            case 1: //dashed
                canvas.setStroke(DSHSTROKE);                                
                
                canvas.drawLine(place.getXMid(), place.getYCorner(), place.getXMid(), place.getYAbove());
                canvas.drawLine(place.getXMid(), place.getYAbove(), fatherPlace.getXMid(), place.getYAbove());
                canvas.drawLine(fatherPlace.getXMid(), place.getYAbove(), fatherPlace.getXMid(), fatherPlace.getYBottom());        

                canvas.setStroke(STDSTROKE);
                
                break;
                    
            case 2: //copy node
                canvas.setPaint(getDrawFillColor());
                canvas.fillRect(fatherPlace.getXCorner(), fatherPlace.getYBelow()-MININODEHALFHEIGHT, Place.getWidth(), 2*MININODEHALFHEIGHT);

                canvas.setPaint(Drawer.getFOREGROUNDCOLOR());
                
                //avoid the border to distinguish from a real node
                canvas.setStroke(DSHLNGSTROKE);                                
                canvas.drawRect(fatherPlace.getXCorner(), fatherPlace.getYBelow()-MININODEHALFHEIGHT, Place.getWidth(), 2*MININODEHALFHEIGHT);
                canvas.setStroke(STDSTROKE);
                
                canvas.drawLine(fatherPlace.getXMid(), fatherPlace.getYBottom(), fatherPlace.getXMid(), fatherPlace.getYBelow()-MININODEHALFHEIGHT);

                canvas.drawString(getLabel(), fatherPlace.getXCorner() + fontMetrics.charWidth(' '), fatherPlace.getYBelow()-MININODEHALFHEIGHT+fontMetrics.getHeight());
                break;
                
            case 3: //do not draw                
        }
    }
    
    @Override
    public final void setDrawPlace(Place childPlace) {                 
        place = childPlace;
    }   
    
    @Override
    public final Color getDrawFillColor () {    
        return getParameter("drwClr").getColor();
    }
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Type = Electric Component\n";
    }                
}
