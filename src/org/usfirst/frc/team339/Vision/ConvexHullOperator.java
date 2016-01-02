/**
 *
 */
package org.usfirst.frc.team339.Vision;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

/** @author Kilroy */
public class ConvexHullOperator implements VisionOperatorInterface
{

boolean useConnectivity8 = true;

    /**
 *
 */
public ConvexHullOperator (boolean useConnectivity8)
    {
        this.useConnectivity8 = useConnectivity8;
    // TODO Auto-generated constructor stub
    }

/* (non-Javadoc)
     * 
     * @see
     * org.usfirst.frc.team339.Vision.VisionOperatorInterface#operate(com.ni
     * .vision.NIVision.Image) */
@Override
public Image operate (Image Source)
    {
    if (this.useConnectivity8 == true)
        {
            NIVision.imaqConvexHull(Source, Source, 0);
            }
    else
        {
            NIVision.imaqConvexHull(Source, Source, 1);
            }
    return Source;
    }

}
