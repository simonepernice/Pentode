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
package powertreedesigner.device.commands.system;

import powertreedesigner.device.Device;
import powertreedesigner.device.command.Command;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.components.Component;
import powertreedesigner.device.components.electricComponents.parameter.ParameterBoolean;
import powertreedesigner.device.components.electricComponents.parameter.ParameterDouble;
import powertreedesigner.device.components.electricComponents.parameter.ParameterInteger;
import powertreedesigner.device.components.electricComponents.parameter.Setting;
import powertreedesigner.device.components.electricComponents.parameter.currency.Currency;
import powertreedesigner.device.components.electricComponents.parameter.currency.Money;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Currencier extends Command {

    public Currencier() {
        super("currency", "command currency");
                
        addSetting(new Setting("cnvrsnrto", new ParameterDouble (1. , ""), "the conversion ratio used to multiply the original currency to compute the BOM amount on the base currency value"));
        addSetting(new Setting("dcmldgts", new ParameterInteger (2), "the number of decimal digits to print after the decimal point for this currency, internally all the values are stored"));
        addSetting(new Setting("base", new ParameterBoolean (false), "to set the base currency used to compute cost "));
    }

    @Override
    public int getGateNumber() {
        return 0; 
    }

    @Override
    public boolean hasLabel() {
        return true;
    }
        
    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
            System.out.println("Setting currency "+getLabel());
            
            Money.CURRENCIES.addCurrency(new Currency(getLabel(), getParameter("cnvrsnrto").getDouble(),(byte) getParameter("dcmldgts").getInteger()), getParameter("base").getBoolean());
            
            d.updateCost();
            
            System.out.println("End currency set.\n");    
    }

    @Override
    public Component makeNewParseable() {
        return new Currencier();
    }      
    
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = create or modify a new currency convert factor setting its conversion factor, decimal digits and the base currency. To convert any currency to the base currency it is first multipied by its 'conversion factor' then divided by the base currency 'conversion factor' which usually is 1 and the result is rounded up. Every time a currency is set the COST variable is updated with all the blackboxes and nodes money converted to the base currency. That variable can be used to compute the total BOM cost with the internal calculator.";
    }      
    
}
