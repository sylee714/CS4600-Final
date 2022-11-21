import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.util.Arrays;
import java.util.Scanner;

// Input check by checking if the user entered a valid username
// Overall step of the send message method
// 1. Get the receiver's public key
// 2. Generate AES key
// 3. Encrypt the message with the AES key
// 4. Encrypt the AES key with the receiver's public key
// 5. Compute MAC with the AES key and the message
// 6. Send results of 3, 4, and 5 to the receiver
public class SendMsg {
    public static void main(String[] args) throws Exception {
        Scanner scan  = new Scanner(System.in);

        // Get receiver's username
        System.out.println("Enter receiver's username: ");
        String receiverUserName = scan.nextLine().toLowerCase();

        // Check if the username is valid
        boolean existUserName = Utils.inUserList(receiverUserName);

        while (!existUserName) {
            System.out.println("No username found.");
            System.out.println("Enter receiver's username: ");
            receiverUserName = scan.nextLine().toLowerCase();
            existUserName = Utils.inUserList(receiverUserName);
        }

        // Get receiver's public key, used to encrypt AES key
        PublicKey receiverPubKey = Utils.getPubKey(receiverUserName);

        // Get the message to encrypt
        System.out.println("Enter message: ");
        String msg = scan.nextLine();

        // Generate AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom secRandom = new SecureRandom();
        keyGen.init(256, secRandom);
        SecretKey AESKey = keyGen.generateKey();

        // Encrypt the message with AES key
        Cipher cipherMsg = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipherMsg.init(cipherMsg.ENCRYPT_MODE, AESKey);
        byte[] cipherText = cipherMsg.doFinal(msg.getBytes());

        // Encrypt the AES Key with the receiver's public key
        Cipher cipherAES = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipherAES.init(Cipher.ENCRYPT_MODE, receiverPubKey);
        cipherAES.update(AESKey.getEncoded());
        byte[] encryptedAES = cipherAES.doFinal();

        // Compute MAC with the AES key and the message
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(AESKey);
        byte[] macResult = mac.doFinal(msg.getBytes());

        // Write to the "transmitted_data.txt" file
        // len of AES = 256 byte
        // len of MAC = 32 byte
        // len of cipher text = depends on the message
        try {
            File data = new File("transmitted_data.txt");

            // Check if the "transmitted_data.txt" file exist
            // If not, create one
            if (data.createNewFile()) {
                //do nothing
            }

            FileOutputStream outputStream = new FileOutputStream(data);

            // Write the data to the file
            outputStream.write(encryptedAES); // 256 byte; AES key
            outputStream.write(macResult); // 32 byte; MAC
            outputStream.write(cipherText); // size varies; Message
            outputStream.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }

    }
}
