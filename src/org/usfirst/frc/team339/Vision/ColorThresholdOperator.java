package org.usfirst.frc.team339.Vision;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;

public class ColorThresholdOperator implements VisionOperatorInterface
{
private final int hueMin, hueMax;
private final int saturationMin, saturationMax;
private final int luminanceMin, luminanceMax;

public ColorThresholdOperator (int hueMin, int hueMax, int satMin,
    int satMax, int luminanceMin, int luminanceMax)
    {
    // initializing the ranges
        this.hueMin = hueMin;
    this.hueMax = hueMax;
    this.saturationMin = satMin;
    this.saturationMax = satMax;
    this.luminanceMin = luminanceMin;
    this.luminanceMax = luminanceMax;
    }

@Override
public Image operate (Image source)
    {
    // Creating new monocolor image with no border
    final Image thresholdImage =
            NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);

        // @TODO: Store NIVision.Range instead of integers so we don't make a
        // new one every time.
    NIVision.imaqColorThreshold(thresholdImage, source, 255,
        NIVision.ColorMode.HSL,
        new NIVision.Range(this.hueMin, this.hueMax), new NIVision.Range(
            this.saturationMin, this.saturationMax), new NIVision.Range(
                this.luminanceMin, this.luminanceMax));
    source.free();
    return thresholdImage;
    }

}
