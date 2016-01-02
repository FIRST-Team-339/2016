/**
 *
 */
package org.usfirst.frc.team339.Vision;

import com.ni.vision.NIVision.Image;

/** @author Kilroy */
public interface VisionOperatorInterface
{
    // Image source will be freed in the operator (if the source image is not
    // the same as the returned image)
    // Returns the image, put through the specific filter.
    public Image operate (Image Source);

}
