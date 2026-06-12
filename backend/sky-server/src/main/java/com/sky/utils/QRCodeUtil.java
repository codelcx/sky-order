package com.sky.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;

@Slf4j
public class QRCodeUtil {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;

    public static String generateQRCode(String content, Path outputPath) {
        try {
            Files.createDirectories(outputPath.getParent());

            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);

            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", outputPath);

            log.info("二维码生成成功：{}", outputPath);
            return outputPath.toString();
        } catch (WriterException | IOException e) {
            log.error("二维码生成失败", e);
            throw new RuntimeException("二维码生成失败", e);
        }
    }
}
