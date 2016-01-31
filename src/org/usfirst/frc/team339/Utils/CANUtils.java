package org.usfirst.frc.team339.Utils;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.HardwareInterfaces.CANNetwork;
import org.usfirst.frc.team339.HardwareInterfaces.CANObject;

/**
 * Contains the method for detecting sticky faults
 */
public class CANUtils
{
/**
 * Prints a message to the driver station that tells whether or not any sticky
 * faults have been found
 */
public static void testForFaults ()
{
    for (int i = 0; i < CANNetwork.canObjects.size(); i++)
    {
        //creates a new temporary CANObject to search for faults
        CANObject tempCANObject =
                CANNetwork.canObjects.get(i);
        
        
        if (tempCANObject.getFault() == true)
        {
            //there is a sticky fault
            SmartDashboard.putBoolean("Sticky Fault", true);
        }
    }
    //there is not a sticky fault
    SmartDashboard.putBoolean("Sticky Fault", false);
}
}

