package com.appAuditza.Auditza.Backend.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class OtpService {

    // Obtenir le répertoire temporaire du système d'exploitation
    private final Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

    /**
     * Crée une image de QR code et la sauvegarde dans un fichier temporaire.
     * @return Le nom unique du fichier généré (ex: "abc-123-def-456.png").
     */
    public String createQrCodeFile(String barCodeData, int height, int width) throws WriterException, IOException {
        
        // 1. Générer un nom de fichier unique pour éviter les conflits
        String fileName = UUID.randomUUID().toString() + ".png";
        Path filePath = this.tempDir.resolve(fileName);

        // 2. Créer la matrice du QR code
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
        
        // 3. Écrire l'image dans le fichier sur le disque
        try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        }

        // 4. Retourner uniquement le nom du fichier
        return fileName;
    }
}