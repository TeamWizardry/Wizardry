package com.teamwizardry.wizardry.common.core.version;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import static com.teamwizardry.wizardry.common.core.version.VersionChecker.onlineVersion;

public class ThreadVersionChecker extends Thread {

	public ThreadVersionChecker() {
		setName("Wizardry Version Checker Thread");
		setDaemon(true);
		if (ConfigValues.versionCheckerEnabled)
			start();
	}

	@Override
	public void run() {
		Wizardry.logger.info("Checking for new updates...");
		try {
			BufferedReader r;
			URL url;
			if (LibrarianLib.DEV_ENVIRONMENT) {
				url = new URL("https://raw.githubusercontent.com/TeamWizardry/Wizardry/master/version/" + MinecraftForge.MC_VERSION + "-dev.txt");
			} else {
				url = new URL("https://raw.githubusercontent.com/TeamWizardry/Wizardry/master/version/" + MinecraftForge.MC_VERSION + ".txt");
			}
			r = new BufferedReader(new InputStreamReader(url.openStream()));

			String line;
			StringBuilder text = new StringBuilder();
			while ((line = r.readLine()) != null) {
				if (onlineVersion == null) {
					onlineVersion = line;
					text.append("VERSION: ").append(onlineVersion).append("\n");
				} else {
					if (!line.isEmpty()) {
						if (NumberUtils.isCreatable(line)) {
							text.append("\n").append("VERSION: ").append(line).append("\n");
						} else {
							text.append(" - ").append(line).append("\n");
						}
					}
				}
			}

			VersionChecker.updateMessage = text.toString();
			r.close();
			Wizardry.logger.error("New version found! -> " + onlineVersion);
			Wizardry.logger.error("Message: " + VersionChecker.updateMessage);
		} catch (Exception e) {
			Wizardry.logger.error("Failed to check for updates! :(");
		}
		VersionChecker.doneChecking = true;
	}
}
