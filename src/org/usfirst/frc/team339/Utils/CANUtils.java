package org.usfirst.frc.team339.Utils;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team339.HardwareInterfaces.CANNetwork;
import org.usfirst.frc.team339.HardwareInterfaces.CANObject;


// import org.usfirst.frc.team339.Hardware.Hardware.getStickyFaultForLim;
/**
 * Contains the method for detecting sticky faults
 */
public class CANUtils
{

//If true, activates debug print statements throughout the class
private static boolean useDebug = true;

/**
 * Prints values of the significant variables/methods in CANUtils if useDebug is
 * true; currently is empty;other debuggers for variables
 * that are declared in specific methods in those methods
 */
private void DebugCANUtils ()
{
    //If true, runs DebugCANUtils

    if (useDebug == true)
    {
        //TODO print variables
        //System.out.println("The value of the testForFaults method is " 
    }
}


/**
 * Prints a message to the driver station that tells whether or not any sticky
 * faults have been found
 */
public static void testForFaults ()
{

    for (int i = 0; i < CANNetwork.canObjects.size(); i++)

        if (org.usfirst.frc.team339.HardwareInterfaces.CANObject
                .getFault() == false)
        {


            //creates a new temporary CANObject to search for faults
            CANObject tempCANObject =
                    CANNetwork.canObjects.get(i);

            //Debug stuff in for loop
            if (useDebug == true)
            {
                System.out.println(
                        "The value of tempCANObject.getfault is "
                                + CANObject.getFault());
            }


            if (CANObject.getFault() == true)
            {
                //there is a sticky fault
                SmartDashboard.putBoolean("Sticky Fault", true);
            }
        }
    //there is not a sticky fault
    SmartDashboard.putBoolean("Sticky Fault", false);


}
}
