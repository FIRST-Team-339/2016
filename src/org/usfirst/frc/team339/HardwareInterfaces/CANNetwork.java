package org.usfirst.frc.team339.HardwareInterfaces;

import java.lang.reflect.Array;
import java.util.ArrayList;
import edu.wpi.first.wpilibj.CANTalon;

/**
 * @author Kilroy II
 *
 */
public class CANNetwork {
	private ArrayList canObjects;
	private int talonNum = 0;

	/**
	 * @param newObjects
	 *            This is an array of CAN objects in use. It is set once in the
	 *            constructor.
	 */
	public CANNetwork(ArrayList newObjects)
	{
		this.canObjects = newObjects;
	}

	/**
	 * This function finds the CAN object given an ID.
	 * 
	 * @param id
	 *            This is the id for a CAN object.
	 * @return This will return the found CAN object. Or null if we can't find the CAN object.  Make sure to check for null before
	 *	you use the return.
	 */
	public CANObject getCAN(int id) 
	{
		for (int i = 0; i < canObjects.size(); i++)
		{
			CANObject tempObj = (CANObject) canObjects.get(i);
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

//	public class CanIdNotFoundException extends Exception {
//		/**
//		 * 
//		 */
//		private static final long serialVersionUID = 1L;
//
//		public CanIdNotFoundException(String message)
//		{
//			super(message);
//		}
//	}

}
