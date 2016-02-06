package org.usfirst.frc.team339.Utils;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.sun.glass.ui.Robot;
import org.usfirst.frc.team339.Hardware.Hardware;
import org.usfirst.frc.team339.HardwareInterfaces.CANNetwork;
import org.usfirst.frc.team339.HardwareInterfaces.CANObject;
import org.usfirst.frc.team339.robot.*;

/**
 * NOTE: THIS CODE IS NOT COMPLETE! THERE ARE STILL ERRORS
 */

// import org.usfirst.frc.team339.Hardware.Hardware.getStickyFaultForLim;
/**
 * Contains the method for detecting sticky faults
 * 
 * @author Daniel Resio, Becky Button, and Cole Ramos
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
        //System.out.println("The value of the testForFaults method is " + future Variables);
    }
}


/**
 * Prints a message to the driver station that tells whether or not any sticky
 * faults have been found
 */
public void testForFaults ()
{
    CANObject tempCANObject = null;
    //commented out becasue Hardware.canNetwork no longer exists
//    for (int i = 0; i < Hardware.canNetwork.canObjects.size(); i++)
//    {
//
//        //creates a new temporary CANObject to search for faults
//        tempCANObject =
//                Hardware.canNetwork.canObjects.get(i);
//
//        //Debug stuff in for loop
//        if (useDebug == true)
//        {
//            System.out.println(
//                    "The value of tempCANObject.getfault is "
//                            + tempCANObject.getFault());
//        }
//
//
//        if (tempCANObject.getFault() == true)
//        {
//            //there is a sticky fault
//            SmartDashboard.putBoolean("Sticky Fault", true);
//        }
//
//        //there is not a sticky fault
//        SmartDashboard.putBoolean("Sticky Fault", false);
//
//    }
}
}
