package org.usfirst.frc.team339.HardwareInterfaces;

/**
 * 
 * A class for the six-position switch.
 * Switch must have six separate ports, and can only be one position at a time.
 * 
 * Recommended: create generalized Multi-Switch class.
 * 
 * @author Michael Andrzej Klaczynski
 *
 */
public class SixPositionSwitch
{

private SingleThrowSwitch position0;
private SingleThrowSwitch position1;
private SingleThrowSwitch position2;
private SingleThrowSwitch position3;
private SingleThrowSwitch position4;
private SingleThrowSwitch position5;

/**
 * Define a six-position switch with six digital input ports.
 * 
 * @param portZero
 * @param portOne
 * @param portTwo
 * @param portThree
 * @param portFour
 * @param portFive
 */
public SixPositionSwitch (int portZero, int portOne, int portTwo,
        int portThree, int portFour, int portFive)
{
	position0 = new SingleThrowSwitch(portZero);
	position1 = new SingleThrowSwitch(portOne);
	position2 = new SingleThrowSwitch(portTwo);
	position3 = new SingleThrowSwitch(portThree);
	position4 = new SingleThrowSwitch(portFour);
	position5 = new SingleThrowSwitch(portFive);
}

/**
 * Define a six-position switch with six digital input ports.
 * Extra parameter to invert the switch.
 * 
 * @param portZero
 * @param portOne
 * @param portTwo
 * @param portThree
 * @param portFour
 * @param portFive
 * @param inverted
 *            - set to 5-4-3-2-1-0
 */
public SixPositionSwitch (int portZero, int portOne, int portTwo,
        int portThree, int portFour, int portFive, boolean inverted)
{

	if (inverted == false)
	//do things normally 
	{
	position0 = new SingleThrowSwitch(portZero);
	position1 = new SingleThrowSwitch(portOne);
	position2 = new SingleThrowSwitch(portTwo);
	position3 = new SingleThrowSwitch(portThree);
	position4 = new SingleThrowSwitch(portFour);
	position5 = new SingleThrowSwitch(portFive);
	}
	else
	//invert it.
	{
	position0 = new SingleThrowSwitch(portFive);
	position1 = new SingleThrowSwitch(portFour);
	position2 = new SingleThrowSwitch(portThree);
	position3 = new SingleThrowSwitch(portTwo);
	position4 = new SingleThrowSwitch(portOne);
	position5 = new SingleThrowSwitch(portZero);
	}
}

/**
 * Gets the position of the switch, starting at 0.
 * defaults to -1.
 * 
 * @return integer value of switch
 */
public int getPosition ()
{
	int value = -1;
	if (position0.isOn())
	{
	value = 0;
	}
	else if (position1.isOn())
	{
	value = 1;
	}
	else if (position2.isOn())
	{
	value = 2;
	}
	else if (position3.isOn())
	{
	value = 3;
	}
	else if (position4.isOn())
	{
	value = 4;
	}
	else if (position5.isOn())
	{
	value = 5;
	}
	return value;
}

}
