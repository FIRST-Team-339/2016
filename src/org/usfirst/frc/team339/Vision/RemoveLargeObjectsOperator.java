/**
 *
 */
package org.usfirst.frc.team339.Vision;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

/** @author Kilroy */
public class RemoveLargeObjectsOperator implements VisionOperatorInterface
{

private final int erosions;
private final boolean connectivity8;

public RemoveLargeObjectsOperator (int iterations, boolean connectivity8)
    {
    this.erosions = iterations;
    this.connectivity8 = connectivity8;
    }

    @Override
public Image operate (Image Source)
    {
    if (this.connectivity8 == true)
        {
        NIVision.imaqSizeFilter(Source, Source, 0, this.erosions,
            NIVision.SizeType.KEEP_SMALL, new NIVision.StructuringElement(
                3, 3, 0));
        }
    else
        {
        NIVision.imaqSizeFilter(Source, Source, 1, this.erosions,
            NIVision.SizeType.KEEP_SMALL, new NIVision.StructuringElement(
                3, 3, 0));
        }
    return Source;
    }

}
