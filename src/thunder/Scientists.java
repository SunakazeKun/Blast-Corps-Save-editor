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

public class Scientists {
    public Scientists(boolean scien1, boolean scien2, boolean scien3, boolean scien4, boolean scien5, boolean scien6) {
        hasArgentTowers = scien1;
        hasIronstoneMine = scien2;
        hasTempestCity = scien3;
        hasOysterHarbor = scien4;
        hasEbonyCoast = scien5;
        hasGloryCrossing = scien6;
        
        this.mask = 0;
        if (hasArgentTowers) mask = mask ^ ARGENT_TOWERS;
        if (hasIronstoneMine) mask = mask ^ IRONSTONE_MINE;
        if (hasTempestCity) mask = mask ^ TEMPEST_CITY;
        if (hasOysterHarbor) mask = mask ^ OYSTER_HARBOR;
        if (hasEbonyCoast) mask = mask ^ EBONY_COAST;
        if (hasGloryCrossing) mask = mask ^ GLORY_CROSSING;
    }
    
    public Scientists(int mask) {
        this.mask = mask;
        hasArgentTowers = ((mask & ARGENT_TOWERS) != 0);
        hasIronstoneMine = ((mask & IRONSTONE_MINE) != 0);
        hasTempestCity = ((mask & TEMPEST_CITY) != 0);
        hasOysterHarbor = ((mask & OYSTER_HARBOR) != 0);
        hasEbonyCoast = ((mask & EBONY_COAST) != 0);
        hasGloryCrossing = ((mask & GLORY_CROSSING) != 0);
    }
    
    public int mask;
    public boolean hasArgentTowers, hasIronstoneMine, hasTempestCity, hasOysterHarbor, hasEbonyCoast, hasGloryCrossing = false;
    
    private final int ARGENT_TOWERS = 1;      // 000001
    private final int IRONSTONE_MINE = 2;     // 000010
    private final int TEMPEST_CITY = 4;       // 000100
    private final int OYSTER_HARBOR = 8;      // 001000
    private final int EBONY_COAST = 16;       // 010000
    private final int GLORY_CROSSING = 32;    // 100000
}