package models.t1.cdn;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import models.AbstractEntity;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import steps.t1.cdn.CdnCertificateClient;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

@Getter
@Builder
public class Certificate extends AbstractEntity {

    private String name;
    private String projectId;

    @Override
    public void delete() {
        CdnCertificateClient.deleteCertificateByName(projectId, name);
    }

    @SneakyThrows
    public String generatePrivateRSAKeyAsString(KeyPair keyPair) {
        return convertKeyToString(keyPair.getPrivate());
    }

    @SneakyThrows
    public String generateSelfSignedCertificateAsString(KeyPair keyPair) {
        return convertCertificateToString(generateSelfSignedCertificate(keyPair));
    }

    @SneakyThrows
    public KeyPair generateKeyPair() {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    @SneakyThrows
    private X509Certificate generateSelfSignedCertificate(KeyPair keyPair) {
        X509Certificate cert = null;
        try {
            // Validity: From now until one year
            Date validityBeginDate = new Date();
            Date validityEndDate = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);

            // Generate self-signed certificate
            X509Principal dnName = new X509Principal("CN=SelfSignedCert");
            X509Principal issuerName = dnName; // Use the same for issuer

            X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
            certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
            certGen.setSubjectDN(dnName);
            certGen.setIssuerDN(issuerName);
            certGen.setNotBefore(validityBeginDate);
            certGen.setNotAfter(validityEndDate);
            certGen.setPublicKey(keyPair.getPublic());
            certGen.setSignatureAlgorithm("SHA256withRSA");

            cert = certGen.generate(keyPair.getPrivate());
        } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException
                 | SignatureException e) {
            e.printStackTrace();
        }
        return cert;
    }

    private String convertKeyToString(PrivateKey privateKey) {
        return "-----BEGIN RSA PRIVATE KEY-----\n" +
                Base64.getEncoder().encodeToString(privateKey.getEncoded()) +
                "\n-----END RSA PRIVATE KEY-----";
    }

    private String convertCertificateToString(X509Certificate certificate) {
        try {
            return "-----BEGIN CERTIFICATE-----\n" +
                    Base64.getEncoder().encodeToString(certificate.getEncoded()) +
                    "\n-----END CERTIFICATE-----";
        } catch (CertificateException e) {
            throw new RuntimeException("Error converting certificate to string", e);
        }
    }
}
