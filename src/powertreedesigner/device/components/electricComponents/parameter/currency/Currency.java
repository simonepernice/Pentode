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
import java.math.RoundingMode;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public final class Currency {
    private final String symbol;
    private final BigDecimal ratio;
    private final byte decDigits;

    public Currency(String symbol, double ratio, byte decDigits) {
        this (symbol, new BigDecimal(ratio), decDigits);
    }
        
    public Currency(String symbol, BigDecimal ratio, byte decDigits) {
        this.symbol = symbol;
        this.ratio = ratio;
        this.decDigits = decDigits;
    }

    public String getSymbol() {
        return symbol;
    }

    public byte getDecDigits() {
        return decDigits;
    }
    
    public String getConversionRatio () {
        return ratio.toPlainString();
    }
    
    public Money convertToBaseCurrency (Money m) {
        Currency bc = Money.CURRENCIES.getBaseCurrency();
        
        if (equals(bc)) return m;
        
        return new Money (m.getValue().multiply(ratio).divide(bc.ratio, RoundingMode.CEILING), bc);
    }            

    @Override
    public final boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof Currency) {
            Currency c = (Currency) other;
            return (c.getSymbol().equals(symbol));
        }
        return false;
    }
}
