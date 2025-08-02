package utility;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HexFormat;

public class PasswordProcessor {

    public static byte[] getRandomSalt() {
        SecureRandom sRandom = new SecureRandom();
        byte[] salt = new byte[16];
        sRandom.nextBytes(salt);
        return salt;
    }

    public static String getHashedPW(String origPW, byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException {

        KeySpec spec = new PBEKeySpec(origPW.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = null;
        factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = factory.generateSecret(spec).getEncoded();
        return getHashHex(hash);
    }

    public static String getHashHex(byte[] array) {
        return HexFormat.of().formatHex(array);
    }

    public static byte[] hexStringToByteArray(String hex) {
        return HexFormat.of().parseHex(hex);
    }

}
