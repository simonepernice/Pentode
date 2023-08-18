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

import java.awt.Color;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public interface Drawable extends Comparable<Drawable> {
    
    public Drawable nextChild ();
        
    public Place getDrawPlace ();
        
    public int getDrawPriority ();
    
    public void resetDrawed  ();
    
    public void addChild (Drawable child);
              
    public void setDrawPlace (Place childPlace); 
    
    public void drawChildLinkedToFather (Drawable father); 
    
    public byte howToDrawLinkToFather();
    
    public Color getDrawFillColor ();

    @Override
    public default int compareTo(Drawable o) {
        return getDrawPriority() - o.getDrawPriority();
    }
    
}
