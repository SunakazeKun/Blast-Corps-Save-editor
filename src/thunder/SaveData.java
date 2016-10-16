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

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class SaveData {
    
    public SaveData(File infile) {
        try {
            file = infile;
            
            if (file.length() == 512) {
                if (isBlastCorps()) {
                    fileLoaded = true;
                    eep = new EEPROM(file);
                }
            }
        }
        catch (Exception ex) {
            fileLoaded = false;
            System.out.println("A problem occured while trying to load the file:\n\n" + ex);
        }
    }
    
    public SaveData() {
        fileLoaded = false;
    }
    
    public void saveFile(File newfile) {
        this.newfile = newfile;
        
        byte[] data = new byte[512];
        ByteBuffer target = ByteBuffer.wrap(data);
        
        target.put(eep.name);           // 0x0 to 0x7
        target.put(eep.selectedLevel);  // 0x8
        target.put(eep.unk9);           // 0x9
        target.put(eep.points);         // 0xA to 0xB
        target.put(eep.rank);           // 0xC
        target.put(eep.unkD);           // 0xD to 0xF
        target.put(eep.vehicles);       // 0x10 to 0x13
        target.put(eep.money);          // 0x14 to 0x17
        target.put(eep.levelMedals);     // 0x18 to 0x53
        target.put(eep.levelPaths);     // 0x54 to 0x8F
        target.put(eep.scientists);     // 0x90
        target.put(eep.event);          // 0x91
        target.put(eep.levelVehicles);   // 0x92 to 0xCD
        target.put(eep.tutorials);      // 0xCE to 0xDF
        target.put(eep.unkE0);          // 0xE0 to 0xED
        target.put(eep.cutscenes);      // 0xEE
        target.put(eep.unkEF);          // 0xEF to F2
        target.put(eep.controlmode);    // 0xF3
        target.put(eep.unkF4);          // 0xF4 to 0xFB
        eep.checksum = computeChecksum(data, 0x100);
        target.put(eep.checksum);       // 0xFC to 0xFF
        target.put(eep.levelTimes);     // 0x100 to 0x1EF
        target.put(eep.language);       // 0x1F0 to 0x1F7
        target.put(eep.saveType);         // 0x1F8 to 0x1FF
        
        try  {
            FileOutputStream out = new FileOutputStream(this.newfile);
            out.write(data);
            out.flush();
            out.close();
        }
        catch (Exception ex) {
            System.out.println("A problem occured while trying to save the file:\n\n" + ex);
        }
    }
    
    public byte[] computeChecksum(byte[] data, int offset) {
        byte[] buf = new byte[4];
        int blocks = (offset + 0x7F) >> 7;
        for (int i = 0; i < 4; i++) {
            buf[i] = 0x00;
            data[offset - 4 + i] = 0x00;
        }
        if (blocks > 0) {
            for (int i = 0; i < blocks; i++) {
                for (int j = 0; j < 4; j++) {
                    int o = (i << 7) + (j << 5);
                    if (o < offset) {
                        byte v0 = computeChecksumByte(data, o);
                        buf[j] += (v0 & 0xFF);
                    }
                }
            }
        }
        return buf;
    }
    
    private byte computeChecksumByte(byte[] data, int offset) {
        byte sum = 0;
        byte key;
        for (int i = 0; i < 33; i++) {
            for (int j = 7; j >= 0; j--) {
                if ((sum & 0x80) != 0)
                    key = (byte)0x85;
                else
                    key = 0x0;
                sum <<= 1;
                if (i == 0x20) {
                    sum &= 0xFF;
                }
                else {
                    byte mask = (byte)(data[i + offset] & (1 << j));
                    byte bit;
                    if (mask != 0)
                        bit = 1;
                    else
                        bit = 0;
                    sum |= bit;
                }
            sum = (byte)(sum ^ key);
            }
        }
        return sum;
    }
    
    public boolean isBlastCorps() throws Exception {
        byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        byte[] gameid = ByteUtils.getBytesFromOffset(0x1F8, 0x8, data);
        
        return !(!Arrays.equals(gameid, S_T_FROM_MENU) && !Arrays.equals(gameid, S_T_FROM_LEVEL_END));
    }
    
    public void makePoints() {
        bronzeMedals = 0;
        silverMedals = 0;
        goldMedals = 0;
        platinumMedals = 0;
        carrierMedals = 0;
        
        for (byte medal : eep.levelMedals) {
            switch (medal) {
                case 1: bronzeMedals++; break;
                case 2: silverMedals++; break;
                case 3: goldMedals++; break;
                case 4: platinumMedals++; break;
            }
        }
        
        for (int carrier : carrierLevels) {
            if ((eep.levelMedals[carrier] > 0x0) && (eep.levelMedals[carrier] < 0x6))
                carrierMedals++;
        }
        
        goldMedals += carrierMedals;
        points = (short) (bronzeMedals + (silverMedals * 2) + (goldMedals * 3) + (platinumMedals * 4));
        
        if (eep.event >= 0xB)
            points += carrierMedals * 3;
        
        if (platinumMedals == 57)
            points += 6;
    }
    
    public void makeRank() {
        int i = points / 12;
        if (i > 30)
            eep.rank = 0x1E;
        else
            eep.rank = (byte) i;
    }
    
    public int[] getTime(int level) {
        byte[] b = ByteUtils.getBytesFromOffset(level * 4, 4, eep.levelTimes);
        float f = (float) ByteUtils.bytesToInt(b) / 655360f;
        
        int minutes = (int) f / 60;
        int seconds = (int) f - (minutes * 60);
        int milliseconds = (int) ((f - (int) f) * 10);
        
        int[] time = {minutes, seconds, milliseconds};
        return time;
    }
    
    public void setTime(int level, int minutes, int seconds, int milliseconds) {
        float f = (minutes * 60f + seconds + milliseconds / 10f + 0.033f) * 655360f;
        byte[] b = ByteUtils.intToBytes((int)f);
        for (int i = 0 ; i < 4 ; i++)
            eep.levelTimes[level * 4 + i] = b[i];
    }
    
    public String getName() {
        String s = new String(eep.name).split("\u0000")[0];
        s = s.replace("#","[0x23]");
        s = s.replace("&","[0x26]");
        s = s.replace("*","[0x2A]");
        s = s.replace(String.valueOf((char)0x7F),"[0x7F]");
        s = s.replace((char)0x61,'"');
        s = s.replace((char)0x62,'#');
        s = s.replace((char)0x64,'*');
        s = s.replace((char)0x65,'+');
        s = s.replace((char)0x6B,'=');
        s = s.replace((char)0x6D,'@');
        return s;
    }
    
    public void setName(String s) {
        s = s.replace('"',(char)0x61);
        s = s.replace('#',(char)0x62);
        s = s.replace('*',(char)0x64);
        s = s.replace('+',(char)0x65);
        s = s.replace('=',(char)0x6B);
        s = s.replace('@',(char)0x6D);
        s = s.replace("[0x23]","#");
        s = s.replace("[0x26]","&");
        s = s.replace("[0x2A]","*");
        s = s.replace("[0x7F]",String.valueOf((char)0x7F));
        
        s = s.replace("[","");
        s = s.replace("x","");
        s = s.replace("]","");
        
        if (s.length() < 8) {
            String sf = new String(new char[8 - s.length()]).replace("\0", "\u0000");
            s += sf;
        }
        else if (s.length() >= 8)
            s = s.substring(0, Math.min(s.length(), 7)) + "\u0000";
        
        eep.name = s.getBytes();
    }
    
    public void getScientists() {
        int mask = eep.scientists;
        hasArgentTowers = ((mask & S_ARGENT_TOWERS) != 0);
        hasIronstoneMine = ((mask & S_IRONSTONE_MINE) != 0);
        hasTempestCity = ((mask & S_TEMPEST_CITY) != 0);
        hasOysterHarbor = ((mask & S_OYSTER_HARBOR) != 0);
        hasEbonyCoast = ((mask & S_EBONY_COAST) != 0);
        hasGloryCrossing = ((mask & S_GLORY_CROSSING) != 0);
    }
    
    public void setScientists(boolean[] b) {
        hasArgentTowers = b[0];
        hasIronstoneMine = b[1];
        hasTempestCity = b[2];
        hasOysterHarbor = b[3];
        hasEbonyCoast = b[4];
        hasGloryCrossing = b[5];
        
        int mask = 0;
        if (hasArgentTowers) mask ^= S_ARGENT_TOWERS;
        if (hasIronstoneMine) mask ^= S_IRONSTONE_MINE;
        if (hasTempestCity) mask ^= S_TEMPEST_CITY;
        if (hasOysterHarbor) mask ^= S_OYSTER_HARBOR;
        if (hasEbonyCoast) mask ^= S_EBONY_COAST;
        if (hasGloryCrossing) mask ^= S_GLORY_CROSSING;
        
        eep.scientists = (byte) mask;
    }
    
    public void getVehicles() {
        int mask = ByteUtils.bytesToInt(eep.vehicles);
        hasAmericanDream = ((mask & V_AMERICAN_DREAM) != 0);
        hasPoliceCar = ((mask & V_POLICE_CAR) != 0);
        hasATeamVan = ((mask & V_A_TEAM_VAN) != 0);
        hasHotrod = ((mask & V_HOTROD) != 0);
        
        hasCrane = ((mask & V_CRANE) != 0);
        hasTrain = ((mask & V_TRAIN) != 0);
        hasBoat1 = ((mask & V_BOAT_1) != 0);
        hasBoat2 = ((mask & V_BOAT_2) != 0);
        hasBoat3 = ((mask & V_BOAT_3) != 0);
    }
    
    public void setVehicles(boolean[] b) {
        hasAmericanDream = b[0];
        hasPoliceCar = b[1];
        hasATeamVan = b[2];
        hasHotrod = b[3];
        
        int mask = 67134;
        if (hasAmericanDream) mask ^= V_AMERICAN_DREAM;
        if (hasPoliceCar) mask ^= V_POLICE_CAR;
        if (hasATeamVan) mask ^= V_A_TEAM_VAN;
        if (hasHotrod) mask ^= V_HOTROD;
        if (hasCrane) mask ^= V_CRANE;
        if (hasTrain) mask ^= V_TRAIN;
        if (hasBoat1) mask ^= V_BOAT_1;
        if (hasBoat2) mask ^= V_BOAT_2;
        if (hasBoat3) mask ^= V_BOAT_3;
        
        eep.vehicles = ByteUtils.intToBytes(mask);
    }
    
    public String getLang() {
        switch (eep.language[1]) {
            case (byte)0x82:
                return "English";
            case (byte)0x45:
                return "German";
            default:
                return "Default";
        }
    }
    
    public void setLang(String lang) {
        switch (lang) {
            case "English":
                eep.language = L_ENGLISH;
                break;
            case "German":
                eep.language = L_GERMAN;
                break;
            default:
                eep.language = L_DEFAULT;
                break;
        }
    }
    
    public int getControlMode() {
        if (eep.controlmode < 0x10)
            return 0;
        else
            return 1;
    }
    
    public void setControlMode(int i) {
        if (i == 1)
            eep.controlmode = 0x10;
        else
            eep.controlmode = 0x0;
    }
    
    public boolean fileLoaded = false;
    public File file, newfile;
    public EEPROM eep;
    
    public short points, bronzeMedals, silverMedals, goldMedals, platinumMedals, carrierMedals;
    
    public boolean hasAmericanDream, hasPoliceCar, hasATeamVan, hasHotrod, hasCrane, hasTrain, hasBoat1, hasBoat2, hasBoat3 = false;
    public boolean hasArgentTowers, hasIronstoneMine, hasTempestCity, hasOysterHarbor, hasEbonyCoast, hasGloryCrossing = false;
    
    private final int S_ARGENT_TOWERS = 1;
    private final int S_IRONSTONE_MINE = 2;
    private final int S_TEMPEST_CITY = 4;
    private final int S_OYSTER_HARBOR = 8;
    private final int S_EBONY_COAST = 16;
    private final int S_GLORY_CROSSING = 32;
    
    private final int V_AMERICAN_DREAM = 256;
    private final int V_POLICE_CAR = 8192;
    private final int V_A_TEAM_VAN = 16384;
    private final int V_HOTROD = 32768;
    private final int V_TRAIN = 128;
    private final int V_CRANE = 64;
    private final int V_BOAT_1 = 2048;
    private final int V_BOAT_2 = 131072;
    private final int V_BOAT_3 = 262144;
    
    private final byte[] L_DEFAULT = ByteUtils.hexStringToBytes("0000000000000000");
    private final byte[] L_ENGLISH = ByteUtils.hexStringToBytes("1982198219821982");
    private final byte[] L_GERMAN = ByteUtils.hexStringToBytes("1945194519451945");
    
    private final byte[] S_T_FROM_MENU = ByteUtils.hexStringToBytes("87569AB6CD076AEC");
    private final byte[] S_T_FROM_LEVEL_END = ByteUtils.hexStringToBytes("2704197125121981");
    
    private final ArrayList<Integer> carrierLevels = new ArrayList<Integer>() {{
        add(0x0);   // Simian Acres
        add(0x1);   // Angel City
        add(0x2);   // Outland Farm
        add(0x3);   // Blackridge Works
        add(0x4);   // Glory Crossing
        add(0x5);   // Shuttle Gully
        add(0x9);   // Crystal Rift
        add(0xA);   // Argent Towers
        add(0xC);   // Diamond Sands
        add(0xD);   // Ebony Coast
        add(0xE);   // Oyster Harbor
        add(0xF);   // Carrick Point
        add(0x10);  // Havoc District
        add(0x11);  // Ironstone Mine
        add(0x12);  // Beeton Tracks
        add(0x1A);  // Obsidian Mile
        add(0x1D);  // Echo Marches
        add(0x21);  // Tempest City
        //add(0x26);  // Shuttle Island
        //add(0x2F);  // CMO Intro
        //add(0x31);  // End Sequence
        add(0x32);  // Shuttle Clear
        add(0x39);  // Ember Hamlet
        add(0x3A);  // Cromlech Court
    }};
}