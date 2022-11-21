import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.util.Arrays;
import java.util.Scanner;

// Input check by checking if the user entered a valid username
// Overall step of the receive message method
// 1. Get the receiver's private key
// 2. Read the "transmitted_data.txt" file to get the encrypted message, MAC, and the encrypted AES key
// 3. Decrypt the encrypted AES key to get the AES key
// 4. Decrypt the encrypted message with the AES key
// 5. Compute MAC using the AES key and the message
// 6. Verify the computed MAC with the sent MAC for authentication
public class ReceiveMsg {
    public static void printResult(String msg, boolean auth) {
        System.out.println("Message: " + msg);
        System.out.println("Authentication: " + auth);
    }

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

        // Get the receiver's private key
        PrivateKey receiverPrvKey = Utils.getPrivKey(receiverUserName);

        // Initialize arraries to hold data
        String fileName = "transmitted_data.txt";
        File f = new File(fileName);
        int fileSize = (int) f.length();
        int aesKeySize = 256;
        int macSize = 32;

        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[fileSize];
        byte[] encryptedAESKey = new byte[aesKeySize];
        byte[] macVerify = new byte[macSize];
        int msg_len = fileSize - aesKeySize - macSize;
        byte[] message = new byte[msg_len];

        // Read the "transmitted_data.txt" file
        try
        {
            //Read bytes with InputStream
            fileInputStream = new FileInputStream(f);
            fileInputStream.read(bFile);
            fileInputStream.close();

            // read aes key
            for (int i = 0; i < aesKeySize; i++)
            {
                encryptedAESKey[i] = bFile[i];
            }

            // read mac
            for (int i = aesKeySize; i < aesKeySize + macSize; i++)
            {
                macVerify[i - aesKeySize] = bFile[i];
            }

            // read message
            int offset = aesKeySize + macSize;
            for (int i = offset; i < fileSize; i++)
            {
                message[i - offset] = bFile[i];
            }

            // Decrypt the AES key using the private key
            Cipher cipherAES = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherAES.init(Cipher.DECRYPT_MODE, receiverPrvKey);
            byte[] decryptedAES = cipherAES.doFinal(encryptedAESKey);

            // Convert the bytes into a SecretKey
            SecretKey decryptedAESKey = new SecretKeySpec(decryptedAES, "AES");

            // Decrypt the message using the decrypted AES key
            Cipher cipherMsg = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipherMsg.init(cipherMsg.DECRYPT_MODE, decryptedAESKey);
            byte[] plainMsg = cipherMsg.doFinal(message);
            String msg = new String(plainMsg, "UTF-8");

            // Authenticate with the AES key and the message
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(decryptedAESKey);
            byte[] macResult = mac.doFinal(plainMsg);
            boolean auth = Arrays.equals(macResult, macVerify);

            printResult(msg, auth);

        }
        catch (Exception e)
        {
            printResult("error occurred", false);
        }

    }
}
