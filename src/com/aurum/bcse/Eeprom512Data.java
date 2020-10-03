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

import com.aurum.bcse.GameUtil.LanguageSetting;
import com.aurum.bcse.GameUtil.SaveType;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.aurum.bcse.GameUtil.*;

public class Eeprom512Data extends AbstractEepromData {
    public String name;
    public byte lastLevel;
    public short points;
    public byte rank;
    public int unlockedVehiclesBits, money;
    public byte[] levelMedals, levelPaths;
    public byte scientistsBits;
    public byte storyProgression;
    public byte[] levelVehicles;
    // uncertain data block
    public byte[] watchedTutorials, unkE0;
    public byte watchedCutscenesBits;
    public byte[] unkEF;
    public byte controlsBits;
    public byte[] unkF4;
    // end of uncertain data
    public int checksum;
    public ClearTime[] levelTimes;
    public LanguageSetting languageSetting;
    public SaveType saveType;
    
    // temporary values
    public int bronzeMedals, silverMedals, goldMedals, platinumMedals, carrierMedals;
    
    public Eeprom512Data() {
        name = "NEW GAME";
        unlockedVehiclesBits = DEFAULT_UNLOCKED_VEHICLES;
        levelMedals = new byte[NUM_LEVELS];
        levelPaths = new byte[NUM_LEVELS];
        levelVehicles = new byte[NUM_LEVELS];
        watchedTutorials = new byte[18];
        unkE0 = new byte[14];
        unkEF = new byte[4];
        unkF4 = new byte[8];
        levelTimes = new ClearTime[NUM_LEVELS];
        languageSetting = LanguageSetting.Undefined;
        saveType = SaveType.FromMenu;
        
        for (int i = 0 ; i < levelTimes.length ; i++)
            levelTimes[i] = new ClearTime();
        
        buffer = ByteBuffer.allocate(GameUtil.EEPROM512_SIZE);
    }
    
    public Eeprom512Data(byte[] raw) {
        levelMedals = new byte[NUM_LEVELS];
        levelPaths = new byte[NUM_LEVELS];
        levelVehicles = new byte[NUM_LEVELS];
        watchedTutorials = new byte[18];
        unkE0 = new byte[14];
        unkEF = new byte[4];
        unkF4 = new byte[8];
        levelTimes = new ClearTime[NUM_LEVELS];
        
        load(raw);
    }
    
    @Override
    public final void load(byte[] raw) {
        if (raw.length != EEPROM512_SIZE)
            throw new IllegalArgumentException("Input size is not 512!");
        buffer = ByteBuffer.wrap(raw);
        buffer.order(ByteOrder.BIG_ENDIAN);
        
        byte[] rawname = new byte[8];
        buffer.get(rawname);
        name = GameUtil.unpackName(rawname);
        lastLevel = buffer.get();
        buffer.get(); // padding
        points = buffer.getShort();
        rank = buffer.get();
        buffer.get(); // padding
        buffer.get(); // padding
        buffer.get(); // padding
        unlockedVehiclesBits = buffer.getInt();
        money = buffer.getInt();
        
        buffer.get(levelMedals);
        buffer.get(levelPaths);
        scientistsBits = buffer.get();
        storyProgression = buffer.get();
        buffer.get(levelVehicles);
        
        buffer.get(watchedTutorials);
        buffer.get(unkE0);
        watchedCutscenesBits = buffer.get();
        buffer.get(unkEF);
        controlsBits = buffer.get();
        buffer.get(unkF4);
        checksum = buffer.getInt();
        
        for (int i = 0 ; i < levelTimes.length ; i++) {
            int millis = buffer.getInt() >>> 16; // lower 16 bits is checksum
            levelTimes[i] = GameUtil.isNonTimedLevel(i) ? null : new ClearTime(millis);
        }
        
        languageSetting = LanguageSetting.valueOf(buffer.getLong());
        saveType = SaveType.valueOf(buffer.getLong());
        
        calcPointsAndRank();
    }
    
    @Override
    public final byte[] save() {
        buffer.position(0);
        
        buffer.put(GameUtil.packName(name));
        buffer.put(lastLevel);
        buffer.put((byte)0); // padding
        buffer.putShort(points);
        buffer.put(rank);
        buffer.put((byte)0); // padding
        buffer.put((byte)0); // padding
        buffer.put((byte)0); // padding
        buffer.putInt(unlockedVehiclesBits | DEFAULT_UNLOCKED_VEHICLES);
        buffer.putInt(money);
        
        buffer.put(levelMedals);
        buffer.put(levelPaths);
        buffer.put(scientistsBits);
        buffer.put(storyProgression);
        buffer.put(levelVehicles);
        buffer.put(watchedTutorials);
        buffer.put(unkE0);
        buffer.put(watchedCutscenesBits);
        buffer.put(unkEF);
        buffer.put(controlsBits);
        buffer.put(unkF4);
        GameUtil.validateSaveBlock(buffer.array(), 0x100);
        
        buffer.position(0x100);
        for (ClearTime levelTime : levelTimes)
            buffer.putInt(levelTime == null ? 0 : levelTime.packTime());
        buffer.putLong(languageSetting.getValue());
        buffer.putLong(saveType.getValue());
        
        return buffer.array();
    }
    
    public void calcPointsAndRank() {
        // Calculate the number of claimed medals.
        bronzeMedals = 0;
        silverMedals = 0;
        goldMedals = 0;
        platinumMedals = 0;
        carrierMedals = 0;
        
        for (byte medal : levelMedals) {
            switch (medal) {
                case 1: bronzeMedals++; break;
                case 2: silverMedals++; break;
                case 3: goldMedals++; break;
                case 4: platinumMedals++; break;
            }
        }
        
        // Missile carrier levels grant extra gold medals.
        for (int carrier : CARRIER_LEVELS) {
            byte medal = levelMedals[carrier];
            
            // Level is cleared?
            if (1 <= medal && medal <= 5)
                carrierMedals++;
        }
        
        goldMedals += carrierMedals;
        
        // Calculate the actual points.
        points = (short)(bronzeMedals + silverMedals * 2 + goldMedals * 3 + platinumMedals * 4);
        
        // Depending on the progression and platinum medals, the player is awarded more points.
        if (storyProgression >= 0xB)
            points += carrierMedals * 3;
        if (storyProgression >= 0xD)
            points += 6;
        
        // A new rank is awarded every 12 points.
        rank = (byte)(points / 12);
        if (rank > 30)
            rank = 30;
    }
}
