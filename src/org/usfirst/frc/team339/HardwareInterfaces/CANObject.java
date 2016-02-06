package org.usfirst.frc.team339.HardwareInterfaces;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * NOTE: THIS CODE IS NOT COMPLETE! THERE ARE STILL ERRORS
 */
//TODO Create new imports for solenoids

/*
 * This is a wrapper object for the various types of CANs we know we can get
 * from the manufacturer.
 * Figure out what all the objects are and determine how to make those objects
 * act like any but only
 * one of these objects from the manufacturer
 */
/**
 * Contains the methods that are used in the class CANUtils in order to detect
 * sticky faults
 * on the CAN devices (the Talons, Jaguars, Pneumatic Control Modules, and Power
 * Distribution Panel)
 * 
 * @author Daniel Resio, Becky Button, and Cole Ramos
 *
 */
public class CANObject
{

//If true, activates debug print statements throughout the class
private boolean useDebug = true;

/**
 * Prints values of the significant variables/methods in CANObject if useDebug is true
 */
private void DebugCANUtils()
{ 
    if(useDebug == true)
    {
        System.out.println("The type Id is " + typeId);
        System.out.println("The value of the getFault method is " + getFault());
    }
}

//add object info 
private int canId;
//id for the CAN type
private int typeId;
//type id 1
private static CANTalon talon = null;
//type id 2
private static CANJaguar jaguar = null;
//type id 3
private static DoubleSolenoid doubleSolenoid = null;
//type id 4
private static PowerDistributionPanel pdp = null;

/**
 * Overrides default constructor
 */
private CANObject ()
{
    //can not use this
}

/**
 * creates CAN talon object
 * 
 * @param newTalon
 *            the CAN talon associated with this object
 * @param newCanId
 *            the ID of the CAN object
 */
public CANObject (final CANTalon newTalon, int newCanId)
{
    talon = newTalon;
    canId = newCanId;
    typeId = 1;
    
    //TODO undo comments if the following code is necessary
    if(useDebug == true)
    {
        System.out.println("The talon is " + talon);
        System.out.println("The canId of the CANTalon is " + canId);
        System.out.println("The type Id of the CANTalon is " + typeId);
    }
}

/**
 * Creates CAN Jaguar object
 * 
 * @param newJaguar
 *            the CAN Jaguar associated with this object
 * @param newCanId
 *            the ID of the CAN object
 */
public CANObject (final CANJaguar newJaguar, int newCanId)
{
    jaguar = newJaguar;
    canId = newCanId;
    typeId = 2;
    
    //TODO undo comments if the following code is necessary
//    if(useDebug == true)
//    {
//        System.out.println("The jaguar is " + jaguar);
//        System.out.println("The canId of the CANJaguar is " + canId);
//        System.out.println("The type Id of the CANJaguar is " + typeId);
//    }
}


/**
 * Creates Power Distribution Board Object
 * 
 * @param newPdp
 *            the CAN Power Distribution Board associated with this object
 * @param newCanId
 *            the ID of the CAN object
 */
public CANObject (final PowerDistributionPanel newPdp, int newCanId)
{
    pdp = newPdp;
    canId = newCanId;
    typeId = 4;
    
    //TODO undo comments if the following code is necessary
//    if(useDebug == true)
//    {
//        System.out.println("The pdp is " + talon);
//        System.out.println("The canId of the CANJaguar is " + canId);
//        System.out.println("The type Id of the CANJaguar is " + typeId);
//    }
}


/**
 * Creates CAN Pnuematics Control Module object
 * 
 * @param newdoubleSolenoid
 *            the CAN Pnuematics Control Module associated with this object
 * @param newCanId
 *            the ID of the CAN object
 */
public CANObject (final DoubleSolenoid newdoubleSolenoid, int newCanId)
{
    doubleSolenoid = newdoubleSolenoid;
    canId = newCanId;
    typeId = 3;
    
    // TODO undo comments if the following code is necessary
//    if(useDebug == true)
//    {
//        System.out.println("The Double Solenoid is " + doubleSolenoid);
//        System.out.println("The canId of the DoubleSolenoid is " + canId);
//        System.out.println("The type Id of the DoubleSolenoid is " + typeId);
//    }
    
}

/**
 * getting the can Id
 * 
 * @return canId of object
 */
public int getCanId ()
{
    return this.canId;
}

/**
 * Sets the can Id
 * 
 * @param canId
 *            canId object
 */
public void setCanId (final int canId)
{
    this.canId = canId;
}

/**
 * Sets the object to a CANTalon
 * 
 * @param talon
 *            talon object to change to
 */
public void setCAN (final CANTalon talon)
{
    CANObject.talon = talon;
    typeId = 1;
}

/**
 * Sets the object to a CAN jaguar
 * 
 * @param jaguar
 *            jaguar object to change to
 */
public void setCAN (final CANJaguar jaguar)
{
    CANObject.jaguar = jaguar;
    typeId = 2;
}

/**
 * Sets the object to a Pneumatics Control Module
 * 
 * @param doubleSolenoid
 *            pneumatics control module object to change to
 */
public void setCAN (final DoubleSolenoid doubleSolenoid)
{
    CANObject.doubleSolenoid = doubleSolenoid;
    typeId = 3;
}

/**
 * Sets the object to a Power Distribution Panel
 * 
 * @param pdp
 *            power distribution panel object to change to
 */
public void setCAN (final PowerDistributionPanel pdp)
{
    CANObject.pdp = pdp;
    typeId = 4;
}

/**
 * Gets the type ID of the object
 * 
 * @return the typeId of the object
 */
public int getType ()
{
    return typeId;
}

/**
 * Checks if the CAN Device is a Talon
 * 
 * @return talon object if type ID is 1; if not returns null
 */
public CANTalon getCANTalon ()
{
    if (typeId == 1)
    {
        return talon;
    }
    return null;
}

/**
 * Checks if the CAN Device is a Jaguar
 * 
 * @return Returns Jaguar if type ID is 2, if not returns null
 */
public CANJaguar getCANJaguar ()
{
    if (typeId == 2)
    {
        return jaguar;
    }
    return null;
}

/**
 * Checks if the CAN Device is a Pnuematic Control Module
 * 
 * @return Returns Pnuematic Control Module if type ID is 3, if not returns null
 */
public DoubleSolenoid getdoubleSolenoid ()
{
    if (typeId == 3)
    {
        return doubleSolenoid;
    }
    return null;
}

/**
 * // * Checks if the CAN Device is the Power Distribution Panel
 * // *
 * 
 * @return Returns Power Distribution Panel if the type ID is 3, if not returns
 *         null
 */
public PowerDistributionPanel getPDP ()
{
    if (typeId == 4)
    {
        return pdp;
    }
    return null;
}

/**
 * Checks for sticky faults on CAN Devices
 * 
 * @return true if there is a fault is present;
 *         false if a fault is not present or the given device is not recognized
 */
public boolean getFault ()
{
    switch (typeId)
    {
        case 1:
//            System.out.println("Fault Foward Limit: " + talon.getFaultForLim() + 
//                    "\nFault Forward Soft Limit: " + talon.getFaultForSoftLim() +
//                    "\nFault Over Temperature: " + talon.getFaultOverTemp() +
//                    "\nFault Hardware Failure: " + talon.getFaultHardwareFailure() + 
//                    "\nFault Reverse Limit: " + talon.getFaultRevLim() +
//                    "\nFault Reverse Soft Limit: " + talon.getFaultRevSoftLim() +
//                    "\nFault Under Voltage: " + talon.getFaultUnderVoltage()
//                    );
            //0 is no fault, greater than 0 if there is a fault
            if (talon.getFaultForLim() > 0
                    || talon.getFaultForSoftLim() > 0
                    || talon.getFaultOverTemp() > 0
                    || talon.getFaultHardwareFailure() > 0
                    || talon.getFaultRevLim() > 0
                    || talon.getFaultRevSoftLim() > 0
                    || talon.getFaultUnderVoltage() > 0)
            {
                return true;
            }
            return false;
        case 2:
            System.out.println("Jaguar Fault: " + jaguar.getFaults());
            if (jaguar.getFaults() > 0)
            {
                return true;
            }
            return false;
        case 3:
            System.out.println("Double Solenoid: " + doubleSolenoid.getPCMSolenoidVoltageStickyFault());
            //TODO the exit line is a problem
            return doubleSolenoid.getPCMSolenoidVoltageStickyFault();
        case 4:
            //pdp can hold sticky faults but cannot be accessed with code
            return false;
    }
    //object does not exist
    return false;
}
}
