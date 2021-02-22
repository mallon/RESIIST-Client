package com.axellience.resiist.client.utils.ihm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.axellience.resiist.client.integration.IntegrationPlatform;

public class AccessApplication
{

    private JFrame                   frmResiistProject;
    private JTextField               loginTextField;
    private JLabel                   passwordLabel;
    private JTextField               passwordField;
    private boolean                  loginDone;
    private String                   projectId;
    private IntegrationPlatform      integrationPlatform;
    private static AccessApplication accessApplication;
    private JLabel                   projIdLabel;
    private JTextField               projIdTextField;

    public static AccessApplication getInstance(IntegrationPlatform integrationPlatform)
    {
        if (accessApplication == null) {
            accessApplication = new AccessApplication(integrationPlatform);
        }
        return accessApplication;
    }

    private AccessApplication(IntegrationPlatform integrationPlatform)
    {
        this.integrationPlatform = integrationPlatform;
        initialize();
    }

    private void initialize()
    {
        frmResiistProject = new JFrame();
        frmResiistProject.setTitle("RESIIST Project");
        frmResiistProject.setBounds(100, 100, 800, 193);
        frmResiistProject.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmResiistProject.setResizable(false);

        JLabel loginLabel = new JLabel("Login");

        loginTextField = new JPasswordField();
        loginTextField.setColumns(10);

        passwordLabel = new JLabel("Password");

        passwordField = new JPasswordField();
        passwordField.setColumns(10);

        projIdLabel = new JLabel("Project ID");

        projIdTextField = new JTextField();
        projIdTextField.setColumns(10);

        JButton loginButton = new JButton("Log in");
        loginButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent arg0)
            {
                String password = passwordField.getText();
                String login = loginTextField.getText();
                projectId = projIdTextField.getText();
                if (!login.isEmpty() || !password.isEmpty()) {
                    loginDone = integrationPlatform.connexion(login, password);
                    if (loginDone && !projectId.isEmpty()) {
                        JOptionPane.showMessageDialog(frmResiistProject,
                                                      "Login successful - Welcome");
                        frmResiistProject.dispose();
                    } else if (!loginDone) {
                        JOptionPane.showMessageDialog(frmResiistProject,
                                                      "Login not successful... Or the server is currently down.");
                    } else if (projectId.isEmpty()) {
                        JOptionPane.showMessageDialog(frmResiistProject,
                                                      "Enter the project identifier.");
                    }
                }
            }
        });

        GroupLayout groupLayout = new GroupLayout(frmResiistProject.getContentPane());
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                                  .addGroup(groupLayout.createSequentialGroup()
                                                                       .addGap(275)
                                                                       .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                                                                            .addComponent(loginLabel)
                                                                                            .addComponent(passwordLabel)
                                                                                            .addComponent(projIdLabel))
                                                                       .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                       .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                                                            .addComponent(loginTextField,
                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                          GroupLayout.DEFAULT_SIZE,
                                                                                                          GroupLayout.PREFERRED_SIZE)
                                                                                            .addComponent(passwordField,
                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                          GroupLayout.DEFAULT_SIZE,
                                                                                                          GroupLayout.PREFERRED_SIZE)
                                                                                            .addComponent(projIdTextField,
                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                          GroupLayout.DEFAULT_SIZE,
                                                                                                          GroupLayout.PREFERRED_SIZE))
                                                                       .addContainerGap(313,
                                                                                        Short.MAX_VALUE))
                                                  .addGroup(groupLayout.createSequentialGroup()
                                                                       .addContainerGap(690,
                                                                                        Short.MAX_VALUE)
                                                                       .addComponent(loginButton)
                                                                       .addGap(23)));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                .addGroup(groupLayout.createSequentialGroup()
                                                                     .addGap(27)
                                                                     .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                                                          .addComponent(loginLabel)
                                                                                          .addComponent(loginTextField,
                                                                                                        GroupLayout.PREFERRED_SIZE,
                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                        GroupLayout.PREFERRED_SIZE))
                                                                     .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                     .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                                                          .addComponent(passwordField,
                                                                                                        GroupLayout.PREFERRED_SIZE,
                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                        GroupLayout.PREFERRED_SIZE)
                                                                                          .addComponent(passwordLabel))
                                                                     .addGap(18)
                                                                     .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                                                          .addComponent(projIdLabel)
                                                                                          .addComponent(projIdTextField,
                                                                                                        GroupLayout.PREFERRED_SIZE,
                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                        GroupLayout.PREFERRED_SIZE))
                                                                     .addPreferredGap(ComponentPlacement.RELATED)
                                                                     .addComponent(loginButton)
                                                                     .addGap(18)));
        frmResiistProject.getContentPane().setLayout(groupLayout);
        frmResiistProject.setVisible(true);
    }

    public boolean isLoginDone()
    {
        return loginDone;
    }

    public String getProjectId()
    {
        return projectId;
    }
}
