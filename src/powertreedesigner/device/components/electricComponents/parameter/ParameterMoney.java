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
package powertreedesigner.device.components.electricComponents.parameter;

import powertreedesigner.device.Device;
import powertreedesigner.device.components.electricComponents.parameter.currency.Money;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.parser.linesToParse.TokenList;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class ParameterMoney extends Parameter {
    private final Money val;    

    public ParameterMoney(Money val) {
        this.val = val;
    }
    
    @Override
    public String getTypeName() {
        return "currency";
    }
        
    @Override
    public Money getMoney() {
        return val;
    }

    @Override
    public Parameter parse(TokenList tokens, Device d) throws ParsingException {
        Money c;
        String param = tokens.next();
        try {
            c = Money.parseMoney(param);
        } catch (NumberFormatException nfe) {
            throw new ParsingException("it was expected a currency while was found "+param+ " "+nfe.getMessage());
        }
        return new ParameterMoney(c);
    }

    @Override
    public String getPrintString() {
        return val.toString();
    }            
    
    @Override
    public String getHelp() {
        return super.getHelp()+" Every money amout should be followed by its currency symbol. If the symbol was not used before it is automatically added to the currency database. If no currency is specified the base currency is used.";
    }    
}
