package org.usfirst.frc.team339.HardwareInterfaces;

import java.lang.reflect.Array;
import java.util.ArrayList;
import org.usfirst.frc.team339.Hardware.Hardware;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SolenoidBase;

/**
 * @author Kilroy II
 *
 */
public class CANNetwork {
	public static ArrayList<CANObject> canObjects = new ArrayList<CANObject>();
	
	/**
	 * This function finds the CAN object given an ID.
	 * 
	 * @param id
	 *            This is the id for a CAN object.
	 * @return This will return the found CAN object. Or null if we can't find the CAN object.  Make sure to check for null before
	 *	you use the return.
	 */
	public CANObject getCANWithId(int id) 
	{
		for (int i = 0; i < canObjects.size(); i++)
		{
			CANObject tempObj = canObjects.get(i);
			// if CAN object id at location i in the array matches provided id,
			// then return that CANObject
			// else do nothing, continue loop to next spot in array, canObjects
			if (tempObj.getCanId() == i)
			{
				return tempObj;
			}

		}
		//return a null object because we can't find the CANDevice you're looking for
		return null;
	}
	
}