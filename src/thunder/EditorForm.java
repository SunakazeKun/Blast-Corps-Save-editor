/*
    Copyright (C) 2016 Aurum
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package thunder;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EditorForm extends javax.swing.JFrame {

    public EditorForm() {
        initComponents();
        populateLevelList();
        txtName.setTransferHandler(null);
    }

    private void getSaveData() {
        updatePoints();
        updateCommPoints();
        
        txtName.setText(save.getName());
        spnMoney.setValue(ByteUtils.bytesToInt(save.eep.money));
        cmoProgress.setSelectedIndex(save.eep.event);
        cmoSelectedLevel.setSelectedIndex(save.eep.selectedLevel);
        cmoControl.setSelectedIndex(save.getControlMode());
        
        lblBronze.setText(String.valueOf(save.bronzeMedals));
        lblSilver.setText(String.valueOf(save.silverMedals));
        lblGold.setText(String.valueOf(save.goldMedals));
        lblPlatinum.setText(String.valueOf(save.platinumMedals));
        lblCarrier.setText(String.valueOf(save.carrierMedals));
        
        save.getVehicles();
        chkVAmericanDream.setSelected(save.hasAmericanDream);
        chkVPoliceCar.setSelected(save.hasPoliceCar);
        chkVATeam.setSelected(save.hasATeamVan);
        chkVHotrod.setSelected(save.hasHotrod);
        
        save.getScientists();
        chkSArgentTowers.setSelected(save.hasArgentTowers);
        chkSIronstoneMine.setSelected(save.hasIronstoneMine);
        chkSTempestCity.setSelected(save.hasTempestCity);
        chkSOysterHarbor.setSelected(save.hasOysterHarbor);
        chkSEbonyCoast.setSelected(save.hasEbonyCoast);
        chkSGloryCrossing.setSelected(save.hasGloryCrossing);
        
        cmoLevel.setSelectedIndex(0);
        cmoLevelMedal.setSelectedIndex(save.eep.levelMedals[cmoLevel.getSelectedIndex()]);
        cmoLevelDishes.setSelectedIndex(save.eep.levelPaths[cmoLevel.getSelectedIndex()]);
        cmoLevelVehicle.setSelectedIndex(save.eep.levelVehicles[cmoLevel.getSelectedIndex()]);
        int[] time = save.getTime(cmoLevel.getSelectedIndex());
        spnLevelTimeMin.setValue(time[0]);
        spnLevelTimeSec.setValue(time[1]);
        spnLevelTimeMil.setValue(time[2]);
        
        cmoLanguage.removeAllItems();
        String lang = save.getLang();
        if (lang.equals("Default")) {
            cmoLanguage.addItem(lang);
            cmoLanguage.setSelectedItem(lang);
        }
        else {
            cmoLanguage.addItem("English");
            cmoLanguage.addItem("German");
            cmoLanguage.setSelectedItem(lang);
        }
    }
    
    private void setSaveData() {
        save.setName(txtName.getText());
        save.setLang((String)cmoLanguage.getSelectedItem());
        save.setControlMode(cmoControl.getSelectedIndex());
        
        boolean[] scientists = { chkSArgentTowers.isSelected(), chkSIronstoneMine.isSelected(), chkSTempestCity.isSelected(), chkSOysterHarbor.isSelected(), chkSEbonyCoast.isSelected(), chkSGloryCrossing.isSelected() };
        save.setScientists(scientists);
        
        boolean[] vehicles = { chkVAmericanDream.isSelected(), chkVPoliceCar.isSelected(), chkVATeam.isSelected(), chkVHotrod.isSelected() };
        save.setVehicles(vehicles);
        
        save.eep.points = ByteUtils.shortToBytes(save.points);
        save.eep.money = ByteUtils.intToBytes((int)spnMoney.getValue());
        save.eep.selectedLevel = (byte)cmoSelectedLevel.getSelectedIndex();
        
        txtName.setText(save.getName());
    }
    
    private void componentsEnabled(boolean b) {
        txtName.setEnabled(b);
        btnNameChars.setEnabled(b);
        spnMoney.setEnabled(b);
        cmoProgress.setEnabled(b);
        cmoSelectedLevel.setEnabled(b);
        cmoControl.setEnabled(b);
        cmoLanguage.setEnabled(b);
        
        cmoLevel.setEnabled(b);
        cmoLevelMedal.setEnabled(b);
        cmoLevelVehicle.setEnabled(b);
        cmoLevelDishes.setEnabled(b);
        spnLevelTimeMin.setEnabled(b);
        spnLevelTimeSec.setEnabled(b);
        spnLevelTimeMil.setEnabled(b);
        btnLevelTimeSet.setEnabled(b);
        
        chkSArgentTowers.setEnabled(b);
        chkSTempestCity.setEnabled(b);
        chkSIronstoneMine.setEnabled(b);
        chkSEbonyCoast.setEnabled(b);
        chkSGloryCrossing.setEnabled(b);
        chkSOysterHarbor.setEnabled(b);
        
        chkVAmericanDream.setEnabled(b);
        chkVPoliceCar.setEnabled(b);
        chkVATeam.setEnabled(b);
        chkVHotrod.setEnabled(b);
        
        mnuUnlockLevels.setEnabled(b);
        mnuUnlockTutorials.setEnabled(b);
        mnuUnlockInfos.setEnabled(b);
        
    }
    
    private void populateLevelList() {
        try {
            InputStream i = Thunder.class.getResourceAsStream("/res/levels.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(i));
            String s;
            String[] d;
            
            while ((s = r.readLine()) != null) {
                s = s.trim();
                d = s.split(",");
                if (s.startsWith("#"))
                    continue;
                
                cmoLevel.addItem(d[1]);
                cmoSelectedLevel.addItem(d[1]);
            }
        }
        catch (Exception ex) {
        }
    }
    
    private String getResourceName(String dir, int index) {
        String res = "/res/" + dir + "/" + index + ".png";
        java.net.URL u = Thunder.class.getResource(res);
        if (u != null)
            return res;
        else
            return "/res/noimage.png";
    }
    
    private void updateLabelIcons() {
        lblLevelVehicle.setIcon(new javax.swing.ImageIcon(getClass().getResource(getResourceName("vehicles",cmoLevelVehicle.getSelectedIndex()))));
        lblLevelMedal.setIcon(new javax.swing.ImageIcon(getClass().getResource(getResourceName("medals",cmoLevelMedal.getSelectedIndex()))));
    }
    
    private void updatePoints() {
        save.makePoints();
        save.makeRank();
        
        txtPoints.setText(String.valueOf(save.points));
        cmoRank.setSelectedIndex(save.eep.rank);
        
        lblBronze.setText(String.valueOf(save.bronzeMedals));
        lblSilver.setText(String.valueOf(save.silverMedals));
        lblGold.setText(String.valueOf(save.goldMedals));
        lblPlatinum.setText(String.valueOf(save.platinumMedals));
        lblCarrier.setText(String.valueOf(save.carrierMedals));
    }
    
    private void updateCommPoints() {
        int counter = 0;
        for (byte data : save.eep.levelPaths) {
            switch (data) {
                case 1:
                case 2:
                    counter += 1;
                    break;
                case 3:
                    counter += 2;
                    break;
            }
        }
        lblCommPoints.setText(String.valueOf(counter));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pneGeneral = new javax.swing.JPanel();
        lblGeneralName = new javax.swing.JLabel();
        lblGeneralMoney = new javax.swing.JLabel();
        lblGeneralPoints = new javax.swing.JLabel();
        lblGeneralRank = new javax.swing.JLabel();
        lblGeneralProgress = new javax.swing.JLabel();
        lblGeneralLevel = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        spnMoney = new javax.swing.JSpinner();
        txtPoints = new javax.swing.JTextField();
        cmoRank = new javax.swing.JComboBox<>();
        cmoProgress = new javax.swing.JComboBox<>();
        cmoSelectedLevel = new javax.swing.JComboBox<>();
        lblBronze = new javax.swing.JLabel();
        lblSilver = new javax.swing.JLabel();
        lblGold = new javax.swing.JLabel();
        lblPlatinum = new javax.swing.JLabel();
        lblCommPoints = new javax.swing.JLabel();
        lblCarrier = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnNameChars = new javax.swing.JButton();
        lblControl = new javax.swing.JLabel();
        cmoControl = new javax.swing.JComboBox<>();
        cmoLanguage = new javax.swing.JComboBox<>();
        lblLanguage = new javax.swing.JLabel();
        chkVAmericanDream = new javax.swing.JCheckBox();
        chkVPoliceCar = new javax.swing.JCheckBox();
        chkVHotrod = new javax.swing.JCheckBox();
        chkVATeam = new javax.swing.JCheckBox();
        pneLevel = new javax.swing.JPanel();
        lblLevel = new javax.swing.JLabel();
        cmoLevel = new javax.swing.JComboBox<>();
        lblLevelVehicle = new javax.swing.JLabel();
        lblLevelMedal = new javax.swing.JLabel();
        lblLevelDishes = new javax.swing.JLabel();
        lblLevelTime = new javax.swing.JLabel();
        cmoLevelVehicle = new javax.swing.JComboBox<>();
        cmoLevelMedal = new javax.swing.JComboBox<>();
        cmoLevelDishes = new javax.swing.JComboBox<>();
        spnLevelTimeMin = new javax.swing.JSpinner();
        spnLevelTimeSec = new javax.swing.JSpinner();
        spnLevelTimeMil = new javax.swing.JSpinner();
        lblLevelTimeSep1 = new javax.swing.JLabel();
        lblLevelTimeSep2 = new javax.swing.JLabel();
        btnLevelTimeSet = new javax.swing.JButton();
        pneScientists = new javax.swing.JPanel();
        chkSArgentTowers = new javax.swing.JCheckBox();
        chkSTempestCity = new javax.swing.JCheckBox();
        chkSIronstoneMine = new javax.swing.JCheckBox();
        chkSEbonyCoast = new javax.swing.JCheckBox();
        chkSGloryCrossing = new javax.swing.JCheckBox();
        chkSOysterHarbor = new javax.swing.JCheckBox();
        tbrFooter = new javax.swing.JToolBar();
        fil0 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        lblDir = new javax.swing.JLabel();
        mbarGeneral = new javax.swing.JMenuBar();
        subFile = new javax.swing.JMenu();
        mnuOpen = new javax.swing.JMenuItem();
        mnuSave = new javax.swing.JMenuItem();
        mnuSaveAs = new javax.swing.JMenuItem();
        mnuDispose = new javax.swing.JMenuItem();
        subOptions = new javax.swing.JMenu();
        mnuUnlockLevels = new javax.swing.JMenuItem();
        mnuUnlockTutorials = new javax.swing.JMenuItem();
        mnuUnlockInfos = new javax.swing.JMenuItem();
        subAbout = new javax.swing.JMenu();
        mnuAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        pneGeneral.setBorder(javax.swing.BorderFactory.createTitledBorder("Main"));

        lblGeneralName.setText("Name");

        lblGeneralMoney.setText("Money");

        lblGeneralPoints.setText("Points");

        lblGeneralRank.setText("Rank");

        lblGeneralProgress.setText("Progress");
        lblGeneralProgress.setToolTipText("");

        lblGeneralLevel.setText("Selected level");

        txtName.setText("NEW GAME");
        txtName.setEnabled(false);
        txtName.setMaximumSize(new java.awt.Dimension(6, 8));
        txtName.setMinimumSize(new java.awt.Dimension(6, 8));
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNameKeyTyped(evt);
            }
        });

        spnMoney.setModel(new javax.swing.SpinnerNumberModel(0, -2147483647, 2147483647, 1));
        spnMoney.setEnabled(false);

        txtPoints.setEditable(false);
        txtPoints.setText("0");

        cmoRank.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Rookie Wrecker", "Trained Crusher", "Experienced Ravager", "Decorated Damager", "Professional Razer", "Expert Destroyer", "Gifted Ruiner", "Accomplished Conqueror", "Master Despoiler", "Deomolition Fanatic", "Grand Eradicator", "Heavy Duty Waster", "Total Pulveriser", "Champion Ransacker", "Mechanical Maestro", "Chief Obliterator", "Commanding Desolator", "Supreme Devastator", "Ultimate Annihilator", "Leveling Legend", "Destructive Psychopath", "Mindless Desecrator", "Hysterical  Claustrophobe", "Uncontrollable Madman", "World Class Megalomaniac", "Captain Of Carnage", "Single Minded Chaosmonger", "Grand High Slaughtermaster", "Lunatic Lord Of Havoc", "Armageddon Adept", "You Can Stop Now." }));
        cmoRank.setEnabled(false);

        cmoProgress.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "New game", "Default", "Easy levels finished", "Medium levels finished", "Hard levels finished", "All scientists found", "Shuttle-Clear unlocked", "Moon unlocked", "Moon finished", "Mercuary unlocked", "?", "Unlock time attack mode", "Unlock platinum medals", "All platinum medals collected" }));
        cmoProgress.setEnabled(false);
        cmoProgress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoProgressActionPerformed(evt);
            }
        });

        cmoSelectedLevel.setEnabled(false);

        lblBronze.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/medals/1.png"))); // NOI18N
        lblBronze.setText("0");

        lblSilver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/medals/2.png"))); // NOI18N
        lblSilver.setText("0");

        lblGold.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/medals/3.png"))); // NOI18N
        lblGold.setText("0");

        lblPlatinum.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/medals/4.png"))); // NOI18N
        lblPlatinum.setText("0");

        lblCommPoints.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/satellite.png"))); // NOI18N
        lblCommPoints.setText("0");

        lblCarrier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/vehicles/0.png"))); // NOI18N
        lblCarrier.setText("0");

        btnNameChars.setText("...");
        btnNameChars.setEnabled(false);
        btnNameChars.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNameCharsActionPerformed(evt);
            }
        });

        lblControl.setText("Control mode");

        cmoControl.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Normal", "Accelerate with joystick" }));
        cmoControl.setEnabled(false);

        cmoLanguage.setEnabled(false);

        lblLanguage.setText("Language");

        chkVAmericanDream.setEnabled(false);
        chkVAmericanDream.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/vehicles/8_deselect.png"))); // NOI18N
        chkVAmericanDream.setMargin(new java.awt.Insets(1, 1, 1, 1));
        chkVAmericanDream.setMaximumSize(new java.awt.Dimension(32, 24));
        chkVAmericanDream.setMinimumSize(new java.awt.Dimension(32, 24));
        chkVAmericanDream.setPreferredSize(new java.awt.Dimension(32, 24));
        chkVAmericanDream.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/res/vehicles/8_select.png"))); // NOI18N

        chkVPoliceCar.setEnabled(false);
        chkVPoliceCar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/vehicles/13_deselect.png"))); // NOI18N
        chkVPoliceCar.setMargin(new java.awt.Insets(1, 1, 1, 1));
        chkVPoliceCar.setMaximumSize(new java.awt.Dimension(32, 24));
        chkVPoliceCar.setMinimumSize(new java.awt.Dimension(32, 24));
        chkVPoliceCar.setPreferredSize(new java.awt.Dimension(32, 24));
        chkVPoliceCar.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/res/vehicles/13_select.png"))); // NOI18N

        chkVHotrod.setEnabled(false);
        chkVHotrod.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/vehicles/15_deselect.png"))); // NOI18N
        chkVHotrod.setMargin(new java.awt.Insets(1, 1, 1, 1));
        chkVHotrod.setMaximumSize(new java.awt.Dimension(32, 24));
        chkVHotrod.setMinimumSize(new java.awt.Dimension(32, 24));
        chkVHotrod.setPreferredSize(new java.awt.Dimension(32, 24));
        chkVHotrod.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/res/vehicles/15_select.png"))); // NOI18N

        chkVATeam.setEnabled(false);
        chkVATeam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/vehicles/14_deselect.png"))); // NOI18N
        chkVATeam.setMargin(new java.awt.Insets(1, 1, 1, 1));
        chkVATeam.setMaximumSize(new java.awt.Dimension(32, 24));
        chkVATeam.setMinimumSize(new java.awt.Dimension(32, 24));
        chkVATeam.setPreferredSize(new java.awt.Dimension(32, 24));
        chkVATeam.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/res/vehicles/14_select.png"))); // NOI18N

        javax.swing.GroupLayout pneGeneralLayout = new javax.swing.GroupLayout(pneGeneral);
        pneGeneral.setLayout(pneGeneralLayout);
        pneGeneralLayout.setHorizontalGroup(
            pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(pneGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pneGeneralLayout.createSequentialGroup()
                        .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblGeneralName)
                            .addComponent(lblGeneralMoney)
                            .addComponent(lblGeneralPoints)
                            .addComponent(lblGeneralRank)
                            .addComponent(lblGeneralProgress)
                            .addComponent(lblGeneralLevel)
                            .addComponent(lblControl)
                            .addComponent(lblLanguage))
                        .addGap(24, 24, 24)
                        .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmoLanguage, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmoControl, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pneGeneralLayout.createSequentialGroup()
                                .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnNameChars, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(spnMoney, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPoints, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cmoRank, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmoProgress, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmoSelectedLevel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pneGeneralLayout.createSequentialGroup()
                        .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pneGeneralLayout.createSequentialGroup()
                                .addComponent(lblBronze)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblSilver)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblGold)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblPlatinum)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblCarrier)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblCommPoints))
                            .addGroup(pneGeneralLayout.createSequentialGroup()
                                .addComponent(chkVAmericanDream, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkVPoliceCar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkVHotrod, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkVATeam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 14, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pneGeneralLayout.setVerticalGroup(
            pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pneGeneralLayout.createSequentialGroup()
                .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGeneralName)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNameChars, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGeneralMoney)
                    .addComponent(spnMoney, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPoints, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGeneralPoints))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmoRank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGeneralRank))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmoProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGeneralProgress))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmoSelectedLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGeneralLevel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblControl)
                    .addComponent(cmoControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmoLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLanguage))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 23, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBronze)
                    .addComponent(lblSilver)
                    .addComponent(lblGold)
                    .addComponent(lblPlatinum)
                    .addComponent(lblCarrier)
                    .addComponent(lblCommPoints))
                .addGap(6, 6, 6)
                .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chkVAmericanDream, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkVPoliceCar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkVHotrod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkVATeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pneLevel.setBorder(javax.swing.BorderFactory.createTitledBorder("Level"));

        lblLevel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/selectedlevel.png"))); // NOI18N

        cmoLevel.setEnabled(false);
        cmoLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoLevelActionPerformed(evt);
            }
        });

        lblLevelVehicle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/vehicles/0.png"))); // NOI18N
        lblLevelVehicle.setText("Vehicle");
        lblLevelVehicle.setIconTextGap(5);

        lblLevelMedal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/noimage.png"))); // NOI18N
        lblLevelMedal.setText("Medal");
        lblLevelMedal.setIconTextGap(6);

        lblLevelDishes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/satellite.png"))); // NOI18N
        lblLevelDishes.setText("Comm.");
        lblLevelDishes.setIconTextGap(6);

        lblLevelTime.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/time.png"))); // NOI18N
        lblLevelTime.setText("Time");
        lblLevelTime.setIconTextGap(6);

        cmoLevelVehicle.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Sideswipe", "Thunderfist", "Skyfall", "Ramdozer", "Backlash", "Crane", "Train", "American Dream", "J-Bomb", "Ballista", "Boat (1)", "Unknown", "Police Car", "A-Team Van", "Red Car", "Cyclone Suit", "Boat (2)", "Boat (3)" }));
        cmoLevelVehicle.setEnabled(false);
        cmoLevelVehicle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoLevelVehicleActionPerformed(evt);
            }
        });

        cmoLevelMedal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Locked", "Bronze", "Silver", "Gold", "Platinum", "Finished", "Red (Unused)", "Green (Unused)", "Unlocked" }));
        cmoLevelMedal.setEnabled(false);
        cmoLevelMedal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoLevelMedalActionPerformed(evt);
            }
        });

        cmoLevelDishes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "First", "Second", "All" }));
        cmoLevelDishes.setEnabled(false);
        cmoLevelDishes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoLevelDishesActionPerformed(evt);
            }
        });

        spnLevelTimeMin.setModel(new javax.swing.SpinnerNumberModel(0, 0, 50, 1));
        spnLevelTimeMin.setEnabled(false);

        spnLevelTimeSec.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnLevelTimeSec.setEnabled(false);

        spnLevelTimeMil.setModel(new javax.swing.SpinnerNumberModel(0, 0, 9, 1));
        spnLevelTimeMil.setEnabled(false);

        lblLevelTimeSep1.setText(":");

        lblLevelTimeSep2.setText(".");

        btnLevelTimeSet.setText("Set time");
        btnLevelTimeSet.setEnabled(false);
        btnLevelTimeSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLevelTimeSetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pneLevelLayout = new javax.swing.GroupLayout(pneLevel);
        pneLevel.setLayout(pneLevelLayout);
        pneLevelLayout.setHorizontalGroup(
            pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pneLevelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pneLevelLayout.createSequentialGroup()
                        .addComponent(lblLevel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmoLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pneLevelLayout.createSequentialGroup()
                        .addGroup(pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLevelVehicle)
                            .addComponent(lblLevelDishes)
                            .addComponent(lblLevelTime)
                            .addComponent(lblLevelMedal))
                        .addGap(16, 16, 16)
                        .addGroup(pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmoLevelDishes, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmoLevelMedal, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmoLevelVehicle, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pneLevelLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(spnLevelTimeMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblLevelTimeSep1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnLevelTimeSec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblLevelTimeSep2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnLevelTimeMil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pneLevelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnLevelTimeSet, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pneLevelLayout.setVerticalGroup(
            pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pneLevelLayout.createSequentialGroup()
                .addGroup(pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblLevel)
                    .addComponent(cmoLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLevelVehicle)
                    .addComponent(cmoLevelVehicle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmoLevelMedal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLevelMedal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLevelDishes)
                    .addComponent(cmoLevelDishes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLevelTime)
                    .addComponent(spnLevelTimeMil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnLevelTimeSec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnLevelTimeMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLevelTimeSep1)
                    .addComponent(lblLevelTimeSep2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLevelTimeSet, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pneScientists.setBorder(javax.swing.BorderFactory.createTitledBorder("Scientists"));

        chkSArgentTowers.setText("Argent Towers");
        chkSArgentTowers.setEnabled(false);

        chkSTempestCity.setText("Tempest City");
        chkSTempestCity.setEnabled(false);

        chkSIronstoneMine.setText("Ironstone Mine");
        chkSIronstoneMine.setEnabled(false);

        chkSEbonyCoast.setText("Ebony Coast");
        chkSEbonyCoast.setEnabled(false);

        chkSGloryCrossing.setText("Glory Crossing");
        chkSGloryCrossing.setEnabled(false);

        chkSOysterHarbor.setText("Oyster Harbor");
        chkSOysterHarbor.setEnabled(false);

        javax.swing.GroupLayout pneScientistsLayout = new javax.swing.GroupLayout(pneScientists);
        pneScientists.setLayout(pneScientistsLayout);
        pneScientistsLayout.setHorizontalGroup(
            pneScientistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pneScientistsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pneScientistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSTempestCity)
                    .addComponent(chkSArgentTowers)
                    .addComponent(chkSIronstoneMine))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(pneScientistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chkSOysterHarbor, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSEbonyCoast, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkSGloryCrossing, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        pneScientistsLayout.setVerticalGroup(
            pneScientistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pneScientistsLayout.createSequentialGroup()
                .addGroup(pneScientistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSArgentTowers)
                    .addComponent(chkSEbonyCoast))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneScientistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSTempestCity)
                    .addComponent(chkSGloryCrossing))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneScientistsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSIronstoneMine)
                    .addComponent(chkSOysterHarbor))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tbrFooter.setBackground(new java.awt.Color(225, 225, 225));
        tbrFooter.setFloatable(false);
        tbrFooter.setRollover(true);
        tbrFooter.add(fil0);

        lblDir.setText(" ");
        tbrFooter.add(lblDir);

        subFile.setMnemonic('F');
        subFile.setText("File");

        mnuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mnuOpen.setMnemonic('O');
        mnuOpen.setText("Open");
        mnuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOpenActionPerformed(evt);
            }
        });
        subFile.add(mnuOpen);

        mnuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mnuSave.setMnemonic('S');
        mnuSave.setText("Save");
        mnuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveActionPerformed(evt);
            }
        });
        subFile.add(mnuSave);

        mnuSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuSaveAs.setMnemonic('A');
        mnuSaveAs.setText("Save as");
        mnuSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveAsActionPerformed(evt);
            }
        });
        subFile.add(mnuSaveAs);

        mnuDispose.setMnemonic('C');
        mnuDispose.setText("Close");
        mnuDispose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDisposeActionPerformed(evt);
            }
        });
        subFile.add(mnuDispose);

        mbarGeneral.add(subFile);

        subOptions.setMnemonic('O');
        subOptions.setText("Options");

        mnuUnlockLevels.setText("All levels unlocked");
        mnuUnlockLevels.setEnabled(false);
        mnuUnlockLevels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUnlockLevelsActionPerformed(evt);
            }
        });
        subOptions.add(mnuUnlockLevels);

        mnuUnlockTutorials.setText("All tutorial cutscenes seen");
        mnuUnlockTutorials.setEnabled(false);
        mnuUnlockTutorials.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUnlockTutorialsActionPerformed(evt);
            }
        });
        subOptions.add(mnuUnlockTutorials);

        mnuUnlockInfos.setText("All tutorials seen");
        mnuUnlockInfos.setEnabled(false);
        mnuUnlockInfos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUnlockInfosActionPerformed(evt);
            }
        });
        subOptions.add(mnuUnlockInfos);

        mbarGeneral.add(subOptions);

        subAbout.setMnemonic('H');
        subAbout.setText("Help");

        mnuAbout.setMnemonic('A');
        mnuAbout.setText("About");
        mnuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAboutActionPerformed(evt);
            }
        });
        subAbout.add(mnuAbout);

        mbarGeneral.add(subAbout);

        setJMenuBar(mbarGeneral);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tbrFooter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pneGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pneLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pneScientists, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pneLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pneScientists, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pneGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tbrFooter, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        setResizable(false);
        setTitle(Thunder.name);
        setIconImage(Toolkit.getDefaultToolkit().createImage(Thunder.class.getResource(Thunder.icon)));
    }//GEN-LAST:event_formWindowOpened
	
    private void mnuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOpenActionPerformed
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Open EEPROM file");
        fc.setFileFilter(new FileNameExtensionFilter("EEPROM files (*.eep)", ".eep", "eep"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Save files (*.sav)", ".sav", "sav"));
        
        String lastdir = Preferences.userRoot().get("thunder_lastDir", null);
        if (lastdir != null)
            fc.setSelectedFile(new File(lastdir));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        String newdir = fc.getSelectedFile().getPath();
        Preferences.userRoot().put("thunder_lastDir", newdir);
        
        save = new SaveData(fc.getSelectedFile());
        if (save.fileLoaded) {
            getSaveData();
            componentsEnabled(true);
            lblDir.setText("Opened file " + save.file.getAbsolutePath());
        }
        else {
            componentsEnabled(false);
            lblDir.setText("Failed to open file " + save.file.getAbsolutePath());
            JOptionPane.showMessageDialog(null, "A problem occured while trying to load the file.", Thunder.name, JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_mnuOpenActionPerformed

    private void mnuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveAsActionPerformed
        if (save.fileLoaded) {
            final JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save EEPROM file");
            fc.setFileFilter(new FileNameExtensionFilter("EEPROM files (*.eep)", ".eep", "eep"));
            fc.addChoosableFileFilter(new FileNameExtensionFilter("Save files (*.sav)", ".sav", "sav"));
            
            String lastdir = Preferences.userRoot().get("thunder_lastDir", null);
            if (lastdir != null)
                fc.setSelectedFile(new File(lastdir));
            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
                return;
            String newdir = fc.getSelectedFile().getPath();
            Preferences.userRoot().put("thunder_lastDir", newdir);
            
            setSaveData();
            save.saveFile(fc.getSelectedFile());
            lblDir.setText("Saved file to " + save.newfile.getAbsolutePath());
        }
    }//GEN-LAST:event_mnuSaveAsActionPerformed

    private void mnuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAboutActionPerformed
        JOptionPane.showMessageDialog(null, "An editor for Blast Corps EEPROM savegame files.\nHuge thanks to queueRAM for figuring out how checksums work.", Thunder.name, JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_mnuAboutActionPerformed
	
    private void mnuDisposeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDisposeActionPerformed
        dispose();
    }//GEN-LAST:event_mnuDisposeActionPerformed

	private void cmoLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoLevelActionPerformed
        try {
            cmoLevelMedal.setSelectedIndex(save.eep.levelMedals[cmoLevel.getSelectedIndex()]);
            cmoLevelVehicle.setSelectedIndex(save.eep.levelVehicles[cmoLevel.getSelectedIndex()]);
            cmoLevelDishes.setSelectedIndex(save.eep.levelPaths[cmoLevel.getSelectedIndex()]);
        
            int[] time = save.getTime(cmoLevel.getSelectedIndex());
            spnLevelTimeMin.setValue(time[0]);
            spnLevelTimeSec.setValue(time[1]);
            spnLevelTimeMil.setValue(time[2]);
        }
        catch (Exception ex) {
            System.out.println("ERROR: No items found (cmoLevel).");
        }
    }//GEN-LAST:event_cmoLevelActionPerformed
	
    private void cmoLevelVehicleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoLevelVehicleActionPerformed
        save.eep.levelVehicles[cmoLevel.getSelectedIndex()] = (byte)cmoLevelVehicle.getSelectedIndex();
        updateLabelIcons();
    }//GEN-LAST:event_cmoLevelVehicleActionPerformed

    private void cmoLevelMedalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoLevelMedalActionPerformed
        save.eep.levelMedals[cmoLevel.getSelectedIndex()] = (byte)cmoLevelMedal.getSelectedIndex();
        updatePoints();
        updateLabelIcons();
    }//GEN-LAST:event_cmoLevelMedalActionPerformed

    private void cmoLevelDishesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoLevelDishesActionPerformed
        save.eep.levelPaths[cmoLevel.getSelectedIndex()] = (byte)cmoLevelDishes.getSelectedIndex();
        updateCommPoints();
    }//GEN-LAST:event_cmoLevelDishesActionPerformed

    private void cmoProgressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoProgressActionPerformed
        save.eep.event = (byte) cmoProgress.getSelectedIndex();
        updatePoints();
    }//GEN-LAST:event_cmoProgressActionPerformed

    private void mnuUnlockLevelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUnlockLevelsActionPerformed
        int i = 0;
        for (byte b : save.eep.levelMedals) {
            if (b == 0x0)
                save.eep.levelMedals[i] = 0x8;
            i++;
        }
        
        updatePoints();
        cmoLevel.setSelectedIndex(cmoLevel.getSelectedIndex());
        JOptionPane.showMessageDialog(null, "Unlocked all levels.", Thunder.name, JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_mnuUnlockLevelsActionPerformed

    private void mnuUnlockTutorialsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUnlockTutorialsActionPerformed
        save.eep.cutscenes = 0x1F;
        JOptionPane.showMessageDialog(null, "Set all training cutscenes as watched.", Thunder.name, JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_mnuUnlockTutorialsActionPerformed

    private void mnuUnlockInfosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUnlockInfosActionPerformed
        for (int i = 0 ; i < save.eep.tutorials.length ; i++)
            save.eep.tutorials[i] = 0x1;
        JOptionPane.showMessageDialog(null, "Set all tutorials as seen.", Thunder.name, JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_mnuUnlockInfosActionPerformed

    private void btnNameCharsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNameCharsActionPerformed
        Object[] possibilities = {"(none)","[0x23] Cursor", "[0x26] Space (alt.)", "[0x2A] Glitch", "[0x7F] Delete"};
        String out = (String)JOptionPane.showInputDialog(null, "", "Add special character", JOptionPane.PLAIN_MESSAGE, null, possibilities, "(none)");
        if (out != null && out.length() > 0 && !out.equals("(none)")) {
            try {
                txtName.getDocument().insertString(txtName.getCaretPosition(), out.split(" ")[0], null);
            }
            catch (Exception ex) {
            }
        }
    }//GEN-LAST:event_btnNameCharsActionPerformed

    private void txtNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyTyped
        char c = evt.getKeyChar();
        if (c != ' ' && c != '!' && c != '$' && c != '%' && c != '\'' && c != '(' && c != ')' && c != ',' && c != '-' && c != '.' && c != '/' && c != '?' && c != '"' && c != '#' && c != '*' && c != '+' && c != '=' && c != '@' && ((c < '0') || (c > ':')) && ((c < 'A') || (c > 'Z')) && ((c < 'a') || (c > 'z')) && (c != java.awt.event.KeyEvent.VK_BACK_SPACE))
            evt.consume();
        evt.setKeyChar(Character.toUpperCase(c));
    }//GEN-LAST:event_txtNameKeyTyped

    private void btnLevelTimeSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLevelTimeSetActionPerformed
        save.setTime(cmoLevel.getSelectedIndex(), (int)spnLevelTimeMin.getValue(), (int)spnLevelTimeSec.getValue(), (int)spnLevelTimeMil.getValue());
    }//GEN-LAST:event_btnLevelTimeSetActionPerformed

    private void mnuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveActionPerformed
        if (save.fileLoaded) {
            setSaveData();
            save.saveFile(save.file.getAbsoluteFile());
            lblDir.setText("Saved file to " + save.newfile.getAbsolutePath());
        }
    }//GEN-LAST:event_mnuSaveActionPerformed
    
    private SaveData save = new SaveData();
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLevelTimeSet;
    private javax.swing.JButton btnNameChars;
    private javax.swing.JCheckBox chkSArgentTowers;
    private javax.swing.JCheckBox chkSEbonyCoast;
    private javax.swing.JCheckBox chkSGloryCrossing;
    private javax.swing.JCheckBox chkSIronstoneMine;
    private javax.swing.JCheckBox chkSOysterHarbor;
    private javax.swing.JCheckBox chkSTempestCity;
    private javax.swing.JCheckBox chkVATeam;
    private javax.swing.JCheckBox chkVAmericanDream;
    private javax.swing.JCheckBox chkVHotrod;
    private javax.swing.JCheckBox chkVPoliceCar;
    private javax.swing.JComboBox<String> cmoControl;
    private javax.swing.JComboBox<String> cmoLanguage;
    private javax.swing.JComboBox<String> cmoLevel;
    private javax.swing.JComboBox<String> cmoLevelDishes;
    private javax.swing.JComboBox<String> cmoLevelMedal;
    private javax.swing.JComboBox<String> cmoLevelVehicle;
    private javax.swing.JComboBox<String> cmoProgress;
    private javax.swing.JComboBox<String> cmoRank;
    private javax.swing.JComboBox<String> cmoSelectedLevel;
    private javax.swing.Box.Filler fil0;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblBronze;
    private javax.swing.JLabel lblCarrier;
    private javax.swing.JLabel lblCommPoints;
    private javax.swing.JLabel lblControl;
    private javax.swing.JLabel lblDir;
    private javax.swing.JLabel lblGeneralLevel;
    private javax.swing.JLabel lblGeneralMoney;
    private javax.swing.JLabel lblGeneralName;
    private javax.swing.JLabel lblGeneralPoints;
    private javax.swing.JLabel lblGeneralProgress;
    private javax.swing.JLabel lblGeneralRank;
    private javax.swing.JLabel lblGold;
    private javax.swing.JLabel lblLanguage;
    private javax.swing.JLabel lblLevel;
    private javax.swing.JLabel lblLevelDishes;
    private javax.swing.JLabel lblLevelMedal;
    private javax.swing.JLabel lblLevelTime;
    private javax.swing.JLabel lblLevelTimeSep1;
    private javax.swing.JLabel lblLevelTimeSep2;
    private javax.swing.JLabel lblLevelVehicle;
    private javax.swing.JLabel lblPlatinum;
    private javax.swing.JLabel lblSilver;
    private javax.swing.JMenuBar mbarGeneral;
    private javax.swing.JMenuItem mnuAbout;
    private javax.swing.JMenuItem mnuDispose;
    private javax.swing.JMenuItem mnuOpen;
    private javax.swing.JMenuItem mnuSave;
    private javax.swing.JMenuItem mnuSaveAs;
    private javax.swing.JMenuItem mnuUnlockInfos;
    private javax.swing.JMenuItem mnuUnlockLevels;
    private javax.swing.JMenuItem mnuUnlockTutorials;
    private javax.swing.JPanel pneGeneral;
    private javax.swing.JPanel pneLevel;
    private javax.swing.JPanel pneScientists;
    private javax.swing.JSpinner spnLevelTimeMil;
    private javax.swing.JSpinner spnLevelTimeMin;
    private javax.swing.JSpinner spnLevelTimeSec;
    private javax.swing.JSpinner spnMoney;
    private javax.swing.JMenu subAbout;
    private javax.swing.JMenu subFile;
    private javax.swing.JMenu subOptions;
    private javax.swing.JToolBar tbrFooter;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPoints;
    // End of variables declaration//GEN-END:variables
}