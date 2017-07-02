package com.teamwizardry.wizardry.common.core.version;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraftforge.common.MinecraftForge;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ThreadVersionChecker extends Thread {

	public ThreadVersionChecker() {
		setName("Wizardry Version Checker Thread");
		setDaemon(true);
		start();
	}

	@Override
	public void run() {
		Wizardry.logger.info("Checking for new updates...");
		try {
			URL url = new URL("https://raw.githubusercontent.com/TeamWizardry/Wizardry/master/version/" + MinecraftForge.MC_VERSION + ".txt");
			BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
			VersionChecker.onlineVersion = r.readLine();
			r.close();
		} catch (Exception e) {
			Wizardry.logger.error("Failed to check for updates! :(");
		}
		VersionChecker.doneChecking = true;
	}
}
