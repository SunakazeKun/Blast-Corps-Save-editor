// Copyright © 2020 Aurum
//
// This file is part of "BCSe"
//
// "BCSe" is free software: you can redistribute it and/or modify it under
// the terms of the GNU General Public License as published by the Free
// Software Foundation, either version 3 of the License, or (at your option)
// any later version.
//
// "BCSe" is distributed in the hope that it will be useful, but WITHOUT ANY 
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
// FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along 
// with "BCSe". If not, see http://www.gnu.org/licenses/.

package com.aurum.bcse;

import com.aurum.bcse.GameUtil.GameVersion;
import com.aurum.bcse.GameUtil.LanguageSetting;
import com.aurum.bcse.GameUtil.MedalTime;
import com.aurum.bcse.GameUtil.SaveType;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.json.simple.JSONArray;

public class Eeprom512Editor extends javax.swing.JFrame {
    private File saveFile;
    private Eeprom512Data saveData;
    private boolean timeLoaded;
    
    private final DefaultComboBoxModel modelProgress, modelLastLevel, modelLanguage;
    private final DefaultComboBoxModel modelSelectedLevel, modelMedal, modelVehicle, modelPaths;
    
    public Eeprom512Editor() {
        modelProgress = new DefaultComboBoxModel();
        modelLastLevel = new DefaultComboBoxModel();
        modelLanguage = new DefaultComboBoxModel();
        modelSelectedLevel = new DefaultComboBoxModel();
        modelMedal = new DefaultComboBoxModel();
        modelVehicle = new DefaultComboBoxModel();
        modelPaths = new DefaultComboBoxModel();
        
        initModels();
        initComponents();
        initLocalization();
        
        switch(BCSe.GAME_VERSION) {
            case NTSC_U: radNTSCU.setSelected(true); break;
            case NTSC_J: radNTSCJ.setSelected(true); break;
            case PAL: radPAL.setSelected(true); break;
        }
        
        enableComponents(false);
        
        // Japanese language is not available
        radJapanese.setVisible(false);
    }
    
    private void initModels() {
        JSONArray names = CommonAssets.getTextList("levels");
        for (int i = 0 ; i < CommonAssets.ORDERED_LEVELS.length ; i++) {
            String text = names.get(CommonAssets.ORDERED_LEVELS[i]).toString();
            modelLastLevel.addElement(text);
            modelSelectedLevel.addElement(text);
        }
        
        names = CommonAssets.getTextList("progress");
        for (int i = 0 ; i < names.size() ; i++)
            modelProgress.addElement(names.get(i).toString());
        
        for (LanguageSetting lang : LanguageSetting.values())
            modelLanguage.addElement(lang);
        
        names = CommonAssets.getTextList("medals");
        for (int i = 0 ; i < CommonAssets.ORDERED_MEDALS.length ; i++)
            modelMedal.addElement(names.get(CommonAssets.ORDERED_MEDALS[i]).toString());
        
        names = CommonAssets.getTextList("vehicles");
        for (int i = 0 ; i < CommonAssets.ORDERED_VEHICLES.length ; i++)
            modelVehicle.addElement(names.get(CommonAssets.ORDERED_VEHICLES[i]).toString());
        
        names = CommonAssets.getTextList("paths");
        for (int i = 0 ; i < names.size() ; i++)
            modelPaths.addElement(names.get(i).toString());
    }
    
    private void initLocalization() {
        switch(BCSe.LOCALIZATION) {
            case "en_US": radEnglish.setSelected(true); break;
            case "de_DE": radGerman.setSelected(true); break;
        }
        
        mnuFile.setText(CommonAssets.getText("editor.menu.file"));
        mniNew.setText(CommonAssets.getText("editor.menu.file.new"));
        mniOpen.setText(CommonAssets.getText("editor.menu.file.open"));
        mniSave.setText(CommonAssets.getText("editor.menu.file.save"));
        mniSaveAs.setText(CommonAssets.getText("editor.menu.file.save_as"));
        mniExit.setText(CommonAssets.getText("editor.menu.file.exit"));
        mnuEdit.setText(CommonAssets.getText("editor.menu.edit"));
        mniCheatAllTutorials.setText(CommonAssets.getText("editor.menu.edit.cheat_all_tutorials"));
        mniCheatAllTutorials.setToolTipText(CommonAssets.getText("editor.menu.edit.cheat_all_tutorials.tooltip"));
        mniCheatHelperVehicles.setText(CommonAssets.getText("editor.menu.edit.cheat_helper_vehicles"));
        mniCheatHelperVehicles.setToolTipText(CommonAssets.getText("editor.menu.edit.cheat_helper_vehicles.tooltip"));
        chkAccelerateJoystick.setText(CommonAssets.getText("editor.menu.edit.accelerate_joystick"));
        mniCheatComplete.setText(CommonAssets.getText("editor.menu.edit.cheat_complete"));
        mnuGameVersion.setText(CommonAssets.getText("editor.menu.game_version"));
        mnuLanguage.setText(CommonAssets.getText("editor.menu.language"));
        mnuHelp.setText(CommonAssets.getText("editor.menu.help"));
        mniAbout.setText(CommonAssets.getText("editor.menu.help.about"));
        
        ((TitledBorder)pnlGeneral.getBorder()).setTitle(CommonAssets.getText("editor.panel.general"));
        lblName.setText(CommonAssets.getText("editor.panel.general.name"));
        txtName.setToolTipText(CommonAssets.getText("editor.panel.general.name.tooltip"));
        lblMoney.setText(CommonAssets.getText("editor.panel.general.money"));
        lblPoints.setText(CommonAssets.getText("editor.panel.general.points"));
        lblRank.setText(CommonAssets.getText("editor.panel.general.rank"));
        lblProgress.setText(CommonAssets.getText("editor.panel.general.progress"));
        lblLastLevel.setText(CommonAssets.getText("editor.panel.general.last_level"));
        lblLanguage.setText(CommonAssets.getText("editor.panel.general.language"));
        
        ((TitledBorder)pnlLevel.getBorder()).setTitle(CommonAssets.getText("editor.panel.level"));
        lblSelectedLevel.setText(CommonAssets.getText("editor.panel.level.selected_level"));
        lblMedal.setText(CommonAssets.getText("editor.panel.level.medal"));
        lblVehicle.setText(CommonAssets.getText("editor.panel.level.vehicle"));
        lblPaths.setText(CommonAssets.getText("editor.panel.level.paths"));
        lblClearTime.setText(CommonAssets.getText("editor.panel.level.clear_time"));
        
        ((TitledBorder)pnlScientists.getBorder()).setTitle(CommonAssets.getText("editor.panel.scientists"));
        chkScientistArgentTowers.setText(CommonAssets.getIndexedText("levels", 10));
        chkScientistTempestCity.setText(CommonAssets.getIndexedText("levels", 33));
        chkScientistIronstoneMine.setText(CommonAssets.getIndexedText("levels", 17));
        chkScientistEbonyCoast.setText(CommonAssets.getIndexedText("levels", 13));
        chkScientistGloryCrossing.setText(CommonAssets.getIndexedText("levels", 4));
        chkScientistOysterHarbor.setText(CommonAssets.getIndexedText("levels", 14));
    }
    
    private void enableComponents(boolean state) {
        enableComponentsInContainer(state, pnlGeneral);
        enableComponentsInContainer(state, pnlLevel);
        enableComponentsInContainer(state, pnlScientists);
        mniSave.setEnabled(state);
        mniSaveAs.setEnabled(state);
        mniCheatAllTutorials.setEnabled(state);
        mniCheatComplete.setEnabled(state);
        mniCheatHelperVehicles.setEnabled(state);
        chkAccelerateJoystick.setEnabled(state);
    }
    
    private void enableComponentsInContainer(boolean val, Container parent) {
        for (Component child : parent.getComponents()) {
            if (child instanceof Container)
                enableComponentsInContainer(val, (Container)child);
            child.setEnabled(val);
        }
    }
    
    private void showMessageDialog(int dialogType, String text) {
        JOptionPane.showMessageDialog(this, CommonAssets.getText(text), BCSe.TITLE, dialogType);
    }
    
    private int showConfirmDialog(int dialogType, String text) {
        return JOptionPane.showConfirmDialog(this, CommonAssets.getText(text), BCSe.TITLE, dialogType);
    }
    
    private void populateData() {
        txtName.setText(saveData.name);
        spnMoney.setValue(saveData.money);
        txtPoints.setText(String.format("%d/360", saveData.points));
        txtRank.setText(CommonAssets.getIndexedText("ranks", saveData.rank));
        cmoProgress.setSelectedIndex(saveData.storyProgression);
        cmoLastLevel.setSelectedIndex(CommonAssets.getOrderedLevelIndex(saveData.lastLevel));
        cmoLanguage.setSelectedItem(saveData.languageSetting);
        
        chkUnlockedAmericanDream.setSelected(BitUtil.test(saveData.unlockedVehiclesBits, GameUtil.FLAG_VEHICLE_AMERICAN_DREAM));
        chkUnlockedHotrod.setSelected(BitUtil.test(saveData.unlockedVehiclesBits, GameUtil.FLAG_VEHICLE_HOTROD));
        chkUnlockedPoliceCar.setSelected(BitUtil.test(saveData.unlockedVehiclesBits, GameUtil.FLAG_VEHICLE_POLICE_CAR));
        chkUnlockedATeamVan.setSelected(BitUtil.test(saveData.unlockedVehiclesBits, GameUtil.FLAG_VEHICLE_A_TEAM_VAN));
        lblBronzeMedals.setText(Integer.toString(saveData.bronzeMedals));
        lblSilverMedals.setText(Integer.toString(saveData.silverMedals));
        lblGoldMedals.setText(Integer.toString(saveData.goldMedals));
        lblPlatinumMedals.setText(Integer.toString(saveData.platinumMedals));
        lblCarrierMedals.setText(Integer.toString(saveData.carrierMedals));
        
        // This will invoke populateLevelData()
        cmoSelectedLevel.setSelectedIndex(0);
        
        chkScientistArgentTowers.setSelected(BitUtil.test(saveData.scientistsBits, GameUtil.FLAG_SCIENTIST_IN_ARGENT_TOWERS));
        chkScientistTempestCity.setSelected(BitUtil.test(saveData.scientistsBits, GameUtil.FLAG_SCIENTIST_IN_TEMPEST_CITY));
        chkScientistIronstoneMine.setSelected(BitUtil.test(saveData.scientistsBits, GameUtil.FLAG_SCIENTIST_IN_IRONSTONE_MINE));
        chkScientistEbonyCoast.setSelected(BitUtil.test(saveData.scientistsBits, GameUtil.FLAG_SCIENTIST_IN_EBONY_COAST));
        chkScientistGloryCrossing.setSelected(BitUtil.test(saveData.scientistsBits, GameUtil.FLAG_SCIENTIST_IN_GLORY_CROSSING));
        chkScientistOysterHarbor.setSelected(BitUtil.test(saveData.scientistsBits, GameUtil.FLAG_SCIENTIST_IN_OYSTER_HARBOR));
        
        chkAccelerateJoystick.setSelected(BitUtil.test(saveData.controlsBits, GameUtil.FLAG_CONTROLS_JOYSTICK_ACCELERATION));
    }
    
    private void populateLevelData() {
        int selectedLevel = CommonAssets.ORDERED_LEVELS[cmoSelectedLevel.getSelectedIndex()];
        
        cmoMedal.setSelectedIndex(CommonAssets.getOrderedMedalIndex(saveData.levelMedals[selectedLevel]));
        cmoVehicle.setSelectedIndex(CommonAssets.getOrderedVehicleIndex(saveData.levelVehicles[selectedLevel]));
        cmoPaths.setSelectedIndex(saveData.levelPaths[selectedLevel]);
        
        timeLoaded = false;
        boolean isTimedLevel = !GameUtil.isNonTimedLevel(selectedLevel);
        
        if (isTimedLevel) {
            ClearTime clearTime = saveData.levelTimes[selectedLevel];
            spnCTMinutes.setValue(clearTime.getMinutes());
            spnCTSeconds.setValue(clearTime.getSeconds());
            spnCTMilliseconds.setValue(clearTime.getMilliseconds());
            timeLoaded = true;
        }
        else {
            spnCTMinutes.setValue(0);
            spnCTSeconds.setValue(0);
            spnCTMilliseconds.setValue(0);
        }
        
        spnCTMinutes.setEnabled(isTimedLevel);
        spnCTSeconds.setEnabled(isTimedLevel);
        spnCTMilliseconds.setEnabled(isTimedLevel);
    }
    
    final void changeMenuLanguage(String lang) {
        if (!BCSe.LOCALIZATION.equals(lang)) {
            BCSe.LOCALIZATION = lang;
            showMessageDialog(JOptionPane.INFORMATION_MESSAGE, "editor.message.language_changed");
        }
    }
    
    final boolean dropChanges() {
        boolean ret = true;
        if (saveData != null) {
            int result = showConfirmDialog(JOptionPane.YES_NO_OPTION, "editor.message.already_editing");
            ret = result == JOptionPane.YES_OPTION;
        }
        return ret;
    }
    
    final void chooseSaveFile(String title) {
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(CommonAssets.getText(title));
        fc.setFileFilter(new FileNameExtensionFilter("EEPROM (*.eep)", ".eep", "eep"));
        
        String lastdir = Preferences.userRoot().get("bcse_lastFile", null);
        if (lastdir != null)
            fc.setSelectedFile(new File(lastdir));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        
        saveFile = fc.getSelectedFile();
        if (!(saveFile.exists() && saveFile.isFile()))
            return;
        Preferences.userRoot().put("bcse_lastFile", saveFile.getPath());
    }
    
    final void loadSaveDataFromFile() {
        if (saveFile == null)
            return;
        
        saveData = null;
        timeLoaded = false;
        
        try (FileInputStream in = new FileInputStream(saveFile)) {
            int bufsize = in.available();
            
            if (bufsize == GameUtil.EEPROM512_SIZE) {
                byte[] raw = new byte[bufsize];
                in.read(raw);
                saveData = new Eeprom512Data(raw);

                if (saveData.saveType == SaveType.Undefined) {
                    showMessageDialog(JOptionPane.ERROR_MESSAGE, "editor.message.invalid_save_file");
                    saveData = null;
                }
                else
                    populateData();
            }
            else
                showMessageDialog(JOptionPane.ERROR_MESSAGE, "editor.message.invalid_save_size");
        }
        catch(IOException ex) {
            System.err.println(ex);
        }
        
        enableComponents(saveData != null);
    }
    
    final void writeSaveDataToFile() {
        if (saveFile == null)
            return;
        
        try (FileOutputStream out = new FileOutputStream(saveFile)) {
            out.write(saveData.save());
            out.flush();
        }
        catch(IOException ex) {
            System.err.println(ex);
        }
    }
    
    final void updatePointsAndRank() {
        saveData.calcPointsAndRank();
        
        txtPoints.setText(String.format("%d/360", saveData.points));
        txtRank.setText(CommonAssets.getIndexedText("ranks", saveData.rank));
        lblBronzeMedals.setText(Integer.toString(saveData.bronzeMedals));
        lblSilverMedals.setText(Integer.toString(saveData.silverMedals));
        lblGoldMedals.setText(Integer.toString(saveData.goldMedals));
        lblPlatinumMedals.setText(Integer.toString(saveData.platinumMedals));
        lblCarrierMedals.setText(Integer.toString(saveData.carrierMedals));
    }
    
    final void updateClearTime() {
        int minutes = (int)spnCTMinutes.getValue();
        int seconds = (int)spnCTSeconds.getValue();
        int milliseconds = (int)spnCTMilliseconds.getValue();
        
        if (minutes >= 54 && seconds >= 36) {
            seconds = 36;
            spnCTSeconds.setValue(36);
            
            if (milliseconds > 7) {
                milliseconds = 7;
                spnCTMilliseconds.setValue(7);
            }
        }
        
        if (timeLoaded) {
            ClearTime clearTime = saveData.levelTimes[CommonAssets.ORDERED_LEVELS[cmoSelectedLevel.getSelectedIndex()]];
            clearTime.setTime(minutes, seconds, milliseconds);
        }
    }
    
    final void cheatAllTutorials() {
        System.arraycopy(GameUtil.COMPLETE_ALL_TUTORIALS, 0, saveData.watchedTutorials, 0, GameUtil.COMPLETE_ALL_TUTORIALS.length);
        saveData.watchedCutscenesBits = 0x1F;
    }
    
    final void toggleVehiclesBit(int bit, boolean state) {
        saveData.unlockedVehiclesBits = BitUtil.toggle(saveData.unlockedVehiclesBits, bit, state);
    }
    
    final void toggleScientistsBit(int bit, boolean state) {
        saveData.scientistsBits = (byte)BitUtil.toggle(saveData.scientistsBits, bit, state);
    }
    
    final void toggleControlsBit(int bit, boolean state) {
        saveData.controlsBits = (byte)BitUtil.toggle(saveData.controlsBits, bit, state);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        rdgLanguage = new javax.swing.ButtonGroup();
        rdgGameVersion = new javax.swing.ButtonGroup();
        pnlGeneral = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        lblMoney = new javax.swing.JLabel();
        lblPoints = new javax.swing.JLabel();
        lblRank = new javax.swing.JLabel();
        lblProgress = new javax.swing.JLabel();
        lblLastLevel = new javax.swing.JLabel();
        lblLanguage = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        spnMoney = new javax.swing.JSpinner();
        txtPoints = new javax.swing.JTextField();
        txtRank = new javax.swing.JTextField();
        cmoProgress = new javax.swing.JComboBox<>();
        cmoLastLevel = new javax.swing.JComboBox<>();
        cmoLanguage = new javax.swing.JComboBox<>();
        pnlVehicles = new javax.swing.JPanel();
        chkUnlockedAmericanDream = new javax.swing.JCheckBox();
        chkUnlockedHotrod = new javax.swing.JCheckBox();
        chkUnlockedPoliceCar = new javax.swing.JCheckBox();
        chkUnlockedATeamVan = new javax.swing.JCheckBox();
        pnlStats = new javax.swing.JPanel();
        lblBronzeMedals = new javax.swing.JLabel();
        lblSilverMedals = new javax.swing.JLabel();
        lblGoldMedals = new javax.swing.JLabel();
        lblPlatinumMedals = new javax.swing.JLabel();
        lblCarrierMedals = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        pnlLevel = new javax.swing.JPanel();
        lblSelectedLevel = new javax.swing.JLabel();
        lblMedal = new javax.swing.JLabel();
        lblVehicle = new javax.swing.JLabel();
        lblPaths = new javax.swing.JLabel();
        lblClearTime = new javax.swing.JLabel();
        cmoSelectedLevel = new javax.swing.JComboBox<>();
        cmoMedal = new javax.swing.JComboBox<>();
        cmoVehicle = new javax.swing.JComboBox<>();
        cmoPaths = new javax.swing.JComboBox<>();
        pnlClearTime = new javax.swing.JPanel();
        spnCTMinutes = new javax.swing.JSpinner();
        lblClearTimeSep1 = new javax.swing.JLabel();
        spnCTSeconds = new javax.swing.JSpinner();
        lblClearTimeSep2 = new javax.swing.JLabel();
        spnCTMilliseconds = new javax.swing.JSpinner();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        pnlScientists = new javax.swing.JPanel();
        chkScientistArgentTowers = new javax.swing.JCheckBox();
        chkScientistTempestCity = new javax.swing.JCheckBox();
        chkScientistIronstoneMine = new javax.swing.JCheckBox();
        chkScientistEbonyCoast = new javax.swing.JCheckBox();
        chkScientistGloryCrossing = new javax.swing.JCheckBox();
        chkScientistOysterHarbor = new javax.swing.JCheckBox();
        mnbMain = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mniNew = new javax.swing.JMenuItem();
        mniOpen = new javax.swing.JMenuItem();
        mniSave = new javax.swing.JMenuItem();
        mniSaveAs = new javax.swing.JMenuItem();
        mspFile = new javax.swing.JPopupMenu.Separator();
        mniExit = new javax.swing.JMenuItem();
        mnuEdit = new javax.swing.JMenu();
        mniCheatAllTutorials = new javax.swing.JMenuItem();
        mniCheatHelperVehicles = new javax.swing.JMenuItem();
        mniCheatComplete = new javax.swing.JMenuItem();
        mspEdit = new javax.swing.JPopupMenu.Separator();
        chkAccelerateJoystick = new javax.swing.JCheckBoxMenuItem();
        mnuGameVersion = new javax.swing.JMenu();
        radNTSCU = new javax.swing.JRadioButtonMenuItem();
        radNTSCJ = new javax.swing.JRadioButtonMenuItem();
        radPAL = new javax.swing.JRadioButtonMenuItem();
        mnuLanguage = new javax.swing.JMenu();
        radEnglish = new javax.swing.JRadioButtonMenuItem();
        radGerman = new javax.swing.JRadioButtonMenuItem();
        radJapanese = new javax.swing.JRadioButtonMenuItem();
        mnuHelp = new javax.swing.JMenu();
        mniAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(com.aurum.bcse.BCSe.TITLE);
        setIconImage(CommonAssets.PROGRAM_ICON);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pnlGeneral.setBorder(javax.swing.BorderFactory.createTitledBorder("General"));
        pnlGeneral.setLayout(new java.awt.GridBagLayout());

        lblName.setText("Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlGeneral.add(lblName, gridBagConstraints);

        lblMoney.setText("Money");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlGeneral.add(lblMoney, gridBagConstraints);

        lblPoints.setText("Points");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlGeneral.add(lblPoints, gridBagConstraints);

        lblRank.setText("Rank");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlGeneral.add(lblRank, gridBagConstraints);

        lblProgress.setText("Progression");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlGeneral.add(lblProgress, gridBagConstraints);

        lblLastLevel.setText("Last level");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlGeneral.add(lblLastLevel, gridBagConstraints);

        lblLanguage.setText("Language");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlGeneral.add(lblLanguage, gridBagConstraints);

        txtName.setToolTipText("<html>Some characters have been assigned to different ASCII-characters:<br>\n&#59; Space (duplicate)<br>\n&#62;  Cursor<br>\n&#60;  DEL<br>\n&#38; Glitched<br>");
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNameKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlGeneral.add(txtName, gridBagConstraints);

        spnMoney.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spnMoney.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnMoneyStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlGeneral.add(spnMoney, gridBagConstraints);

        txtPoints.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlGeneral.add(txtPoints, gridBagConstraints);

        txtRank.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlGeneral.add(txtRank, gridBagConstraints);

        cmoProgress.setModel(modelProgress);
        cmoProgress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoProgressActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlGeneral.add(cmoProgress, gridBagConstraints);

        cmoLastLevel.setModel(modelLastLevel);
        cmoLastLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoLastLevelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlGeneral.add(cmoLastLevel, gridBagConstraints);

        cmoLanguage.setModel(modelLanguage);
        cmoLanguage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoLanguageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlGeneral.add(cmoLanguage, gridBagConstraints);

        chkUnlockedAmericanDream.setIcon(CommonAssets.IMGS_UNLOCKED_AMERICAN_DREAM[0]);
        chkUnlockedAmericanDream.setSelectedIcon(CommonAssets.IMGS_UNLOCKED_AMERICAN_DREAM[1]);
        chkUnlockedAmericanDream.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUnlockedAmericanDreamActionPerformed(evt);
            }
        });
        pnlVehicles.add(chkUnlockedAmericanDream);

        chkUnlockedHotrod.setIcon(CommonAssets.IMGS_UNLOCKED_HOTROD[0]);
        chkUnlockedHotrod.setSelectedIcon(CommonAssets.IMGS_UNLOCKED_HOTROD[1]);
        chkUnlockedHotrod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUnlockedHotrodActionPerformed(evt);
            }
        });
        pnlVehicles.add(chkUnlockedHotrod);

        chkUnlockedPoliceCar.setIcon(CommonAssets.IMGS_UNLOCKED_POLICE_CAR[0]);
        chkUnlockedPoliceCar.setSelectedIcon(CommonAssets.IMGS_UNLOCKED_POLICE_CAR[1]);
        chkUnlockedPoliceCar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUnlockedPoliceCarActionPerformed(evt);
            }
        });
        pnlVehicles.add(chkUnlockedPoliceCar);

        chkUnlockedATeamVan.setIcon(CommonAssets.IMGS_UNLOCKED_A_TEAM_VAN[0]);
        chkUnlockedATeamVan.setSelectedIcon(CommonAssets.IMGS_UNLOCKED_A_TEAM_VAN[1]);
        chkUnlockedATeamVan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUnlockedATeamVanActionPerformed(evt);
            }
        });
        pnlVehicles.add(chkUnlockedATeamVan);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlGeneral.add(pnlVehicles, gridBagConstraints);

        lblBronzeMedals.setIcon(CommonAssets.ICONS_MEDALS[1]);
        lblBronzeMedals.setText("0");
        pnlStats.add(lblBronzeMedals);

        lblSilverMedals.setIcon(CommonAssets.ICONS_MEDALS[2]);
        lblSilverMedals.setText("0");
        pnlStats.add(lblSilverMedals);

        lblGoldMedals.setIcon(CommonAssets.ICONS_MEDALS[3]);
        lblGoldMedals.setText("0");
        pnlStats.add(lblGoldMedals);

        lblPlatinumMedals.setIcon(CommonAssets.ICONS_MEDALS[4]);
        lblPlatinumMedals.setText("0");
        pnlStats.add(lblPlatinumMedals);

        lblCarrierMedals.setIcon(CommonAssets.ICONS_VEHICLES[0]);
        lblCarrierMedals.setText("0");
        pnlStats.add(lblCarrierMedals);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlGeneral.add(pnlStats, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlGeneral.add(filler1, gridBagConstraints);

        pnlLevel.setBorder(javax.swing.BorderFactory.createTitledBorder("Level data"));
        pnlLevel.setLayout(new java.awt.GridBagLayout());

        lblSelectedLevel.setIcon(CommonAssets.ICON_MENU_LAST_LEVEL);
        lblSelectedLevel.setText("Selected");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlLevel.add(lblSelectedLevel, gridBagConstraints);

        lblMedal.setIcon(CommonAssets.ICONS_MEDALS[0]);
        lblMedal.setText("Medal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlLevel.add(lblMedal, gridBagConstraints);

        lblVehicle.setIcon(CommonAssets.ICONS_VEHICLES[0]);
        lblVehicle.setText("Vehicle");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlLevel.add(lblVehicle, gridBagConstraints);

        lblPaths.setIcon(CommonAssets.ICON_MENU_COMM_POINT);
        lblPaths.setText("Comm. points");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlLevel.add(lblPaths, gridBagConstraints);

        lblClearTime.setIcon(CommonAssets.ICON_MENU_CLEAR_TIME);
        lblClearTime.setText("Clear time");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlLevel.add(lblClearTime, gridBagConstraints);

        cmoSelectedLevel.setModel(modelSelectedLevel);
        cmoSelectedLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoSelectedLevelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlLevel.add(cmoSelectedLevel, gridBagConstraints);

        cmoMedal.setModel(modelMedal);
        cmoMedal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoMedalActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlLevel.add(cmoMedal, gridBagConstraints);

        cmoVehicle.setModel(modelVehicle);
        cmoVehicle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoVehicleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlLevel.add(cmoVehicle, gridBagConstraints);

        cmoPaths.setModel(modelPaths);
        cmoPaths.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoPathsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlLevel.add(cmoPaths, gridBagConstraints);

        spnCTMinutes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 54, 1));
        spnCTMinutes.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnCTMinutesStateChanged(evt);
            }
        });
        pnlClearTime.add(spnCTMinutes);

        lblClearTimeSep1.setText(":");
        pnlClearTime.add(lblClearTimeSep1);

        spnCTSeconds.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spnCTSeconds.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnCTSecondsStateChanged(evt);
            }
        });
        pnlClearTime.add(spnCTSeconds);

        lblClearTimeSep2.setText(".");
        pnlClearTime.add(lblClearTimeSep2);

        spnCTMilliseconds.setModel(new javax.swing.SpinnerNumberModel(0, 0, 9, 1));
        spnCTMilliseconds.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnCTMillisecondsStateChanged(evt);
            }
        });
        pnlClearTime.add(spnCTMilliseconds);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlLevel.add(pnlClearTime, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlLevel.add(filler2, gridBagConstraints);

        pnlScientists.setBorder(javax.swing.BorderFactory.createTitledBorder("Found Scientists in"));
        pnlScientists.setLayout(new java.awt.GridBagLayout());

        chkScientistArgentTowers.setText("Argent Towers");
        chkScientistArgentTowers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScientistArgentTowersActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlScientists.add(chkScientistArgentTowers, gridBagConstraints);

        chkScientistTempestCity.setText("Tempest City");
        chkScientistTempestCity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScientistTempestCityActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlScientists.add(chkScientistTempestCity, gridBagConstraints);

        chkScientistIronstoneMine.setText("Ironstone Mine");
        chkScientistIronstoneMine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScientistIronstoneMineActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlScientists.add(chkScientistIronstoneMine, gridBagConstraints);

        chkScientistEbonyCoast.setText("Ebony Coast");
        chkScientistEbonyCoast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScientistEbonyCoastActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlScientists.add(chkScientistEbonyCoast, gridBagConstraints);

        chkScientistGloryCrossing.setText("Glory Crossing");
        chkScientistGloryCrossing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScientistGloryCrossingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlScientists.add(chkScientistGloryCrossing, gridBagConstraints);

        chkScientistOysterHarbor.setText("Oyster Harbor");
        chkScientistOysterHarbor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScientistOysterHarborActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlScientists.add(chkScientistOysterHarbor, gridBagConstraints);

        mnuFile.setMnemonic('F');
        mnuFile.setText("File");

        mniNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        mniNew.setMnemonic('N');
        mniNew.setText("New");
        mniNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniNewActionPerformed(evt);
            }
        });
        mnuFile.add(mniNew);

        mniOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mniOpen.setMnemonic('O');
        mniOpen.setText("Open");
        mniOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniOpenActionPerformed(evt);
            }
        });
        mnuFile.add(mniOpen);

        mniSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mniSave.setMnemonic('S');
        mniSave.setText("Save");
        mniSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSaveActionPerformed(evt);
            }
        });
        mnuFile.add(mniSave);

        mniSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mniSaveAs.setMnemonic('A');
        mniSaveAs.setText("Save as");
        mniSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSaveAsActionPerformed(evt);
            }
        });
        mnuFile.add(mniSaveAs);
        mnuFile.add(mspFile);

        mniExit.setMnemonic('E');
        mniExit.setText("Exit");
        mniExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniExitActionPerformed(evt);
            }
        });
        mnuFile.add(mniExit);

        mnbMain.add(mnuFile);

        mnuEdit.setMnemonic('E');
        mnuEdit.setText("Edit");

        mniCheatAllTutorials.setText("All tutorials seen");
        mniCheatAllTutorials.setToolTipText("This will mark all tutorial messages and vehicle intros as seen.");
        mniCheatAllTutorials.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniCheatAllTutorialsActionPerformed(evt);
            }
        });
        mnuEdit.add(mniCheatAllTutorials);

        mniCheatHelperVehicles.setText("Unlock helper vehicles");
        mniCheatHelperVehicles.setToolTipText("Sets the boats, crane and train as used/unlocked.");
        mniCheatHelperVehicles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniCheatHelperVehiclesActionPerformed(evt);
            }
        });
        mnuEdit.add(mniCheatHelperVehicles);

        mniCheatComplete.setText("Make perfect file");
        mniCheatComplete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniCheatCompleteActionPerformed(evt);
            }
        });
        mnuEdit.add(mniCheatComplete);
        mnuEdit.add(mspEdit);

        chkAccelerateJoystick.setSelected(true);
        chkAccelerateJoystick.setText("Accelerate with Joystick");
        chkAccelerateJoystick.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAccelerateJoystickActionPerformed(evt);
            }
        });
        mnuEdit.add(chkAccelerateJoystick);

        mnbMain.add(mnuEdit);

        mnuGameVersion.setMnemonic('V');
        mnuGameVersion.setText("Game version");

        rdgGameVersion.add(radNTSCU);
        radNTSCU.setSelected(true);
        radNTSCU.setText("NTSC-U");
        radNTSCU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radNTSCUActionPerformed(evt);
            }
        });
        mnuGameVersion.add(radNTSCU);

        rdgGameVersion.add(radNTSCJ);
        radNTSCJ.setText("NTSC-J");
        radNTSCJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radNTSCJActionPerformed(evt);
            }
        });
        mnuGameVersion.add(radNTSCJ);

        rdgGameVersion.add(radPAL);
        radPAL.setText("PAL");
        radPAL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radPALActionPerformed(evt);
            }
        });
        mnuGameVersion.add(radPAL);

        mnbMain.add(mnuGameVersion);

        mnuLanguage.setMnemonic('L');
        mnuLanguage.setText("Language");

        rdgLanguage.add(radEnglish);
        radEnglish.setSelected(true);
        radEnglish.setText("English");
        radEnglish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radEnglishActionPerformed(evt);
            }
        });
        mnuLanguage.add(radEnglish);

        rdgLanguage.add(radGerman);
        radGerman.setText("Deutsch");
        radGerman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radGermanActionPerformed(evt);
            }
        });
        mnuLanguage.add(radGerman);

        rdgLanguage.add(radJapanese);
        radJapanese.setText("日本語");
        radJapanese.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radJapaneseActionPerformed(evt);
            }
        });
        mnuLanguage.add(radJapanese);

        mnbMain.add(mnuLanguage);

        mnuHelp.setMnemonic('H');
        mnuHelp.setText("Help");

        mniAbout.setText("About");
        mniAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniAboutActionPerformed(evt);
            }
        });
        mnuHelp.add(mniAbout);

        mnbMain.add(mnuHelp);

        setJMenuBar(mnbMain);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlLevel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlScientists, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlScientists, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        BCSe.saveSettings();
    }//GEN-LAST:event_formWindowClosing

    private void mniNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniNewActionPerformed
        if (dropChanges()) {
            saveFile = null;
            saveData = new Eeprom512Data();
            populateData();
            enableComponents(true);
        }
    }//GEN-LAST:event_mniNewActionPerformed
    
    private void mniOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniOpenActionPerformed
        if (dropChanges()) {
            chooseSaveFile("editor.file_chooser.open");
            loadSaveDataFromFile();
        }
    }//GEN-LAST:event_mniOpenActionPerformed

    private void mniSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSaveActionPerformed
        if (saveFile == null)
            chooseSaveFile("editor.file_chooser.save");
        writeSaveDataToFile();
    }//GEN-LAST:event_mniSaveActionPerformed

    private void mniSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSaveAsActionPerformed
        if (saveData == null)
            return;
        chooseSaveFile("editor.file_chooser.save");
        writeSaveDataToFile();
    }//GEN-LAST:event_mniSaveAsActionPerformed

    private void mniExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniExitActionPerformed
        dispose();
    }//GEN-LAST:event_mniExitActionPerformed

    private void mniCheatAllTutorialsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCheatAllTutorialsActionPerformed
        cheatAllTutorials();
    }//GEN-LAST:event_mniCheatAllTutorialsActionPerformed

    private void mniCheatHelperVehiclesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCheatHelperVehiclesActionPerformed
        saveData.unlockedVehiclesBits |= (1 << GameUtil.FLAG_VEHICLE_CRANE);
        saveData.unlockedVehiclesBits |= (1 << GameUtil.FLAG_VEHICLE_TRAIN);
        saveData.unlockedVehiclesBits |= (1 << GameUtil.FLAG_VEHICLE_BOAT_1);
        saveData.unlockedVehiclesBits |= (1 << GameUtil.FLAG_VEHICLE_BOAT_2);
        saveData.unlockedVehiclesBits |= (1 << GameUtil.FLAG_VEHICLE_BOAT_3);
    }//GEN-LAST:event_mniCheatHelperVehiclesActionPerformed

    private void mniCheatCompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniCheatCompleteActionPerformed
        saveData.lastLevel = 0x00; // Simian Acres
        saveData.unlockedVehiclesBits = GameUtil.ALL_UNLOCKED_VEHICLES;
        saveData.money = GameUtil.MAX_MONEY;
        saveData.scientistsBits = GameUtil.ALL_FOUND_SCIENTISTS;
        saveData.storyProgression = 0xD; // All platinum medals collected
        
        for (int i = 0 ; i < GameUtil.NUM_LEVELS ; i++) {
            System.arraycopy(GameUtil.COMPLETE_ALL_MEDALS, 0, saveData.levelMedals, 0, GameUtil.NUM_LEVELS);
            System.arraycopy(GameUtil.COMPLETE_ALL_PATHS, 0, saveData.levelPaths, 0, GameUtil.NUM_LEVELS);
            System.arraycopy(GameUtil.COMPLETE_ALL_VEHICLES, 0, saveData.levelVehicles, 0, GameUtil.NUM_LEVELS);
            
            if (!GameUtil.isNonTimedLevel(i)) {
                int medalTime = GameUtil.getMedalTime(BCSe.GAME_VERSION, i, MedalTime.Platinum);
                saveData.levelTimes[i].setTime(medalTime);
            }
        }
        
        cheatAllTutorials();
        saveData.calcPointsAndRank();
        
        populateData();
    }//GEN-LAST:event_mniCheatCompleteActionPerformed

    private void radEnglishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radEnglishActionPerformed
        changeMenuLanguage("en_US");
    }//GEN-LAST:event_radEnglishActionPerformed

    private void radGermanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radGermanActionPerformed
        changeMenuLanguage("de_DE");
    }//GEN-LAST:event_radGermanActionPerformed

    private void radJapaneseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radJapaneseActionPerformed
        changeMenuLanguage("jp_JP");
    }//GEN-LAST:event_radJapaneseActionPerformed

    private void mniAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniAboutActionPerformed
        showMessageDialog(JOptionPane.INFORMATION_MESSAGE, "editor.message.about");
    }//GEN-LAST:event_mniAboutActionPerformed

    private void txtNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyTyped
        char ch = evt.getKeyChar();
        
        if ('a' <= ch && ch <= 'z') {
            ch = Character.toUpperCase(ch);
            evt.setKeyChar(ch);
        }
        
        if (ch != KeyEvent.VK_BACK_SPACE && (txtName.getText().length() >= GameUtil.NAME_LENGTH || !GameUtil.isAllowedChar(ch)))
            evt.consume();
    }//GEN-LAST:event_txtNameKeyTyped

    private void txtNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyReleased
        saveData.name = txtName.getText();
    }//GEN-LAST:event_txtNameKeyReleased

    private void spnMoneyStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnMoneyStateChanged
        saveData.money = (int)spnMoney.getValue();
    }//GEN-LAST:event_spnMoneyStateChanged

    private void cmoProgressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoProgressActionPerformed
        saveData.storyProgression = (byte)cmoProgress.getSelectedIndex();
        updatePointsAndRank();
    }//GEN-LAST:event_cmoProgressActionPerformed

    private void cmoLastLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoLastLevelActionPerformed
        saveData.lastLevel = (byte)CommonAssets.ORDERED_LEVELS[cmoLastLevel.getSelectedIndex()];
    }//GEN-LAST:event_cmoLastLevelActionPerformed

    private void cmoLanguageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoLanguageActionPerformed
        saveData.languageSetting = (LanguageSetting)cmoLanguage.getSelectedItem();
    }//GEN-LAST:event_cmoLanguageActionPerformed

    private void cmoSelectedLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoSelectedLevelActionPerformed
        populateLevelData();
    }//GEN-LAST:event_cmoSelectedLevelActionPerformed

    private void cmoMedalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoMedalActionPerformed
        int selectedLevel = CommonAssets.ORDERED_LEVELS[cmoSelectedLevel.getSelectedIndex()];
        int val = CommonAssets.ORDERED_MEDALS[cmoMedal.getSelectedIndex()];
        saveData.levelMedals[selectedLevel] = (byte)val;
        lblMedal.setIcon(CommonAssets.ICONS_MEDALS[val]);
        updatePointsAndRank();
    }//GEN-LAST:event_cmoMedalActionPerformed

    private void cmoVehicleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoVehicleActionPerformed
        int selectedLevel = CommonAssets.ORDERED_LEVELS[cmoSelectedLevel.getSelectedIndex()];
        int val = CommonAssets.ORDERED_VEHICLES[cmoVehicle.getSelectedIndex()];
        saveData.levelVehicles[selectedLevel] = (byte)val;
        lblVehicle.setIcon(CommonAssets.ICONS_VEHICLES[val]);
    }//GEN-LAST:event_cmoVehicleActionPerformed

    private void cmoPathsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoPathsActionPerformed
        int selectedLevel = CommonAssets.ORDERED_LEVELS[cmoSelectedLevel.getSelectedIndex()];
        saveData.levelPaths[selectedLevel] = (byte)cmoPaths.getSelectedIndex();
    }//GEN-LAST:event_cmoPathsActionPerformed

    private void spnCTMinutesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnCTMinutesStateChanged
        updateClearTime();
    }//GEN-LAST:event_spnCTMinutesStateChanged

    private void spnCTSecondsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnCTSecondsStateChanged
        updateClearTime();
    }//GEN-LAST:event_spnCTSecondsStateChanged

    private void spnCTMillisecondsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnCTMillisecondsStateChanged
        updateClearTime();
    }//GEN-LAST:event_spnCTMillisecondsStateChanged

    private void chkUnlockedAmericanDreamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUnlockedAmericanDreamActionPerformed
        toggleVehiclesBit(GameUtil.FLAG_VEHICLE_AMERICAN_DREAM, chkUnlockedAmericanDream.isSelected());
    }//GEN-LAST:event_chkUnlockedAmericanDreamActionPerformed

    private void chkUnlockedHotrodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUnlockedHotrodActionPerformed
        toggleVehiclesBit(GameUtil.FLAG_VEHICLE_HOTROD, chkUnlockedHotrod.isSelected());
    }//GEN-LAST:event_chkUnlockedHotrodActionPerformed

    private void chkUnlockedPoliceCarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUnlockedPoliceCarActionPerformed
        toggleVehiclesBit(GameUtil.FLAG_VEHICLE_POLICE_CAR, chkUnlockedPoliceCar.isSelected());
    }//GEN-LAST:event_chkUnlockedPoliceCarActionPerformed

    private void chkUnlockedATeamVanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUnlockedATeamVanActionPerformed
        toggleVehiclesBit(GameUtil.FLAG_VEHICLE_A_TEAM_VAN, chkUnlockedAmericanDream.isSelected());
    }//GEN-LAST:event_chkUnlockedATeamVanActionPerformed

    private void chkScientistArgentTowersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScientistArgentTowersActionPerformed
        toggleScientistsBit(GameUtil.FLAG_SCIENTIST_IN_ARGENT_TOWERS, chkScientistArgentTowers.isSelected());
    }//GEN-LAST:event_chkScientistArgentTowersActionPerformed

    private void chkScientistTempestCityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScientistTempestCityActionPerformed
        toggleScientistsBit(GameUtil.FLAG_SCIENTIST_IN_TEMPEST_CITY, chkScientistTempestCity.isSelected());
    }//GEN-LAST:event_chkScientistTempestCityActionPerformed

    private void chkScientistIronstoneMineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScientistIronstoneMineActionPerformed
        toggleScientistsBit(GameUtil.FLAG_SCIENTIST_IN_IRONSTONE_MINE, chkScientistIronstoneMine.isSelected());
    }//GEN-LAST:event_chkScientistIronstoneMineActionPerformed

    private void chkScientistEbonyCoastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScientistEbonyCoastActionPerformed
        toggleScientistsBit(GameUtil.FLAG_SCIENTIST_IN_EBONY_COAST, chkScientistEbonyCoast.isSelected());
    }//GEN-LAST:event_chkScientistEbonyCoastActionPerformed

    private void chkScientistGloryCrossingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScientistGloryCrossingActionPerformed
        toggleScientistsBit(GameUtil.FLAG_SCIENTIST_IN_GLORY_CROSSING, chkScientistGloryCrossing.isSelected());
    }//GEN-LAST:event_chkScientistGloryCrossingActionPerformed

    private void chkScientistOysterHarborActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScientistOysterHarborActionPerformed
        toggleScientistsBit(GameUtil.FLAG_SCIENTIST_IN_OYSTER_HARBOR, chkScientistOysterHarbor.isSelected());
    }//GEN-LAST:event_chkScientistOysterHarborActionPerformed

    private void chkAccelerateJoystickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAccelerateJoystickActionPerformed
        toggleControlsBit(GameUtil.FLAG_CONTROLS_JOYSTICK_ACCELERATION, chkAccelerateJoystick.isSelected());
    }//GEN-LAST:event_chkAccelerateJoystickActionPerformed

    private void radNTSCJActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radNTSCJActionPerformed
        BCSe.GAME_VERSION = GameVersion.NTSC_J;
    }//GEN-LAST:event_radNTSCJActionPerformed

    private void radNTSCUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radNTSCUActionPerformed
        BCSe.GAME_VERSION = GameVersion.NTSC_U;
    }//GEN-LAST:event_radNTSCUActionPerformed

    private void radPALActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radPALActionPerformed
        BCSe.GAME_VERSION = GameVersion.PAL;
    }//GEN-LAST:event_radPALActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem chkAccelerateJoystick;
    private javax.swing.JCheckBox chkScientistArgentTowers;
    private javax.swing.JCheckBox chkScientistEbonyCoast;
    private javax.swing.JCheckBox chkScientistGloryCrossing;
    private javax.swing.JCheckBox chkScientistIronstoneMine;
    private javax.swing.JCheckBox chkScientistOysterHarbor;
    private javax.swing.JCheckBox chkScientistTempestCity;
    private javax.swing.JCheckBox chkUnlockedATeamVan;
    private javax.swing.JCheckBox chkUnlockedAmericanDream;
    private javax.swing.JCheckBox chkUnlockedHotrod;
    private javax.swing.JCheckBox chkUnlockedPoliceCar;
    private javax.swing.JComboBox<String> cmoLanguage;
    private javax.swing.JComboBox<String> cmoLastLevel;
    private javax.swing.JComboBox<String> cmoMedal;
    private javax.swing.JComboBox<String> cmoPaths;
    private javax.swing.JComboBox<String> cmoProgress;
    private javax.swing.JComboBox<String> cmoSelectedLevel;
    private javax.swing.JComboBox<String> cmoVehicle;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel lblBronzeMedals;
    private javax.swing.JLabel lblCarrierMedals;
    private javax.swing.JLabel lblClearTime;
    private javax.swing.JLabel lblClearTimeSep1;
    private javax.swing.JLabel lblClearTimeSep2;
    private javax.swing.JLabel lblGoldMedals;
    private javax.swing.JLabel lblLanguage;
    private javax.swing.JLabel lblLastLevel;
    private javax.swing.JLabel lblMedal;
    private javax.swing.JLabel lblMoney;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPaths;
    private javax.swing.JLabel lblPlatinumMedals;
    private javax.swing.JLabel lblPoints;
    private javax.swing.JLabel lblProgress;
    private javax.swing.JLabel lblRank;
    private javax.swing.JLabel lblSelectedLevel;
    private javax.swing.JLabel lblSilverMedals;
    private javax.swing.JLabel lblVehicle;
    private javax.swing.JMenuBar mnbMain;
    private javax.swing.JMenuItem mniAbout;
    private javax.swing.JMenuItem mniCheatAllTutorials;
    private javax.swing.JMenuItem mniCheatComplete;
    private javax.swing.JMenuItem mniCheatHelperVehicles;
    private javax.swing.JMenuItem mniExit;
    private javax.swing.JMenuItem mniNew;
    private javax.swing.JMenuItem mniOpen;
    private javax.swing.JMenuItem mniSave;
    private javax.swing.JMenuItem mniSaveAs;
    private javax.swing.JMenu mnuEdit;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuGameVersion;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenu mnuLanguage;
    private javax.swing.JPopupMenu.Separator mspEdit;
    private javax.swing.JPopupMenu.Separator mspFile;
    private javax.swing.JPanel pnlClearTime;
    private javax.swing.JPanel pnlGeneral;
    private javax.swing.JPanel pnlLevel;
    private javax.swing.JPanel pnlScientists;
    private javax.swing.JPanel pnlStats;
    private javax.swing.JPanel pnlVehicles;
    private javax.swing.JRadioButtonMenuItem radEnglish;
    private javax.swing.JRadioButtonMenuItem radGerman;
    private javax.swing.JRadioButtonMenuItem radJapanese;
    private javax.swing.JRadioButtonMenuItem radNTSCJ;
    private javax.swing.JRadioButtonMenuItem radNTSCU;
    private javax.swing.JRadioButtonMenuItem radPAL;
    private javax.swing.ButtonGroup rdgGameVersion;
    private javax.swing.ButtonGroup rdgLanguage;
    private javax.swing.JSpinner spnCTMilliseconds;
    private javax.swing.JSpinner spnCTMinutes;
    private javax.swing.JSpinner spnCTSeconds;
    private javax.swing.JSpinner spnMoney;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPoints;
    private javax.swing.JTextField txtRank;
    // End of variables declaration//GEN-END:variables
}
