import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.util.Scanner;

// This class creates a pair of RSA keys for the entered username.
public class GenerateKeyPair {

    public static void main(String[] args) throws Exception {
        // Get the username
        System.out.println("Only letters allowed");
        Scanner scan  = new Scanner(System.in);
        System.out.println("Enter username: ");

        String userName = scan.nextLine().toLowerCase();
        boolean validUserName = Utils.checkInput(userName);
        boolean existUserName = Utils.inUserList(userName);

        // Check if the username is valid and is already existing
        while (!validUserName || existUserName) {
            if (!validUserName) {
                System.out.println("Invalid username.");
            } else if (existUserName) {
                System.out.println("Username already existed. Enter a new one.");
            }
            System.out.println("Enter username: ");
            userName = scan.nextLine().toLowerCase();
            validUserName = Utils.checkInput(userName);
            existUserName = Utils.inUserList(userName);
        }

        userName = userName;
        System.out.println("Username: " + userName);

        // Create a key pair of public and private for the username
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair pair = keyPairGen.generateKeyPair();
        PrivateKey privKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
        System.out.println("Keys generated");

        // Create a dir to store all the keys
        File keys = new File("keys");
        if (!keys.exists()){
            keys.mkdirs();
        }

        // Save the keys in the "keys" directory
        String outFile = "keys/" + userName;
        FileOutputStream out = new FileOutputStream(outFile + ".key");
        out.write(privKey.getEncoded());
        out.close();

        out = new FileOutputStream(outFile + ".pub");
        out.write(publicKey.getEncoded());
        out.close();

//        System.err.println("Private key format: " + privKey.getFormat());
        // prints "Private key format: PKCS#8" on my machine

//        System.err.println("Public key format: " + publicKey.getFormat());
        // prints "Public key format: X.509" on my machine

        // Add the username to the "userList.txt" file
        try {
            File userList = new File("userList.txt");
            if (userList.createNewFile()) {
                //do nothing
            }
            FileWriter fileWrite = new FileWriter("userList.txt", true);
            fileWrite.write(userName + "\n");
            fileWrite.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");

        }

    }
}
