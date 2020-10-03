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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class CommonAssets {
    private CommonAssets() {}
    
    public static final BufferedImage PROGRAM_ICON = loadImage("icon.png");
    
    public static InputStream openStream(String path) throws IOException {
        return CommonAssets.class.getResourceAsStream("/assets/" + path);
    }
    
    public static byte[] loadBytes(String path) {
        byte[] ret;
        
        try (InputStream in = openStream(path)) {
            ret = new byte[in.available()];
            in.read(ret);
        }
        catch(IOException ex) {
            ret = new byte[0];
            System.err.print(ex);
        }
        
        return ret;
    }
    
    public static BufferedImage loadImage(String path) {
        BufferedImage ret = null;
        
        try (InputStream in = openStream(path)) {
            return ImageIO.read(in);
        }
        catch(IOException ex) {
            System.err.print(ex);
        }
        
        return ret;
    }
    
    public static ImageIcon loadIcon(String path) {
        return new ImageIcon(loadImage(path));
    }
    
    //----------------------------------------------------------------------------------------------
    
    private static JSONObject LOCALIZATION;
    
    public static void initLocalization() {
        try {
            String path = String.format("text/%s.json", BCSe.LOCALIZATION);
            LOCALIZATION = (JSONObject)new JSONParser().parse(new InputStreamReader(openStream(path)));
        }
        catch (IOException | ParseException ex) {
            System.err.println(ex);
        }
    }
    
    public static String getText(String key) {
        if (LOCALIZATION != null && LOCALIZATION.containsKey(key))
            return LOCALIZATION.get(key).toString();
        return key;
    }
    
    public static JSONArray getTextList(String key) {
        if (LOCALIZATION != null && LOCALIZATION.containsKey(key))
            return (JSONArray)LOCALIZATION.get(key);
        return null;
    }
    
    public static String getIndexedText(String key, int index) {
        JSONArray list = getTextList(key);
        if (list != null && index < list.size())
            return list.get(index).toString();
        return String.format("%s.%d", key, index);
    }
    
    //----------------------------------------------------------------------------------------------
    
    public static final ImageIcon ICON_DEFAULT = loadIcon("img/no_image.png");
    public static final ImageIcon ICON_MENU_CLEAR_TIME = loadIcon("img/editor/clear_time.png");
    public static final ImageIcon ICON_MENU_COMM_POINT = loadIcon("img/editor/communication_point.png");
    public static final ImageIcon ICON_MENU_LAST_LEVEL = loadIcon("img/editor/last_level.png");
    public static final ImageIcon[] ICONS_VEHICLES = {
        loadIcon("img/vehicles/carrier.png"),
        loadIcon("img/vehicles/sideswipe.png"),
        loadIcon("img/vehicles/thunderfist.png"),
        loadIcon("img/vehicles/skyfall.png"),
        loadIcon("img/vehicles/ramdozer.png"),
        loadIcon("img/vehicles/backlash.png"),
        null,
        null,
        loadIcon("img/vehicles/american_dream.png"),
        loadIcon("img/vehicles/j_bomb.png"),
        loadIcon("img/vehicles/ballista.png"),
        null,
        null,
        loadIcon("img/vehicles/police_car.png"),
        loadIcon("img/vehicles/a_team_van.png"),
        loadIcon("img/vehicles/hotrod.png"),
        loadIcon("img/vehicles/cyclone_suit.png"),
        null,
        null
    };
    public static final ImageIcon[] ICONS_MEDALS = {
        ICON_DEFAULT,
        loadIcon("img/medals/bronze.png"),
        loadIcon("img/medals/silver.png"),
        loadIcon("img/medals/gold.png"),
        loadIcon("img/medals/platinum.png"),
        loadIcon("img/medals/blank.png"),
        null,
        null,
        loadIcon("img/medals/blank.png")
    };
    public static final ImageIcon[] IMGS_UNLOCKED_AMERICAN_DREAM = {
        loadIcon("img/editor/american_dream_false.png"),
        loadIcon("img/editor/american_dream_true.png")
    };
    public static final ImageIcon[] IMGS_UNLOCKED_POLICE_CAR = {
        loadIcon("img/editor/police_car_false.png"),
        loadIcon("img/editor/police_car_true.png")
    };
    public static final ImageIcon[] IMGS_UNLOCKED_A_TEAM_VAN = {
        loadIcon("img/editor/a_team_van_false.png"),
        loadIcon("img/editor/a_team_van_true.png")
    };
    public static final ImageIcon[] IMGS_UNLOCKED_HOTROD = {
        loadIcon("img/editor/hotrod_false.png"),
        loadIcon("img/editor/hotrod_true.png")
    };
    
    //----------------------------------------------------------------------------------------------
    
    public static final int[] ORDERED_LEVELS = {
        0x00, // Simian Acres
        0x03, // Blackridge Works
        0x0A, // Argent Towers
        0x10, // Havoc District
        0x0F, // Carrick Point
        0x21, // Tempest City
        0x12, // Beeton Tracks
        0x1D, // Echo Marches
        0x3A, // Cromlech Court
        0x11, // Ironstone Mine
        0x0D, // Ebony Coast
        0x02, // Outland Farm
        0x05, // Shuttle Gully
        0x04, // Glory Crossing
        0x39, // Ember Hamlet
        0x01, // Angel City
        0x0E, // Oyster Harbor
        0x0C, // Diamond Sands
        0x1A, // Obsidian Mile
        0x09, // Crystal Rift
        0x32, // Shuttle Clear
        0x26, // Shuttle Island
        0x2F, // CMO Intro
        0x31, // End Sequence
        0x37, // Backlash
        0x13, // J-Bomb
        0x1C, // Sideswipe
        0x35, // Thunderfist
        0x07, // Skyfall
        0x14, // Jade Plateau
        0x06, // Salvage Wharf
        0x22, // Orion Plaza
        0x15, // Marine Quarter
        0x16, // Cooter Creek
        0x0B, // Skerries
        0x08, // Twilight Foundry
        0x1E, // Kipling Plant
        0x19, // Sleek Streets
        0x23, // Glander's Ranch
        0x30, // Silver Junction
        0x2A, // Moraine Chase
        0x27, // Mica Park
        0x20, // Morgan Hall
        0x1B, // Corvine Bluff
        0x38, // Bison Ridge
        0x29, // Cobalt Quarry
        0x25, // Geode Square
        0x3B, // Lizard Island
        0x36, // Saline Watch
        0x24, // Dagger Pass
        0x34, // Magma Peak
        0x18, // Baboon Catacomb
        0x17, // Gibbon's Gate
        0x1F, // Falchion Field
        0x33, // Dark Heartland
        0x28, // Moon
        0x2B, // Mercury
        0x2C, // Venus
        0x2D, // Mars
        0x2E  // Neptune
    };
    
    // crane, train, boats and unkC are excluded
    public static final int[] ORDERED_VEHICLES = {
        0x00, // Default
        0x04, // Ramdozer
        0x05, // Backlash
        0x03, // Skyfall
        0x01, // Sideswipe
        0x0A, // Ballista
        0x02, // Thunderfist
        0x10, // Cyclone Suit
        0x09, // J-Bomb
        0x08, // American Dream
        0x0F, // Hotrod
        0x0D, // Police Car
        0x0E  // A-Team Van
    };
    
    // red and green are excluded
    public static final int[] ORDERED_MEDALS = {
        0x00, // locked
        0x08, // unlocked
        0x05, // finished
        0x01, // bronze
        0x02, // silver
        0x03, // gold
        0x04  // platinum
    };
    
    private static int getOrderedIndex(int[] indices, int index, int defidx) {
        for (int i = 0 ; i < indices.length ; i++)
            if (indices[i] == index)
                return i;
        return defidx;
    }
    
    public static int getOrderedLevelIndex(int index) {
        return getOrderedIndex(ORDERED_LEVELS, index, 0);
    }
    
    public static int getOrderedVehicleIndex(int index) {
        return getOrderedIndex(ORDERED_VEHICLES, index, 0);
    }
    
    public static int getOrderedMedalIndex(int index) {
        return getOrderedIndex(ORDERED_MEDALS, index, 2);
    }
}
