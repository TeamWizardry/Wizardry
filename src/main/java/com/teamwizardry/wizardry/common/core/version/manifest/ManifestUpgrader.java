package com.teamwizardry.wizardry.common.core.version.manifest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.teamwizardry.wizardry.Wizardry;

public class ManifestUpgrader {
	private final File directory;
	private HashMap<String, HashMap<String, String>> manifestMap;
	
	private boolean hasManifestChanged = false; 
	private boolean hasFolderChanged = false;
	
	private boolean isFinalized = false;
	private boolean hasErrors = false;
	
	ManifestUpgrader(File directory) {
		this.directory = directory;
	}
	
	void startUpgradeManifest() {
		if( isFinalized )
			throw new IllegalStateException("Upgrade is already finalized");

		Wizardry.logger.info("    > Starting to upgrade manifest ...");
		
		try {
			// Read manifest file
			File externalManifest = new File(directory, ManifestHandler.MANIFEST_FILENAME);
			if (externalManifest.exists()) {
	
				if (!externalManifest.canRead()) {
					Wizardry.logger.error("    > SOMETHING WENT WRONG! Can't access manifest file for reading! No upgrade possible.");
					hasErrors = true;
					return;
				}
				
				manifestMap = new HashMap<>();
				ManifestUtils.loadManifestFile(externalManifest, manifestMap, false);
			}
		}
		catch(IOException exc) {
			Wizardry.logger.error("    > SOMETHING WENT WRONG! Can't read manifest file! No upgrade possible.");
			hasErrors = true;
		}
	}
	
	public void changeCategoryName(String oldName, String newName) {
		if( hasErrors )
			return;	// Ignore
		if( isFinalized )
			throw new IllegalStateException("Upgrade is already finalized");
		
		// rename category in manifest
		if( manifestMap != null ) {
			HashMap<String, String> entry = manifestMap.get(oldName);
			if( entry != null ) {
				manifestMap.remove(oldName);
				manifestMap.put(newName, entry);
				
				hasManifestChanged = true;
			}
		}
		
		// rename folder
		File folder = new File(directory + "/" + oldName + "/");
		if( folder.exists() ) {
			folder.renameTo(new File(directory + "/" + newName + "/"));
			
			hasFolderChanged = true;
		}
	}
	
	public void finalizeUpgrade() {
		if( hasErrors )
			return;	// Ignore
		if( isFinalized )
			throw new IllegalStateException("Upgrade is already finalized");
		
		try {
			// Write manifest file
			if( manifestMap != null && hasManifestChanged ) {
				File externalManifest = new File(directory, ManifestHandler.MANIFEST_FILENAME);
				
				externalManifest.delete();
				externalManifest.createNewFile();
				
				ManifestUtils.writeJsonToFile(ManifestUtils.generateManifestJson(manifestMap), externalManifest);
				
				Wizardry.logger.info("    > Successfully updated manifest file");
			}
			
			if( !hasFolderChanged && !hasManifestChanged )
				Wizardry.logger.info("    > Manifest is up-to-date. No changes were performed.");
		}
		catch(IOException exc) {
			exc.printStackTrace();
		}
		
		isFinalized = true;
	}
}
