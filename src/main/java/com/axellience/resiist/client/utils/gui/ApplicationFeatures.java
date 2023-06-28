package com.axellience.resiist.client.utils.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.opengroup.archimate.CourseOfAction;
import org.opengroup.archimate.Grouping;
import org.opengroup.archimate.Value;

import com.axellience.resiist.client.acquisitiontointegration.AcquisitionToIntegration;
import com.axellience.resiist.client.acquisitiontointegration.History;
import com.axellience.resiist.client.acquisitiontointegration.integration.IntegrationPlatform;
import com.axellience.resiist.client.decisionmaking.DecisionMaking;
import com.axellience.resiist.client.utils.ModelUtils;
import com.axellience.resiist.client.utils.ResiistConstants;
import com.genmymodel.archimatediag.TextWidget;
import com.genmymodel.ecoreonline.graphic.GMMUtil;

public class ApplicationFeatures
{
    private static ApplicationFeatures       applicationFeatures;
    private DataSourceToModelSynchronization dataSourceToModelSynchronization;

    private JFrame                   frmResiistProject;
    private AcquisitionToIntegration acquisitionToIntegration;
    private DecisionMaking           decisionMaking;
    private ResourceImpl             resourceImpl;

    public static ApplicationFeatures getInstance(AcquisitionToIntegration acquisitionToIntegration,
                                                  DecisionMaking decisionMaking)
    {
        if (applicationFeatures == null) {
            applicationFeatures = new ApplicationFeatures(acquisitionToIntegration, decisionMaking);
        }
        return applicationFeatures;
    }

    private ApplicationFeatures(AcquisitionToIntegration acquisitionToIntegration,
                                DecisionMaking decisionMaking)
    {
        this.acquisitionToIntegration = acquisitionToIntegration;
        this.decisionMaking = decisionMaking;
        resourceImpl = acquisitionToIntegration.getModelFromServerAsBinary();
        initialize();
    }

    private void initialize()
    {
        frmResiistProject = new JFrame();
        frmResiistProject.setTitle("RESIIST Project - Features");
        frmResiistProject.setBounds(100, 100, 295, 195);
        frmResiistProject.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmResiistProject.setResizable(false);

        if (!decisionMaking.isServerRunning()) {
            decisionMaking.setModelResource(resourceImpl);
            decisionMaking.launchDecisionEvaluationServer();
        }

        JButton synchro = new JButton("Data sources to Model Synchronisation");
        synchro.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent arg0)
            {
                Thread dsms = new Thread(() -> launchSynchronization());
                dsms.start();
            }

            private void launchSynchronization()
            {
                dataSourceToModelSynchronization =
                        DataSourceToModelSynchronization.getInstance(acquisitionToIntegration,
                                                                     applicationFeatures);
                frmResiistProject.setVisible(false);
            }
        });

        JButton decisionEvaluationWeb = new JButton("Decision Evaluation - Web tool");
        decisionEvaluationWeb.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent arg0)
            {
                Thread dewt = new Thread(() -> launchWebToolDecisionEvaluation());
                dewt.start();
            }

            private void launchWebToolDecisionEvaluation()
            {
                decisionMaking.setModelResource(resourceImpl);
                decisionMaking.launchDecisionEvaluationServer();
                File csvDecisionFile = decisionMaking.transformDecisionModelToCSV();
                decisionMaking.setCsvFile(csvDecisionFile);
                decisionMaking.openImportUrl();
            }
        });

        JButton decisionEvaluationInternal = new JButton("Decision Evaluation");
        decisionEvaluationInternal.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent arg0)
            {
                Thread de = new Thread(() -> launchDecisionEvaluation());
                de.start();
            }

        });

        JButton continuousDecision = new JButton("Continuous Decision Evaluation");
        continuousDecision.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent arg0)
            {
                Thread dewt = new Thread(this::launchContinuousDecisionEvaluation);
                dewt.start();
            }

            private void launchContinuousDecisionEvaluation()
            {
                if (dataSourceToModelSynchronization != null) {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask()
                    {
                        boolean historyNotLaunched = false;

                        @Override
                        public void run()
                        {
                            launchDecisionEvaluation();

                            if (!historyNotLaunched) {
                                launchDecisionEvaluationHistory();
                                historyNotLaunched = true;
                            }
                        }
                    }, 0, 5000);
                } else
                    needRunningSynchroMessage();
            }

            private void needRunningSynchroMessage()
            {
                JOptionPane.showMessageDialog(frmResiistProject,
                                              "First, launch a synchronization between data source(s) and the model");
            }

            private void launchDecisionEvaluationHistory()
            {
                if (dataSourceToModelSynchronization != null) {
                    History history = dataSourceToModelSynchronization.getHistory();

                    String decisionReportId = getDecisionReportID();
                    String decisionList = decisionMaking.getDecisionList();
                    if (history != null && decisionReportId != null && decisionList != null) {
                        history.launchGlobalDecisionEvaluationHistory(decisionReportId,
                                                                      decisionList);
                    }
                } else
                    needRunningSynchroMessage();
            }

            private String getDecisionReportID()
            {
                return ModelUtils.getAllElements(resourceImpl, Grouping.class)
                                 .stream()
                                 .filter(elem -> isADMReport(elem))
                                 .findFirst()
                                 .map(elem -> GMMUtil.getUUID(elem))
                                 .orElse(null);
            }

            private boolean isADMReport(Grouping grouping)
            {
                EMap<String, String> details = ModelUtils.getElementDetails(grouping);
                return details.containsKey(ResiistConstants.REPORT);
            }
        });

        GroupLayout groupLayout = new GroupLayout(frmResiistProject.getContentPane());
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.CENTER)
                                                  .addGroup(groupLayout.createSequentialGroup()
                                                                       .addGap(10)
                                                                       .addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
                                                                                            .addComponent(synchro)
                                                                                            .addComponent(decisionEvaluationWeb)
                                                                                            .addComponent(decisionEvaluationInternal)
                                                                                            .addComponent(continuousDecision))
                                                                       .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                       .addContainerGap(313,
                                                                                        Short.MAX_VALUE)));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.CENTER)
                                                .addGroup(groupLayout.createSequentialGroup()
                                                                     .addGap(10)
                                                                     .addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
                                                                                          .addComponent(synchro))
                                                                     .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                     .addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
                                                                                          .addComponent(decisionEvaluationWeb))
                                                                     .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                     .addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
                                                                                          .addComponent(decisionEvaluationInternal))
                                                                     .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                     .addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
                                                                                          .addComponent(continuousDecision))
                                                                     .addPreferredGap(ComponentPlacement.RELATED)));
        frmResiistProject.getContentPane().setLayout(groupLayout);

        frmResiistProject.setVisible(true);
    }

    private void launchDecisionEvaluation()
    {
        File csvDecisionFile = decisionMaking.transformDecisionModelToCSV();
        decisionMaking.setCsvFile(csvDecisionFile);

        displayReportDateAndTime();

        List<String[]> orderedDecisions = decisionMaking.sortDecisions();
        displayTotalFlows(orderedDecisions);

        String bestAlternativeName = orderedDecisions.get(orderedDecisions.size() - 1)[0];
        String secondBestAlternativeName = orderedDecisions.get(orderedDecisions.size() - 2)[0];

        displayGreenColorOnBestAlternative(bestAlternativeName, secondBestAlternativeName);

        displayReportedInfluencedIndicatorValues();
    }

    private void displayReportDateAndTime()
    {
        IntegrationPlatform ip = IntegrationPlatform.getInstance();
        LocalTime time = LocalTime.now();
        LocalDate date = LocalDate.now();
        for (TextWidget textWidget : ip.getDateReportTextWidget(resourceImpl)) {
            String flowValue = "Evaluation done at (date and hour) : " + date + ", at:" + time;
            ip.setValueForTextWidget(textWidget, textWidget.getValue(), flowValue);
        }
    }

    private void displayGreenColorOnBestAlternative(String bestAlternativeName,
                                                    String secondbestAlternativeName)
    {
        IntegrationPlatform ip = IntegrationPlatform.getInstance();

        CourseOfAction bestAlternativeInreport =
                decisionMaking.getBestAlternativeInReport(bestAlternativeName);
        String elementId = GMMUtil.getUUID(bestAlternativeInreport);
        ip.setColorForIndicator(elementId, "#29a329");

        CourseOfAction secondBestAlternativeInreport =
                decisionMaking.getBestAlternativeInReport(secondbestAlternativeName);
        String secondEelementId = GMMUtil.getUUID(secondBestAlternativeInreport);
        ip.setColorForIndicator(secondEelementId, "#7ec77e");

        decisionMaking.getReportAlternatives()
                      .stream()
                      .filter(ra -> ra != bestAlternativeInreport
                                    && ra != secondBestAlternativeInreport)
                      .forEach(this::reinitColor);
    }

    private void reinitColor(CourseOfAction courseOfAction)
    {
        IntegrationPlatform ip = IntegrationPlatform.getInstance();
        String elementId = GMMUtil.getUUID(courseOfAction);
        ip.setColorForIndicator(elementId, "#f5deaa");
    }

    private void displayReportedInfluencedIndicatorValues()
    {
        IntegrationPlatform ip = IntegrationPlatform.getInstance();
        Map<String, Map<String, String>> alternAnfPerf = decisionMaking.getAlternativeAndValues();
        for (Value perfReport : decisionMaking.getReportPerformances()) {
            String perfReportName = ModelUtils.getArchimateName(perfReport);
            EMap<String, String> perfReportDetails = ModelUtils.getElementDetails(perfReport);
            String alterName = perfReportDetails.get(ResiistConstants.ALTERNATIVE);
            String criterionDec = perfReportDetails.get(ResiistConstants.DECISION_CRITERION);
            Map<String, String> criterionAndPerf = alternAnfPerf.get(alterName);
            String perfReportValue = criterionAndPerf.get(criterionDec);
            String perfReportOldValue = ModelUtils.getArchimateName(perfReport);
            ip.setValueForPerf(perfReport, perfReportOldValue, Double.valueOf(perfReportValue));
        }
    }

    private void displayTotalFlows(List<String[]> orderedDecisions)
    {
        IntegrationPlatform ip = IntegrationPlatform.getInstance();
        List<TextWidget> alterFlowTextWidgets = ip.getAternativeTotalFlowTextWidget(resourceImpl);
        for (TextWidget textWidget : alterFlowTextWidgets) {
            EMap<String, String> valueDetails = ModelUtils.getElementDetails(textWidget);
            String alterName = valueDetails.get(ResiistConstants.ALTERNATIVE);
            String flowValue = decisionMaking.getFlowValue(alterName, orderedDecisions);
            String presentedFlowValue = "Total flow: " + flowValue;
            ip.setValueForTextWidget(textWidget, textWidget.getValue(), presentedFlowValue);

            EMap<String, String> textWidgetDetails = ModelUtils.getElementDetails(textWidget);
            String decisionReportId = textWidgetDetails.get(ResiistConstants.DECISION_REPORT);

            setAsReportCustomProperties(decisionReportId, alterName, flowValue);
        }
    }

    private void setAsReportCustomProperties(String decisionReportId, String alterName,
                                             String flowValue)
    {
        IntegrationPlatform ip = IntegrationPlatform.getInstance();
        Grouping parentGrouping = (Grouping) resourceImpl.getEObject(decisionReportId);
        EMap<String, String> groupingDetails = ModelUtils.getElementDetails(parentGrouping);
        String oldValue = groupingDetails.get(alterName);
        String detailIndex = String.valueOf(groupingDetails.indexOfKey(alterName));

        ip.setValueForDecisionInReport(parentGrouping, detailIndex, oldValue, flowValue);
    }

    public JFrame getFrmResiistProject()
    {
        return frmResiistProject;
    }
}
