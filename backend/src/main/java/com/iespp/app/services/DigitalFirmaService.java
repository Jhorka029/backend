package com.iespp.app.services;

import jakarta.annotation.PostConstruct;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.*;

@Service
public class DigitalFirmaService {

    private static final String KEYSTORE_FILE = "firma_iespp.p12";
    private static final String KEYSTORE_PASSWORD = "iespp2026firma";
    private static final String KEY_ALIAS = "iespp-firma";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private X509Certificate certificate;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @PostConstruct
    public void init() {
        try {
            if (loadKeyStore()) {
                System.out.println("[FirmaService] Keystore cargado exitosamente.");
            } else {
                System.out.println("[FirmaService] Generando nuevo par de llaves RSA...");
                generateKeyPairAndCert();
                saveKeyStore();
                System.out.println("[FirmaService] Llaves y certificado generados.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando DigitalFirmaService", e);
        }
    }

    private boolean loadKeyStore() {
        File ksFile = new File(KEYSTORE_FILE);
        if (!ksFile.exists()) return false;
        try (FileInputStream fis = new FileInputStream(ksFile)) {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(fis, KEYSTORE_PASSWORD.toCharArray());
            if (ks.containsAlias(KEY_ALIAS)) {
                privateKey = (PrivateKey) ks.getKey(KEY_ALIAS, KEYSTORE_PASSWORD.toCharArray());
                certificate = (X509Certificate) ks.getCertificate(KEY_ALIAS);
                publicKey = certificate.getPublicKey();
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("[FirmaService] Error cargando keystore: " + e.getMessage());
            return false;
        }
    }

    private void saveKeyStore() throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(null, KEYSTORE_PASSWORD.toCharArray());
        ks.setKeyEntry(KEY_ALIAS, privateKey, KEYSTORE_PASSWORD.toCharArray(), new java.security.cert.Certificate[]{certificate});
        try (FileOutputStream fos = new FileOutputStream(KEYSTORE_FILE)) {
            ks.store(fos, KEYSTORE_PASSWORD.toCharArray());
        }
    }

    private void generateKeyPairAndCert() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyGen.initialize(KEY_SIZE, new SecureRandom());
        KeyPair keyPair = keyGen.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
        certificate = generateCertificate(keyPair);
    }

    private X509Certificate generateCertificate(KeyPair keyPair) throws Exception {
        X500Name dn = new X500Name("CN=IESPP Firma Digital, OU=Sistema de Matricula, O=Instituto Pedagogico IESPP, L=Chiclayo, ST=Lambayeque, C=PE");
        BigInteger serial = new BigInteger(64, new SecureRandom());
        Date notBefore = new Date();
        Date notAfter = new Date(System.currentTimeMillis() + 10L * 365 * 24 * 60 * 60 * 1000L);
        SubjectPublicKeyInfo pubKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(dn, serial, notBefore, notAfter, dn, pubKeyInfo);
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA").setProvider("BC").build(keyPair.getPrivate());
        X509CertificateHolder holder = certBuilder.build(signer);
        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);
    }

    public Map<String, Object> firmarDocumento(String documentoHash, String signerName, String signerEmail, String signerRole) {
        try {
            String signedAt = Instant.now().toString();
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            String dataToSign = documentoHash + "|" + signedAt + "|" + signerEmail;
            signature.update(dataToSign.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = signature.sign();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("signature", Base64.getEncoder().encodeToString(signatureBytes));
            result.put("algorithm", SIGNATURE_ALGORITHM);
            result.put("signedAt", signedAt);
            result.put("signerName", signerName);
            result.put("signerEmail", signerEmail);
            result.put("signerRole", signerRole);
            result.put("documentHash", documentoHash);
            result.put("serialNumber", certificate.getSerialNumber().toString(16));
            result.put("issuerDN", certificate.getIssuerX500Principal().getName());
            result.put("subjectDN", certificate.getSubjectX500Principal().getName());
            result.put("validFrom", certificate.getNotBefore().toInstant().toString());
            result.put("validUntil", certificate.getNotAfter().toInstant().toString());
            result.put("publicKeyAlgorithm", publicKey.getAlgorithm());
            result.put("publicKeySize", KEY_SIZE);
            result.put("fingerprint", getCertificateFingerprint());
            result.put("certificatePem", getCertificatePem());
            result.put("certificateDer", Base64.getEncoder().encodeToString(certificate.getEncoded()));
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error al firmar documento", e);
        }
    }

    public boolean verificarFirma(String documentoHash, String signatureBase64, String signedAt, String signerEmail) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            String dataToVerify = documentoHash + "|" + signedAt + "|" + signerEmail;
            signature.update(dataToVerify.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(signatureBase64));
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> getInformacionCertificado() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("algorithm", SIGNATURE_ALGORITHM);
        info.put("publicKeyAlgorithm", publicKey.getAlgorithm());
        info.put("publicKeySize", KEY_SIZE);
        info.put("serialNumber", certificate.getSerialNumber().toString(16));
        info.put("issuerDN", certificate.getIssuerX500Principal().getName());
        info.put("subjectDN", certificate.getSubjectX500Principal().getName());
        info.put("validFrom", certificate.getNotBefore().toInstant().toString());
        info.put("validUntil", certificate.getNotAfter().toInstant().toString());
        info.put("fingerprint", getCertificateFingerprint());
        info.put("certificatePem", getCertificatePem());
        return info;
    }

    public String getCertificatePem() {
        try {
            String b64 = Base64.getEncoder().encodeToString(certificate.getEncoded());
            StringBuilder pem = new StringBuilder("-----BEGIN CERTIFICATE-----\n");
            for (int i = 0; i < b64.length(); i += 64) {
                pem.append(b64, i, Math.min(i + 64, b64.length())).append('\n');
            }
            pem.append("-----END CERTIFICATE-----");
            return pem.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String getCertificateFingerprint() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(certificate.getEncoded());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02X:", b));
            if (sb.length() > 0) sb.setLength(sb.length() - 1);
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
