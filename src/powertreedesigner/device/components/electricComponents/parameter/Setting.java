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
package powertreedesigner.device.components.electricComponents.parameter;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Setting {
    private final String label;
    private Parameter value;
    private final String help;
    private boolean required;

    public Setting(String label, Parameter defValue, boolean required, String help) {
        this.label = label;
        this.value = defValue;
        this.help = help+" "+defValue.getHelp();
        this.required = required;
    }
    
    public Setting(String label, Parameter defValue, String help) {
        this(label, defValue, false, help);
    }

    public Parameter getValue() {
        return value;
    }

    public void setValue(Parameter value) {
        this.value = value;
        required = false;
    }

    public boolean isRequired() {
        return required;
    }

    public String getLabel() {
        return label;
    }

    public String getHelp() {
        return help;
    }
    
    
    
}
