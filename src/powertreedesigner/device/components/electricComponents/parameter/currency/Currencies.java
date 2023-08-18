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

import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public final class Currencies {
    private Currency baseCurrency;
    
    private final HashMap <String, Currency> currencies;

    public Currencies() {
        currencies = new HashMap<> ();
        addCurrency (new Currency ("€", 1. , (byte) 2), true);
        
    }
    
    public void addCurrency (Currency cc, boolean isBaseCurrency) {
        currencies.put(cc.getSymbol(), cc);
        if (isBaseCurrency) baseCurrency = cc;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }        
    
    public Currency getAndAddCurrency (String cs) {
        Currency c = currencies.get(cs);
        if (c == null) {
            c = new Currency (cs, 1., baseCurrency.getDecDigits());
            currencies.put(cs, c);
        }
        return c;
    }
    
    public Currency getCurrency (String cs) {
        return currencies.get(cs);
    }
    
    public void deleteCurrency (String cs) {
        currencies.remove(cs);
    }
    
    public Collection<Currency> getValues () {
        return currencies.values();
    }
    
}
