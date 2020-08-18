package com.assinador;

import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import sun.security.pkcs11.SunPKCS11;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Assinador {
    private static String configFilePath = null;
    private static KeyStore keyStore = null;
    private static String password = null;
    private static String alias = null;

    private static void initializeProvider() {
        try {
            SunPKCS11 provider = new SunPKCS11(configFilePath);
            Security.removeProvider(provider.getName());
            Security.addProvider(provider);
            keyStore = KeyStore.getInstance("PKCS11");
            keyStore.load(null, password.toCharArray());
            alias = keyStore.aliases().nextElement();
            System.out.println(alias);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean signFile(File file, String destinationFolder) {
        if (file.length() >= 2000000) {
            JOptionPane.showMessageDialog(null, file.getName() + " é maior que o tamanho permitido de 2MB");

            return false;
        }

        try {
            ArrayList<X509Certificate> certificates = new ArrayList<X509Certificate>();
            CMSTypedData content = new CMSProcessableFile(file);
            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
            certificates.add(certificate);
            JcaCertStore certStore = new JcaCertStore(certificates);
            CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
            JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder("SHA256withRSA");
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
            ContentSigner sha256Signer = contentSignerBuilder.build(privateKey);
            Security.addProvider(new BouncyCastleProvider());
            generator.addSignerInfoGenerator(
                    new JcaSignerInfoGeneratorBuilder(
                            new JcaDigestCalculatorProviderBuilder().setProvider("SunPKCS11-OpenSC").build())
                            .build(sha256Signer, certificate));
            generator.addCertificates(certStore);
            CMSSignedData signedData = generator.generate(content, true);
            FileOutputStream fileOuputStream = new FileOutputStream(new File(destinationFolder + "/" + file.getName() + ".p7s"));
            fileOuputStream.write(signedData.getEncoded());
            fileOuputStream.flush();
            fileOuputStream.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public static void main(String[] args) {
        configFilePath = args[0];
        password = args[1];
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setDialogTitle("Selecione o(s) arquivo(s) para assinar");
        fileChooser.showOpenDialog(null);
        File[] files = fileChooser.getSelectedFiles();
        JFileChooser folderChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        folderChooser.setAcceptAllFileFilterUsed(false);
        folderChooser.setDialogTitle("Selecione a pasta onde ficarão os arquivos assinados");
        folderChooser.showSaveDialog(null);
        String destinationFolder = folderChooser.getSelectedFile().getAbsolutePath();
        HashMap<File, Boolean> result = new HashMap<File, Boolean>();
        StringBuilder resultMessage = new StringBuilder();
        StringBuilder successfullySigned = new StringBuilder();
        StringBuilder failures = new StringBuilder();

        initializeProvider();

        for (File file : files) {
            result.put(file, signFile(file, destinationFolder));
        }

        for (Map.Entry<File, Boolean> entry : result.entrySet()) {
            if (entry.getValue()) {
                successfullySigned.append(entry.getKey().getName()).append("\n");
            } else {
                failures.append(entry.getKey().getName()).append("\n");
            }
        }

        if (successfullySigned.length() > 0) {
            resultMessage.append("Assinados com sucesso:\n").append(successfullySigned).append("\n");
        }

        if (failures.length() > 0) {
            resultMessage.append("Falha ao assinar:\n").append(failures).append("\n");
        }

        JOptionPane.showMessageDialog(null, resultMessage.toString(), "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }
}
