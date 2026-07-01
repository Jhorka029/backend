package com.iespp.app.services;

import com.iespp.app.models.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.X509Certificate;
import java.util.Calendar;

@Service
public class PdfFirmaService implements SignatureInterface {

    @Autowired
    private DigitalFirmaService firmaService;

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public byte[] firmarPdf(byte[] pdfBytes, User user) throws Exception {
        X509Certificate cert = firmaService.getCertificate();
        java.security.PrivateKey privateKey = firmaService.getPrivateKey();

        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDSignature signature = new PDSignature();
            signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
            signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
            signature.setName(user.getNombreCompleto());
            signature.setLocation("Chiclayo, Lambayeque, Perú");
            signature.setReason("Reporte General del Sistema - IESPP");
            signature.setSignDate(Calendar.getInstance());

            document.addSignature(signature, this);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                document.saveIncremental(baos);
                return baos.toByteArray();
            }
        }
    }

    @Override
    public byte[] sign(InputStream content) throws java.io.IOException {
        try {
            byte[] data = content.readAllBytes();
            X509Certificate cert = firmaService.getCertificate();
            java.security.PrivateKey privateKey = firmaService.getPrivateKey();
            return createCmsSignature(data, privateKey, cert);
        } catch (Exception e) {
            throw new java.io.IOException("Error al firmar PDF", e);
        }
    }

    private byte[] createCmsSignature(byte[] data, java.security.PrivateKey privateKey, X509Certificate cert) throws Exception {
        X509CertificateHolder certHolder = new JcaX509CertificateHolder(cert);

        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();

        ContentSigner contentSigner = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM)
            .setProvider(BouncyCastleProvider.PROVIDER_NAME)
            .build(privateKey);

        SignerInfoGenerator signerInfo = new JcaSignerInfoGeneratorBuilder(
            new JcaDigestCalculatorProviderBuilder()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .build()
        ).build(contentSigner, certHolder);

        generator.addSignerInfoGenerator(signerInfo);
        generator.addCertificate(certHolder);

        CMSTypedData signedContent = new CMSTypedData() {
            @Override
            public ASN1ObjectIdentifier getContentType() {
                return CMSObjectIdentifiers.data;
            }

            @Override
            public Object getContent() {
                return data;
            }

            @Override
            public void write(OutputStream out) {
                try {
                    out.write(data);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        CMSSignedData cmsData = generator.generate(signedContent, false);
        return cmsData.getEncoded();
    }
}
