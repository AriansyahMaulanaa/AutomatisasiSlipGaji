package com.slipgaji.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import java.awt.image.BufferedImage;

public class BarcodeUtil {

    private static final MultiFormatReader reader = new MultiFormatReader();

    static {
        java.util.Map<DecodeHintType, Object> hints = new java.util.HashMap<>();
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, java.util.EnumSet.of(
            BarcodeFormat.CODE_128, BarcodeFormat.CODE_39, BarcodeFormat.EAN_13,
            BarcodeFormat.EAN_8, BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX,
            BarcodeFormat.ITF, BarcodeFormat.CODABAR));
        reader.setHints(hints);
    }

    public static String decode(BufferedImage image) {
        if (image == null) return null;
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = reader.decodeWithState(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
