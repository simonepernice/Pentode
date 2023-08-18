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
package powertreedesigner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import powertreedesigner.device.Device;
import powertreedesigner.device.exception.PTDException;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class PowerTreeDesigner {
    public static boolean donated ;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        donated = Licence.check();
        
        if (args.length != 1) {
            System.out.println("It is required to provide the device file name");
            return;
        }        
        
        Device device;
        String dLabel = args[0].endsWith(".ptd")?args[0].substring(0, args[0].length()-4):args[0];
        try {
            device = new Device (dLabel);
        } catch (ParsingException pe) {
            System.out.println("The label used is not a correct syntax");
            return;
        }        

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));                       
        
        boolean firstRun = true;
        LinkedList<String> inputLine = new LinkedList<>();
        inputLine.add("load "+dLabel);
        
        while (true) {
            try {              
                if (firstRun) {
                    firstRun = false;
                    device.getParser().parse(inputLine);                
                    device.getParser().getLinesToParse().discardFirstParsedLine();
                    continue;
                }

                boolean moreLine;
                inputLine.clear();
                do {
                    String line;
                    System.out.print ("> ");                    
                    line = in.readLine();
                    
                    if (line.endsWith("+")) {
                        moreLine = true;
                        line = line.substring(0, line.length()-1);
                    } else {
                        moreLine = false;
                    }
                    inputLine.add(line);                        
                } while (moreLine);

                device.getParser().parse(inputLine);         
            } catch (PTDException ptde) {
                System.out.println(ptde.getMessage());
            } catch (Exception e) {
                System.out.println("There was an internal error, please send the following stack trace and the input file to pernice@libero.it");
                e.printStackTrace();
            }                     
        }        
    }
    
}
