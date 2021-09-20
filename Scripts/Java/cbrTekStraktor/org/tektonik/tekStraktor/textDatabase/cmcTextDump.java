package org.tektonik.tekStraktor.textDatabase;

import org.tektonik.tekStraktor.dao.cmcTextDAO;
import org.tektonik.tekStraktor.drawing.cmcGraphPageObject;
import org.tektonik.tekStraktor.model.cmcProcEnums;
import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tekStraktor.model.comicPage;
import org.tektonik.tools.generalpurpose.gpAppendStream;
import org.tektonik.tools.logger.logLiason;



public class cmcTextDump {

	cmcProcSettings xMSet=null;
	logLiason logger=null;
	
	//-----------------------------------------------------------------------
	public cmcTextDump( cmcProcSettings xi , logLiason ilog)
	//-----------------------------------------------------------------------
	{
		xMSet=xi;
		logger=ilog;
	}

	//-----------------------------------------------------------------------
	public boolean create(cmcGraphPageObject[] ar,comicPage cpi , String Language)
	//-----------------------------------------------------------------------
	{
		if( ar == null ) return false;
		//
		int aantal =0;
		for(int i=0;i<ar.length;i++)
		{   // need to dump text and non text paragraph - because tipes can manually be changed
			if( (ar[i].tipe != cmcProcEnums.PageObjectType.TEXTPARAGRAPH) && (ar[i].tipe != cmcProcEnums.PageObjectType.PARAGRAPH) ) continue;
			aantal++;
		}
		cmcTextObject[] ar_text = new cmcTextObject[aantal];
		int teller=0;
		for(int i=0;i<ar.length;i++)
		{
			if( (ar[i].tipe != cmcProcEnums.PageObjectType.TEXTPARAGRAPH) && (ar[i].tipe != cmcProcEnums.PageObjectType.PARAGRAPH) ) continue;
		    //	
			cmcTextObject x = new cmcTextObject();
			x.UID = ar[i].UID;
			x.BundelIdx = ar[i].BundelIdx;
			x.removed = ar[i].removed;
			x.confidence = (ar[i].tipe == cmcProcEnums.PageObjectType.TEXTPARAGRAPH) ? cmcProcEnums.TextConfidence.TEXT : cmcProcEnums.TextConfidence.NO_TEXT;
			//
			ar_text[teller] = x;
			teller++;
  		}
	    //	
		cmcTextDAO dao = new cmcTextDAO(xMSet,logger);
		return dao.createEmptyXMLFile(ar_text, cpi, Language);
	}
	
}
