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

import javax.swing.UIManager;

public class Thunder {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
            System.out.println("A problem occured while trying to set 'SystemLookAndFeel':\n\n" + ex);
        }
        
        new EditorForm().setVisible(true);
        //SaveData save = new SaveData(null);
        //save.getScientists();
    }
    
    public static String name = "Blast Corps EEPROM Save Editor v1.1 - by SunakazeKun";
    public static String icon = "/res/icon.png";
}