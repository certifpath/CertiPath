package com.appAuditza.Auditza.Backend.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class OtpUtils {

    /**
     * Génère la chaîne de formatage standard otpauth://totp/ pour un QR code.
     * Cette chaîne contient la clé secrète et les métadonnées nécessaires
     * pour les applications d'authentification comme Google Authenticator.
     *
     * @param secretKey La clé secrète OTP (doit être encodée en Base32).
     * @param account   L'identifiant de l'utilisateur (généralement son email).
     * @param issuer    Le nom de votre application ou entreprise.
     * @return La chaîne formatée, prête à être encodée dans un QR code.
     */
    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + secretKey // La clé secrète est déjà en Base32, pas besoin de l'encoder à nouveau.
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            // Cette exception ne devrait jamais se produire car UTF-8 est toujours supporté.
            throw new IllegalStateException(e);
        }
    }
}