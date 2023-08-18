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
package powertreedesigner;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

public class Licence {

    private final static byte[][] LICENSED_MAC_ADD_STR = {
        {84, 4, -90, 51, 104, -81},             //main laptop
        {-64, 24, -123, -117, 118, 118},        //ultra laptop
        {64, 22, 126, 68, -116, 110},           //travel laptop
        {-42, 11, 77, -127, -112, -73},           //travel laptop
        {40, -29, 71, -73, 31, -102},           //travel laptop
        {(byte) 0xEC, (byte) 0x8E, (byte) 0xB5, (byte) 0x9E, (byte) 0xEB, (byte) 0x79}                     //work laptop
    
    };

    public static boolean check() {
        
        System.out.println ("Pentode (Power Tree Designer) v. 1.20");
        System.out.println ();
        System.out.println ("Copyright (C) 2016  Simone Pernice ( email: pernice@libero.it, website: http://simonepernice.freehostia.com/J2ME/ptd/ptd.html, Pentode is hosted on sourceforge.net/projects/penthode/ )");
        System.out.println ("Pentode is under GPL 3.0 see https://www.gnu.org/licenses/gpl-3.0.txt ");               
        System.out.println ();
        System.out.println ("First Pentode version made on 3rd December 2016, Last version on 30th Agust 2018");
        System.out.println ();

        boolean macTest = false;

        try {

            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            byte[] foundMac=null;

            int j = 0;
            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();

                ++j;
                byte[] mac = network.getHardwareAddress();

                if (mac == null) {
                    continue;
                } 
                
                foundMac = mac;

                int i;
                for (byte[] allowedMAC : LICENSED_MAC_ADD_STR) {
                    i = 0;
                    if (mac.length != allowedMAC.length) throw new RuntimeException ("Error found on internal licence address");
                    for (byte b : allowedMAC) {   
                        macTest = (b == mac[i]);
                        ++i;
                        if (! macTest) break;
                    }
                    if (macTest) break;  
                }
                              
            }

            if (!macTest) {
                System.out.println("This copy of Pentode has not received any donation.");
                System.out.println("It is possible to buy support for 100€ for personal use and 1000€ for company use (max 15 users).");
                System.out.println("The support provides: help on Pentode usage issue by email (time limited), a tutorial, bugs fix, trainings and new feature addition (those may require an extra cost depending on complexity).");
                System.out.println("If you want to donate or have support just write a mail to pernice@libero.it ");
                System.out.println("If donate send the following data: "+ printableMacAddress(foundMac)+" to recive a version for your PC which does not print this message.");
                System.out.println();
            } else {
                System.out.println("Thank you for supporting Pentode development with a donation.");
            }
            
            System.out.println ();

        } catch (SocketException e) {

            return false;

        }

        return macTest;

    }

    private static String printableMacAddress(byte[] mac) {
        if (mac == null) {
            return "no MAC";
        }
        return Arrays.toString(mac);
    }

}
