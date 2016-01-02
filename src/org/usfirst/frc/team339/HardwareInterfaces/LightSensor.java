// ====================================================================
// FILE NAME: LightSensor.java (Team 339 - Kilroy)
//
// CREATED ON: February 3, 2010
// CREATED BY: S. V. Pakington
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This class Enhances the Digital Input class. It adds a member
// function isOn() which will return a true if the light is reflected.
// All other member functions from the base class are unchanged.
//
// QS18VN6D red light sensor characteristics:
// - diffuse mode has a range of 20" off a normal surface, 3' off of a shiny
// metal surface.
// - 940 nm infrared sensing mode.
// - output style is NPN.
//
// The wiring diagram for a QS18VN6D red light sensor from
// Banner Corp. is as follows:
// Wiring the QS18VN6D Banner Red Light sensor is as follows:
// Brown Lead - +12V 20 amp circuit breaker protected line
// Blue Lead - Ground of an PWM wire of a digital input/output pin group
// White Lead - SIGnal Pin of a PWM wire of a digital input/output pin group
// Black Lead - Do not connect to anything
//
//
// QS18VN6LV red light sensor characteristics:
// - retroreflective mode has a range of 21'.
// - diffuse mode has a range of 18".
// - 660 nm visible red sensing mode.
// - output style is NPN.
//
// The wiring diagram for a QS18VN6LV red light sensor from
// Banner Corp. is as follows:
// Wiring the QS18VN6LV Banner Red Light sensor is as follows:
// Brown Lead - +12V 20 amp circuit breaker protected line
// Blue Lead - Ground of an PWM wire of a digital input/output pin group
// White Lead - SIGnal Pin of a PWM wire of a digital input/output pin group
// Black Lead - Do not connect to anything
//
// Photoswitch 42SML-7100 ser.B (Source) characteristics:
// - 3 degree field of view
// - 10mm to 9.2m sensing distance
// - infrared
// - 11-28V DC 45mA
//
// Photoswitch 42SMR-7100 ser.B (Receiver) characteristics:
// - 8 degree field of view
// - infrared
// - complementary N.O./N.C. output (Normally Open/Normally Closed)
// - 11-28V DC 25mA
//
// The wiring diagram for a 42SML-7100 ser.B from Allen-Bradley is:
// Red Lead - +11-28V 20 amp circuit breaker protected line*
// Black Lead - -11-28V 20 amp circuit breaker protected line*
// The wiring diagram for a 42SMR-7100 ser.B from Allen Bradley is:
// Red Lead - +11-28V 20 amp circuit breaker protected line*
// Black Lead - -11-28V 20 amp circuit breaker protected line*
// White Normally Closed (N.C.) lead - White Signal pin of a PWM wire of a
// digital i/o pin group
// Green Normally Open (N.O.) lead - Nothing**
// *These are miliamp circuits and so can be wired to the same terminals
// of the Power Distribution Board, or they can be wired together (red
// and red, black and black)
// **With the listed configuration (white - white, green - nothing), IsOn()
// returns true when the beam is unbroken, false when it is broken.
// Reversing the wiring (white - nothing, green - PWM white) will reverse
// this.
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
/** This class Enhances the Digital Input class. It adds a member
 * function isOn() which will return true if the light is reflected.
 * All other member functions from the base class are unchanged.
 *
 * @class LightSensor
 * @author S. V. Pakington
 * @written February 3, 2010
 *          ------------------------------------------------------- */

public class LightSensor extends DigitalInput
{
// -------------------------------------------------------
/** Create an instance of a LightSensor class.
 * Creates a digital input given a channel and uses the default module.
 *
 * @method LightSensor()
 * @param channel
 *            - the port for the digital input
 * @author S. V. Pakington
 * @written Feb 3, 2010
 *          ------------------------------------------------------- */
public LightSensor (final int channel)
    {
    super(channel);
    } // end LightSensor

// -------------------------------------------------------
/** Create an instance of a LightSensor class.
 * Creates a digital input given a channel and the module
 * passed in.
 *
 * @method LightSensor()
 * @param channel
 *            - the port for the digital input
 * @param slot
 *            - the slot where the digital board is located
 * @author S. V. Pakington
 * @written Feb 3, 2010
 *          ------------------------------------------------------- */
/* public LightSensor (final int slot,
     * final int channel)
     * {
     * super(slot, channel);
     * } // end LightSensor */
// -------------------------------------------------------
/** This function tells us whether there is something
 * blocking the red light emitted by the sensor or not.
 *
 * @method isOn
 * @return Is on or not, true or false
 * @author S. V. Pakington
 * @written Feb 3, 2010
 *          ------------------------------------------------------- */
public boolean isOn ()
    {
    return (this.get());
    } // end isOn
} // end LightSensor
