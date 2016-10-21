package org.usfirst.frc.team339.Vision.operators;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;

public class HSLColorThresholdOperator
        implements VisionOperatorInterface
{

private final NIVision.Range hueRange;

private final NIVision.Range satRange;

private final NIVision.Range lumRange;

public HSLColorThresholdOperator (int hueMin, int hueMax, int satMin,
        int satMax, int luminanceMin, int luminanceMax)
{
    hueRange = new NIVision.Range(hueMin, hueMax);
    satRange = new NIVision.Range(satMin, satMax);
    lumRange = new NIVision.Range(luminanceMin, luminanceMax);
}

public HSLColorThresholdOperator (NIVision.Range hueRange,
        NIVision.Range satRange, NIVision.Range lumRange)
{
    this.hueRange = hueRange;
    this.satRange = satRange;
    this.lumRange = lumRange;
}

@Override
public Image operate (Image source)
{
    // Creating new monocolor image with no border
    final Image thresholdImage = NIVision
            .imaqCreateImage(ImageType.IMAGE_U8, 0);

    // @TODO: Store NIVision.Range instead of integers so we don't make a
    // new one every time.
    NIVision.imaqColorThreshold(thresholdImage, source, 255,
            NIVision.ColorMode.HSL, this.hueRange, this.satRange,
            this.lumRange);
    source.free();
    return thresholdImage;
}

}
