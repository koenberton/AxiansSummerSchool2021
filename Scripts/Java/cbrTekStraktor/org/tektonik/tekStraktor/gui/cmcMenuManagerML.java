package org.tektonik.tekStraktor.gui;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class cmcMenuManagerML {

	cmcMenuShared memShared = null;
	JMenuItem arMenuItem[] = new JMenuItem[150];
	

	//---------------------------------------------------------------------------------
	public cmcMenuManagerML( cmcMenuShared is)
	//---------------------------------------------------------------------------------
	{
	   	memShared = is;
	}

	//---------------------------------------------------------------------------------
	public boolean makeMenus(JMenuBar menuBar , Font xfont , int Breedte , String[] ar)
	//---------------------------------------------------------------------------------
	{
		JMenu fileMenu = new JMenu("File");
		fileMenu.setPreferredSize(new Dimension(Breedte,menuBar.getHeight()));
		fileMenu.setFont(xfont);
		fileMenu.setOpaque(true);
		//
		JMenu importMenu = new JMenu("Import");
		importMenu.setPreferredSize(new Dimension(Breedte,menuBar.getHeight()));
		importMenu.setFont(xfont);
		importMenu.setOpaque(true);
		//
		JMenu exportMenu = new JMenu("Export");
		exportMenu.setPreferredSize(new Dimension(Breedte,menuBar.getHeight()));
		exportMenu.setFont(xfont);
		exportMenu.setOpaque(true);
		//
		JMenu toolsMenu = new JMenu("Tools");
		toolsMenu.setPreferredSize(new Dimension(Breedte,menuBar.getHeight()));
		toolsMenu.setFont(xfont);
		toolsMenu.setOpaque(true);
		//
		JMenu editMenu = new JMenu("Edit");
		editMenu.setPreferredSize(new Dimension(Breedte,menuBar.getHeight()));
		editMenu.setFont(xfont);
		editMenu.setOpaque(true);
		//
		JMenu projectMenu = new JMenu("Project");
		projectMenu.setPreferredSize(new Dimension(Breedte,menuBar.getHeight()));
		projectMenu.setFont(xfont);
		projectMenu.setOpaque(true);
		//
		JMenu propertyMenu = new JMenu("Properties");
		propertyMenu.setPreferredSize(new Dimension(Breedte,menuBar.getHeight()));
		propertyMenu.setFont(xfont);
		propertyMenu.setOpaque(true);
		//
		JMenu filterMenu = new JMenu("Image Filters");
		filterMenu.setPreferredSize(new Dimension(Breedte,menuBar.getHeight()));
		filterMenu.setFont(xfont);
		filterMenu.setOpaque(true);
		//
		JMenu moreMenu = new JMenu("More");
		moreMenu.setPreferredSize(new Dimension(Breedte,menuBar.getHeight()));
		moreMenu.setFont(xfont);
		moreMenu.setOpaque(true);
		//
		JMenu ClassifierMenu = new JMenu("Classifiers");
		ClassifierMenu.setPreferredSize(new Dimension(Breedte,menuBar.getHeight()));
		ClassifierMenu.setFont(xfont);
		ClassifierMenu.setOpaque(true);
		//
		JMenu NeuralNetworkMenu = new JMenu("Neural network");
		NeuralNetworkMenu.setPreferredSize(new Dimension(Breedte,menuBar.getHeight()));
		NeuralNetworkMenu.setFont(xfont);
		NeuralNetworkMenu.setOpaque(true);
		//
		JMenu GoogleMenu = new JMenu("Google");
		GoogleMenu.setPreferredSize(new Dimension(Breedte,menuBar.getHeight()));
		GoogleMenu.setFont(xfont);
		GoogleMenu.setOpaque(true);
		//
		JMenu NaiveBayesMenu = new JMenu("Naive Bayes");
		NaiveBayesMenu.setPreferredSize(new Dimension(Breedte,menuBar.getHeight()));
		NaiveBayesMenu.setFont(xfont);
		NaiveBayesMenu.setOpaque(true);
		//
		JMenu DecisionTreeMenu = new JMenu("Decision Tree");
		DecisionTreeMenu.setPreferredSize(new Dimension(Breedte,menuBar.getHeight()));
		DecisionTreeMenu.setFont(xfont);
		DecisionTreeMenu.setOpaque(true);
		//
		JMenu KNNMenu = new JMenu("K Nearest Neighbours");
		KNNMenu.setPreferredSize(new Dimension(Breedte*2,menuBar.getHeight()));
		KNNMenu.setFont(xfont);
		KNNMenu.setOpaque(true);
		//
		JMenu SVMMenu = new JMenu("Support Vector Machine");
		SVMMenu.setPreferredSize(new Dimension(Breedte*2,menuBar.getHeight()));
		SVMMenu.setFont(xfont);
		SVMMenu.setOpaque(true);
		//
		JMenu LogMenu = new JMenu("Logging information");
		LogMenu.setPreferredSize(new Dimension(Breedte*2,menuBar.getHeight()));
		LogMenu.setFont(xfont);
		LogMenu.setOpaque(true);
		//
		for(int i=0;i<arMenuItem.length;i++) arMenuItem[i]=null;
		//
		menuBar.add(fileMenu);
		menuBar.add(toolsMenu);
		//
		int k=-1;
		JMenu current = fileMenu;
		for(int i=0;i<org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.values().length;i++)
		{
			k++;
			String sText = org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.values()[i].toString();
			if( sText.startsWith("DIV") ) {
				current.add(new JSeparator());
				continue;
			}
			if( sText.compareToIgnoreCase("IMPORT")==0 ) {
				current.add(importMenu);
				current = importMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("IMPORT_CONT")==0 ) {
				current = fileMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("EXPORT")==0 ) {
				current.add(exportMenu);
				current = exportMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("EXPORT_CONT")==0 ) {
				current = fileMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("LOGGING_CONT")==0 ) {
				current = toolsMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("LOGGING")==0 ) {
				current.add(LogMenu);
				current = LogMenu;
				continue;
			}
			if( sText.startsWith("TOOLS") ) {
				current = toolsMenu;
				continue;
			}
			if( sText.startsWith("PROJECT_START") ) {
				current.add(projectMenu);
				current = projectMenu;
				continue;
			}
			if( sText.startsWith("PROJECT_STOP") ) {
				current = fileMenu;
				continue;
			}
			if( sText.startsWith("PROPERTIES_START") ) {
				current.add(propertyMenu);
				current = propertyMenu;
				continue;
			}
			if( sText.startsWith("PROPERTIES_STOP") ) {
				current = fileMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("EDIT_ZONE_START")==0 ) {
				current.add(editMenu);
				current = editMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("EDIT_ZONE_STOP")==0 ) {
				current = toolsMenu;
				continue;
			}
			if( sText.startsWith("IMAGE_FILTERS") ) {
				current.add(filterMenu);
				current = filterMenu;
				continue;
			}
			if( sText.startsWith("TOOLS_CONT") ) {
				current = toolsMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("MORE") == 0) {
				current.add(moreMenu);
				current = moreMenu;
				continue;
			}
			if( sText.startsWith("MORE_CONT") ) {
				current = toolsMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("CLASSIFIERS") == 0) {
				current.add(ClassifierMenu);
				current = ClassifierMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("NEURAL_NETWORK") == 0) {
				current.add(NeuralNetworkMenu);
				current = NeuralNetworkMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("GOOGLE") == 0) {
				current.add(GoogleMenu);
				current = GoogleMenu;
				continue;
			}
			if( sText.startsWith("GOOGLE_CONT") ) {
				current = ClassifierMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("NAIVE_BAYES") == 0) {
				current.add(NaiveBayesMenu);
				current = NaiveBayesMenu;
				continue;
			}
			if( sText.startsWith("NAIVE_BAYES_CONT") ) {
				current = ClassifierMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("DECISION_TREE") == 0) {
				current.add(DecisionTreeMenu);
				current = DecisionTreeMenu;
				continue;
			}
			if( sText.startsWith("DECISION_TREE_CONT") ) {
				current = ClassifierMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("K_NEAREST_NEIGHBOURS") == 0) {
				current.add(KNNMenu);
				current = KNNMenu;
				continue;
			}
			if( sText.startsWith("K_NEAREST_NEIGHBOURS_CONT") ) {
				current = ClassifierMenu;
				continue;
			}
			if( sText.compareToIgnoreCase("SUPPORT_VECTOR_MACHINE") == 0) {
				current.add(SVMMenu);
				current = SVMMenu;
				continue;
			}
			if( sText.startsWith("SUPPORT_VECTOR_MACHINE_CONT") ) {
				current = ClassifierMenu;
				continue;
			}
			if( sText.startsWith("CLASSIFIERS_CONT") ) {
				current = fileMenu;
				continue;
			}
			if( sText.startsWith("NEURAL_NETWORK_CONT") ) {
				current = fileMenu;
				continue;
			}
            //
			arMenuItem[k] = new JMenuItem( memShared.makeLabel(sText) );
			arMenuItem[k].setFont(xfont);
			
			
			arMenuItem[k].addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent actionEvent) {
	                performMenuItemAction( actionEvent.getSource() );
	            }
	        });
		    current.add(arMenuItem[k]);
		}
		setRecent(ar);	
		return true;
	}
	
	//---------------------------------------------------------------------------------
	private void performMenuItemAction(Object iO)
	//---------------------------------------------------------------------------------
	{
		org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML pdx = null;
		try {
		  if( iO == null ) return;
		  JMenuItem mi = (JMenuItem)iO;
//System.err.println( mi.getFont().toString() + " " + mi.getLabel());
		  int idx=-1;
		  for(int i=0;i<org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.values().length;i++)
		  {
			if( arMenuItem[i] == null ) continue;
			if( mi == arMenuItem[i] ) { idx=i; break; }
		  }
		  if( idx < 0 ) return;
		  pdx = org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.values()[idx];
		}
		catch( Exception e ) {
			return;
		}
		if( pdx == null ) return;
		//
		switch( pdx )
		{
		//case OPEN_PROJECT  : { memShared.performCallback("doProject","O"); break; }
		//case EDIT_PROJECT  : { memShared.performCallback("doProject","E"); break; }
		//case NEW_PROJECT   : { memShared.performCallback("doProject","N"); break; }
		case OPEN_IMAGE    : { memShared.performCallback("doClickImageButton"); break;}
		//case OPEN_REPORT   : { memShared.performCallback("doClickReportButton"); break;}
		//case OPEN_ARCHIVE  : { memShared.performCallback("doClickArchiveBrowser"); break;} 
		case SAVE          : { memShared.performCallback("doSave"); break;}
		case REFRESH       : { memShared.performCallback("doClickRefreshButton"); break;}
		case LOAD_IMAGE    : { memShared.performCallback("doClickImageButton"); break;}
		//case EXTRACT_TEXT  : { memShared.performCallback("doClickExtractButton"); break;}
		//case START_EDIT    : { memShared.performCallback("doClickEditButton"); break;}
		//case STOP_EDIT     : { memShared.performCallback("doClickEditButton"); break;}
		//case OCR           : { memShared.performCallback("doClickOCRButton"); break;}
		//case TRANSLATE     : { memShared.performCallback("doClickTranslateButton"); break;}
		//case REINJECT      : { memShared.performCallback("doClickReInjectButton"); break;}
		//case REPORT        : { memShared.performCallback("doClickReportButton"); break;}
		case INFO          : { memShared.performCallback("doClickInfoButton"); break;}
		//case EDIT_OPTIONS  : { memShared.performCallback("doClickOptionsButton"); break; }
		//case QUICK_EDIT    : { memShared.performCallback("doQuickEdit"); break; }
		//case DETAILED_EDIT : { memShared.performCallback("doDetailedEdit"); break; }
		//case TESSERACT_OPTIONS  : { memShared.performCallback("doTesseractOptionDialog"); break; }
		//case TESSERACT_VERSION  : { memShared.performCallback("doTesseractVersionDialog"); break; }
		//case IMPORT_TEXT   : { memShared.performCallback("doImportText"); break; }
		//case EXPORT_TEXT   : { memShared.performCallback("doExportText"); break; }
		case TOGGLE_DETAIL_PANE       : { memShared.performCallback("doToggleDetailPane"); break;}
		//
		case MACHINE_LEARNING_MODELER        : { memShared.performCallback("doMachineLearningModeler"); break; }
		case MAKE_TENSORFLOW_TRAINING_SET    : { memShared.performCallback("doVRMakeTrainingImageSetTensorflow"); break; }
		case MAKE_NAIVE_BAYES_TRAINING_SET   : { memShared.performCallback("doVRMakeTrainingImageSetBayes"); break; }
		case MAKE_DECISION_TREE_TRAINING_SET : { memShared.performCallback("doVRMakeTrainingImageSetDecisionTree"); break; }
		case MAKE_SVM_TRAINING_SET           : { memShared.performCallback("doVRMakeTrainingImageSetSVM"); break; }
		//
		case EXTRACT_NAIVE_BAYES_FEATURES    : { memShared.performCallback("doVRExtractFeaturesBayes"); break; }
		case EXTRACT_DECISION_TREE_FEATURES  : { memShared.performCallback("doVRExtractFeaturesDecisionTree"); break; }
		case EXTRACT_SVM_FEATURES            : { memShared.performCallback("doVRExtractFeaturesSVM"); break; }
		//
		case TRAIN_NAIVE_BAYES_MODEL         : { memShared.performCallback("doVRTrainModelBayes"); break; }
		case TRAIN_DECISION_TREE_MODEL       : { memShared.performCallback("doVRTrainModelDecisionTree"); break; }
		case TRAIN_KNN_MODEL                 : { memShared.performCallback("doVRTrainModelKNN"); break; }
		//
		case EXTRACT_NAIVE_BAYES_SINGLE_SET  : { memShared.performCallback("doVRExtractAndRunSingleSetBayes"); break; }
		case EXTRACT_SVM_SINGLE_SET          : { memShared.performCallback("doVRExtractAndRunSingleSetSVM"); break; }
		case EXTRACT_DECISION_TREE_SINGLE_SET: { memShared.performCallback("doVRExtractAndRunSingleSetDecisionTree"); break; }
		case EXTRACT_KNN_SINGLE_SET          : { memShared.performCallback("doVRExtractAndRunSingleSetKNN"); break; }
		case EXTRACT_TENSORFLOW_SINGLE_SET   : { memShared.performCallback("doVRExtractAndRunSingleSetTensorflow"); break; }
		//
		case READJUST_TEXTBUBBLES_VIA_NAIVE_BAYES   : { memShared.performCallback("doVRReadjustBayes"); break; }
		case READJUST_TEXTBUBBLES_VIA_DECISION_TREE : { memShared.performCallback("doVRReadjustDecisionTree"); break; }
		case READJUST_TEXTBUBBLES_VIA_KNN           : { memShared.performCallback("doVRReadjustKNN"); break; }
		case READJUST_TEXTBUBBLES_VIA_TENSORFLOW    : { memShared.performCallback("doVRReadjustTensorflow"); break; }
		//
		case VIEW_ERRORS : { memShared.performCallback("doViewErrors"); break; }
		case VIEW_LOGGING : { memShared.performCallback("doViewLogs"); break; }
		//
		case RECENT1 : { memShared.performCallback("doRecent","0"); break; }
		case RECENT2 : { memShared.performCallback("doRecent","1"); break; }
		case RECENT3 : { memShared.performCallback("doRecent","2"); break; }
		case RECENT4 : { memShared.performCallback("doRecent","3"); break; }
		case RECENT5 : { memShared.performCallback("doRecent","4"); break; }
	    // filters
		case INVERT : 
		case ORIGINAL : 
		case GRAYSCALE : 
		case MONOCHROME_OTSU : 
		case BLUEPRINT : 
		case MONOCHROME_SAUVOLA :
		case BLEACHED :
		case MONOCHROME_NIBLAK :
		case MAINFRAME :
		case CONVOLUTION_EDGE :
		case CONVOLUTION_GAUSSIAN :
		case CONVOLUTION_SHARPEN :
		case CONVOLUTION_GLOW :
		case SOBEL_ON_GRAYSCALE :
		case SOBEL :
		case GRADIENT_WIDE :
		case GRADIENT_NARROW :
		case GRADIENT_MAGNITUDE :
		case GRADIENT_THETA :
		case HORIZONTAL_GRADIENT :
		case VERTICAL_GRADIENT :
		case CANNY :
		case MAXLLOYD :
		case TEXTURE :	
		case HISTOGRAM_EQUALISATION : { memShared.performCallback("doClickImageFilterButton",""+pdx); break; }
	    //
		//case HOUSEKEEPING    : { memShared.performCallback("doHousekeeping"); break; }
		//case STATISTICS      : { memShared.performCallback("doClickStatisticsButton"); break;}
		//case MONITOR         : { memShared.performCallback("doClickMonitor"); break;}
		//case ARCHIVE_VIEWER  : { memShared.performCallback("doClickArchiveViewer"); break;}
		//case ARCHIVE_BROWSER : { memShared.performCallback("doClickArchiveBrowser"); break;}
	    //
		case EXIT : { memShared.performCallback("doClose"); break; }
		default : { memShared.performCallback("popMessage" , "Currently no code foreseen for [" + pdx + "]"); break; }
		}
	}
	
	//---------------------------------------------------------------------------------
	public void setRecent(String[] ar)
	//---------------------------------------------------------------------------------
	{
		int k=-1;
		for(int i=0;i<org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.values().length;i++)
		{
			String sText = org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.values()[i].toString();
			if( sText.startsWith("RECENT") == false ) continue;
			k++;
			String ss = "";
			if( k >= ar.length ) ss = "";
			else {
			if( ar[k] == null ) ss = "";  else ss = ar[k];
			}
			arMenuItem[i].setText( ss );
		}
	}
	
	//---------------------------------------------------------------------------------
	public void setMenuItemStatus(org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML mi , boolean stat)
	//---------------------------------------------------------------------------------
	{
		  int idx=-1;
		  for(int i=0;i<org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.values().length;i++)
		  {
			if( mi == org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.values()[i] ) {
				idx=i;
				break;
			}
		  }
		  if( idx < 0 ) return;
          arMenuItem[idx].setEnabled( stat );
	}
	//---------------------------------------------------------------------------------
	public void setMenuItemVisible(org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML mi , boolean stat)
	//---------------------------------------------------------------------------------
	{
			  int idx=-1;
			  for(int i=0;i<org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.values().length;i++)
			  {
				if( mi == org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.values()[i] ) {
					idx=i;
					break;
				}
			  }
			  if( idx < 0 ) return;
	          arMenuItem[idx].setVisible( stat );
	}
	//---------------------------------------------------------------------------------
	public void setMenuAppState(org.tektonik.tekStraktor.model.cmcProcEnums.APPLICATION_STATE as)
	//---------------------------------------------------------------------------------
	{
		//setMenuItemVisible( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.START_EDIT , true );
		//setMenuItemVisible( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.STOP_EDIT , false);
		//setMenuItemVisible( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.QUICK_EDIT , false);
		//setMenuItemVisible( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.DETAILED_EDIT , false);
		//	
		if( (as == org.tektonik.tekStraktor.model.cmcProcEnums.APPLICATION_STATE.IDLE) ) {
			// disable all 
			for(int i=0;i<arMenuItem.length;i++)
			{
			 if( arMenuItem[i] != null ) arMenuItem[i].setEnabled(false);
			}
			// enable the following
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.OPEN_PROJECT , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.EDIT_PROJECT , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.NEW_PROJECT , true );
			//
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.OPEN_IMAGE , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.OPEN_REPORT , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.OPEN_ARCHIVE , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.INFO , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.TOGGLE_DETAIL_PANE , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.GENERAL_PROPERTIES , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.TESSERACT_OPTIONS , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.TESSERACT_VERSION , true );
			//
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.MACHINE_LEARNING_MODELER , true );
			//
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.MAKE_NAIVE_BAYES_TRAINING_SET , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.EXTRACT_NAIVE_BAYES_FEATURES  , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.TRAIN_NAIVE_BAYES_MODEL , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.EXTRACT_NAIVE_BAYES_SINGLE_SET , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.READJUST_TEXTBUBBLES_VIA_NAIVE_BAYES , true );
			//
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.MAKE_DECISION_TREE_TRAINING_SET , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.EXTRACT_DECISION_TREE_FEATURES  , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.TRAIN_DECISION_TREE_MODEL , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.EXTRACT_DECISION_TREE_SINGLE_SET , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.READJUST_TEXTBUBBLES_VIA_DECISION_TREE , true );
			//
			// setMenuItemStatus( cbrTekStraktorModel.cmcProcEnums.MENU_ITEMS.MAKE_KNN_TRAINING_SET , true );
			// setMenuItemStatus( cbrTekStraktorModel.cmcProcEnums.MENU_ITEMS.EXTRACT_KNN_FEATURES  , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.TRAIN_KNN_MODEL , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.EXTRACT_KNN_SINGLE_SET , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.READJUST_TEXTBUBBLES_VIA_KNN , true );
		    //
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.MAKE_SVM_TRAINING_SET , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.EXTRACT_SVM_FEATURES  , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.EXTRACT_SVM_SINGLE_SET , true );
			//
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.MAKE_TENSORFLOW_TRAINING_SET , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.EXTRACT_TENSORFLOW_SINGLE_SET , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.READJUST_TEXTBUBBLES_VIA_TENSORFLOW , true );
			//
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.RECENT1 , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.RECENT2 , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.RECENT3 , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.RECENT4 , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.RECENT5 , true );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.EXIT , true );
			//
		    setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.LOAD_IMAGE , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.EXTRACT_TEXT , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.START_EDIT , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.TRANSLATE , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.REINJECT , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.OCR , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.REPORT , true );
			//
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.HOUSEKEEPING , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.STATISTICS , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.MONITOR , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.ARCHIVE_VIEWER , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.ARCHIVE_BROWSER , true );
		    //
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.IMPORT_TEXT , true );
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.EXPORT_TEXT , true );

			return;
		}
	    //	in flux modi
		if( (as == org.tektonik.tekStraktor.model.cmcProcEnums.APPLICATION_STATE.IMAGELOADING) ||
			(as == org.tektonik.tekStraktor.model.cmcProcEnums.APPLICATION_STATE.EXTRACTING) ||
			(as == org.tektonik.tekStraktor.model.cmcProcEnums.APPLICATION_STATE.EDITLOADING) ||
			(as == org.tektonik.tekStraktor.model.cmcProcEnums.APPLICATION_STATE.OCR_BUSY) ||
			(as == org.tektonik.tekStraktor.model.cmcProcEnums.APPLICATION_STATE.STATSGATHERING) ) {
		    // disable all 
			for(int i=0;i<arMenuItem.length;i++)
			{
				if( arMenuItem[i] != null ) arMenuItem[i].setEnabled(false);
			}
			// allow monitor
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.MONITOR , true );
			//
			return;
		}
		// IMAGE
		if( (as == org.tektonik.tekStraktor.model.cmcProcEnums.APPLICATION_STATE.IMAGE) ) {
			// enable all 
			for(int i=0;i<arMenuItem.length;i++)
			{
			 if( arMenuItem[i] != null ) arMenuItem[i].setEnabled(true);
			}
			return;
		}
		// EDIT
		if( (as == org.tektonik.tekStraktor.model.cmcProcEnums.APPLICATION_STATE.EDIT) ) {
		  // disable all 
		  for(int i=0;i<arMenuItem.length;i++)
		  {
			 if( arMenuItem[i] != null ) arMenuItem[i].setEnabled(false);
		  }
		  //
		  //setMenuItemVisible( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.START_EDIT , false );
		  //setMenuItemVisible( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.STOP_EDIT , true );
		  //setMenuItemVisible( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.QUICK_EDIT , true);
		  //setMenuItemVisible( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.DETAILED_EDIT , true);
		  //
		  //setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.STOP_EDIT , true );
		  //setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.QUICK_EDIT , true );
		  //setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.DETAILED_EDIT , true );
		  //
		  setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.SAVE , true );
		  //setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.EDIT_OPTIONS , true );
		  setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.EXIT , true );
			
	    }
		// Extract or Stats
		if( (as == org.tektonik.tekStraktor.model.cmcProcEnums.APPLICATION_STATE.EXTRACTED) ||
			(as == org.tektonik.tekStraktor.model.cmcProcEnums.APPLICATION_STATE.EXTRACTED) ) {
			// enable all 
			for(int i=0;i<arMenuItem.length;i++)
			{
			 if( arMenuItem[i] != null ) arMenuItem[i].setEnabled(true);
			}
			// disable filters
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.ORIGINAL , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.BLUEPRINT , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.MAINFRAME , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.MONOCHROME_NIBLAK, false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.MONOCHROME_SAUVOLA , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.MONOCHROME_OTSU , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.BLEACHED , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.GRAYSCALE , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.ORIGINAL , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.INVERT , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.SOBEL_ON_GRAYSCALE , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.SOBEL , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.HISTOGRAM_EQUALISATION , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.GRADIENT_WIDE , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.GRADIENT_NARROW , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.GRADIENT_MAGNITUDE , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.GRADIENT_THETA , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.VERTICAL_GRADIENT , false );
			setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.HORIZONTAL_GRADIENT , false );
			
			//setMenuItemStatus( org.tektonik.tekStraktor.model.cmcProcEnums.MENU_ITEMS_ML.HOUSEKEEPING ,false );
			//
			return;
		}
		
		
	 }

	
}
