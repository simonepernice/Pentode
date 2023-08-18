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

import powertreedesigner.device.exception.IterationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import powertreedesigner.device.Device;
import powertreedesigner.device.exception.ParsingException;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class LinesToParse {
    private final static HashMap<String, IterationCommand> BEGINICDICT = new HashMap<>();       
    private final static HashMap<String, IterationCommand> ENDICDICT= new HashMap<>();     
    
    public static void addIterationCommand (String begin, String end) {
        IterationCommand ic = new IterationCommand(begin, end);
        if (BEGINICDICT.put(ic.getBegin(), ic) != null) throw new RuntimeException ("Two iteration commands have the same begin name");        
        if (ENDICDICT.put(ic.getEnd(), ic) != null) throw new RuntimeException ("Two iteration commands have the same end name");        
    }    
    
    private final LinkedList<String> inputLines;
    
    private final ArrayList<String> linesToParse;
    private int nextLine;
    private String line;
    
    private final Stack<LocatedIterationCommand> stackIC;
    private LocatedIterationCommand lastIC;
    
    private final Device device;
        
    public LinesToParse(Device device) {
        inputLines = new LinkedList<>();
        linesToParse = new ArrayList<>(100);
        stackIC = new Stack<> ();
        this.device = device;
        nextLine = 0;
        lastIC = null;
    }

    public LinkedList<String> getInputLines() {
        return inputLines;
    }
         
    public void addLines (List<String> lines) {
        inputLines.addAll(lines);        
        
        linesToParse.clear();
        linesToParse.addAll(lines);
        nextLine = 0;
    }

    public boolean hasNext () {
        return nextLine < linesToParse.size();
    }
    
    public int getLineNumber () {
        return nextLine; //that is correct because editor number first line as 1 instead of 0
    }
    
    public String getLine () {
        return line;
    }
    
    public void discardLastParsedLine () {
        inputLines.removeLast();
    }        
    
    public void discardFirstParsedLine () {
        inputLines.removeFirst();
    } 
    
    public TokenList getNextLineSplitted () throws IterationException, ParsingException {
        
        TokenList tokens = null;
        lastIC = null;
        
        while (hasNext()) {
            line = linesToParse.get(nextLine);
            ++nextLine;
            
            line = device.replaceDefinition(line);
            line = line.trim();
            
            if (line.length() == 0 || line.charAt(0) == '#') continue;

            tokens = TokenList.splitInTokens(line);
            
            if (tokens.size() > 0) break;
        }
        
        if (nextLine > linesToParse.size() || tokens == null) return null;
        
        IterationCommand iCommand = BEGINICDICT.get(tokens.peekNext());
        if (iCommand != null) {
            stackIC.push(new LocatedIterationCommand(iCommand, nextLine-1));
            return tokens;
        }
        
        iCommand = ENDICDICT.get(tokens.peekNext());
        if (iCommand != null) {
            if (stackIC.size()>0 && tokens.peekNext().equals(stackIC.peek().getEnd())) lastIC = stackIC.pop();
            else throw new IterationException("found end of iteration "+tokens.peekNext()+" without the begin");            
        }
                
        return tokens;
    }
 
    public void goToEndLoop () throws IterationException, ParsingException {
        if (stackIC.isEmpty()) throw new IterationException("there is not any loop in progress to reach its end");
        
        final int stackDeepTarget = stackIC.size()-1;
                        
        do {
            getNextLineSplitted ();
            if (! hasNext()) throw new IterationException("reached the end of lines without finding "+stackIC.peek().getEnd());
        } while (stackIC.size() != stackDeepTarget);
        
    }
    
    public void goToBeginLoop () throws IterationException {
        if (lastIC == null) throw new IterationException("there is not any start of loops");
        nextLine = lastIC.getLocation();
    }    
    
    public void continueToBeginLoop () throws IterationException {
        if (stackIC.isEmpty()) throw new IterationException("there is not any loop in progress to reach its begin");
        lastIC = stackIC.pop();
        goToBeginLoop();
    }
    
}
