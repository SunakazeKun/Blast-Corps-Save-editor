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
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.UIManager;

public final class BCSe {
    private BCSe() {}
    
    public static final String TITLE = "BCSe -- Blast Corps Save Editor -- © 2020 Aurum";
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
            System.err.print(ex);
        }
        
        initSettings();
        CommonAssets.initLocalization();
        
        JFrame editor = new Eeprom512Editor();
        editor.setVisible(true);
    }
    
    //----------------------------------------------------------------------------------------------
    
    public static String LOCALIZATION;
    public static GameVersion GAME_VERSION;
    
    public static void initSettings() {
        Preferences prefs = Preferences.userRoot();
        LOCALIZATION = prefs.get("bcse.localization", "en_US");
        GAME_VERSION = GameVersion.valueOf(prefs.get("bcse.game_version", "NTSC_U"));
    }
    
    public static void saveSettings() {
        Preferences prefs = Preferences.userRoot();
        prefs.put("bcse.localization", LOCALIZATION);
        prefs.put("bcse.game_version", GAME_VERSION.name());
    }
}
