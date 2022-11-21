import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Set;

// Provide util methods to be used in other classes
public class Utils {
    private static String keyDir = "keys/"; // directory that stores all the keys
    private static String userListFileName = "userList.txt"; // text file that contains all the users

    // Returns the private key of the entered username
    public static PrivateKey getPrivKey(String userName) throws Exception {
        /* Read all bytes from the private key file */
        Path path = Paths.get(keyDir + userName + ".key");
        byte[] bytes = Files.readAllBytes(path);

        /* Generate private key. */
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pvt = kf.generatePrivate(ks);

        return pvt;
    }

    // Returns the public key of the entered username
    public static PublicKey getPubKey(String userName) throws Exception {
        /* Read all the public key bytes */
        Path path = Paths.get(keyDir + userName + ".pub");
        byte[] bytes = Files.readAllBytes(path);

        /* Generate public key. */
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pub = kf.generatePublic(ks);

        return pub;
    }

    // Checks if the entered username exist in the "userList.txt" file
    public static boolean inUserList(String userName) throws Exception {
        Set<String> userList = new HashSet<String>();
        try
        {
            File file = new File(userListFileName);    //creates a new file instance
            FileReader fr = new FileReader(file);   //reads the file
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
            StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
            String line;
            while((line = br.readLine()) != null)
            {
                userList.add(line);
            }
            fr.close();    //closes the stream and release the resources

            return userList.contains(userName);
        }
        catch(IOException e)
        {
            return false;
        }
    }

    // Checks if the entered username is valid
    // Only letters are allowed
    public static boolean checkInput(String userInput) throws Exception {
        for (int i = 0; i < userInput.length(); ++i) {
            if (('a' <= userInput.charAt(i) && 'z' >= userInput.charAt(i)) ||
                    ('A' <= userInput.charAt(i) && 'Z' >= userInput.charAt(i))) {
                // do nothing
            } else {
                return false;
            }
        }
        return true;
    }

}
