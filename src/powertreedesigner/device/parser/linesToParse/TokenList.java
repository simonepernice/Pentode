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
package powertreedesigner.device.parser.linesToParse;

import java.util.LinkedList;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class TokenList {
    private final static boolean DEBUG = false;
    private final LinkedList<String> tokens;
    
    public TokenList () {
        tokens = new LinkedList<>();
    }
    
    public void add (String token) {
        tokens.add(token);
    }        
    
    public boolean hasNext () {
        return !tokens.isEmpty();
    }
    
    public boolean hasNext (String token) {
        if (tokens.isEmpty()) return false;
        return tokens.getFirst().equals(token);
    }
    
    public boolean hasNext (int deep, String token) {
        if (tokens.size()<deep) return false;
        return tokens.get(deep-1).equals(token);
    }
    
    public String next () throws ParsingException {
        if (tokens.isEmpty()) throw new ParsingException ("reached the end of line while still looking for elements ");
        return tokens.pollFirst();      
    }
    
    public void skipNext (String next) throws ParsingException {
        String nt = next () ;
        if (! nt.equals(next)) throw new ParsingException ("it was expected token "+next+" while it was found "+nt);
    }
    
    public String next (int i) throws ParsingException {
        String s=null;
        while (i>0) {
            s = next();
            --i;
        }
        return s;
    }
    
    public int size () {
        return tokens.size();
    }
    
    public String peekNext () {
        return tokens.peekFirst();
    }
    
    public static TokenList splitInTokens (String line) throws ParsingException {
        if (DEBUG) System.out.println("Split in token");
        if (DEBUG) System.out.println("Input = "+line);
        TokenList out = new TokenList();
        StringBuilder command = new StringBuilder ();
        
        boolean inString = false;        
        int i = 0;
        char c;
        final char[] lineTCA = line.toCharArray();
        while (i < lineTCA.length) {
            c = lineTCA[i++];
                    
            if (inString) {
                if (c == '"') {
                    out.add(command.toString());
                    command.setLength(0);
                    inString = false;
                    continue;
                }
                
                command.append(c);
                continue;
            }
            
            if (c == '"') {
                if (command.length() > 0) {
                    out.add(command.toString());
                    command.setLength(0);
                }
                inString = true;
                continue;
            }
                        
            if (Character.isWhitespace(c)) {
                if (command.length() > 0) {
                    out.add(command.toString());
                    command.setLength(0);
                }                
                continue;
            }
            
            if (c == '.') {
                if (command.length() > 0) {
                    if (Character.isDigit(command.charAt(0))) { //we are within a number
                        command.append(c);
                        continue;
                    }
                    out.add(command.toString());
                    command.setLength(0);
                }
                out.add(".");
                continue;
            }           
            
            if (c == '=') {
                if (command.length() > 0) { 
                    if (! Character.isLetterOrDigit(command.charAt(0))) {//to catch >= <= != 
                        command.append(c);
                        out.add(command.toString());
                        command.setLength(0);                        
                        continue;
                    }
                    out.add(command.toString());
                    command.setLength(0);
                }
                out.add(String.valueOf(c));
                continue;
            }
            
            if ("#,[]".indexOf(c) != -1) {
                if (command.length() > 0) { 
                    out.add(command.toString());
                    command.setLength(0);
                }
                out.add(String.valueOf(c));
                continue;
            }

            command.append(c);
        }
        if (inString) throw new ParsingException ("reached end of line without closing \" ");
        if (command.length()>0) out.add(command.toString());
        if (DEBUG) System.out.println("Output ="+out.toString());
        return out;
    }    

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append('[');
        for (String s : tokens) out.append('{').append(s).append("} ");
        out.append(']');
        return out.toString();
    }
    
    
}
