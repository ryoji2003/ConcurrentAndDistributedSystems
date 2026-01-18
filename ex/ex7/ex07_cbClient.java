import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.net.URI;
import java.util.*;
import org.mozartspaces.core.*;
import org.mozartspaces.capi3.*;

public class ex07_cbClient
{
    static public byte[] encryptedMessage(String password)
	{
		try
		{
            String checker = "test message";
			byte[] checkedFile = Arrays.copyOf(checker.getBytes(), checker.length());
			
			MessageDigest digest = MessageDigest.getInstance("SHA");
			digest.update(password.getBytes());
			SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
			Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
			aes.init(Cipher.ENCRYPT_MODE, key);
			return aes.doFinal(checkedFile);
		}
		catch(Exception e)
		{
            return null;
		}
	}
	
    public static void main(String argv[]) throws Exception
    {
        java.util.Random r = new java.util.Random();
        String jobId = String.valueOf(r.nextInt(1000000));
        String password = "";

        for (int i = 0; i < 5; ++i)
            password += (char)('a' + r.nextInt(26));

        System.out.println("Password generated: " + password);

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        Capi capi = new Capi(core);

        // Randomly select one of two job containers for load balancing
        int containerChoice = r.nextInt(2) + 1;  // 1 or 2
        String containerName = "JobKeys" + containerChoice;

        ContainerReference jobKeyContainer = capi.lookupContainer(containerName, URI.create("xvsm://localhost:9876"), MzsConstants.RequestTimeout.DEFAULT, null);
        ContainerReference queueContainer = capi.lookupContainer("Queue", URI.create("xvsm://localhost:9876"), MzsConstants.RequestTimeout.DEFAULT, null);
        ContainerReference resultContainer = capi.lookupContainer("Results", URI.create("xvsm://localhost:9876"), MzsConstants.RequestTimeout.DEFAULT, null);

        Entry newEntry = new Entry(encryptedMessage(password), KeyCoordinator.newCoordinationData(jobId));
        capi.write(queueContainer, newEntry);
        capi.write(jobKeyContainer, new Entry(jobId));
        System.out.println("Job published to container: " + containerName);

        ArrayList<String> entries = capi.take(resultContainer, KeyCoordinator.newSelector(jobId), MzsConstants.RequestTimeout.INFINITE, null);
        System.out.println("Found password: " + entries.get(0));
   
        core.shutdown(true);
    }
}
