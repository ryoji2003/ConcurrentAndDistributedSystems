import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.net.URI;
import org.mozartspaces.core.*;
import org.mozartspaces.capi3.*;
import java.util.ArrayList;

public class ex07_cbWorker
{
	static public boolean decryptedMessage(byte[] checkedFile, String password)
	{
		try
		{
            String checker = "test message";
			MessageDigest digest = MessageDigest.getInstance("SHA");
			digest.update(password.getBytes());
			SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
			Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
			aes.init(Cipher.DECRYPT_MODE, key);
			String cleartext = new String(aes.doFinal(checkedFile));
			
			if(!cleartext.substring(0, checker.length()).equals(checker))
				throw new Exception();
		}
		catch(Exception e)
		{
			return false;
		}
		
		return true;
	}

    static String crackPassword(byte [] message)
    {
        for(char a = 'a'; a <= 'z'; ++a)
            for(char b = 'a'; b <= 'z'; ++b)
                for(char c = 'a'; c <= 'z'; ++c)
                    for(char d = 'a'; d <= 'z'; ++d)
                        for(char e = 'a'; e <= 'z'; ++e)
                        {
                            String pass = "" + a + b + c + d + e;
                            if (decryptedMessage(message, pass))
                                return pass;
                        }
        return "NOT FOUND";
    }

    public static void main(String argv[]) throws Exception
    {
        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        Capi capi = new Capi(core);

        // Randomly select one of two job containers for load balancing
        java.util.Random r = new java.util.Random();
        int containerChoice = r.nextInt(2) + 1;  // 1 or 2
        String containerName = "JobKeys" + containerChoice;

        ContainerReference jobKeyContainer = capi.lookupContainer(containerName, URI.create("xvsm://localhost:9876"), MzsConstants.RequestTimeout.DEFAULT, null);
        ContainerReference queueContainer = capi.lookupContainer("Queue", URI.create("xvsm://localhost:9876"), MzsConstants.RequestTimeout.DEFAULT, null);
        ContainerReference resultContainer = capi.lookupContainer("Results", URI.create("xvsm://localhost:9876"), MzsConstants.RequestTimeout.DEFAULT, null);

        System.out.println("Worker connected to container: " + containerName);

        for(;;)
        {
            ArrayList<String> jobs = capi.take(jobKeyContainer, AnyCoordinator.newSelector(1), MzsConstants.RequestTimeout.INFINITE, null);
            String jobId = jobs.get(0);

            System.out.println("Received job " + jobId);
            
            ArrayList<byte[]> entries = capi.take(queueContainer, KeyCoordinator.newSelector(jobId), MzsConstants.RequestTimeout.INFINITE, null);
            byte[] message = entries.get(0);

            String pass = crackPassword(message);
            System.out.println("Found password: " + pass);
            
            Entry newEntry = new Entry(pass,  KeyCoordinator.newCoordinationData(jobId));
            capi.write(resultContainer, newEntry);
        }
    }
}
