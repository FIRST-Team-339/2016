// ====================================================================
// FILE NAME: IRSensor.java (Team 339 - Kilroy)
//
// CREATED ON: January 15, 2011
// CREATED BY: S. V. Pakington
// MODIFIED ON:
// MODIFIED BY:
// ABSTRACT:
// This class enhances the Digital Input class. It adds a member
// function isOn() which will return a true if the light is reflected.
// All other member functions from the base class are unchanged.

// NOTE: This wiring diagram is being utilized for a US Digital
// E4P-360-250-D-D-D-B Infrared sensor.

// Wiring the Allen-Bradley 42EF-D1MNAK-A2 Infrared sensor is as follows:
// Brown Lead - +12V 20 amp circuit breaker protected line
// Blue Lead - Ground of an PWM wire of a digital input/output pin group
// White Lead - Signal Pin of a PWM wire of a digital input/output pin group
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
 * This class enhances the Digital Input class. It adds a member
 * function isOn() which will return true if the light is reflected.
 * All other member functions from the base class are unchanged.
 *
 * @class IRSensor()
 * @author S. V. Pakington
 * @written January 15, 2011
 *          -------------------------------------------------------
 */

public class IRSensor extends DigitalInput
{
    // -------------------------------------------------------
    /**
     * Create an instance of a IRSensor class.
     * Creates a digital input given a channel and uses the default module.
     *
     * @method IRSensor()
     * @param channel
     *            - the port for the digital input
     * @author S. V. Pakington
     * @written January 15, 2011
     *          -------------------------------------------------------
     */
    public IRSensor (final int channel)
        {
        super(channel);
        } // end IRSensor

    // -------------------------------------------------------
    /**
     * Create an instance of a IRSensor class.
     * Creates a digital input given a channel and uses the default module.
     *
     * @method IRSensor()
     * @param channel
     *            - the port for the digital input
     * @param slot
     *            - the slot where the digital board is located
     * @author S. V. Pakington
     * @written January 15, 2011
     *          -------------------------------------------------------
     */
    /*
     * public IRSensor (final int slot,
     * final int channel)
     * {
     * super(slot, channel);
     * } // end LightSensor
     */
    // -------------------------------------------------------
    /**
     * This function tells us whether there is something
     * blocking the infrared light emitted by the sensor or not.
     *
     * @method isOn
     * @return Is on or not, true or false
     * @author S. V. Pakington
     * @written Feb 3, 2010
     *          -------------------------------------------------------
     */
    public boolean isOn ()
        {
        return (this.get());
        } // end isOn
} // end LightSensor
