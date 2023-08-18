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

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Place {   
    protected final static int XORIGIN = 20;
    protected final static int YORIGIN = 20;
    
    protected static int XDisplacement;
    protected static int YDisplacement ;   
    protected static int YDisplacement2 ;   
    
    protected static int height ;
    protected static int width ;
    
    protected static int width2 ;
    protected static int height2 ;
    
    protected static int arcWidth;
    protected static int arcHeight;
      
    static {
        setXDisplacement(40);
        setYDisplacement(40);
        setHeight(100);
        setWidth(150);
    }
    protected int x, y;   

    protected Place(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Place (Place p) {
        x = p.x;
        y = p.y;
    }   

    public int getXCorner() {
        return x;
    }

    public int getYCorner() {
        return y;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }
    
    public static int getArcWidth() {
        return arcWidth;
    }

    public static int getArcHeight() {
        return arcHeight;
    }

    public static void setXDisplacement(int XDisplacement) {
        Place.XDisplacement = XDisplacement;
    }

    public static void setYDisplacement(int YDisplacement) {
        Place.YDisplacement = YDisplacement;
        YDisplacement2 = YDisplacement/2;
    }

    public static void setHeight(int PCheight) {
        Place.height = PCheight;
        height2 = PCheight/2;
        arcHeight = PCheight;
    }

    public static void setWidth(int PCwidth) {
        Place.width = PCwidth;
        width2=PCwidth/2;
        arcWidth = PCwidth;
    }
            
    public static int getYDisplacement () {
        return YDisplacement;
    }

    public static int getXDisplacement() {
        return XDisplacement;
    }
    
    public int getXMid() {
        return x + width2;
    }
    
    public int getYBottom() {
        return y + height;
    }
    
    public int getYAbove() {
        return y - YDisplacement2;
    }    
    
    public int getYBelow() {
        return y + height + YDisplacement2;
    }    
        
}
