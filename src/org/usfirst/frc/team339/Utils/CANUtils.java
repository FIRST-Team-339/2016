package org.usfirst.frc.team339.Utils;

import edu.wpi.first.wpilibj.DriverStation;
import org.usfirst.frc.team339.HardwareInterfaces.CANObject;
import edu.wpi.first.wpilibj.CANTalon;

// import org.usfirst.frc.team339.Hardware.Hardware.getStickyFaultForLim;
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
    if (org.usfirst.frc.team339.HardwareInterfaces.CANObject
            .getFault() == false)
    {
        DriverStation.reportError("No sticky faults have been detected",
                false);

    }
    else
    {
        DriverStation.reportError("A sticky fault has been detected",
                false);
    }
 
}
}
