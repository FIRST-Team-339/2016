// ====================================================================
// FILE NAME: DoubleSolenoid.java (Team 339 - Kilroy)
//
// CREATED ON: Jan 25, 2014
// CREATED BY: Will Stuckey
// MODIFIED ON: Jan 25, 2014
// MODIFIED BY: Will Stuckey
// ReWritten to make use of the new Solenoid class from
// the WPI library
// ABSTRACT:
// This file represents a single solenoid.
//
// The wiring setup for a VPLE18-M5H-4/2-1/4 from Festo is:
// Place solenoid such that the light faces you
// Place connector on top of four metal prongs with a spacer in between
// The connector will have the numbers 1 (Left), 2(Right), 3(Bottom), and a
// symbol(Top)
// Method A ignores the connection points for slots 3 and the symbol.
// Thread the wire end a two-wire Solenoid Cable through the water seal on the
// black hood
// Pull the two wires through the square end of the black hood
// Screw the black wire into slot 1, and the red wire into slot 2 of the
// connector
// Push the hood down into the connector until it clicks into place
// Place the special screw (Has two different thread sizes with a smooth stretch
// between them)
// Through the top of the Black Hood, such that it secures the hood, connector,
// and spacer
// Into the solenoid module.
// Place a piece of black electrical tape over the water seal hole to anchor the
// cable.
// The female adapter end of the cable will plug into the solenoid board.
// Be certain that the light turns on when the module is powered. -David Robie
//
//
// NOTE: Please do not release this code without permission from
// Team 339.
// ====================================================================

package org.usfirst.frc.team339.HardwareInterfaces;

/**
 * This class is a single solenoid.
 *
 * @class Solenoid
 * @author Will Stuckey
 */
public class SingleSolenoid extends edu.wpi.first.wpilibj.Solenoid
{
    /**
     * @param channel
     *            The channel on the module to control.
     */
    public SingleSolenoid (int channel)
        {
        super(channel);
        }

    /**
     * @param channel
     *            The channel on the module to control.
     * @param noduleNumber
     *            The module number of the solenoid module to use.
     */
    public SingleSolenoid (int channel, int noduleNumber)
        {
        super(noduleNumber, channel);
        }
}
