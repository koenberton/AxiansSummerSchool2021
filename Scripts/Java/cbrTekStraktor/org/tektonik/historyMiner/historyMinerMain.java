package org.tektonik.historyMiner;

import org.tektonik.tools.generalpurpose.gpUtils;

public class historyMinerMain {

	static gpUtils utils = null;
	
	public static void main(String[] args) {
		
		utils = new gpUtils();
		
		coarseSearch cs = new coarseSearch(utils);
		//cs.maakBatchFile( "C:\\Temp\\tekHistory\\PDFs" );
		cs.doProximitySearchInFolder( "C:\\Temp\\tekHistory\\ExtractedTexts" , "Pittem" );
	
	}

	
}
