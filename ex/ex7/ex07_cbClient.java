import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.net.URI;
import java.util.*;
import org.mozartspaces.core.*;
import org.mozartspaces.capi3.*;

public class ex07_cbClient {
    
    static public byte[] encryptedMessage(String password) {
        try {
            String message = "test message";
            
            MessageDigest digest = MessageDigest.getInstance("SHA");
            digest.update(password.getBytes());
            SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
            
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.ENCRYPT_MODE, key);
            
            return aes.doFinal(message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String argv[]) throws Exception {
        java.util.Random r = new java.util.Random();
        String jobId = String.valueOf(r.nextInt(1000000));
        
        String password = "";
        for (int i = 0; i < 5; ++i) password += (char)('a' + r.nextInt(26));
        System.out.println("Password generated: " + password);

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        Capi capi = new Capi(core);
        
        int queueNum = r.nextBoolean() ? 1 : 2;
        String targetQueueName = "Queue" + queueNum;
        String targetJobKeysName = "JobKeys" + queueNum;
        System.out.println("Selected queue: " + targetQueueName);

        ContainerReference jobKeyContainer = capi.lookupContainer(targetJobKeysName, URI.create("xvsm://localhost:9876"), MzsConstants.RequestTimeout.DEFAULT, null);
        ContainerReference queueContainer = capi.lookupContainer(targetQueueName, URI.create("xvsm://localhost:9876"), MzsConstants.RequestTimeout.DEFAULT, null);
        ContainerReference resultContainer = capi.lookupContainer("Results", URI.create("xvsm://localhost:9876"), MzsConstants.RequestTimeout.DEFAULT, null);

        byte[] encryptedData = encryptedMessage(password);
        Entry newEntry = new Entry(encryptedData, KeyCoordinator.newCoordinationData(jobId));

        capi.write(queueContainer, newEntry);
        capi.write(jobKeyContainer, new Entry(jobId));

        ArrayList<String> entries = capi.take(resultContainer, KeyCoordinator.newSelector(jobId), MzsConstants.RequestTimeout.INFINITE, null);
        System.out.println("Found password: " + entries.get(0));
   
        core.shutdown(true);
    }
}