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
package powertreedesigner.device.commands.drawer;

import com.orsonpdf.PDFDocument;
import com.orsonpdf.Page;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.components.ElectricComponent;
import powertreedesigner.device.components.electricComponents.components.Component;
import java.io.File;
import java.io.IOException;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import powertreedesigner.device.Device;
import powertreedesigner.device.command.Command;
import powertreedesigner.device.components.electricComponents.parameter.ParameterOptions;
import powertreedesigner.device.components.electricComponents.parameter.Setting;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Drawer extends Command {
    private Placer placer;
    private static Color BACKGROUNDCOLOR = Color.white;
    private static Color FOREGROUNDCOLOR = Color.black;

    public static void setBACKGROUNDCOLOR(Color BACKGROUNDCOLOR) {
        Drawer.BACKGROUNDCOLOR = BACKGROUNDCOLOR;
    }

    public static Color getFOREGROUNDCOLOR() {
        return FOREGROUNDCOLOR;
    }

    public static void setFOREGROUNDCOLOR(Color FOREGROUNDCOLOR) {
        Drawer.FOREGROUNDCOLOR = FOREGROUNDCOLOR;
    }        
    
    public Drawer() {
        super ("draw", "command work output");        
        
        addSetting(new Setting("outputFormat", new ParameterOptions ("pdf","svg"), "the output file format"));        
    }

    @Override
    public int getGateNumber() {
        return 0;
    }

    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        
        try {
            draw (d);
        } catch (IOException ex) {
            throw new ParsingException(ex.getMessage());
        }
        System.out.println("Drawing end.\n");
    }

    @Override
    public Component makeNewParseable() {
        return new Drawer();
    }
    
    public void draw (Device device) throws IOException {     
        final String ext = "."+getParameter("outputFormat").getPrintString();
        
        System.out.println("Drawing "+getLabel()+ext);
        
        final int OUTFORMAT = getParameter("outputFormat").getInteger();
        
        placer = new Placer ();
        
        device.resetPlotPlace();
        device.resetDrawed();                 

        recursivePlaceChild (device);

        Graphics2D canvas;
        PDFDocument pdfDoc=null;

        if (OUTFORMAT == 1) {
            canvas = new SVGGraphics2D (placer.getMaxWidth(), placer.getMaxHeight());
        } else{        
            pdfDoc = new PDFDocument();
            Page page = pdfDoc.createPage(new Rectangle(placer.getMaxWidth(), placer.getMaxHeight()));
            canvas = page.getGraphics2D();
        }
        
        canvas.setPaint(BACKGROUNDCOLOR);
        canvas.fillRect(0, 0,placer.getMaxWidth(), placer.getMaxHeight());
        canvas.setFont(new Font ("Arial", 1, 10));
      
        ElectricComponent.setCanvas(canvas);
        ElectricComponent.drawTitleOnCanvas(getLabel(), placer.getMaxWidth());
                                
        device.resetDrawed(); 
        recursiveDrawChildLinkedToFather (device, null);       
        
        if (OUTFORMAT==1) {
            SVGUtils.writeToSVG(new File(getLabel()+ext), ((SVGGraphics2D)canvas).getSVGElement());
        } else {
            pdfDoc.writeToFile(new File(getLabel()+ext));
        }
    }
    
    public void recursivePlaceChild (Drawable child) {       
        if (child.getDrawPlace() != null)  {
            return;
        }        
        
        Place myPlace = null;
        
        Drawable nextChild;

        while ((nextChild = child.nextChild()) != null) {
            placer.goMoreDeep();
            recursivePlaceChild(nextChild);       
            placer.goLessDeep();

            if (myPlace == null)  myPlace = placer.getPlace();//to left aligh the draw
        }              
        
        if (myPlace == null) myPlace = placer.getPlace();//if this is terminal child

        placer.setUsedPlace (myPlace);
        child.setDrawPlace(myPlace);
        
        placer.setPlace(myPlace);        
    }
    
    private void recursiveDrawChildLinkedToFather (Drawable child, Drawable father) {                     
        Drawable nextChild;
        
        while ((nextChild = child.nextChild()) != null) {
            recursiveDrawChildLinkedToFather(nextChild, child);
        }                
        
        child.drawChildLinkedToFather(father);         
    }

    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = draws the power tree of the given netlist on a file in pdf or svg file\n";
    }
        
    
}
