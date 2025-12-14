import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.file.*;
import java.util.*;

class ex02_fileCrypto
{
    static public void Encrypt(String inFileName, String outFileName, String password)
	{
		try
		{
			byte[] file = Files.readAllBytes(Paths.get(inFileName));
			byte[] checkedFile = Arrays.copyOf(checker.getBytes(), checker.length() + file.length);
			
			System.arraycopy(file, 0, checkedFile, checker.length(), file.length);
			MessageDigest digest = MessageDigest.getInstance("SHA");
			digest.update(password.getBytes());
			SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
			Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
			aes.init(Cipher.ENCRYPT_MODE, key);
			Files.write(Paths.get(outFileName), aes.doFinal(checkedFile));
		}
		catch(Exception e)
		{
		}
	}
	
	static public boolean Decrypt(String inFileName, String outFileName, String password)
	{
		try
		{
			byte[] checkedFile = Files.readAllBytes(Paths.get(inFileName));
			
			MessageDigest digest = MessageDigest.getInstance("SHA");
			digest.update(password.getBytes());
			SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
			Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
			aes.init(Cipher.DECRYPT_MODE, key);
			String cleartext = new String(aes.doFinal(checkedFile));
			
			if(!cleartext.substring(0, checker.length()).equals(checker))
				throw new Exception();
		
			Files.write(Paths.get(outFileName), cleartext.substring(checker.length()).getBytes());

		}
		catch(Exception e)
		{
			return false;
		}
		
		return true;
	}
	
	static public void main(String args[])
    {
		if(args[0].equals("e"))
			Encrypt(args[1], args[2], args[3]);
		else
			System.out.println(Decrypt(args[1], args[2], args[3]));
	}

	private static final String checker = "correct header";
}
