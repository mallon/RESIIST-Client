package com.axellience.resiist.client.utils.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.axellience.resiist.client.acquisitiontointegration.AcquisitionToIntegration;
import com.axellience.resiist.client.acquisitiontointegration.History;
import com.axellience.resiist.client.acquisitiontointegration.acquisition.VideoSource;

public class DataSourceToModelSynchronization
{
    private static DataSourceToModelSynchronization dataSourceToModelSynchronization;
    private JFrame                                  frmResiistProject;
    private AcquisitionToIntegration                acquisitionToIntegration;
    private ApplicationFeatures                     applicationFeatures;
    private History                                 history;

    public static DataSourceToModelSynchronization getInstance(AcquisitionToIntegration acquisitionToIntegration,
                                                               ApplicationFeatures applicationFeatures)
    {
        if (dataSourceToModelSynchronization == null) {
            dataSourceToModelSynchronization =
                    new DataSourceToModelSynchronization(acquisitionToIntegration,
                                                         applicationFeatures);
        }
        return dataSourceToModelSynchronization;
    }

    private DataSourceToModelSynchronization(AcquisitionToIntegration acquisitionToIntegration,
                                             ApplicationFeatures applicationFeatures)
    {
        this.acquisitionToIntegration = acquisitionToIntegration;
        this.applicationFeatures = applicationFeatures;
        acquisitionToIntegration.integrationConfiguration();
        acquisitionToIntegration.interpretationConfiguration();
        history = new History(acquisitionToIntegration.getProjectId(),
                              acquisitionToIntegration.getIndicatorIds());
        initialize();
    }

    private void initialize()
    {
        frmResiistProject = new JFrame();
        frmResiistProject.setTitle("RESIIST Project - Data Source To Model Synchronization");
        frmResiistProject.setBounds(100, 100, 322, 210);
        frmResiistProject.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmResiistProject.setResizable(false);

        JButton synchroCSV = new JButton("Synchronization using CSV as data sources");
        synchroCSV.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent arg0)
            {
                acquisitionToIntegration.connexionToDataSourcesAsRealTypeSimulation();
                acquisitionToIntegration.start();
                new VideoSource().start();
            }
        });

        JButton gamaCSV = new JButton("<html><center>Synchronization using GAMA simulation<br>"
                                      + "engine outputs as data sources</center></html>");
        gamaCSV.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent arg0)
            {
                acquisitionToIntegration.connexionToDataSourcesAsGamaOutputs();
                acquisitionToIntegration.start();
                new VideoSource().start();
            }
        });

        JButton synchroVideo = new JButton("Synchronization using video as data sources");
        synchroVideo.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent arg0)
            {
                acquisitionToIntegration.connexionToDataSourceAsVideo();
                acquisitionToIntegration.start();
                new VideoSource().start();
            }
        });

        JButton back = new JButton("Back to application features");
        back.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent arg0)
            {
                applicationFeatures.getFrmResiistProject().setVisible(true);
                frmResiistProject.dispose();
            }
        });

        GroupLayout groupLayout = new GroupLayout(frmResiistProject.getContentPane());
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                                  .addGroup(groupLayout.createSequentialGroup()
                                                                       .addGap(10)
                                                                       .addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
                                                                                            .addComponent(synchroCSV)
                                                                                            .addComponent(gamaCSV)
                                                                                            .addComponent(synchroVideo)
                                                                                            .addComponent(back))
                                                                       .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                       .addContainerGap(313,
                                                                                        Short.MAX_VALUE)));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                .addGroup(groupLayout.createSequentialGroup()
                                                                     .addGap(10)
                                                                     .addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
                                                                                          .addComponent(synchroCSV))
                                                                     .addPreferredGap(ComponentPlacement.UNRELATED)

                                                                     .addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
                                                                                          .addComponent(gamaCSV))
                                                                     .addPreferredGap(ComponentPlacement.UNRELATED)

                                                                     .addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
                                                                                          .addComponent(synchroVideo))
                                                                     .addPreferredGap(ComponentPlacement.UNRELATED)

                                                                     .addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
                                                                                          .addComponent(back))
                                                                     .addPreferredGap(ComponentPlacement.RELATED)));
        frmResiistProject.getContentPane().setLayout(groupLayout);

        frmResiistProject.setVisible(true);
    }

    public History getHistory()
    {
        return history;
    }

    public boolean hasSendIndicatorValue()
    {
        return acquisitionToIntegration.hasSendIndicatorValue();
    }
}
