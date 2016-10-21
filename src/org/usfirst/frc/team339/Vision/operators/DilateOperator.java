package org.usfirst.frc.team339.Vision.operators;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;

public class DilateOperator implements VisionOperatorInterface
{
private int iterations;

public DilateOperator (int iterations)
{
    this.iterations = iterations;
}

@Override
public Image operate (Image Source)
{
    final Image out = NIVision
            .imaqCreateImage(ImageType.IMAGE_U8, 0);
    for (int i = 0; i < iterations; i++)
        {
            NIVision.imaqGrayMorphology(Source, out,
                    NIVision.MorphologyMethod.DILATE,
                    new NIVision.StructuringElement());
        }
    return out;
}

}
