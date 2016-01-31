// ====================================================================
// FILE NAME: SingleThrowSwitch.java (Team 339 - Kilroy)
//
// CREATED ON: Sep 19, 2009
// CREATED BY: Bob Brown
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This class Enhances the Digital Input class. It adds a member
// function isOn() which will return a true if the switch is on.
// All other member functions from the base class are unchanged.
//
// REMEMBER: This class includes all functions normally found in
// the WPI DigitalInput class.
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

package org.usfirst.frc.team339.HardwareInterfaces;

import edu.wpi.first.wpilibj.DigitalInput;

// -------------------------------------------------------
/**
 * This class Enhances the Digital Input class. It adds a member
 * function isOn() which will return a true if the switch is on.
 * All other member functions from the base class are unchanged.
 *
 * @class SingleThrowSwitch
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
public class SingleThrowSwitch extends DigitalInput
{
// -------------------------------------------------------
/**
 * Create an instance of a Single Throw Switch class.
 * Creates a digital input given a channel and uses the default module.
 *
 * @method SingleThrowSwitch()
 * @param channel
 *            the port for the digital input
 * @author Bob Brown
 * @written Sep 18 2009
 *          -------------------------------------------------------
 */
public SingleThrowSwitch (final int channel)
{
    super(channel);
} // end constructor

// ------------------------------------------------------
/**
 * This function overrides the parent function get() but
 * is marked as deprecated so users will not call get()
 * in place of isOn()
 *
 * @method get()
 * @author Will Stuckey
 * @see isOn()
 * @return false
 * @deprecated
 *             -------------------------------------------------------
 */
@Deprecated
@Override
public boolean get ()
{
    return false;
}

// -------------------------------------------------------
/**
 * Create an instance of a Single Throw Switch class.
 * Creates a digital input given an channel and module.
 *
 * @method SingleThrowSwitch()
 * @param slot
 *            the slot where the digital module is located
 * @param channel
 *            the port for the digital input
 * @author Bob Brown
 * @written Sep 18 2009
 *          -------------------------------------------------------
 */

/*
 * public
 * SingleThrowSwitch (final int slot,
 * final int channel)
 * {
 * super(slot, channel);
 * } // end constructor
 */
// -------------------------------------------------------
/**
 * This function returns whether or not the switch is on
 * or not.
 *
 * @method isOn
 * @return is on or not. Works even if not plugged in
 * @author Bob Brown
 * @written Sep 19, 2009
 *          -------------------------------------------------------
 */
public boolean isOn ()
{
    return (!super.get());
} // end isOn

// TODO: add a setReversed(boolean) method to software-reverse the switch

} // end class
