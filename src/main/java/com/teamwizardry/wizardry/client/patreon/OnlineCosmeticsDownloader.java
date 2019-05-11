package com.teamwizardry.wizardry.client.patreon;

import com.teamwizardry.wizardry.Wizardry;

import java.net.URL;

import static com.teamwizardry.wizardry.common.core.version.VersionChecker.onlineVersion;

public class OnlineCosmeticsDownloader extends Thread {

	public OnlineCosmeticsDownloader() {
		setName("Wizardry Cosmetics Downloader Thread");
		setDaemon(true);
		start();
	}

	@Override
	public void run() {
		Wizardry.logger.info("Downloading capes...");
		try {
			URL url = new URL("");
			//	BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));

			//	r.close();
			Wizardry.logger.error("Cape downloads complete! -> " + onlineVersion);
		} catch (Exception e) {
			Wizardry.logger.error("Failed to download capes! :(");
		}
	}
}
