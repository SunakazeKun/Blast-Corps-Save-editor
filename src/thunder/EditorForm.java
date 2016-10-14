/*
 * Copyright (C) 2016 Aurum
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package thunder;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class EditorForm extends javax.swing.JFrame {

    public EditorForm() {
        initComponents();
        populateLists();
    }

    private void getSaveData() {
        /*
            Should be used whenever a file is opened.
        */
        
        updatePoints();
        updateCommPoints();
        
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
        
        txtChecksum.setText(String.format("%X", ByteUtils.bytesToInt(save.eep.checksum)));
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
        
        cmoLevel.setSelectedIndex(0);
        cmoLevelMedal.setSelectedIndex(save.eep.levelMedal[cmoLevel.getSelectedIndex()]);
        cmoLevelDishes.setSelectedIndex(save.eep.levelPaths[cmoLevel.getSelectedIndex()]);
        cmoLevelVehicle.setSelectedIndex(save.eep.levelVehicle[cmoLevel.getSelectedIndex()]);
        txtLevelTime.setText(save.getTime(cmoLevel.getSelectedIndex()));
    }
    
    private void setSaveData() {
        /*
            Should be used whenever a file is saved.
        */
        
        save.setName(txtName.getText());
        save.setLang((String)cmoLanguage.getSelectedItem());
        save.setControlMode(cmoControl.getSelectedIndex());
        save.eep.points = ByteUtils.shortToBytes(save.points);
        save.eep.money = ByteUtils.intToBytes((int)spnMoney.getValue());
        save.eep.selectedLevel = (byte)cmoSelectedLevel.getSelectedIndex();
    }
    
    private void componentsEnabled(boolean b) {
        /*
            This enables or disables components.
            Should be used whenever a file is opened or saved.
        */
        
        cmoLevel.setEnabled(b);
        cmoLevelMedal.setEnabled(b);
        cmoLevelVehicle.setEnabled(b);
        cmoLevelDishes.setEnabled(b);
        txtLevelTime.setEnabled(b);
        mnuUnlockLevels.setEnabled(b);
        mnuUnlockVehicles.setEnabled(b);
        mnuUnlockScientists.setEnabled(b);
        mnuUnlockTutorials.setEnabled(b);
        mnuUnlockInfos.setEnabled(b);
        txtName.setEnabled(b);
        spnMoney.setEnabled(b);
        cmoProgress.setEnabled(b);
        cmoSelectedLevel.setEnabled(b);
        cmoControl.setEnabled(b);
        cmoLanguage.setEnabled(b);
    }
    
    private void populateLists() {
        /*
            Populates lists with strings and adds them as items to combo boxes.
        */
        
        cmoLanguage.removeAllItems();
        
        populateList("/res/data/levels.txt").stream().forEach((level) -> { cmoSelectedLevel.addItem(level); cmoLevel.addItem(level); });
        populateList("/res/data/ranks.txt").stream().forEach((rank) -> { cmoRank.addItem(rank); });
        populateList("/res/data/medals.txt").stream().forEach((medal) -> { cmoLevelMedal.addItem(medal); });
        populateList("/res/data/vehicles.txt").stream().forEach((vehicle) -> { cmoLevelVehicle.addItem(vehicle); });
        populateList("/res/data/events.txt").stream().forEach((event) -> { cmoProgress.addItem(event);}) ;
    }
    
    private List<String> populateList(String in) {
        /*
            Returns a list of strings which are used for combo boxes.
            The input file's lines are split into numbers and text.
            Right now, the numbers are unused and completely ignored,
            but I will make use of them when I want to sort the combo boxes.
            * in : The input resource.
        */
        
        List<String> l = new ArrayList();
        try {
            InputStream i = Thunder.class.getResourceAsStream(in);
            BufferedReader r = new BufferedReader(new InputStreamReader(i));
            String s;
            String[] d;
            
            while ((s = r.readLine()) != null) {
                s = s.trim();
                d = s.split(",");
                if (s.startsWith("#"))
                    continue;
                
                l.add(d[1]);
            }
        }
        catch (Exception ex) {
        }
        return l;
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
    
    private void updateLabelIcons() {
        lblLevelVehicle.setIcon(new javax.swing.ImageIcon(getClass().getResource(getResourceName("vehicles",cmoLevelVehicle.getSelectedIndex()))));
        lblLevelMedal.setIcon(new javax.swing.ImageIcon(getClass().getResource(getResourceName("medals",cmoLevelMedal.getSelectedIndex()))));
    }
    
    private String getResourceName(String dir, int index) {
        String res = "/res/" + dir + "/" + index + ".png";
        URL u = Thunder.class.getResource(res);
        if (u != null)
            return res;
        else
            return "/res/noimage.png";
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
        txtLevelTime = new javax.swing.JTextField();
        pneOther = new javax.swing.JPanel();
        lblLanguage = new javax.swing.JLabel();
        lblControl = new javax.swing.JLabel();
        lblChecksum = new javax.swing.JLabel();
        cmoLanguage = new javax.swing.JComboBox<>();
        cmoControl = new javax.swing.JComboBox<>();
        txtChecksum = new javax.swing.JTextField();
        tbrFooter = new javax.swing.JToolBar();
        fil0 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        lblDir = new javax.swing.JLabel();
        mbarGeneral = new javax.swing.JMenuBar();
        subFile = new javax.swing.JMenu();
        mnuOpen = new javax.swing.JMenuItem();
        mnuSave = new javax.swing.JMenuItem();
        mnuDispose = new javax.swing.JMenuItem();
        subOptions = new javax.swing.JMenu();
        mnuUnlockLevels = new javax.swing.JMenuItem();
        mnuUnlockVehicles = new javax.swing.JMenuItem();
        mnuUnlockScientists = new javax.swing.JMenuItem();
        mnuUnlockTutorials = new javax.swing.JMenuItem();
        mnuUnlockInfos = new javax.swing.JMenuItem();

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
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNameKeyReleased(evt);
            }
        });

        spnMoney.setModel(new javax.swing.SpinnerNumberModel(0, -2147483647, 2147483647, 1));
        spnMoney.setEnabled(false);

        txtPoints.setEditable(false);
        txtPoints.setText("0");

        cmoRank.setEnabled(false);

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
                            .addComponent(lblGeneralMoney))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(spnMoney, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pneGeneralLayout.createSequentialGroup()
                        .addComponent(lblGeneralPoints)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtPoints, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pneGeneralLayout.createSequentialGroup()
                        .addComponent(lblGeneralRank)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmoRank, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pneGeneralLayout.createSequentialGroup()
                        .addComponent(lblGeneralProgress)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmoProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pneGeneralLayout.createSequentialGroup()
                        .addComponent(lblGeneralLevel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                        .addComponent(cmoSelectedLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pneGeneralLayout.createSequentialGroup()
                        .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pneGeneralLayout.createSequentialGroup()
                                .addComponent(lblBronze)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblSilver)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblGold)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblPlatinum))
                            .addGroup(pneGeneralLayout.createSequentialGroup()
                                .addComponent(lblCommPoints)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblCarrier)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pneGeneralLayout.setVerticalGroup(
            pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pneGeneralLayout.createSequentialGroup()
                .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGeneralName)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBronze)
                    .addComponent(lblSilver)
                    .addComponent(lblGold)
                    .addComponent(lblPlatinum))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCarrier)
                    .addComponent(lblCommPoints))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        cmoLevelVehicle.setEnabled(false);
        cmoLevelVehicle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoLevelVehicleActionPerformed(evt);
            }
        });

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

        txtLevelTime.setEditable(false);
        txtLevelTime.setText("0:00.0");

        javax.swing.GroupLayout pneLevelLayout = new javax.swing.GroupLayout(pneLevel);
        pneLevel.setLayout(pneLevelLayout);
        pneLevelLayout.setHorizontalGroup(
            pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pneLevelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pneLevelLayout.createSequentialGroup()
                        .addComponent(lblLevel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmoLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pneLevelLayout.createSequentialGroup()
                        .addGroup(pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLevelVehicle)
                            .addComponent(lblLevelDishes)
                            .addComponent(lblLevelTime)
                            .addComponent(lblLevelMedal))
                        .addGap(14, 14, 14)
                        .addGroup(pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmoLevelVehicle, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmoLevelMedal, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmoLevelDishes, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtLevelTime))))
                .addContainerGap())
        );
        pneLevelLayout.setVerticalGroup(
            pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pneLevelLayout.createSequentialGroup()
                .addGroup(pneLevelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblLevel)
                    .addComponent(cmoLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                    .addComponent(txtLevelTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLevelTime)))
        );

        pneOther.setBorder(javax.swing.BorderFactory.createTitledBorder("Other"));

        lblLanguage.setText("Language");

        lblControl.setText("Control mode");

        lblChecksum.setText("Checksum");

        cmoLanguage.setEnabled(false);

        cmoControl.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Normal", "Accelerate with joystick" }));
        cmoControl.setEnabled(false);

        txtChecksum.setEditable(false);
        txtChecksum.setText("00000000");

        javax.swing.GroupLayout pneOtherLayout = new javax.swing.GroupLayout(pneOther);
        pneOther.setLayout(pneOtherLayout);
        pneOtherLayout.setHorizontalGroup(
            pneOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pneOtherLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pneOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pneOtherLayout.createSequentialGroup()
                        .addGroup(pneOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLanguage)
                            .addComponent(lblChecksum))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pneOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmoLanguage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtChecksum, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pneOtherLayout.createSequentialGroup()
                        .addComponent(lblControl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmoControl, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pneOtherLayout.setVerticalGroup(
            pneOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pneOtherLayout.createSequentialGroup()
                .addGroup(pneOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLanguage)
                    .addComponent(cmoLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pneOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmoControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblControl))
                .addGap(6, 6, 6)
                .addGroup(pneOtherLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblChecksum)
                    .addComponent(txtChecksum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        tbrFooter.setBackground(new java.awt.Color(225, 225, 225));
        tbrFooter.setFloatable(false);
        tbrFooter.setRollover(true);
        tbrFooter.add(fil0);

        lblDir.setText(" ");
        tbrFooter.add(lblDir);

        subFile.setText("File");

        mnuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mnuOpen.setText("Open");
        mnuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOpenActionPerformed(evt);
            }
        });
        subFile.add(mnuOpen);

        mnuSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mnuSave.setText("Save");
        mnuSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaveActionPerformed(evt);
            }
        });
        subFile.add(mnuSave);

        mnuDispose.setText("Close");
        mnuDispose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDisposeActionPerformed(evt);
            }
        });
        subFile.add(mnuDispose);

        mbarGeneral.add(subFile);

        subOptions.setText("Options");

        mnuUnlockLevels.setText("All levels unlocked");
        mnuUnlockLevels.setEnabled(false);
        mnuUnlockLevels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUnlockLevelsActionPerformed(evt);
            }
        });
        subOptions.add(mnuUnlockLevels);

        mnuUnlockVehicles.setText("All vehicles unlocked");
        mnuUnlockVehicles.setEnabled(false);
        mnuUnlockVehicles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUnlockVehiclesActionPerformed(evt);
            }
        });
        subOptions.add(mnuUnlockVehicles);

        mnuUnlockScientists.setText("All scientists found");
        mnuUnlockScientists.setEnabled(false);
        mnuUnlockScientists.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUnlockScientistsActionPerformed(evt);
            }
        });
        subOptions.add(mnuUnlockScientists);

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

        setJMenuBar(mbarGeneral);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pneGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pneLevel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pneOther, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(14, Short.MAX_VALUE))
            .addComponent(tbrFooter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pneLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pneOther, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pneGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tbrFooter, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmoLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoLevelActionPerformed
        try {
            cmoLevelMedal.setSelectedIndex(save.eep.levelMedal[cmoLevel.getSelectedIndex()]);
            cmoLevelVehicle.setSelectedIndex(save.eep.levelVehicle[cmoLevel.getSelectedIndex()]);
            cmoLevelDishes.setSelectedIndex(save.eep.levelPaths[cmoLevel.getSelectedIndex()]);
            txtLevelTime.setText(save.getTime(cmoLevel.getSelectedIndex()));
        }
        catch (Exception ex) {
            System.out.println("ERROR: No items found (cmoLevel).");
        }
    }//GEN-LAST:event_cmoLevelActionPerformed

    private void txtNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyReleased
        if (txtName.getText().length() > 7) {
            txtName.setText(txtName.getText().substring(0, Math.min(txtName.getText().length(), 7)));
        }
    }//GEN-LAST:event_txtNameKeyReleased

    private void mnuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOpenActionPerformed
        final JFileChooser fc = new JFileChooser();
        String lastdir = Preferences.userRoot().get("thunder_lastDir", null);
        fc.setDialogTitle("Open EEPROM file");
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

    private void mnuSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaveActionPerformed
        if (save.fileLoaded) {
            final JFileChooser fc = new JFileChooser();
            String lastdir = Preferences.userRoot().get("thunder_lastDir", null);
            fc.setDialogTitle("Save EEPROM file");
            if (lastdir != null)
                fc.setSelectedFile(new File(lastdir));
            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
                return;
            String newdir = fc.getSelectedFile().getPath();
            Preferences.userRoot().put("thunder_lastDir", newdir);
            
            setSaveData();
            save.saveFile(fc.getSelectedFile());
            txtChecksum.setText(String.format("%X", ByteUtils.bytesToInt(save.eep.checksum)));
            lblDir.setText("Saved file to " + save.newfile.getAbsolutePath());
        }
    }//GEN-LAST:event_mnuSaveActionPerformed

    private void cmoLevelVehicleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoLevelVehicleActionPerformed
        try {
            save.eep.levelVehicle[cmoLevel.getSelectedIndex()] = (byte)cmoLevelVehicle.getSelectedIndex();
            updateLabelIcons();
        }
        catch (Exception ex) {
            System.out.println("ERROR: No items found (cmoLevelVehicle).");
        }
    }//GEN-LAST:event_cmoLevelVehicleActionPerformed

    private void cmoLevelMedalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoLevelMedalActionPerformed
        try {
            save.eep.levelMedal[cmoLevel.getSelectedIndex()] = (byte)cmoLevelMedal.getSelectedIndex();
            updatePoints();
            updateLabelIcons();
        }
        catch (Exception ex) {
            System.out.println("ERROR: No items found (cmoLevelMedal).");
        }
    }//GEN-LAST:event_cmoLevelMedalActionPerformed

    private void cmoLevelDishesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoLevelDishesActionPerformed
        try {
            save.eep.levelPaths[cmoLevel.getSelectedIndex()] = (byte)cmoLevelDishes.getSelectedIndex();
            updateCommPoints();
        }
        catch (Exception ex) {
            System.out.println("ERROR: No items found (cmoLevelDishes).");
        }
    }//GEN-LAST:event_cmoLevelDishesActionPerformed

    private void mnuDisposeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDisposeActionPerformed
        dispose();
    }//GEN-LAST:event_mnuDisposeActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        setResizable(false);
        setTitle(Thunder.name);
        setIconImage(Toolkit.getDefaultToolkit().createImage(Thunder.class.getResource(Thunder.icon)));
    }//GEN-LAST:event_formWindowOpened

    private void cmoProgressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoProgressActionPerformed
        try {
            save.eep.event = (byte) cmoProgress.getSelectedIndex();
            updatePoints();
        }
        catch (Exception ex) {
            System.out.println("ERROR: No items found (cmoProgress).");
        }
    }//GEN-LAST:event_cmoProgressActionPerformed

    private void mnuUnlockLevelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUnlockLevelsActionPerformed
        int i = 0;
        for (byte b : save.eep.levelMedal) {
            if (b == 0x0)
                save.eep.levelMedal[i] = 0x8;
            i++;
        }
        
        updatePoints();
        cmoLevel.setSelectedIndex(cmoLevel.getSelectedIndex());
        JOptionPane.showMessageDialog(null, "Unlocked all levels.", Thunder.name, JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_mnuUnlockLevelsActionPerformed

    private void mnuUnlockVehiclesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUnlockVehiclesActionPerformed
        save.eep.vehicles = new byte[] {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
        JOptionPane.showMessageDialog(null, "Unlocked all vehicles.", Thunder.name, JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_mnuUnlockVehiclesActionPerformed

    private void mnuUnlockScientistsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUnlockScientistsActionPerformed
        save.eep.scientists = 0x3F;
        JOptionPane.showMessageDialog(null, "Unlocked all six scientists.", Thunder.name, JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_mnuUnlockScientistsActionPerformed

    private void mnuUnlockTutorialsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUnlockTutorialsActionPerformed
        save.eep.cutscenes = 0x1F;
        JOptionPane.showMessageDialog(null, "Set all training cutscenes as watched.", Thunder.name, JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_mnuUnlockTutorialsActionPerformed

    private void mnuUnlockInfosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUnlockInfosActionPerformed
        for (int i = 0 ; i < save.eep.tutorials.length ; i++)
            save.eep.tutorials[i] = 0x1;
        JOptionPane.showMessageDialog(null, "Set all tutorials as seen.", Thunder.name, JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_mnuUnlockInfosActionPerformed
    
    private SaveData save;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JLabel lblChecksum;
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
    private javax.swing.JLabel lblLevelVehicle;
    private javax.swing.JLabel lblPlatinum;
    private javax.swing.JLabel lblSilver;
    private javax.swing.JMenuBar mbarGeneral;
    private javax.swing.JMenuItem mnuDispose;
    private javax.swing.JMenuItem mnuOpen;
    private javax.swing.JMenuItem mnuSave;
    private javax.swing.JMenuItem mnuUnlockInfos;
    private javax.swing.JMenuItem mnuUnlockLevels;
    private javax.swing.JMenuItem mnuUnlockScientists;
    private javax.swing.JMenuItem mnuUnlockTutorials;
    private javax.swing.JMenuItem mnuUnlockVehicles;
    private javax.swing.JPanel pneGeneral;
    private javax.swing.JPanel pneLevel;
    private javax.swing.JPanel pneOther;
    private javax.swing.JSpinner spnMoney;
    private javax.swing.JMenu subFile;
    private javax.swing.JMenu subOptions;
    private javax.swing.JToolBar tbrFooter;
    private javax.swing.JTextField txtChecksum;
    private javax.swing.JTextField txtLevelTime;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPoints;
    // End of variables declaration//GEN-END:variables
}