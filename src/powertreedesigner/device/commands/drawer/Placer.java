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

import java.util.ArrayList;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public final class Placer extends Place {
       
    private final ArrayList<Integer> firstFreeX;
    private int deep;
    
    public Placer() {
        super (XORIGIN, YORIGIN-height - YDisplacement); //begin above the canvas because the first element is device which has not to be rendered
        
        firstFreeX = new ArrayList<>(50);
        firstFreeX.add(XORIGIN);
        deep = 0;
    }
    
    public Place getPlace () {
        return new Place (this);
    }
    
    public void setPlace (Place p) {
        x = p.x;
        if (y != p.y) {
            y = p.y;
            deep = (y-(YORIGIN-height - YDisplacement))/(YDisplacement+height);
        }        
    }
                
    public void goMoreDeep () {
        ++deep;
        final int fx;
        if (deep == firstFreeX.size()) firstFreeX.add(fx = XORIGIN);
        else if (deep > firstFreeX.size()) throw new RuntimeException("Internal error in goMoreDeep called at top");
        else fx = firstFreeX.get(deep);
        
        y += height + YDisplacement;
        
        while (x < fx) x += width + XDisplacement;
        
    }
    
    public void setUsedPlace (Place p) {
        firstFreeX.set(deep, p.x+1);
    }
    
    public void goLessDeep () {
        --deep;
        if (deep < 0) throw new RuntimeException ("Internal error in goLessDeep called at top");
        
        y -= height + YDisplacement;
               
    }    
    
    public int getMaxWidth () {
        int mx = 0;
        for (int xMaxAtDeep : firstFreeX) if (xMaxAtDeep > mx) mx = xMaxAtDeep;
        return mx + width+XDisplacement;
    }
    
    public int getMaxHeight () {
        return XORIGIN + (firstFreeX.size()-1) * (YDisplacement + height) + YDisplacement;
    }
}
