import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import java.security.spec.PKCS8EncodedKeySpec;

//https://www.novixys.com/blog/how-to-generate-rsa-keys-java/#2_Generating_a_Key_Pair
public class Main {
    public static void main(String[] args) throws Exception {
//        //Creating KeyPair generator object
//        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
//
//        //Initializing the KeyPairGenerator
//        keyPairGen.initialize(2048);
//
//        //Generating the pair of keys
//        KeyPair pair = keyPairGen.generateKeyPair();
//
//        //Getting the private key from the key pair
//        PrivateKey privKey = pair.getPrivate();
//
//        //Getting the public key from the key pair
//        PublicKey publicKey = pair.getPublic();
//        System.out.println("Keys generated");
//
//        String outFile = "alice";
//        FileOutputStream out = new FileOutputStream(outFile + ".key");
//        out.write(privKey.getEncoded());
//        out.close();
//
//        out = new FileOutputStream(outFile + ".pub");
//        out.write(publicKey.getEncoded());
//        out.close();
//
//        System.err.println("Private key format: " + privKey.getFormat());
//// prints "Private key format: PKCS#8" on my machine
//
//        System.err.println("Public key format: " + publicKey.getFormat());
//// prints "Public key format: X.509" on my machine

        /* Read all bytes from the private key file */
        Path path = Paths.get("alice.key");
        byte[] bytes = Files.readAllBytes(path);

        /* Generate private key. */
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pvt = kf.generatePrivate(ks);
        System.out.println(pvt);


    }
}