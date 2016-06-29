package com.teamwizardry.wizardry.api.util.misc;

import com.teamwizardry.librarianlib.api.LoggerBase;

public class Logs extends LoggerBase {
	public static final Logs I = new Logs();
	
    protected Logs() {
		super("Wizardry");
	}
    
    @Override
    public LoggerBase getInstance() {
    	return I;
    }
}