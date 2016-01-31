package org.usfirst.frc.team339.HardwareInterfaces;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SolenoidBase;

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
 * @author Kilroy II
 *
 */
public class CANObject
{
//add object info 
private int canId;
//id for the CAN type
private static int typeId;
//type id 1
private static CANTalon talon;
//type id 2
private static CANJaguar jaguar;
//type id 3
private static SolenoidBase pcm;
//type id 4
private static PowerDistributionPanel pdp;

/**
 * Overrides default constructor
 */
private CANObject ()
{
    //can not use this
}

public CANObject (Object newObject, int newCanId)
{
    canId = newCanId;
    
    if (canId < 10)
    {
        typeId = 4;
    }
    else if (canId < 20)
    {
        typeId = 1;
    }
    else if (canId < 30)
    {
        typeId = 3;
    }
    switch (typeId)
    {
        case 1: talon = (CANTalon)(newObject);
        case 2: jaguar = (CANJaguar)(newObject);
        case 3: pcm = (SolenoidBase)(newObject);
        case 4: pdp = (PowerDistributionPanel)(newObject);
    }
}

/**
 * creates CAN talon object
 * 
 * @param newTalon
 *            the CAN talon associated with this object
 * @param newCanId
 *            the ID of the CAN object
 */
public CANObject (CANTalon newTalon, int newCanId)
{
    talon = newTalon;
    canId = newCanId;
    typeId = 1;
}

/**
 * Creates CAN Jaguar object
 * 
 * @param newJaguar
 *            the CAN Jaguar associated with this object
 * @param newCanId
 *            the ID of the CAN object
 */
public CANObject (CANJaguar newJaguar, int newCanId)
{
    jaguar = newJaguar;
    canId = newCanId;
    typeId = 2;
}

/**
 * Creates CAN Pnuematics Control Module object
 * 
 * @param newPcm
 *            the CAN Pnuematics Control Module associated with this object
 * @param newCanId
 *            the ID of the CAN object
 */
public CANObject (SolenoidBase newPcm, int newCanId)
{
    pcm = newPcm;
    canId = newCanId;
    typeId = 3;
}

/**
 * Creates Power Distribution Board Object
 * 
 * @param newPdp
 *            the CAN Power Distribution Board associated with this object
 * @param newCanId
 *            the ID of the CAN object
 */
public CANObject (PowerDistributionPanel newPdp, int newCanId)
{
    pdp = newPdp;
    canId = newCanId;
    typeId = 4;
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
public void setCanId (int canId)
{
    this.canId = canId;
}

/**
 * Sets the object to a CANTalon
 * 
 * @param talon
 *            talon object to change to
 */
public void setCAN (CANTalon talon)
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
public void setCAN (CANJaguar jaguar)
{
    CANObject.jaguar = jaguar;
    typeId = 2;
}

/**
 * Sets the object to a Pneumatics Control Module
 * 
 * @param pcm
 *            pneumatics control module object to change to
 */
public void setCAN (SolenoidBase pcm)
{
    CANObject.pcm = pcm;
    typeId = 3;
}

/**
 * Sets the object to a Power Distribution Panel
 * 
 * @param pdp
 *            power distribution panel object to change to
 */
public void setCAN (PowerDistributionPanel pdp)
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
public SolenoidBase getPCM ()
{
    if (typeId == 3)
    {
        return pcm;
    }
    return null;
}

/**
 * Checks if the CAN Device is the Power Distribution Panel
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
public static boolean getFault ()
{

    switch (typeId)
    {
        case 1:
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
            else
            {
                return false;
            }
        case 2:
            if (jaguar.getFaults() > 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        case 3:
            return pcm.getPCMSolenoidVoltageStickyFault();
        case 4:
            //pdp cannot hold sticky faults or not?
            return false;
    }
    //object does not exist
    return false;
}
}
