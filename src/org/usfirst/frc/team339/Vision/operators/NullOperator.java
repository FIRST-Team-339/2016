/**
 *
 */
package org.usfirst.frc.team339.Vision.operators;

import com.ni.vision.NIVision.Image;

/** @author Kilroy */
public class NullOperator implements VisionOperatorInterface
{

boolean useConnectivity8 = true;

/**
*
*/
public NullOperator ()
{
    //intentionally blank constructor
}

@Override
public Image operate (Image Source)
{
    return Source;
}

}
