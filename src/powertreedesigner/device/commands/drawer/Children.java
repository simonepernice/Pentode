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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Children implements Drawable { 
    private final LinkedList<Drawable> children;
    private Iterator<Drawable> childrenIterator;    

    public Children() {
        this.children = new LinkedList<>();
        this.childrenIterator = null;
    }
        
    @Override
    public Drawable nextChild() {
        if (childrenIterator.hasNext()) return childrenIterator.next();
        return null;
    }

    @Override
    public Place getDrawPlace() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public int getDrawPriority() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void resetDrawed() {
        Collections.sort(children);
        childrenIterator = children.iterator();
    }

    @Override
    public void addChild(Drawable child) {
        children.add(child);
    }

    @Override
    public void setDrawPlace(Place childPlace) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void drawChildLinkedToFather(Drawable father) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Color getDrawFillColor() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public byte howToDrawLinkToFather() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
          
}
