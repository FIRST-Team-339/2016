/**
 *
 */
package org.usfirst.frc.team339.Vision.operators;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;

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

/*
 * (non-Javadoc)
 * 
 * @see
 * org.usfirst.frc.team339.Vision.VisionOperatorInterface#operate(com.ni
 * .vision.NIVision.Image)
 */
@Override
public Image operate (Image Source)
{
    final Image hulledImage = NIVision
            .imaqCreateImage(ImageType.IMAGE_U8, 0);
    if (this.useConnectivity8 == true)
        {
        NIVision.imaqConvexHull(hulledImage, Source, 0);
        }
    else
        {
        NIVision.imaqConvexHull(hulledImage, Source, 1);
        }
    Source.free();
    return hulledImage;
}

}
