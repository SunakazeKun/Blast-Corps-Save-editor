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

public final class GameUtil {
    private GameUtil() {}
    
    public enum GameVersion {
        NTSC_U, NTSC_J, PAL
    }
    
    public enum LanguageSetting {
        Undefined(0x0000000000000000L),
        English  (0x1982198219821982L),
        German   (0x1945194519451945L);
        
        private final long value;
        
        private LanguageSetting(long val) {
            value = val;
        }
        
        @Override
        public String toString() {
            return CommonAssets.getIndexedText("languages", ordinal());
        }
        
        public long getValue() {
            return value;
        }
        
        public static LanguageSetting valueOf(long val) {
            for (LanguageSetting ls : values())
                if (ls.getValue() == val)
                    return ls;
            return Undefined;
        }
    }
    
    public enum SaveType {
        Undefined(0x0000000000000000L),
        FromMenu (0x87569AB6CD076AECL),
        FromLevel(0x2704197125121981L);
        
        private final long value;
        
        private SaveType(long val) {
            value = val;
        }
        
        public long getValue() {
            return value;
        }
        
        public static SaveType valueOf(long val) {
            for (SaveType st : values())
                if (st.getValue() == val)
                    return st;
            return Undefined;
        }
    }
    
    //----------------------------------------------------------------------------------------------
    
    public static final int EEPROM512_SIZE = 512;
    public static final int NAME_LENGTH = 8;
    public static final int NUM_LEVELS = 60;
    public static final int MAX_MONEY = 84164006;
    
    public static final int DEFAULT_UNLOCKED_VEHICLES = 0x1063E;
    public static final int ALL_UNLOCKED_VEHICLES = 0x7EFFE;
    public static final int FLAG_VEHICLE_CRANE = 6;
    public static final int FLAG_VEHICLE_TRAIN = 7;
    public static final int FLAG_VEHICLE_AMERICAN_DREAM = 8;
    public static final int FLAG_VEHICLE_BOAT_1 = 11;
    public static final int FLAG_VEHICLE_POLICE_CAR = 13;
    public static final int FLAG_VEHICLE_A_TEAM_VAN = 14;
    public static final int FLAG_VEHICLE_HOTROD = 15;
    public static final int FLAG_VEHICLE_BOAT_2 = 17;
    public static final int FLAG_VEHICLE_BOAT_3 = 18;
    
    public static final int ALL_FOUND_SCIENTISTS = 0x3F;
    public static final int FLAG_SCIENTIST_IN_ARGENT_TOWERS = 0;
    public static final int FLAG_SCIENTIST_IN_IRONSTONE_MINE = 1;
    public static final int FLAG_SCIENTIST_IN_TEMPEST_CITY = 2;
    public static final int FLAG_SCIENTIST_IN_OYSTER_HARBOR = 3;
    public static final int FLAG_SCIENTIST_IN_EBONY_COAST = 4;
    public static final int FLAG_SCIENTIST_IN_GLORY_CROSSING = 5;
    
    public static final int FLAG_CONTROLS_JOYSTICK_ACCELERATION = 4;
    
    //----------------------------------------------------------------------------------------------
    
    // These levels grant extra gold medals which is essential for calculating rank points.
    public static final int[] CARRIER_LEVELS = {
        0x00, // Simian Acres
        0x01, // Angel City
        0x02, // Outland Farm
        0x03, // Blackridge Works
        0x04, // Glory Crossing
        0x05, // Shuttle Gully
        0x09, // Crystal Rift
        0x0A, // Argent Towers
        0x0C, // Diamond Sands
        0x0D, // Ebony Coast
        0x0E, // Oyster Harbor
        0x0F, // Carrick Point
        0x10, // Havoc District
        0x11, // Ironstone Mine
        0x12, // Beeton Tracks
        0x1A, // Obsidian Mile
        0x1D, // Echo Marches
        0x21, // Tempest City
        0x32, // Shuttle Clear
        0x39, // Ember Hamlet
        0x3A  // Cromlech Court
    };
    
    //----------------------------------------------------------------------------------------------
    
    // These bytes are used by several cheat options in the editors. The values are copied from one
    // array into another in the save file.
    public static final byte[] COMPLETE_ALL_MEDALS = CommonAssets.loadBytes("data/all_medals.bin");
    public static final byte[] COMPLETE_ALL_PATHS = CommonAssets.loadBytes("data/all_paths.bin");
    public static final byte[] COMPLETE_ALL_VEHICLES = CommonAssets.loadBytes("data/all_vehicles.bin");
    public static final byte[] COMPLETE_ALL_TUTORIALS = CommonAssets.loadBytes("data/all_tutorials.bin");
    
    //----------------------------------------------------------------------------------------------
    
    // Indices for each medal time type. Used for getMedalTime(...).
    public static final int MEDAL_TIME_BRONZE = 0;
    public static final int MEDAL_TIME_SILVER = 1;
    public static final int MEDAL_TIME_GOLD = 2;
    public static final int MEDAL_TIME_PLATINUM = 3;
    
    // The medal time requirements differ in each version. While the NTSC times are mostly the same
    // except for updated Platinum times, the PAL times are increased by 20% to account for that
    // version's slower frame rate.
    private static final short[] MEDAL_TIMES_NTSC_U = loadMedalTimes("NTSC_U");
    private static final short[] MEDAL_TIMES_NTSC_J = loadMedalTimes("NTSC_J");
    private static final short[] MEDAL_TIMES_PAL = loadMedalTimes("PAL");
    
    private static short[] loadMedalTimes(String gameVersion) {
        short[] medalTimes = new short[240];
        byte[] rawdata = CommonAssets.loadBytes(String.format("data/medal_times_%s.bin", gameVersion));
        
        for (int i = 0 ; i < medalTimes.length ; i++) {
            // Too lazy to use DataInputStream
            short mil = (short)(((rawdata[i * 2] & 0xFF) << 8) | rawdata[i * 2 + 1] & 0xFF);
            medalTimes[i] = mil;
        }
        
        return medalTimes;
    }
    
    public static int getMedalTime(GameVersion gameVersion, int level, int type) {
        int index = level * 4 + type;
        
        switch(gameVersion) {
            case NTSC_J: return MEDAL_TIMES_NTSC_J[index];
            case NTSC_U: return MEDAL_TIMES_NTSC_U[index];
            case PAL: return MEDAL_TIMES_PAL[index];
        }
        
        return 0;
    }
    
    // Shuttle Island, CMO Intro and End Sequence are oddballs. For some reason, it is not possible
    // for these levels to have saved clear times. However, storing medals, used vehicles and un-
    // locked paths all work... What's more strange is that all of these levels have proper medal
    // clear times defined. Anyway, if those levels were to have saved clear times, the game would
    // detect the save data as corrupted or invalid.
    public static final boolean isNonTimedLevel(int level) {
        return level == 0x26 || level == 0x2F || level == 0x31;
    }
    
    //----------------------------------------------------------------------------------------------
    
    // Order is BC -> ASCII
    private static final int[] CHARMAP = {
        0x23, 0x3E, // CUR -> >
        0x26, 0x3B, // SPC -> ;
        0x2A, 0x26, // GLI -> &
        0x61, 0x22, // a -> "
        0x62, 0x23, // b -> #
        0x64, 0x2A, // d -> *
        0x65, 0x2B, // e -> +
        0x6B, 0x3D, // k -> =
        0x6D, 0x40, // m -> @
        0x7F, 0x3C  // DEL -> <
    };
    
    // Luckily, we were able to fit all available chars inside the specified range. All unused chars
    // in those bounds were replaced with other special characters that are available in the game,
    // as seen in the charmap above.
    public static boolean isAllowedChar(char ch) {
        return ' ' <= ch && ch <= 'Z';
    }
    
    public static String unpackName(byte[] rawname) {
        StringBuilder sb = new StringBuilder(NAME_LENGTH);
        
        for (int i = 0 ; i < NAME_LENGTH ; i++) {
            char ch = (char)(rawname[i] & 0xFF);
            
            // terminate string
            if (ch == 0)
                break;
            else {
                // ASCII-fy the characters
                for (int j = 0 ; j < CHARMAP.length ; j += 2) {
                    if (CHARMAP[j] == ch) {
                        ch = (char)CHARMAP[j + 1];
                        break;
                    }
                }
                
                sb.append(isAllowedChar(ch) ? ch : ' ');
            }
        }
        
        return sb.toString();
    }
    
    public static byte[] packName(String name) {
        byte[] rawname = new byte[NAME_LENGTH];
        
        for (int i = 0 ; i < Math.min(NAME_LENGTH, name.length()) ; i++) {
            char ch = name.charAt(i);
            
            if (isAllowedChar(ch)) {
                // BC-fy the character
                for (int j = 0 ; j < CHARMAP.length ; j += 2) {
                    if (CHARMAP[j + 1] == ch) {
                        ch = (char)CHARMAP[j];
                        break;
                    }
                }
                
                rawname[i] = (byte)ch;
            }
            // illegal chars are replaced with spaces
            else
                rawname[i] = 0x20;
        }
        
        return rawname;
    }
    
    //----------------------------------------------------------------------------------------------
    
    public static void validateSaveBlock(byte[] data, int size) {
        byte[] buf = new byte[4];
        int blocks = (size + 0x7F) >> 7;
        
        // Clear old checksum
        for (int i = 0; i < 4; i++)
            data[size - 4 + i] = 0x00;
        
        // Calculate checksum
        if (blocks > 0) {
            for (int i = 0; i < blocks; i++) {
                for (int j = 0; j < 4; j++) {
                    int o = (i << 7) + (j << 5);
                    if (o < size) {
                        buf[j] += (calcChecksumByte(data, o) & 0xFF);
                    }
                }
            }
        }
        
        // Write new checksum
        System.arraycopy(buf, 0, data, size - 4, 4);
    }
    
    private static byte calcChecksumByte(byte[] data, int offset) {
        byte sum = 0;
        byte key;
        
        for (int i = 0; i < 33; i++) {
            for (int j = 7; j >= 0; j--) {
                key = (sum & 0x80) != 0 ? (byte)0x85 : 0x0;
                sum <<= 1;
                
                if (i == 0x20)
                    sum &= 0xFF;
                else {
                    byte mask = (byte)(data[i + offset] & (1 << j));
                    sum |= mask != 0 ? 1 : 0;
                }
                
                sum = (byte)(sum ^ key);
            }
        }
        return sum;
    }
}
