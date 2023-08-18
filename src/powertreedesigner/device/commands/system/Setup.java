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

import java.awt.Color;
import powertreedesigner.device.Device;
import powertreedesigner.device.command.Command;
import powertreedesigner.device.commands.drawer.Drawer;
import powertreedesigner.device.commands.drawer.Place;
import powertreedesigner.device.commands.drawer.Placer;
import powertreedesigner.device.commands.simulator.Integrator;
import powertreedesigner.device.exception.ParsingException;
import powertreedesigner.device.components.electricComponents.components.Component;
import powertreedesigner.device.components.electricComponents.parameter.EngNotation;
import powertreedesigner.device.components.electricComponents.parameter.ParameterColor;
import powertreedesigner.device.components.electricComponents.parameter.ParameterInteger;
import powertreedesigner.device.components.electricComponents.parameter.ParameterOptions;
import powertreedesigner.device.components.electricComponents.parameter.Setting;

/**
 *
 * @author Simone Pernice pernice@libero.it
 */
public class Setup extends Command {

    public Setup() {
        super("setup", "command system");
        
        addSetting(new Setting("decimalDigits", new ParameterInteger (1), "the number of decima digit to show"));
        addSetting(new Setting("drawBoxWidth", new ParameterInteger (Placer.getArcWidth()), "the width (in pixels) of the boxes used to draw the power tree"));
        addSetting(new Setting("drawBoxHeight", new ParameterInteger (Placer.getHeight()), "the height (in pixels) of the boxes used to draw the power tree"));
        addSetting(new Setting("drawBoxSpaceX", new ParameterInteger (Placer.getXDisplacement()), "the x spacing (in pixels) between boxes"));
        addSetting(new Setting("drawBoxSpaceY", new ParameterInteger (Placer.getYDisplacement()), "the y spacing (in pixels) between boxes"));
        addSetting(new Setting("drawBackGroundColor", new ParameterColor (Color.white), "the background color used to color the page of the drawing"));
        addSetting(new Setting("drawForeGroundColor", new ParameterColor (Color.black), "the foreground color used to color the labels and boxes of the drawing"));
        addSetting(new Setting("integratorAlgorithm", new ParameterOptions(Integrator.METHOD), "sets the integration algorithm to use. Euler needs 1 step to compute the next node voltages. Midpoint, Heun and Ralston 2 steps. RungeKutta3  3 steps, while the other RungeKutta needs 4 steps for 1 point. RungeKutta3 is the best compromise speed and transient accuracy. Euler is the fastest and can be used safely if a steady state analysis is performed. RungeKutta4 methods are the most accurate but also require to reduce the dvMax in order to get an accurate transient simulation."));
    }

    @Override
    public boolean hasLabel() {
        return false;
    }
        
    @Override
    public void addToDevice(Device d, String[] nodes) throws ParsingException {
        EngNotation.setDECIMALPLACES(getParameter("decimalDigits").getInteger());
        Place.setHeight(getParameter("drawBoxHeight").getInteger());
        Place.setWidth(getParameter("drawBoxWidth").getInteger());
        Place.setXDisplacement(getParameter("drawBoxSpaceX").getInteger());
        Place.setYDisplacement(getParameter("drawBoxSpaceY").getInteger());
        Drawer.setBACKGROUNDCOLOR(getParameter("drawBackGroundColor").getColor());
        Drawer.setFOREGROUNDCOLOR(getParameter("drawForeGroundColor").getColor());
        Integrator.setIntegrationAlgorithm(getParameter("integratorAlgorithm").getInteger());
    }

    @Override
    public Component makeNewParseable() {
        return new Setup();
    }      

    @Override
    public int getGateNumber() {
        return 0;
    }
        
    @Override
    public String getHelp() {
        return super.getHelp()+"-Function = sets up the basic behaviour of the power tree designer: decimal digits, ...\n";
    }      
}
