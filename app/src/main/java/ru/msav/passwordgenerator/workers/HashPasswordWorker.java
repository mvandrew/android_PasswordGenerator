package ru.msav.passwordgenerator.workers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by msav on 30.12.2017.
 */

public class HashPasswordWorker extends BaseWorker {

    /**
     * Алгоритм хэширования пароля
     */
    private HashAlgorithm Algorithm;

    public HashAlgorithm getAlgorithm() {
        return Algorithm;
    }

    public void setAlgorithm(HashAlgorithm algorithm) {
        Algorithm = algorithm;
        updatePassword();
    }


    /**
     * Кодовая фраза для шифрования
     */
    private String Passphrase;

    public String getPassphrase() {
        return Passphrase;
    }

    public void setPassphrase(String passphrase) {
        Passphrase = passphrase;
        updatePassword();
    }

    public HashPasswordWorker() {
        super();

        // Инициализация свойств
        setAlgorithm(HashAlgorithm.MD5);
        setPassphrase("");
    }

    @Override
    public boolean updatePassword() {
        boolean res = false;

        if (getPassphrase() == null || getPassphrase().equals("")) {
            setPassword("");
            res = true;
        } else {
            String algorithmID = "";
            int hashLength = 0;
            switch (getAlgorithm()) {
                case MD5:
                    algorithmID = "MD5";
                    hashLength = 16;
                    break;
                case SHA256:
                    algorithmID = "SHA-256";
                    hashLength = 32;
                    break;
            }

            if (!algorithmID.equals("") && hashLength > 0) {
                try {
                    MessageDigest digest = MessageDigest.getInstance(algorithmID);
                    digest.reset();
                    digest.update(getPassphrase().getBytes("UTF-8"));
                    byte[] arHash = digest.digest();
                    StringBuffer hexBuffer = new StringBuffer();

                    for (int i = 0; i < arHash.length; i++) {
                        String hex = Integer.toHexString(0xff & arHash[i]);
                        if (hex.length() == 1)
                            hexBuffer.append('0');
                        hexBuffer.append(hex);
                    }

                    setPassword(hexBuffer.toString());
                    res = true;

                } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return res;
    }
}
