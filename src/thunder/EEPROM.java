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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package thunder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EEPROM {
    public EEPROM(File file) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        
        // general
        name = ByteUtils.getBytesFromOffset(0x0, 0x8, data);
        money = ByteUtils.getBytesFromOffset(0x14, 0x4, data);
        event = data[0x91];
        points = ByteUtils.getBytesFromOffset(0xA, 0x2, data);
        rank = data[0xC];
        selectedLevel = data[0x8];
        scientists = data[0x90];
        vehicles = ByteUtils.getBytesFromOffset(0x10, 0x4, data);
        
        // level
        levelMedal = ByteUtils.getBytesFromOffset(0x18, 0x3C, data);
        levelPaths = ByteUtils.getBytesFromOffset(0x54, 0x3C, data);
        levelVehicle = ByteUtils.getBytesFromOffset(0x92, 0x3C, data);
        levelTimes = ByteUtils.getBytesFromOffset(0x100, 0xF0, data);
        
        // other
        controlmode = data[0xF3];
        cutscenes = data[0xEE];
        tutorials = ByteUtils.getBytesFromOffset(0xCE, 0x12, data);
        language = ByteUtils.getBytesFromOffset(0x1F0, 0x8, data);
        checksum = ByteUtils.getBytesFromOffset(0xFC,0x4, data);
        gameid = ByteUtils.getBytesFromOffset(0x1F8, 0x8, data);
        
        // unknown
        unk9 = data[0x9];
        unkD = ByteUtils.getBytesFromOffset(0xD, 0x3, data);
        unkE0 = ByteUtils.getBytesFromOffset(0xE0, 0xE, data);
        unkEF = ByteUtils.getBytesFromOffset(0xEF, 0x4, data);
        unkF4 = ByteUtils.getBytesFromOffset(0xF4, 0x8, data);
    }
    
    public byte[] name, money, points, vehicles;
    public byte[] levelMedal, levelPaths, levelVehicle, levelTimes;
    public byte[] tutorials, language, checksum, gameid;
    public byte[] unkD, unkE0, unkEF, unkF4;
    public byte unk9;
    public byte rank, event, scientists, selectedLevel, cutscenes, controlmode;
}