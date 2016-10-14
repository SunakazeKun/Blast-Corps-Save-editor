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
                    scientists = new Scientists(eep.scientists);
                }
            }
        }
        catch (Exception ex) {
            fileLoaded = false;
            System.out.println("A problem occured while trying to load the file:\n\n" + ex);
        }
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
        target.put(eep.levelMedal);     // 0x18 to 0x53
        target.put(eep.levelPaths);     // 0x54 to 0x8F
        target.put(eep.scientists);     // 0x90
        target.put(eep.event);          // 0x91
        target.put(eep.levelVehicle);   // 0x92 to 0xCD
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
        target.put(eep.gameid);         // 0x1F8 to 0x1FF
        
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
        
        return !(!Arrays.equals(gameid, gameid1) && !Arrays.equals(gameid, gameid2));
    }
    
    public String getTime(int i) {
        byte[] b = ByteUtils.getBytesFromOffset(i * 4, 4, eep.levelTimes);
        float seconds = (float) ByteUtils.bytesToInt(b) / 655360;
        int minutes = (int) seconds / 60;
        seconds -= (minutes * 60);
        
        return String.format(java.util.Locale.US, "%d:%04.1f", minutes, seconds);
    }
    
    public void setTime(int i, float f) {
        // nope...
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
     
    public void makePoints() {
        bronzeMedals = 0;
        silverMedals = 0;
        goldMedals = 0;
        platinumMedals = 0;
        carrierMedals = 0;
        
        for (byte medal : eep.levelMedal) {
            switch (medal) {
                case 1: bronzeMedals++; break;
                case 2: silverMedals++; break;
                case 3: goldMedals++; break;
                case 4: platinumMedals++; break;
            }
        }
        
        for (int carrier : carrierLevels) {
            if ((eep.levelMedal[carrier] > 0x0) && (eep.levelMedal[carrier] < 0x6))
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
                eep.language = ByteUtils.hexStringToBytes("1982198219821982");
                break;
            case "German":
                eep.language = ByteUtils.hexStringToBytes("1945194519451945");
                break;
            default:
                eep.language = ByteUtils.hexStringToBytes("0000000000000000");
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
    
    public boolean fileLoaded;
    public File file, newfile;
    public EEPROM eep;
    public Scientists scientists;
    public short points, bronzeMedals, silverMedals, goldMedals, platinumMedals, carrierMedals;
    
    private final byte[] gameid1 = ByteUtils.hexStringToBytes("87569AB6CD076AEC");
    private final byte[] gameid2 = ByteUtils.hexStringToBytes("2704197125121981");
    
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