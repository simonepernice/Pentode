/*
 * Copyright (C) 2018 Simone Pernice pernice@libero.it
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
package powertreedesigner.device.components.electricComponents.parameter.currency;

import java.math.BigDecimal;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Money { 
    public final static Currencies CURRENCIES = new Currencies();
    
    final private BigDecimal value;
    final private String currency;
    
    public Money (double value) {
        this (new BigDecimal(value), CURRENCIES.getBaseCurrency());
    }
    
    public Money (BigDecimal value) {
        this (value, CURRENCIES.getBaseCurrency());
    }
    
    public Money (BigDecimal value, Currency currency) {
        this.value = value;
        this.currency = currency.getSymbol();
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public Currency getCurrency () {
        return CURRENCIES.getCurrency(currency);
    }
    
    @Override
    public String toString () {
        Currency c = getCurrency();
        StringBuilder num = new StringBuilder (value.toPlainString());
        int i = num.indexOf(".");
        if (i == -1) {
            num.append(".");
            i = num.length()-1;
        }
        while (num.length()-i-1 > c.getDecDigits()) num.setLength(num.length()-1);
        while (num.length()-i-1 < c.getDecDigits()) num.append('0');
        
        num.append(c.getSymbol());

        return num.toString();
    }
        
    static public Money parseMoney (String value) throws NumberFormatException {        
        int ci;
        final int s = value.length();
        for (ci = 0; ci < s; ++ ci) {
            char c = value.charAt(ci);
            if (Character.isDigit(c) || c=='.') continue;
            break;
        }
        
        String vp; 
        String cp; 

        vp = value.substring(0, ci);
        cp = (ci == s ? CURRENCIES.getBaseCurrency().getSymbol(): value.substring(ci));            

        //return new Money(BigDecimal.valueOf(Double.parseDouble(vp)), CURRENCIES.getAndAddCurrency(cp));
        return new Money(new BigDecimal(vp), CURRENCIES.getAndAddCurrency(cp));
        
    }

    public double toDoubleInBaseCurrency () {
        return getCurrency().convertToBaseCurrency(this).getValue().doubleValue();
    }
    
    
}
