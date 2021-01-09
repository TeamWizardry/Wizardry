package com.teamwizardry.wizardry.api;

import org.apache.logging.log4j.Logger;

import com.teamwizardry.librarianlib.courier.CourierChannel;

public class WizConsts
{
    private static CourierChannel courier;
    private static Logger logger;
    
    public static void setLogger(Logger logger) { if (WizConsts.logger == null) WizConsts.logger = logger; }
    public static Logger getLogger() { return logger; }
    
    public static void setCourier(CourierChannel courier) { if (WizConsts.courier == null) WizConsts.courier = courier; }
    public static CourierChannel getCourier() { return courier; }
}
