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

public class ClearTime {
    private int milliseconds, seconds, minutes;
    
    public ClearTime() {
        minutes = 0;
        seconds = 0;
        milliseconds = 0;
    }
    
    public ClearTime(int mil) {
        setTime(mil);
    }
    
    public int getMilliseconds() {
        return milliseconds;
    }
    
    public int getSeconds() {
        return seconds;
    }
    
    public int getMinutes() {
        return minutes;
    }
    
    public void setTime(int mil) {
        int tmpseconds = mil / 10;
        minutes = tmpseconds / 60;
        seconds = tmpseconds % 60;
        milliseconds = mil % 10;
    }
    
    public void setTime(int min, int sec, int mil) {
        if (0 > mil || mil > 9)
            throw new IllegalArgumentException("Milliseconds must be in range 0-9 inclusive.");
        milliseconds = mil;
        
        if (0 > sec || sec > 59)
            throw new IllegalArgumentException("Seconds must be in range 0-59 inclusive.");
        seconds = sec;
        
        if (0 > min || min > 54)
            throw new IllegalArgumentException("Minutes must be in range 0-54 inclusive.");
        minutes = min;
        
        // max clear time that the game can handle is 54:36.7
        if (minutes >= 54) {
            minutes = 54;
            
            if (seconds >= 36) {
                seconds = 36;
                
                if (milliseconds >= 7)
                    milliseconds = 7;
            }
        }
    }
    
    public int packTime() {
        // upper 16 bits is the time in milliseconds
        // lower 16 bits is the checksum
        int upper = milliseconds + seconds * 10 + minutes * 600;
        if (upper == 0)
            return 0;
        int lower = (upper & 0xFF00) ^ 0x55FF;
        lower ^= (upper >> 8);
        lower ^= upper & 0xFF;
        lower ^= lower >> 8;
        return (upper << 16) | lower & 0xFFFF;
    }
}
