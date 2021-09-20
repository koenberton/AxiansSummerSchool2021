package org.tektonik.tekStraktor.gui;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.tektonik.MachineLearning.cmcMachineLearningConstants;
import org.tektonik.MachineLearning.ARFF.ARFFEnums;
import org.tektonik.MachineLearning.ARFF.cmcARFFSplitter;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPDTO;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPDTOCore;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPSupport;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPThreadLauncher;
import org.tektonik.MachineLearning.NeuralNetwork.cmcMLPenums;
import org.tektonik.MachineLearning.NeuralNetwork.NeuralNetwork.KMeans;
import org.tektonik.MachineLearning.NeuralNetwork.monitor.cmcEpochMonitorInfoAndDiagrams;
import org.tektonik.MachineLearning.dao.cmcARFFDAOLight;
import org.tektonik.MachineLearning.dao.cmcMLPDTODAO;
import org.tektonik.tekStraktor.model.cmcProcEnums;
import org.tektonik.tekStraktor.model.cmcProcSettings;
import org.tektonik.tools.generalGUIPurpose.windows.cmcFileViewer;
import org.tektonik.tools.generalpurpose.gpFileChooser;
import org.tektonik.tools.generalpurpose.gpUnZipFileList;
import org.tektonik.tools.logger.logLiason;

public class cmcMultiLayerPerceptronDialog {

	
	private static int WIDGET_HEIGTH  = 20;
	private static int BUTTON_HEIGTH  = 25;
	private static int BUTTON_WIDTH   = 140;
	private static int XMARGIN        = 10;
	private static int YMARGIN        = 10;
	private static int VERTICAL_GAP   = 2;
	private static int HORIZONTAL_GAP = 6;
	private static int LABEL_WIDTH    = BUTTON_WIDTH;
	private static int NUM_WIDTH      = LABEL_WIDTH / 2;
	private static int DROP_WIDTH     = LABEL_WIDTH * 2;
	
	
	cmcProcSettings xMSet = null;
	logLiason logger = null;
	cmcProcEnums  cenums = null;
	cmcMLPenums  mlpenums = null;
	
	cmcMLPDTO mlpdto = null;
    private cmcEpochMonitorInfoAndDiagrams prodia = null;	
    private cmcMLPThreadLauncher thman = null;
    private cmcARFFDAOLight arffdao = null;
    private cmcMLPSupport mlpsupp=null;
   	
	//
	private static JDialog dialog = null;
	private static JTabbedPane tabbedPane = null;
	//
	private static JPanel firstTabPane = null;
	private static JPanel secondTabPane = null;
	private static JPanel thirdTabPane = null;
	// first screen
	private JTextField txtDataFileName = null;
	private JTextField txtNbrHiddenLayers = null;
	private JTextField txtRelationName = null;
	private JTextField txtRelationComment = null;
	private JTextField txtNbrNeuronsPerLayer = null;
	private JTextField txtMiniBatchSize = null;
	private JTextField txtNbrARFFRows = null;
	private JTextField txtLearningRate = null;
	private JTextField txtAFactor = null;
	private JTextField txtDropOut = null;
	private JCheckBox ckAutoLearner = null;
	private JCheckBox ckDetailedDebug = null;
	private JCheckBox ckNice = null;
	private JTextField txtMaximumNbrEpochs = null;
	private JComboBox cbActivationFunction = null;
	private JComboBox cbOutputActivationFunction = null;
	private JComboBox cbCostFunction = null;
	private JComboBox cbOptimizationType = null;
	private JComboBox cbAssessmentType = null;
	private JComboBox cbWeightType = null;
	private JTextArea txtInfo = null;
	private JScrollPane scrollInfo=null;
	private static JButton btnT1Browse = null;
	private static JButton btnT1Archive = null;
	private static JButton btnT1Save = null;
	private static JButton btnT1Close = null;
	private static JButton btnT1Run = null;
	private static JButton btnT1Continue = null;
	private static JButton btnT1ViewLogs = null;
	private static JButton btnT1ViewData = null;
	//
	private JLabel lblProgress = null;
	private JLabel lblTick1 = null;
	private JCheckBox ckCurve = null;
	private JCheckBox ckRunningAverage = null;
	private JCheckBox ckSpeed = null;
	private static JButton btnT2Stop = null;
	//
	private JLabel lblROC = null;
	private JCheckBox ckNAClass = null;
	private static JButton btnT3Stop = null;
	
	private Font dfont = null;
	private String LastErrorMsg = null;
	private String DEFAULT_TITLE = "Multi-layered Neural Network";
	private long lastImageRefresh = 0L;
	private boolean isRunning=false;
	private int currentPane= -1;
	private boolean previousStatsReloaded=false;
	private boolean runHasBeenStopped=false;
	private boolean restartability=false;
	
	
	//------------------------------------------------------------
    private void do_log(int logLevel , String sIn)
	//------------------------------------------------------------
    {
       if( logger != null ) logger.write( this.getClass().getName() , logLevel , sIn);
       else 
       if (logLevel == 0 ) System.err.println(sIn);
       else System.out.println(sIn);
    }
	//------------------------------------------------------------
    private void do_error(String sIn)
	//------------------------------------------------------------
    {
    	LastErrorMsg = sIn;
    	do_log(0,sIn);
    }
    //------------------------------------------------------------
    public String getLastErrorMessage()
    //------------------------------------------------------------
    {
       return this.LastErrorMsg;	
    }
    //-----------------------------------------------------------------------
  	private void popMessage(String sMsg)
  	//-----------------------------------------------------------------------
  	{
  		JOptionPane.showMessageDialog(null,sMsg,DEFAULT_TITLE,JOptionPane.WARNING_MESSAGE);
    }
    //------------------------------------------------------------
	public cmcMultiLayerPerceptronDialog(JFrame jf , cmcProcSettings iM , logLiason ilog )
	//------------------------------------------------------------
	{
		xMSet = iM;
		logger = ilog;
		if( xMSet.xU.IsDir( xMSet.getSandBoxTempDir()) == false) {
	        System.err.println("oops - Sandbox folders not present");
	        System.exit(1);;
	    }
	    cenums = new cmcProcEnums(xMSet);
	    mlpenums = new cmcMLPenums();
	    prodia = new cmcEpochMonitorInfoAndDiagrams( xMSet , logger );
	    arffdao = new cmcARFFDAOLight( xMSet , logger );
	    mlpsupp = new cmcMLPSupport(xMSet,logger);
	    //
	    dfont = xMSet.getPreferredFont();  
	    contentRefresh(false);
		initialize(jf);
		setWidgetStatus(true);
	}
	//------------------------------------------------------------
	private void initialize(JFrame jf)
	//------------------------------------------------------------
	{
		    
			try
	        {
	            dialog = new JDialog(jf,"",Dialog.ModalityType.DOCUMENT_MODAL);  // final voor de dispose   
	            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	            dialog.setTitle( DEFAULT_TITLE );
	            dialog.setLocationRelativeTo( jf );
	            dialog.setLocation( xMSet.MLPframe.x , xMSet.MLPframe.y );
	            dialog.setLocationByPlatform(false);
	            dialog.setBounds( xMSet.MLPframe );
	            //
	            dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
	    	        public void windowClosing(WindowEvent winEvt) {
	    	        	if ( doDialogClose() == true ) {
	    	        		CleanUp();
	    	        		dialog.dispose();
	    	        	}
	    	        }
	    	    });
	            //
	    		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	    		tabbedPane.setFont(dfont);

	    		firstTabPane = makeFirstPanel();
	    		secondTabPane = makeSecondPanel();
	    		thirdTabPane = makeThirdPanel();
	    		
	    		tabbedPane.addTab("Neural Network Parameters", firstTabPane );
	    		tabbedPane.addTab("Epoch statistics", secondTabPane);
	    		tabbedPane.addTab("ROC", thirdTabPane);
	    	
	    		tabbedPane.addChangeListener(new ChangeListener() {
	                @Override
	                public void stateChanged(ChangeEvent e) {
	                    if (e.getSource() instanceof JTabbedPane) {
	                        JTabbedPane pane = (JTabbedPane) e.getSource();
	                        performTabPane( pane.getSelectedIndex() );
	                    }
	                }
	            });
	    	
	            //dialog.pack(); DONT
	    		dialog.getContentPane().add(tabbedPane);
	            dialog.setLocationByPlatform(true);
	            dialog.addComponentListener(new ComponentAdapter() 
	            {
	                public void componentResized(ComponentEvent e)
	                {
	                    doResizeDialog();
	                }
	            });
	            //
	           
	            // Timer
	    		ActionListener timerListener = new ActionListener(){
	    			 public void actionPerformed(ActionEvent event){
	    				 try {
	    			         long passed = 	System.currentTimeMillis() - lastImageRefresh;	 
	    				     if( passed >= (cmcMachineLearningConstants.REFRESH_INTERVAL_IN_SEC*1000L) ) {
	    					   lastImageRefresh = System.currentTimeMillis();
	    					   if(isRunning) doImageRefresh();
	    				     }
	    				     // check whether still running
	    				     if( isRunning ) checkRunStatus();
	    				     // ticker
	    				     doTicker();
	    				 }
	    				 catch(Exception e) {
	    					 System.err.println("Something went wrong" + xMSet.xU.LogStackTrace(e) );
	    				 }
	    			 }
	    			 //
	    		};
	    		Timer displayTimer = new Timer(500, timerListener);
	    		displayTimer.start();
	    		
	            doResizeDialog();
	            setWidgetStatus(true);
	            doHeader();
	            dialog.setVisible(true);
	        } 
	        catch (Exception e) 
	        {
	            do_error( "(Initialize)" + xMSet.xU.LogStackTrace(e) );
	        }
	}
	
	//------------------------------------------------------------
	private void doResizeDialog()
	//------------------------------------------------------------
	{
			xMSet.MLPframe = dialog.getBounds();
			int dwidth = dialog.getWidth();
			int dheigth = dialog.getHeight();
			//
			txtDataFileName.setBounds( txtDataFileName.getX() , txtDataFileName.getY() , dwidth - (4 * XMARGIN) - txtDataFileName.getX() , txtDataFileName.getHeight());
			txtRelationName.setBounds( txtRelationName.getX() , txtRelationName.getY() , dwidth - (4 * XMARGIN) - txtRelationName.getX() , txtRelationName.getHeight());
			txtRelationComment.setBounds( txtRelationComment.getX() , txtRelationComment.getY() , dwidth - (4 * XMARGIN) - txtRelationComment.getX() , txtRelationComment.getHeight());
			scrollInfo.setBounds( scrollInfo.getX() , scrollInfo.getY() , dwidth - (4 * XMARGIN) - scrollInfo.getX() , scrollInfo.getHeight());
					
	        //		
			int y = dheigth - 80 - BUTTON_HEIGTH;
			Rectangle r = btnT1Run.getBounds(); r.y = y;
			r = btnT1Browse.getBounds(); r.y = y;
			btnT1Browse.setBounds(r);
			r = btnT1Archive.getBounds(); r.y = y;
			btnT1Archive.setBounds(r);
			r = btnT1Save.getBounds(); r.y = y;
			btnT1Save.setBounds(r);
			r = btnT1Run.getBounds(); r.y = y;
			btnT1Run.setBounds(r);
			r = btnT1Continue.getBounds(); r.y = y;
			btnT1Continue.setBounds(r);
			r = btnT1Close.getBounds(); r.y = y;
			btnT1Close.setBounds(r);
			r = btnT1ViewLogs.getBounds(); r.y = y;
			btnT1ViewLogs.setBounds(r);
			r = btnT1ViewData.getBounds(); r.y = y;
			btnT1ViewData.setBounds(r);
			//
			
		    //
			r = lblProgress.getBounds(); 
			r.width = dwidth - (r.x * 4); 
			r.height = y - (BUTTON_HEIGTH);
			lblProgress.setBounds(r);
			r = btnT2Stop.getBounds(); r.y = y;
			btnT2Stop.setBounds(r);
			r = ckCurve.getBounds(); r.y = y;
			ckCurve.setBounds(r);
			r = ckRunningAverage.getBounds(); r.y = y;
			ckRunningAverage.setBounds(r);
			r = ckSpeed.getBounds(); r.y = y;
			ckSpeed.setBounds(r);
			r = lblTick1.getBounds(); r.y = y; r.x = dwidth - 50;
			lblTick1.setBounds(r);
			//
			r = lblProgress.getBounds(); 
			lblROC.setBounds(r);
			r = ckNAClass.getBounds(); r.y = y;
			ckNAClass.setBounds(r);
			r = btnT3Stop.getBounds(); r.y = y;
			btnT3Stop.setBounds(r);
			//
			if( isRunning ) doImageRefresh();
	}
	
	//------------------------------------------------------------
	private JPanel makeFirstPanel()
	//------------------------------------------------------------
	{
			int x = XMARGIN;
			int y = YMARGIN;
		
			
			JPanel p = new JPanel();
			p.setLayout(null);   // Absolute positioning
			//
			JLabel lblDataFileName = new JLabel("Data File");
			lblDataFileName.setFont(dfont);
		    lblDataFileName.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblDataFileName);
		    //
		    y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblRelationName = new JLabel("Relation name");
			lblRelationName.setFont(dfont);
		    lblRelationName.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblRelationName);
		    //
		    y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblRelationComment = new JLabel("Comment");
			lblRelationComment.setFont(dfont);
		    lblRelationComment.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblRelationComment);
		    //
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblHiddenLayers = new JLabel("#Hidden Layers");
			lblHiddenLayers.setFont(dfont);
		    lblHiddenLayers.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblHiddenLayers);
		    //
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblNeuronsPerLayer = new JLabel("#Neurons per layer");
			lblNeuronsPerLayer.setFont(dfont);
		    lblNeuronsPerLayer.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblNeuronsPerLayer);
		    //
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblMiniBatchSize = new JLabel("#Records in minibatch");
			lblMiniBatchSize.setFont(dfont);
		    lblMiniBatchSize.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblMiniBatchSize);
		    //
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblLearningRate = new JLabel("Learning Rate");
			lblLearningRate.setFont(dfont);
		    lblLearningRate.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblLearningRate);
		    //
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblALearningRate = new JLabel("Automatic learning rate");
			lblALearningRate.setFont(dfont);
		    lblALearningRate.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblALearningRate);
		    //
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblMaximumNbrEpochs = new JLabel("Maximum #epochs");
			lblMaximumNbrEpochs.setFont(dfont);
			lblMaximumNbrEpochs.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblMaximumNbrEpochs);
		    //
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblAFactor = new JLabel("Activation A Factor");
			lblAFactor.setFont(dfont);
			lblAFactor.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblAFactor);
		    //
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblDropOut = new JLabel("Dropout Ratio");
			lblDropOut.setFont(dfont);
			lblDropOut.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblDropOut);
		    //
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblActivationFunction = new JLabel("Activation function");
			lblActivationFunction.setFont(dfont);
			lblActivationFunction.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblActivationFunction);
		    //
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblOActivationFunction = new JLabel("Output Activation function");
			lblOActivationFunction.setFont(dfont);
			lblOActivationFunction.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblOActivationFunction);
		    //
		    y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblCostFunction = new JLabel("Cost function");
			lblCostFunction.setFont(dfont);
			lblCostFunction.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblCostFunction);
		    //
		    y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblOptimizationType = new JLabel("Optimization type");
			lblOptimizationType.setFont(dfont);
			lblOptimizationType.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblOptimizationType);
		    //
		    y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblAssessmentType = new JLabel("Assessment type");
			lblAssessmentType.setFont(dfont);
			lblAssessmentType.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblAssessmentType);
		    //
		    y += WIDGET_HEIGTH + VERTICAL_GAP;
			JLabel lblWeightType = new JLabel("Weight Initialization type");
			lblWeightType.setFont(dfont);
			lblWeightType.setBounds( x , y , LABEL_WIDTH , WIDGET_HEIGTH );
		    p.add(lblWeightType);
		    
		    // Input fields (2nd column)
		    x = lblDataFileName.getX() + lblDataFileName.getWidth() + HORIZONTAL_GAP;
		    y = lblDataFileName.getY();
			txtDataFileName = new JTextField( mlpdto==null ? "" : mlpdto.getLongARFFFileName() );
			txtDataFileName.setBounds( x , y , LABEL_WIDTH * 4, WIDGET_HEIGTH );
			txtDataFileName.setFont(dfont);
			txtDataFileName.setEditable(false);
			p.add(txtDataFileName);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			txtRelationName = new JTextField(arffdao == null ? "" : arffdao.getRelationName());
			txtRelationName.setBounds( x , y , LABEL_WIDTH * 4, WIDGET_HEIGTH );
			txtRelationName.setFont(dfont);
			txtRelationName.setEditable(false);
			p.add(txtRelationName);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			txtRelationComment = new JTextField( arffdao == null ? "" : arffdao.getRelationComment() );
			txtRelationComment.setBounds( x , y , LABEL_WIDTH * 4, WIDGET_HEIGTH );
			txtRelationComment.setFont(dfont);
			txtRelationComment.setEditable(false);
			p.add(txtRelationComment);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			txtNbrHiddenLayers = new JTextField( mlpdto==null ? "0" : ""+mlpdto.getNbrOfHiddenLayers() );
			txtNbrHiddenLayers.setBounds( x , y , NUM_WIDTH, WIDGET_HEIGTH );
			txtNbrHiddenLayers.setFont(dfont);
			p.add(txtNbrHiddenLayers);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			txtNbrNeuronsPerLayer = new JTextField( mlpdto==null ? "0" : ""+mlpdto.getNbrOfNeuronsPerHiddenLayer()  );
			txtNbrNeuronsPerLayer.setBounds( x , y , NUM_WIDTH , WIDGET_HEIGTH );
			txtNbrNeuronsPerLayer.setFont(dfont);
			p.add(txtNbrNeuronsPerLayer);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			txtMiniBatchSize = new JTextField( mlpdto==null ? "0" : ""+mlpdto.getSizeOfMiniBatch());
			txtMiniBatchSize.setBounds( x , y , NUM_WIDTH , WIDGET_HEIGTH );
			txtMiniBatchSize.setFont(dfont);
			p.add(txtMiniBatchSize);
			//
			txtNbrARFFRows = new JTextField( arffdao == null ? "?" : ""+arffdao.getNumberOfDataRows() );
			txtNbrARFFRows.setBounds( txtMiniBatchSize.getX() + txtMiniBatchSize.getWidth() + HORIZONTAL_GAP , y , NUM_WIDTH , WIDGET_HEIGTH );
			txtNbrARFFRows.setFont(dfont);
			txtNbrARFFRows.setEditable(false);
			p.add(txtNbrARFFRows);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			txtLearningRate = new JTextField( mlpdto==null ? "0" : ""+mlpdto.getLearningRate()  );
			txtLearningRate.setBounds( x , y , NUM_WIDTH , WIDGET_HEIGTH );
			txtLearningRate.setFont(dfont);
			p.add(txtLearningRate);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			ckAutoLearner = new JCheckBox();
			ckAutoLearner.setBounds( x , y , LABEL_WIDTH * 2, WIDGET_HEIGTH );
			ckAutoLearner.setFont(dfont);
			ckAutoLearner.setSelected(false);
			p.add(ckAutoLearner);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			txtMaximumNbrEpochs = new JTextField( mlpdto==null ? "0" : ""+mlpdto.getMaximumNumberOfEpochs()  );
			txtMaximumNbrEpochs.setBounds( x , y , NUM_WIDTH , WIDGET_HEIGTH );
			txtMaximumNbrEpochs.setFont(dfont);
			p.add(txtMaximumNbrEpochs);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			txtAFactor = new JTextField( mlpdto==null ? "0" : ""+mlpdto.getAFactor()  );
			txtAFactor.setBounds( x , y , NUM_WIDTH , WIDGET_HEIGTH );
			txtAFactor.setFont(dfont);
			p.add(txtAFactor);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			txtDropOut = new JTextField( mlpdto==null ? "0" : ""+mlpdto.getDropOutRatio()  );
			txtDropOut.setBounds( x , y , NUM_WIDTH , WIDGET_HEIGTH );
			txtDropOut.setFont(dfont);
			p.add(txtDropOut);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			String[] choices1 = mlpenums.getActivationFunctionTypeList();
			cbActivationFunction = new JComboBox<String>(choices1);
			cbActivationFunction.setFont(dfont);
			cbActivationFunction.setMaximumSize(cbActivationFunction.getPreferredSize()); // added code
			cbActivationFunction.setBounds( x , y , DROP_WIDTH , WIDGET_HEIGTH );
			cbActivationFunction.setSelectedIndex( xMSet.xU.getIdxFromList(choices1 , mlpdto==null ? null : ""+mlpdto.getActivationFunction()) );
			p.add(cbActivationFunction);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			String[] choicesZ = mlpenums.getActivationFunctionTypeList();
			cbOutputActivationFunction = new JComboBox<String>(choicesZ);
			cbOutputActivationFunction.setFont(dfont);
			cbOutputActivationFunction.setMaximumSize(cbOutputActivationFunction.getPreferredSize()); // added code
			cbOutputActivationFunction.setBounds( x , y , DROP_WIDTH , WIDGET_HEIGTH );
			cbOutputActivationFunction.setSelectedIndex( xMSet.xU.getIdxFromList(choicesZ , mlpdto==null ? null : ""+mlpdto.getOutputActivationFunction()) );
			p.add(cbOutputActivationFunction);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			String[] choices2 = mlpenums.getCostFunctionTypeList();
			cbCostFunction = new JComboBox<String>(choices2);
			cbCostFunction.setFont(dfont);
			cbCostFunction.setMaximumSize(cbCostFunction.getPreferredSize()); // added code
			cbCostFunction.setBounds( x , y , DROP_WIDTH , WIDGET_HEIGTH );
			cbCostFunction.setSelectedIndex( xMSet.xU.getIdxFromList(choices2 , mlpdto==null ? null : ""+mlpdto.getCostFunctionType()) );
			p.add(cbCostFunction);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			String[] choices3 = mlpenums.getOptimizationTypeList();
			cbOptimizationType = new JComboBox<String>(choices3);
			cbOptimizationType.setFont(dfont);
			cbOptimizationType.setMaximumSize(cbOptimizationType.getPreferredSize()); // added code
			cbOptimizationType.setBounds( x , y , DROP_WIDTH , WIDGET_HEIGTH );
			cbOptimizationType.setSelectedIndex( xMSet.xU.getIdxFromList(choices3 , mlpdto==null ? null : ""+mlpdto.getOptimizationType()) );
			p.add(cbOptimizationType);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			String[] choices4 = mlpenums.getAssessmentTypeList();
			cbAssessmentType = new JComboBox<String>(choices4);
			cbAssessmentType.setFont(dfont);
			cbAssessmentType.setMaximumSize(cbAssessmentType.getPreferredSize()); // added code
			cbAssessmentType.setBounds( x , y , DROP_WIDTH , WIDGET_HEIGTH );
			cbAssessmentType.setSelectedIndex( xMSet.xU.getIdxFromList(choices4 , mlpdto==null ? null : ""+mlpdto.getAssessmentType()) );
			p.add(cbAssessmentType);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			String[] choices5 = mlpenums.getWeightInitializationTypeList();
			cbWeightType = new JComboBox<String>(choices5);
			cbWeightType.setFont(dfont);
			cbWeightType.setMaximumSize(cbWeightType.getPreferredSize()); // added code
			cbWeightType.setBounds( x , y , DROP_WIDTH , WIDGET_HEIGTH );
			cbWeightType.setSelectedIndex( xMSet.xU.getIdxFromList(choices5 , mlpdto==null ? null : ""+mlpdto.getWeightStrategy()) );
			p.add(cbWeightType);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			ckDetailedDebug = new JCheckBox("Detailed debug info");
			ckDetailedDebug.setBounds( x , y , LABEL_WIDTH * 2, WIDGET_HEIGTH );
			ckDetailedDebug.setFont(dfont);
			ckDetailedDebug.setSelected(true);
			p.add(ckDetailedDebug);
			//
			//y += WIDGET_HEIGTH + VERTICAL_GAP;
			ckNice = new JCheckBox("Appease");
			ckNice.setBounds( x + ckDetailedDebug.getWidth() + HORIZONTAL_GAP, y , LABEL_WIDTH * 2, WIDGET_HEIGTH );
			ckNice.setFont(dfont);
			ckNice.setSelected(true);
			p.add(ckNice);
			//
			y += WIDGET_HEIGTH + VERTICAL_GAP;
			txtInfo = new JTextArea("");
			txtInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN,  12));
			txtInfo.setEditable(false);
			scrollInfo = new JScrollPane(txtInfo);
			scrollInfo.setBounds( x , y , LABEL_WIDTH * 4 , WIDGET_HEIGTH  * 10);
			p.add(scrollInfo);
			
		    // Buttons
			x = lblDataFileName.getX();
			y += (WIDGET_HEIGTH*2) + VERTICAL_GAP;
			//
			btnT1Browse = new JButton("Browse ARFF");
			btnT1Browse.setBounds( x, y , BUTTON_WIDTH , BUTTON_HEIGTH);
			btnT1Browse.setFont(dfont);
	        btnT1Browse.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if( btnT1Browse.isEnabled() ) {
		               selectDataFileName();	
		         	}
				}
			});
			p.add(btnT1Browse);
			//
			x += BUTTON_WIDTH + HORIZONTAL_GAP;
			btnT1Archive = new JButton("Browse archive");
			btnT1Archive.setBounds( x , y , BUTTON_WIDTH , BUTTON_HEIGTH);
			btnT1Archive.setFont(dfont);
	        btnT1Archive.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if( btnT1Archive.isEnabled() ) {
						if( fetchFromArchive() == false ) {
							popMessage( "Could not restore archive : " + LastErrorMsg );
						}
					}
				}
			});
			p.add(btnT1Archive);
			//
			x += BUTTON_WIDTH + HORIZONTAL_GAP;
			btnT1Save = new JButton("Save");
			btnT1Save.setBounds( x , y , BUTTON_WIDTH , BUTTON_HEIGTH);
			btnT1Save.setFont(dfont);
	        btnT1Save.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if( btnT1Save.isEnabled() ) performSave();
				}
			});
			p.add(btnT1Save);
			//
			x += BUTTON_WIDTH + HORIZONTAL_GAP;
			btnT1Run = new JButton("Run");
			btnT1Run.setBounds( x , y , BUTTON_WIDTH , BUTTON_HEIGTH);
			btnT1Run.setFont(dfont);
	        btnT1Run.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if( btnT1Run.isEnabled() ) doRun(false);
				}
			});
			p.add(btnT1Run);
			//
			x += BUTTON_WIDTH + HORIZONTAL_GAP;
			btnT1Continue = new JButton("Continue training");
			btnT1Continue.setBounds( x , y , BUTTON_WIDTH , BUTTON_HEIGTH);
			btnT1Continue.setFont(dfont);
	        btnT1Continue.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if( btnT1Continue.isEnabled() ) doRun(true);
				}
			});
			p.add(btnT1Continue);
			//
			x += BUTTON_WIDTH + HORIZONTAL_GAP;
			btnT1ViewLogs = new JButton("View logs");
			btnT1ViewLogs.setBounds( x , y , BUTTON_WIDTH , BUTTON_HEIGTH);
			btnT1ViewLogs.setFont(dfont);
	        btnT1ViewLogs.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					viewAllLogs();
				}
			});
			p.add(btnT1ViewLogs);
			//
			x += BUTTON_WIDTH + HORIZONTAL_GAP;
			btnT1ViewData = new JButton("View Data");
			btnT1ViewData.setBounds( x , y , BUTTON_WIDTH , BUTTON_HEIGTH);
			btnT1ViewData.setFont(dfont);
	        btnT1ViewData.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					cmcMLDialog cmet = new cmcMLDialog( null , xMSet , logger );
				}
			});
			p.add(btnT1ViewData);
			//
			x += BUTTON_WIDTH + HORIZONTAL_GAP;
			btnT1Close = new JButton("Close");
			btnT1Close.setBounds( x , y , BUTTON_WIDTH , BUTTON_HEIGTH);
			btnT1Close.setFont(dfont);
	        btnT1Close.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
				  if( btnT1Close.isEnabled() ) { CleanUp(); dialog.dispose(); }
				}
			});
			p.add(btnT1Close);
			
		    //		
			return p;
	}

	//------------------------------------------------------------
	private JPanel makeSecondPanel()
	//------------------------------------------------------------
	{
			int x = XMARGIN;
			int y = YMARGIN;
			
			JPanel p = new JPanel();
			p.setLayout(null);   // Absolute positioning
			 
			// canvas for diagram
			lblProgress = new JLabel();
			lblProgress.setBackground( Color.WHITE );
			lblProgress.setBounds( x , y , cmcMachineLearningConstants.MONITOR_DIAGRAM_WIDTH , cmcMachineLearningConstants.MONITOR_DIAGRAM_HEIGTH );
			lblProgress.setText("Nothing to display");
			p.add( lblProgress );
			
			//
			y += lblProgress.getHeight() + VERTICAL_GAP;
			btnT2Stop = new JButton("Stop");
			btnT2Stop.setBounds( x , y , BUTTON_WIDTH , BUTTON_HEIGTH);
			btnT2Stop.setFont(dfont);
	        btnT2Stop.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
				   if( btnT2Stop.isEnabled() ) requestStop(); // click also works when disabled
				}
			});
			p.add(btnT2Stop);
			//
			x = btnT2Stop.getX() + btnT2Stop.getWidth() + HORIZONTAL_GAP;
			ckCurve = new JCheckBox("Show curves");
			ckCurve.setBounds( x , y , LABEL_WIDTH * 2, WIDGET_HEIGTH );
			ckCurve.setFont(dfont);
			ckCurve.setSelected(true);
			p.add(ckCurve);
			//
			x += ckCurve.getWidth() + HORIZONTAL_GAP;
			ckRunningAverage = new JCheckBox("Running averages");
			ckRunningAverage.setBounds( x , y , LABEL_WIDTH * 2, WIDGET_HEIGTH );
			ckRunningAverage.setFont(dfont);
			ckRunningAverage.setSelected(true);
			p.add(ckRunningAverage);
			//
			x += ckRunningAverage.getWidth() + HORIZONTAL_GAP;
			ckSpeed = new JCheckBox("Gradients (log)");
			ckSpeed.setBounds( x , y , LABEL_WIDTH * 2, WIDGET_HEIGTH );
			ckSpeed.setFont(dfont);
			ckSpeed.setSelected(true);
			p.add(ckSpeed);
			//
			x += BUTTON_WIDTH + HORIZONTAL_GAP;
			lblTick1 = new JLabel();
			lblTick1.setBackground( Color.GREEN );
			lblTick1.setOpaque(true);
			lblTick1.setBounds( x , y , BUTTON_HEIGTH / 2 , BUTTON_HEIGTH / 2);
			lblTick1.setText("");
			p.add( lblTick1 );
		 	//
			
			return p;
	}
	
	//------------------------------------------------------------
	private JPanel makeThirdPanel()
	//------------------------------------------------------------
	{
				int x = XMARGIN;
				int y = YMARGIN;
				
				JPanel p = new JPanel();
				p.setLayout(null);   // Absolute positioning
				
				// canvas for diagram
				lblROC = new JLabel();
				lblROC.setBackground( Color.WHITE );
				lblROC.setBounds( x , y , 10 , 10 );
				lblROC.setText("ROC");
				p.add( lblROC );
				
				//
				y += lblROC.getHeight() + VERTICAL_GAP;
				btnT3Stop = new JButton("Stop");
				btnT3Stop.setBounds( x , y , BUTTON_WIDTH , BUTTON_HEIGTH);
				btnT3Stop.setFont(dfont);
		        btnT3Stop.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
					   if( btnT3Stop.isEnabled() ) requestStop(); // click also works when disabled
					}
				});
				p.add(btnT3Stop);	
				//	
				x = btnT3Stop.getX() + btnT3Stop.getWidth() + HORIZONTAL_GAP;
				ckNAClass = new JCheckBox("Show inconclusive bucket");
				ckNAClass.setBounds( x , y , LABEL_WIDTH * 2, WIDGET_HEIGTH );
				ckNAClass.setFont(dfont);
				ckNAClass.setSelected(true);
				p.add(ckNAClass);
			    //		
				return p;
	}
	
	//------------------------------------------------------------
	private void doHeader()
	//------------------------------------------------------------
	{
		try {
		 String title = null;
		 if( mlpdto != null ) {
			title = xMSet.xU.getFolderOrFileName( mlpdto.getLongARFFFileName() );
		 }
		 title = (title == null) ?  DEFAULT_TITLE : DEFAULT_TITLE + " [ARFF=" + xMSet.xU.Capitalize( title ) + "]";
		 dialog.setTitle( title );
		}
		catch(Exception e) { return; }
	}
	
	//------------------------------------------------------------
	private void contentRefresh(boolean skipreload)
	//------------------------------------------------------------
	{
		// if there is a previous run for this ARFF then reload else reset
		if( skipreload == false ) {  // reload
		   if( reloadParamsPreviousRunFromStats(xMSet.getLastARFFFileName()) ) {
			 mlpdto.setLongARFFFileName( xMSet.getLastARFFFileName() );
			 increaseMaximumEpochs(mlpdto);
			 doHeader();
			 return;
		   }
		}
		// RESET
		if( mlpdto == null ) {
			mlpdto = new cmcMLPDTO( xMSet.getLastARFFFileName() );
		    mlpdto.setNbrOfHiddenLayers( 2 );
		    mlpdto.setNbrOfNeuronsPerHiddenLayer( 32 );
		    mlpdto.setMaximumNumberOfEpochs( cmcMachineLearningConstants.DEFAULT_NBR_OF_EPOCHS );
		    mlpdto.setSizeOfMiniBatch( 1000 );
		    mlpdto.setLearningRate( (double)0.5 );
		    mlpdto.setActivationFunction( cmcMLPenums.ACTIVATION_FUNCTION_TYPE.SIGMOID );
		    mlpdto.setOutputActivationFunction( cmcMLPenums.ACTIVATION_FUNCTION_TYPE.SIGMOID );
		    mlpdto.setCostFunctionType( cmcMLPenums.COST_FUNCTION_TYPE.SQUARED_ERROR );
		    mlpdto.setOptimizationType( cmcMLPenums.OPTIMIZATION_TYPE.NONE );
		    mlpdto.setAssessmentType( cmcMLPenums.ASSESSMENT_TYPE.ONE_HOT_ENCODED_WA_TWIST_EUCLIDIAN );
		    mlpdto.setWeightStrategy( cmcMLPenums.WEIGHT_INITIALIZATION_TYPE.XAVIER );
		    mlpdto.setDropOutRatio( (double)0.05 );
	    }
		else {
		    mlpdto.setLongARFFFileName( xMSet.getLastARFFFileName() );
		}
		restartability = mlpsupp.checkMLPRestartability( mlpdto.getLongARFFFileName() );
		//
		if( xMSet.xU.IsBestand(xMSet.getLastARFFFileName()) == false ) return;
		if( arffdao.performFirstPass( xMSet.getLastARFFFileName() , "contentRefresh" ) == false ) return;
		mlpdto.setSizeOfMiniBatch( ((arffdao.getNumberOfDataRows() / 10) + 1) * 10 );  // set higher - why??
		doHeader();
	}
	
	//------------------------------------------------------------
	private void performTabPane( int paneIndex )
	//------------------------------------------------------------
	{
		currentPane = paneIndex;
		if( currentPane == 0 ) {
			setWidgetStatus( !isRunning );
		}
		else
		if( (currentPane == 1) || (currentPane == 2)) {
			if( previousStatsReloaded || runHasBeenStopped) forceImageRefresh();
			                        else doImageRefresh();
		}
	}
	
	//------------------------------------------------------------
	private void selectDataFileName()
	//------------------------------------------------------------
	{
		   gpFileChooser jc = new gpFileChooser( xMSet.getSandBoxSourceDir() );
		   jc.setFilter("ARFF");
	   	   jc.runDialog();
	       String fname = jc.getAbsoluteFilePath();
	       if( fname==null ) return;
	       // Look for TEST file if not exist then ask
	       String WithoutFolder = xMSet.xU.getFolderOrFileName(fname );
	       String WithoutTrain  = xMSet.xU.RemplaceerIgnoreCase( fname , "-Train.", ".");
	       String NoSuffix = xMSet.xU.getFileNameWithoutSuffix( WithoutTrain );
	       String TestFileName = NoSuffix + "-Test.arff";
	       boolean GotTestFile = xMSet.xU.IsBestand( TestFileName );
	       String TargetTrainFile = xMSet.getSandBoxTrainDir() + xMSet.xU.ctSlash + xMSet.xU.getFolderOrFileName(WithoutTrain);
	       String TargetTestFile  = xMSet.getSandBoxTestDir() + xMSet.xU.ctSlash + xMSet.xU.getFolderOrFileName(WithoutTrain);
	       if(  GotTestFile == false) {

	    	   JTextField jperc = new JTextField(10); 
	    	   jperc.setText(""+cmcMachineLearningConstants.TEST_SET_PERCENTAGE);
	    	   JComboBox jkernel = new JComboBox();
	    	   jkernel.addItem(""+ARFFEnums.TEST_DATA_CREATON_TYPE.MATCHING_DISTRIBUTION); 
	    	   jkernel.addItem(""+ARFFEnums.TEST_DATA_CREATON_TYPE.ONE_PASS_RANDOM); 
	    	   jkernel.setSelectedIndex(0);
	    	   Object[] msg = {
	    			   "No Test Data found for [" + WithoutFolder + "]\n" + 
	   	    		   "Do you want to create a test data file in folder [" + xMSet.getSandBoxTestDir() + "]?\n" , "" ,
	   	    		   "" , "" ,
	    			   "Percentage of test data to be sourced from original ARRF file", jperc , 
	    			   "Test Dadta Creation Strategy" , jkernel  };
	    	   int answer = JOptionPane.showConfirmDialog(
	    			    null,
	    	            msg,
	    	            "Test Data",
	    	            JOptionPane.YES_NO_CANCEL_OPTION,
		                JOptionPane.WARNING_MESSAGE  );
	           
	    	   
	           switch (answer) {
	            case JOptionPane.YES_OPTION: {
	            	double dd = xMSet.xU.NaarDoubleNAN(jperc.getText());
	            	if( Double.isNaN(dd)) dd = -1;
	                if( (dd<5) || (dd>50) ) {
	                	do_error("Percentage should be between 5 and 50");
	                	return;
	            	}
	            	String ss = (String)jkernel.getSelectedItem();
	            	ARFFEnums.TEST_DATA_CREATON_TYPE tipe = ARFFEnums.TEST_DATA_CREATON_TYPE.MATCHING_DISTRIBUTION;
	            	if( ss.trim().toUpperCase().startsWith("ONE_PASS")) tipe = ARFFEnums.TEST_DATA_CREATON_TYPE.ONE_PASS_RANDOM;
	            	//
	            	cmcARFFSplitter splr = new cmcARFFSplitter( xMSet , logger );
	            	GotTestFile = splr.splitARFFInTrainingAndTestNew( fname , TargetTrainFile, TargetTestFile , tipe , (int)dd);
	            	if( GotTestFile == false ) return;
	            	break;} //  
	            case JOptionPane.NO_OPTION: break;
	            case JOptionPane.CANCEL_OPTION: return;
	            default : return;
	           }
	       }
	       else {  // er is een -TEST file just move it
	        try {
	          // Move to TRAIN
	          if( xMSet.xU.IsBestand(TargetTrainFile) ) {
	    	   if( xMSet.xU.VerwijderBestand( TargetTrainFile ) == false ) {
	    		   do_error("Cannot remove [" + TargetTrainFile + "]");
	    		   return;
	    	   }
	          }
	          xMSet.xU.copyFile( fname , TargetTrainFile );
	          if( xMSet.xU.IsBestand(TargetTrainFile) == false ) {
	        	 do_error("Cannot move [" + WithoutFolder + "] to [" + TargetTrainFile + "]");
	    		 return;
	          }	 
	          // Move to TEST
	          if( xMSet.xU.IsBestand(TargetTestFile) ) {
		       if( xMSet.xU.VerwijderBestand( TargetTestFile ) == false ) {
		    	   do_error("Cannot remove [" + TargetTestFile + "]");
		    	   return;
		         }
		      }
		      xMSet.xU.copyFile( TestFileName , TargetTestFile );
		      if( xMSet.xU.IsBestand(TargetTestFile) == false ) {
		       	 do_error("Cannot move [" + TestFileName + "] to [" + TargetTestFile + "]");
		    	 return;
		      }	 
	         }
	         catch(Exception e ) {
	    	   do_error("Could not create/copy Train en Test files\nTrain=" + TargetTrainFile + "\nTest=" + TargetTestFile + "\n" + e.getMessage() );
	    	   return;
	         }
	       }
	       //
	       xMSet.setLastARFFFileName( TargetTrainFile );  // moet de TRAIN/file zijn
	       runHasBeenStopped = false;
	       previousStatsReloaded = false;
	       mlpsupp.fileCleanUp(); // will force a clean start
	       prodia.doReset();
	       contentRefresh(true);
	       setWidgetStatus(true);
	}
	
	/*
	//------------------------------------------------------------
	private boolean doMove( String LongFileName , String SourceDir , String TargetDir , boolean uu)
	//------------------------------------------------------------
	{
	
	   String ShortFileName = xMSet.xU.getFolderOrFileName( LongFileName );
	   if( ShortFileName == null ) return false;
	   String LongSourceFileName = SourceDir + xMSet.xU.ctSlash + ShortFileName;
	   if( xMSet.xU.IsBestand( LongSourceFileName) == false) return false;
	   String LongTargetFileName = TargetDir + xMSet.xU.ctSlash + ShortFileName;
	   if( xMSet.xU.IsBestand( LongTargetFileName) == true) {
		   if ( xMSet.xU.VerwijderBestand( LongTargetFileName) == false) return false;
	   }
	   try {
		   do_log( 9 , "Moving " + LongSourceFileName + " -> " + LongTargetFileName );
		   xMSet.xU.copyFile( LongSourceFileName , LongTargetFileName );
		   return true;
	   }
	   catch(Exception e) { 
		   do_error("Cannot move " + LongSourceFileName + " -> " + LongTargetFileName + " " + e.getMessage());
		   return false; }
	}
	*/
	//------------------------------------------------------------
	private boolean doFileMove( String ShortFileName , String SourceDir , String TargetDir )
	//------------------------------------------------------------
	{
		   if( ShortFileName == null ) return false;
		   String LongSourceFileName = SourceDir + xMSet.xU.ctSlash + ShortFileName;
		   if( xMSet.xU.IsBestand( LongSourceFileName) == false) {
			   do_error("(doFileMove) Cannot find [" + LongSourceFileName + "]");
			   return false;
		   }
		   String LongTargetFileName = TargetDir + xMSet.xU.ctSlash + ShortFileName;
		   if( xMSet.xU.IsBestand( LongTargetFileName) == true) {
			   if ( xMSet.xU.VerwijderBestand( LongTargetFileName) == false) return false;
		   }
		   try {
			   do_log( 9 , "Moving " + LongSourceFileName + " -> " + LongTargetFileName );
			   xMSet.xU.copyFile( LongSourceFileName , LongTargetFileName );
			   xMSet.xU.VerwijderBestand( LongSourceFileName );
			   return true;
		   }
		   catch(Exception e) { 
			   do_error("Cannot move " + LongSourceFileName + " -> " + LongTargetFileName + " " + e.getMessage());
			   return false; }
	}
	
	//------------------------------------------------------------
	private boolean fetchFromArchive()
	//------------------------------------------------------------
	{
		   gpFileChooser jc = new gpFileChooser( xMSet.getBayesResultDir() );
		   jc.setFilter("ZIP");
	   	   jc.runDialog();
	       String fname = jc.getAbsoluteFilePath();
	       if( fname==null ) return true;
	       if( fname.trim().toUpperCase().endsWith(".ZIP") == false ){ do_error("Not an archive file[" + fname + "]"); return false; }
	       //
	       xMSet.purgeDirByName( xMSet.getCacheDir() , true );
	       gpUnZipFileList unzip = new gpUnZipFileList( fname , xMSet.getCacheDir() , null , logger , true);
	       boolean ok = unzip.UnzippedCorrectly();
	       unzip=null;
	       if( !ok ) { popMessage("Could not unzip [" + fname + "]"); return false; }
	       // get the ARFF File name
	       //ArrayList<String> list = xMSet.xU.GetFilesInDir( xMSet.getCacheDir() , null );
	       ArrayList<String> list = xMSet.xU.GetFilesInDirRecursive( xMSet.getCacheDir() , null ); // Tmp,train,test
		   if( list == null ) { do_error("Cannot determine files in " +  xMSet.getCacheDir() ); return false; }
		   // maak relatief
		   for(int i=0;i<list.size();i++)
	       {
	    	   if( list.get(i) == null ) continue;
	    	   String Short = list.get(i).substring( xMSet.getCacheDir().length() + 1 );
	           list.set(i , Short );	   
	       }
	       String model = null;
	       String manifest = null;
	       for(int i=0;i<list.size();i++)
	       {
	    	   if( list.get(i) == null ) continue;
	    	   if( list.get(i).trim().toUpperCase().endsWith("_MLP.XML")  ) model = list.get(i).trim(); 	
	    	   if( list.get(i).trim().toUpperCase().endsWith("MANIFEST.XML")  ) manifest = list.get(i).trim(); 
	           //do_log(1,list.get(i));
	       }
	       if( model == null ) { do_error("Cannot find model"); return false; }
	       if( manifest == null ) { do_error("Cannot find manifest file"); return false; }
	       //
	       String TrainARFFFileName = mlpsupp.extractARFFFileNameFromManifest(  xMSet.getCacheDir() + xMSet.xU.ctSlash + manifest );
	       if( TrainARFFFileName == null ) {
	    	   do_error("Could not extract Training ARFFFileName from manifest file [" + manifest + "]");
	    	   return false;
	       }
	       String OriginalARFFFileName = xMSet.getSandBoxSourceDir() + xMSet.xU.ctSlash + xMSet.xU.getFolderOrFileName( TrainARFFFileName );
	       if( xMSet.xU.IsBestand( OriginalARFFFileName ) == false ) {
	    	   do_error( "The original ARFF File [" + OriginalARFFFileName + "] can no longer be located. Please restore.");
	    	   return false;
	       }
	       //do_log( 1 , "Original ARFF [" + OriginalARFFFileName + "] still present");
	    
	       // moven
	       if( mlpsupp.fileCleanUp() == false ) { popMessage("Could not purge [" + xMSet.getSandBoxDir() + "]"); return false; }
	       if( doFileMove( model , xMSet.getCacheDir() , xMSet.getSandBoxDir() ) == false ) { do_error("Could not move " + model ); return false;}
	       if( doFileMove( manifest , xMSet.getCacheDir() , xMSet.getSandBoxDir() ) == false ) { do_error("Could not move " + manifest ); return false;}
		   if( doFileMove( "Tmp" + xMSet.xU.ctSlash + xMSet.xU.getFolderOrFileName(xMSet.getEpochWeightDumpFileName()) , xMSet.getCacheDir() , xMSet.getSandBoxDir() ) == false ) { do_error("Could not move " +xMSet.getEpochWeightDumpFileName() ); return false;}
	       if( doFileMove( "Tmp" + xMSet.xU.ctSlash + xMSet.xU.getFolderOrFileName(xMSet.getEpochStatXMLFileName()) , xMSet.getCacheDir() , xMSet.getSandBoxDir() ) == false ) { do_error("Could not move " +xMSet.getEpochStatXMLFileName() ); return false;}
	       //  ARFF files
    	   String TestFile = xMSet.getARFFFileNameTestSet( TrainARFFFileName );
	  	   if( doFileMove( "Test" + xMSet.xU.ctSlash + xMSet.xU.getFolderOrFileName(TestFile) , xMSet.getCacheDir() , xMSet.getSandBoxDir() ) == false ) { do_error("Could not move " + TestFile ); return false;}
	       String TrainingFile = xMSet.getARFFFileNameTrainingSet( TrainARFFFileName );
	  	   if( doFileMove( "Train" + xMSet.xU.ctSlash + xMSet.xU.getFolderOrFileName(TrainingFile) , xMSet.getCacheDir() , xMSet.getSandBoxDir() ) == false ) { do_error("Could not move " + TrainingFile ); return false;}
		   //
	       do_log( 9, "Archive [" + xMSet.xU.getFolderOrFileName( TrainARFFFileName ) + "] restored [#" + list.size() + "]");
	       //
	       xMSet.setLastARFFFileName( TrainARFFFileName );
	       runHasBeenStopped = false;
	       previousStatsReloaded = false;
	       reloadParamsPreviousRunFromStats( xMSet.getLastARFFFileName() );
	       setWidgetStatus(true);
           doHeader();
           //
           return true;
	}
		
	//------------------------------------------------------------
	private boolean doRun(boolean carryon)
	//------------------------------------------------------------
	{
		String FName               = txtDataFileName.getText();
		String tHidden             = txtNbrHiddenLayers.getText();
		String tNeurons            = txtNbrNeuronsPerLayer.getText();
		String tMiniBatchSize      = txtMiniBatchSize.getText();
		String tActivationFunction = cbActivationFunction.getSelectedItem().toString();
		String tOutActFunction     = cbOutputActivationFunction.getSelectedItem().toString();
		String tCostFunction       = cbCostFunction.getSelectedItem().toString();
		String tOptimizationType   = cbOptimizationType.getSelectedItem().toString();
		String tAssessmentType     = cbAssessmentType.getSelectedItem().toString();
		String tWeightType         = cbWeightType.getSelectedItem().toString();
		String tLearningRate       = txtLearningRate.getText();
		String tAFactor            = txtAFactor.getText();
		String tDropOut            = txtDropOut.getText();
		String tMaxEpochs          = txtMaximumNbrEpochs.getText();
		//
		int iHidden = xMSet.xU.NaarInt( tHidden );
		int iNeurons = xMSet.xU.NaarInt( tNeurons );
		int iMiniBatchSize = xMSet.xU.NaarInt( tMiniBatchSize );
		cmcMLPenums.ACTIVATION_FUNCTION_TYPE actf = mlpenums.getActivationFunctionType( tActivationFunction );
		cmcMLPenums.ACTIVATION_FUNCTION_TYPE oactf = mlpenums.getActivationFunctionType( tOutActFunction );
		cmcMLPenums.COST_FUNCTION_TYPE costf = mlpenums.getCostFunctionType( tCostFunction );
		cmcMLPenums.OPTIMIZATION_TYPE optimtp = mlpenums.getOptimizationType( tOptimizationType );
		cmcMLPenums.ASSESSMENT_TYPE assf = mlpenums.getAssessmentType( tAssessmentType );
		cmcMLPenums.WEIGHT_INITIALIZATION_TYPE weif = mlpenums.getWeightInitializationType(tWeightType);
		double dLearningRate = xMSet.xU.NaarDoubleNAN( tLearningRate );
		double dAFactor = xMSet.xU.NaarDoubleNAN( tAFactor );
		double dDropOut = xMSet.xU.NaarDoubleNAN( tDropOut );
		int iMaxEpochs = xMSet.xU.NaarInt( tMaxEpochs );
		//
		String err="";
		if( xMSet.xU.IsBestand( FName ) == false ) err += "Cannot locate file [" + FName + "]";
		if( (iHidden<1) || (iHidden>10) ) err += "too many/few hidden layers [" + iHidden + "]";
		if( (iNeurons<1) || (iNeurons>1024) ) err += "too many/few hidden neurons [" + iNeurons + "]";
		if( (iMiniBatchSize<1) || (iMiniBatchSize>cmcMachineLearningConstants.MAX_MINIBATCH_LINES) ) err += "too low/high [" + iMiniBatchSize + "/" + cmcMachineLearningConstants.MAX_MINIBATCH_LINES + "]";
		if( (iMaxEpochs<1) || (iMaxEpochs>20000) ) err += "too many/few epochs [" + iMaxEpochs + "]";
		if( Double.isNaN( dLearningRate ) ) dLearningRate=-1;
		if( (dLearningRate<=0) || (dLearningRate>25) ) err += "too low/high LearningRate [" + dLearningRate + "]";
		if( Double.isNaN( dAFactor ) ) dAFactor=-9999;
		if( (dAFactor==-9999) || (dAFactor>25) ) err += "too low/high AFactor [" + tAFactor + "]";
		if( (dDropOut<0) || (dDropOut>(double)0.3) ) err += "too low/high DropOut [" + tDropOut + "]";
		if( actf == null ) err += "Unsupported activation function [" + tActivationFunction + "]";
		if( oactf == null ) err += "Unsupported output activation function [" + tOutActFunction + "]";
		if( costf == null ) err += "Unsupported cost function [" + costf + "]";
		if( assf == null ) err += "Unsupported assessment type [" + costf + "]";
		if( weif == null ) err += "Unsupported weight initialization type type [" + weif + "]";
		
		
		// 
		if( (actf != null) && (oactf != null) ) {
		  if( actf == cmcMLPenums.ACTIVATION_FUNCTION_TYPE.SOFTMAX ) err += "Activation output cannot be [" + actf + "]";
		  // SOFTMAX moet samen met CROSS ENTROPY en one hot encoded
		  if( oactf == cmcMLPenums.ACTIVATION_FUNCTION_TYPE.SOFTMAX ) {
		    if( costf != null ) {
			  if( costf !=  cmcMLPenums.COST_FUNCTION_TYPE.CROSS_ENTROPY ) err += "Softmax must be followed by Cross Entropy";
		    }
		    if( assf != null  ) {
			  if( (assf !=  cmcMLPenums.ASSESSMENT_TYPE.ONE_HOT_ENCODED_MAXARGS) && (assf !=  cmcMLPenums.ASSESSMENT_TYPE.ONE_HOT_ENCODED_EUCLIDIAN)) err += "Softmax must have a One Hot Encoded evaluation";
		    }
		  }
		}	
		//
		if( oactf != null ) {
		  if( oactf == cmcMLPenums.ACTIVATION_FUNCTION_TYPE.CONSTANT_ONE ) err += "Output Activation cannot be [" + oactf + "]";
		  if( oactf == cmcMLPenums.ACTIVATION_FUNCTION_TYPE.IGNORE ) err += "Output Activation cannot be [" + oactf + "]";
		  if( oactf == cmcMLPenums.ACTIVATION_FUNCTION_TYPE.UNKNOWN ) err += "Output Activation cannot be [" + oactf + "]";
		  if( oactf == cmcMLPenums.ACTIVATION_FUNCTION_TYPE.RELU ) err += "Output Activation cannot be [" + oactf + "]";
		}
		
		if( err.trim().length() != 0 ) {
			popMessage( err ); return false;
		}
		//
		cmcMLPDTOCore oldcore = new cmcMLPDTOCore( FName  );
		oldcore.kloonCore( mlpdto );
		cmcMLPDTOCore newcore = new cmcMLPDTOCore( FName  );
	    newcore.setActivationFunction( actf );
	    newcore.setOutputActivationFunction( oactf );
  		newcore.setCostFunctionType( costf );
  		newcore.setOptimizationType( optimtp );
  		newcore.setNbrOfHiddenLayers( iHidden );
  		newcore.setNbrOfNeuronsPerHiddenLayer( iNeurons );
  		newcore.setSizeOfMiniBatch( iMiniBatchSize );
  		newcore.setMaximumNumberOfEpochs( iMaxEpochs );
  		newcore.setLearningRate( dLearningRate ); 
  		newcore.setAFactor( dAFactor );
  		newcore.setDropOutRatio( dDropOut );
  		newcore.setAssessmentType( assf );
  		newcore.setWeightStrategy(weif);
  		//
  		if( carryon ) {
		    if( canModelBeRestarted( newcore , oldcore ) == false ) return false;
  		}
	    //
  		mlpdto.kloonCore( newcore );
  		if( carryon ) { // copy the weight and stat file
  			if( copyPreviousFiles() == false ) return false; 
  		}
  		//
  		mlpsupp.fileCleanUp();
  		//
  		thman = new cmcMLPThreadLauncher(xMSet,logger, newcore );
  		thman.setDetailedDebug( ckDetailedDebug.isSelected() );
  		thman.setNiceFactor( ckNice.isSelected() );
  		thman.setIsContinuation( carryon );
  		thman.start();
  		//
  		previousStatsReloaded=false;
  		runHasBeenStopped=false;
  		isRunning=true;
  		setWidgetStatus( false );
  		blankPicture();
  		tabbedPane.setSelectedIndex(1);
  	 	//
  		oldcore = null;
  		newcore = null;
		return true;
	}
	
	//------------------------------------------------------------
	private boolean canModelBeRestarted(cmcMLPDTOCore newcore , cmcMLPDTOCore oldcore)
	//------------------------------------------------------------
	{
		do_log( 9 , "NL=" + newcore.getNbrOfHiddenLayers() + " OL=" + oldcore.getNbrOfHiddenLayers() + "NN=" + newcore.getNbrOfNeuronsPerHiddenLayer() + " ON=" + oldcore.getNbrOfNeuronsPerHiddenLayer() );
		String ss = "";
		// the number of layers and numbre of neurons cannot be modified
		if( newcore.getNbrOfHiddenLayers() != oldcore.getNbrOfHiddenLayers() ) {
			ss += "[Number of hidden layers cannot be changed]\n";
		}
		if( newcore.getNbrOfNeuronsPerHiddenLayer() != oldcore.getNbrOfNeuronsPerHiddenLayer() ) {
			ss += "Number of neurons cannot be changed";
		}
		if( ss.length() == 0 )  return true;
		popMessage( "The continuation of training will be stopped:\n" + ss );
		return false;
	}
	
	//------------------------------------------------------------
	private boolean reloadParamsPreviousRunFromStats(String ARFFFileName )
	//------------------------------------------------------------
	{
	    previousStatsReloaded=false;
	    restartability = mlpsupp.checkMLPRestartability( ARFFFileName );
	    if( restartability == false ) return false;
	    
		// reload the MLPDTO and stats via convenience function
		prodia.forceReRead();
		if( prodia.getEpochStats() == false ) return false;
		mlpdto = prodia.getMLPDTO();
		if( mlpdto == null ) return false;
	 
		// the ARFFilename should match
		if( mlpdto.getLongARFFFileName().compareToIgnoreCase(ARFFFileName) != 0 ) {
			restartability=false;
			return false;
		}
		//
		if( arffdao != null ) arffdao.performFirstPass( ARFFFileName , "reloadParamsPreviousRunFromStats" );
		//
		previousStatsReloaded=true;
		return true;
	}
	
	//------------------------------------------------------------
	private boolean copyPreviousFiles()
	//------------------------------------------------------------
	{
		 if( mlpsupp.RemoveEpochFile( xMSet.getPreviousEpochWeightDumpFileName() ) == false ) return false;
		 if( mlpsupp.RemoveEpochFile( xMSet.getPreviousEpochStatXMLFileName() ) == false ) return false;	
		 if( mlpsupp.RemoveEpochFile( xMSet.getPreviousModelFileName() ) == false ) return false;	
		 try {
			 xMSet.xU.copyFile( xMSet.getEpochWeightDumpFileName() , xMSet.getPreviousEpochWeightDumpFileName() );
			 xMSet.xU.copyFile( xMSet.getEpochStatXMLFileName() , xMSet.getPreviousEpochStatXMLFileName() );
			 xMSet.xU.copyFile( mlpsupp.getXMLModelName(mlpdto.getLongARFFFileName()) , xMSet.getPreviousModelFileName() );
		 }
		 catch(Exception e ) {
			 do_error("Copying previous files");
			 return false;
		 }
		 return true;
	}
	
	//------------------------------------------------------------
	private void setWidgetStatus( boolean active )
	//------------------------------------------------------------
	{
		txtDataFileName.setText(mlpdto==null ? "" : mlpdto.getLongARFFFileName());
		txtRelationName.setText(arffdao == null ? "" : arffdao.getRelationName());
		txtRelationComment.setText( arffdao == null ? "" : arffdao.getRelationComment() );
		txtNbrHiddenLayers.setText( mlpdto==null ? "0" : ""+mlpdto.getNbrOfHiddenLayers() );
		txtNbrNeuronsPerLayer.setText( mlpdto==null ? "0" : ""+mlpdto.getNbrOfNeuronsPerHiddenLayer()  );
		txtMiniBatchSize.setText( mlpdto==null ? "0" : ""+mlpdto.getSizeOfMiniBatch());
		txtNbrARFFRows.setText( arffdao == null ? "?" : ""+arffdao.getNumberOfDataRows() );
		txtLearningRate.setText( mlpdto==null ? "0" : ""+mlpdto.getLearningRate()  );
		txtMaximumNbrEpochs.setText( mlpdto==null ? "0" : ""+mlpdto.getMaximumNumberOfEpochs()  );
		txtAFactor.setText( mlpdto==null ? "0" : ""+mlpdto.getAFactor()  );
		String[] choices1 = mlpenums.getActivationFunctionTypeList();
		cbActivationFunction.setSelectedIndex( xMSet.xU.getIdxFromList(choices1 , mlpdto==null ? null : ""+mlpdto.getActivationFunction()) );
		String[] choices2 = mlpenums.getCostFunctionTypeList();
		cbCostFunction.setSelectedIndex( xMSet.xU.getIdxFromList(choices2 , mlpdto==null ? null : ""+mlpdto.getCostFunctionType()) );
		String[] choices3 = mlpenums.getOptimizationTypeList();
		//do_error( "-->" + choices3[0] + " " + mlpdto.getOptimizationType() + " " + cbOptimizationType);
		cbOptimizationType.setSelectedIndex( xMSet.xU.getIdxFromList(choices3 , mlpdto==null ? null : ""+mlpdto.getOptimizationType()) );
		//
		String[] choices4 = mlpenums.getAssessmentTypeList();
		cbAssessmentType.setSelectedIndex( xMSet.xU.getIdxFromList(choices4 , mlpdto==null ? null : ""+mlpdto.getAssessmentType()) );
		//
		String[] choices5 = mlpenums.getWeightInitializationTypeList();
		cbWeightType.setSelectedIndex( xMSet.xU.getIdxFromList(choices5 , mlpdto==null ? null : ""+mlpdto.getWeightStrategy()) );
		
		//
		//
		btnT1Run.setText(  (previousStatsReloaded||runHasBeenStopped) ? "Recycle training" : "Start" );
		btnT1Save.setEnabled(active && runHasBeenStopped );
		btnT1Browse.setEnabled(active);
		btnT1Archive.setEnabled(active);
  		btnT1Run.setEnabled(active);
  		btnT1Continue.setEnabled(active && (previousStatsReloaded||runHasBeenStopped) && restartability );
  		btnT1Close.setEnabled(active);
  		//
  		btnT2Stop.setEnabled(!active);
  		lblTick1.setVisible( isRunning );
  		//
  		btnT3Stop.setEnabled(!active);
  		//
  		txtInfo.setText("");
  		if( prodia == null ) return;
  		int prevNbrRuns = prodia.getActualNbrOfentries();
  		String lc = prodia.getLastConfusionInfo();
  		txtInfo.setText( ((isRunning)?"Current run " : "Previous runs ") +
  				         "[#Epochs=" + prevNbrRuns + 
  				         "] [Cost=" + String.format("%10.3f", prodia.getLastCost()).trim() + 
  				         "] [Accuracy=" + String.format("%10.3f", prodia.getLastAccuracy()).trim() + 
  				         "]" + ((lc==null)?"":lc) );
  		txtInfo.setCaretPosition(0); // this forces the cursor to the top
  		//
  		txtRelationComment.setToolTipText( arffdao.getFullCommentHTMLString() );
    }
	
	//------------------------------------------------------------
	private boolean doDialogClose()
	//------------------------------------------------------------
	{
		if( isRunning ) {
		  String[] buttonLabels = new String[] {"Yes", "No", "Cancel"};
          String defaultOption = buttonLabels[0];
          Icon icon = null;
          int answer = JOptionPane.showOptionDialog( null ,
                "Training cycle active for [" + mlpdto.getLongARFFFileName() + "]\n" +
                "Do you want to save before exiting?",
                "Warning",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                icon,
                buttonLabels,
                defaultOption);    
          switch (answer) {
           case JOptionPane.YES_OPTION: { if( performSave() == false ) return false; break; }
           case JOptionPane.NO_OPTION: break;
           case JOptionPane.CANCEL_OPTION: return false;
           default : return false;
          }
          requestStop();
		}
		return true;
	}
	
	//------------------------------------------------------------
	private void requestStop()
	//------------------------------------------------------------
	{
		try {
		  btnT2Stop.setEnabled(false); // immediately disable to prtevent retriggering
		  btnT3Stop.setEnabled(false); 
	  	  mlpsupp.requestInterrupt();
	 	}
		catch(Exception e) {
			do_error( "Error - check status");
			return;
		}
	}

	//------------------------------------------------------------
	private void Terminate()
	//------------------------------------------------------------
	{
		  isRunning=false;
	  	  runHasBeenStopped=true;
	  	  mlpsupp.resetInterrupt();
	  	  performTabPane(currentPane);
	  	  prodia.forceReRead();
	  	  setWidgetStatus( true );
	}
	
	//------------------------------------------------------------
	private void checkRunStatus()
	//------------------------------------------------------------
	{
		try {
		  if( thman == null ) return;
	  	  if( thman.hasCompleted() == false ) return;
	  	  Terminate();
	  	  String err = thman.getLastErrorMsg();
		  if( err ==  null ) err="";
		  if( err.trim().length() != 0 ) { 
			  popMessage( "Error training network : " + err );
			  return;
		  }	  	 	  
		  if( reloadModel() == false ) {
			  popMessage( "Error reloading network" );
			  return;
		  }
		  popMessage( "Run has been stopped"); 
		}
		catch(Exception e) {
			do_error( "Error - check status");
			return;
		}
  	}

	//------------------------------------------------------------
	private boolean reloadModel()
	//------------------------------------------------------------
	{
			cmcMLPDTODAO dao = new cmcMLPDTODAO( xMSet , logger);
			cmcMLPDTO newmodel = dao.readFullMLPDTO( mlpsupp.getXMLModelName(mlpdto.getLongARFFFileName()) );
			if( newmodel == null ) return false;
			mlpdto = null;
			mlpdto = newmodel;
			restartability = mlpsupp.checkMLPRestartability(mlpdto.getLongARFFFileName());
			increaseMaximumEpochs(mlpdto );
			return true;
	}
	
	//------------------------------------------------------------
	private void increaseMaximumEpochs(cmcMLPDTO dto )
	//------------------------------------------------------------
	{
		int mx = mlpdto.getEpochsPerformed() + cmcMachineLearningConstants.DEFAULT_NBR_OF_EPOCHS;
		mlpdto.setMaximumNumberOfEpochs(((mx / 200) + 1) * 200);
	}
	
	//------------------------------------------------------------
	private boolean putImageFileOnLabel(JLabel lbl , String ImageFileName )
	//------------------------------------------------------------
	{
		try {
		    BufferedImage bufImg=ImageIO.read(new File(ImageFileName));
		    lbl.setIcon(new ImageIcon(bufImg));
		    return true;
		}
		catch (Exception e) {
		    do_error("Unable to read image file [" + ImageFileName + "] " + e.getMessage());
		    return false;
		}
	}
	//------------------------------------------------------------
	private boolean forceImageRefresh()
	//------------------------------------------------------------
	{
		String  ImageFileName = null;
		//
		prodia.setShowCurve( ckCurve.isSelected() );
		prodia.setShowRunningAverage( ckRunningAverage.isSelected() );
		prodia.setShowSpeed( ckSpeed.isSelected() );
		// Loss and accuracy
		if( currentPane == 1 ) {
			 ImageFileName = xMSet.getEpochPictureFileName();
			 if( prodia.createProgressDiagram( lblProgress.getWidth(), lblProgress.getHeight() , btnT1Close.getBackground() ) == false ) {
				 do_error("Creating image [" + ImageFileName + "] -> " + prodia.getLastError() );
				 return false;
			 }
		    
			 if( xMSet.xU.IsBestand( ImageFileName ) == false ) return true;
			 if( putImageFileOnLabel( lblProgress , ImageFileName ) == false) return false;
		}
		// ROC
		else 
		if( currentPane == 2 ) {
			 ImageFileName = xMSet.getEpochROCImageFileName();
			 if( prodia.createROCDiagram( lblROC.getWidth(), lblROC.getHeight() , btnT1Close.getBackground() , ckNAClass.isSelected()) == false ) {
				 do_error("Creating ROC image [" + ImageFileName + "] -> " + prodia.getLastError() );
				 return false;
			 }
			 if( xMSet.xU.IsBestand( ImageFileName ) == false ) return true;
			 if( putImageFileOnLabel( lblROC , ImageFileName ) == false) return false;
		}
		return true;		
	}
	//------------------------------------------------------------
	private boolean doImageRefresh()
	//------------------------------------------------------------
	{
		if( mlpdto == null ) return false;
		if( isRunning == false ) return false;
		return forceImageRefresh();
	}
	
	//------------------------------------------------------------
	private void blankPicture()
	//------------------------------------------------------------
	{
		if( prodia == null )  return;
		if( mlpdto == null ) return;
		//
		String  ImageFileName = xMSet.getEpochPictureFileName();
		if( prodia.createBlankDiagram( lblProgress.getWidth(), lblProgress.getHeight() , btnT1Close.getBackground() , ImageFileName ) == false ) return;
		if( xMSet.xU.IsBestand( ImageFileName ) == false ) return;
		putImageFileOnLabel( lblProgress , ImageFileName );
		//
		ImageFileName = xMSet.getEpochROCImageFileName();
		if( prodia.createBlankDiagram( lblROC.getWidth(), lblROC.getHeight() , btnT1Close.getBackground() , ImageFileName) == false ) return;
		if( xMSet.xU.IsBestand( ImageFileName ) == false ) return;
		putImageFileOnLabel( lblROC , ImageFileName );
	}
	
	//------------------------------------------------------------
	private void viewAllLogs()
	//------------------------------------------------------------
	{
	   String[] pp = new String[3];
	   pp[0] = (xMSet.xU.IsBestand(xMSet.getEpochStatXMLFileName())) ? xMSet.getEpochStatXMLFileName() : null;
	   pp[1] = (xMSet.xU.IsBestand(xMSet.getEpochWeightDumpFileName())) ? xMSet.getEpochWeightDumpFileName() : null;
	   pp[2] = (xMSet.xU.IsBestand(mlpsupp.getXMLModelName(mlpdto.getLongARFFFileName()))) ? mlpsupp.getXMLModelName(mlpdto.getLongARFFFileName()) : null;
	   cmcFileViewer ff = new cmcFileViewer( null , xMSet , logger , pp);
	   ff=null;
	}
	
	//------------------------------------------------------------
	private void doTicker()
	//------------------------------------------------------------
	{
		if( isRunning == false ) return;
		boolean tik = ((System.currentTimeMillis() / 1000L) % 2L) == 0;
		if( lblTick1.isVisible() == tik ) return;
		lblTick1.setVisible(tik);
		if( lblTick1.isVisible() ) {
		   long interval = cmcMachineLearningConstants.REFRESH_INTERVAL_IN_SEC * 1000L;
		   Color col = Color.GREEN;
		   long lastupdateperiod = System.currentTimeMillis() - xMSet.xU.getModificationTime( xMSet.getEpochStatXMLFileName() );
		   if( lastupdateperiod > (4*interval) ) col = Color.RED;
		   else
		   if( lastupdateperiod > (2*interval) ) col = Color.ORANGE;
	       lblTick1.setBackground( col );
		}
	}
	
	//------------------------------------------------------------
	private boolean performSave()
	//------------------------------------------------------------
	{
	  try {
		String ShortName = xMSet.xU.getFileNameWithoutSuffix(xMSet.xU.getFolderOrFileName( mlpdto.getLongARFFFileName()));
	 	if( ShortName == null ) return false;
		ShortName += mlpdto.getNbrOfHiddenLayers() + "-" + mlpdto.getNbrOfNeuronsPerHiddenLayer() + "-" + mlpdto.getActivationFunction() + "-" + mlpdto.getLearningRate();
		ShortName = xMSet.xU.RemplaceerIgnoreCase( ShortName , "." , "-" ) + "-" + xMSet.xU.prntDateTime( System.currentTimeMillis() , "yyMMdd" );
		ShortName = xMSet.xU.Capitalize( ShortName );
		String filler = ""; for(int i=0;i<80;i++) filler += " ";
		//
		JTextField jName = new JTextField(10); jName.setText( ShortName );
		Object[] msg = {"Archive label:" + filler,  jName };
		int result = JOptionPane.showConfirmDialog(
		     null,
		     msg,
		     DEFAULT_TITLE,
		     JOptionPane.OK_CANCEL_OPTION,
		     JOptionPane.QUESTION_MESSAGE);
		if (result != JOptionPane.YES_OPTION) return false;
		ShortName = jName.getText();
		String LongName = mlpsupp.getArchiveFileName( ShortName );
		if( xMSet.xU.IsBestand( LongName ) ) {
			 result = JOptionPane.showConfirmDialog(
				     null,
				     "Archive [" + LongName + "] already exists. Continue?",
				     DEFAULT_TITLE,
				     JOptionPane.OK_CANCEL_OPTION,
				     JOptionPane.WARNING_MESSAGE );
				if (result != JOptionPane.YES_OPTION) return false;
		}
		//
		return mlpsupp.createArchive( mlpdto , ShortName );
	  }
	  catch(Exception e ) { do_error("Oops"); return false; }
	}
	
	//------------------------------------------------------------
	private void CleanUp()
	//------------------------------------------------------------
	{
	   if( mlpdto != null ) {
	     String LongARFFFileName = mlpdto.getLongARFFFileName();
	     if( LongARFFFileName != null ) {
	    	 String ShortFileName = xMSet.xU.getFolderOrFileName( LongARFFFileName );
	    	 if( ShortFileName != null ) {
	    		 String CurrentModel = xMSet.xU.getFileNameWithoutSuffix( ShortFileName );
	    		 mlpsupp.removeClutter( CurrentModel , xMSet.getSandBoxDir() );
	    	 }
	     }
	   }
	   // remainder
	}
	
	/*
	 * private boolean doSMO()
	{
		//
		
	   JTextField jsoft = new JTextField(10); jsoft.setText(""+mldto.SMO_SoftMargin);
	   JTextField jtolerance = new JTextField(10); jtolerance.setText(""+mldto.SMO_Tolerance);
	   JTextField jcycles = new JTextField(10); jcycles.setText(""+mldto.SMO_Cycles);
	   JComboBox jkernel = new JComboBox();
	   jkernel.addItem(""+mldto.SMO_KernelTipe); jkernel.setSelectedIndex(0);
	   if( (""+mldto.SMO_KernelTipe).compareToIgnoreCase("NONE")!=0)  jkernel.addItem("NONE");
	   if( (""+mldto.SMO_KernelTipe).compareToIgnoreCase("RBF")!=0)  jkernel.addItem("RBF");
	   if( (""+mldto.SMO_KernelTipe).compareToIgnoreCase("GAUSSIAN")!=0)  jkernel.addItem("GAUSSIAN");
	   JTextField jsigma = new JTextField(10); jsigma.setText(""+mldto.SMO_GaussianSigma);
	   JTextField jgamma = new JTextField(10); jgamma.setText(""+mldto.SMO_RBFGamma);
	   Object[] msg = {"Soft margin:", jsoft , "Number of cycles:",  jcycles , "Tolerance:" , jtolerance , "Kernel:" , jkernel , "Sigma:" , jsigma , "Gamma:" , jgamma };
	   int result = JOptionPane.showConfirmDialog(
	            null,
	            msg,
	            "Sequential Minimum Optimization",
	            JOptionPane.OK_CANCEL_OPTION,
	            JOptionPane.PLAIN_MESSAGE);
	    if (result == JOptionPane.YES_OPTION) {
		     double dd = xMSet.xU.NaarDoubleNAN(jsoft.getText());
		     if( !Double.isNaN(dd) ) mldto.SMO_SoftMargin = dd;
		     dd = xMSet.xU.NaarDoubleNAN(jtolerance.getText());
		     if( !Double.isNaN(dd) ) mldto.SMO_Tolerance = dd;
		     int pp = xMSet.xU.NaarInt(jcycles.getText());
		     if( pp > 0 ) mldto.SMO_Cycles = pp;
		     dd = xMSet.xU.NaarDoubleNAN(jsigma.getText());
		     if( !Double.isNaN(dd) ) mldto.SMO_GaussianSigma = dd;
		     dd = xMSet.xU.NaarDoubleNAN(jgamma.getText());
		     if( !Double.isNaN(dd) ) mldto.SMO_RBFGamma = dd;
		     cmcMachineLearningEnums enu = new cmcMachineLearningEnums();
		     mldto.SMO_KernelTipe = enu.getKernelType((String)jkernel.getSelectedItem());
	    }
	    else return false;
		//
	    cmcSMODTOCore params = new cmcSMODTO( mldto.LongDataFileName , mldto.SMO_KernelTipe );
  		params.setC(mldto.SMO_SoftMargin);
  		params.setMaxCycles(mldto.SMO_Cycles);
  		params.setTolerance(mldto.SMO_Tolerance);
  		params.setGamma(mldto.SMO_RBFGamma);
  		params.setSigma(mldto.SMO_GaussianSigma);
  		//
		cmcSMO smo = new cmcSMO(xMSet,logger);
		boolean ib = smo.testSMO( mldto.LongDataFileName , mldto.LongModelName , params );
  		if( ib == false ) {
  			popMessage( smo.getLastErrorMsg() );
  			smo=null;
  			return false;
  		}
  		
  		
  		smo=null;
  		return true;
	}
	 */
}