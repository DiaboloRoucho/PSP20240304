package dam.psp;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;

public class VerifRegistro {

	public static void main(String[] args) {

		try (BufferedInputStream inF = new BufferedInputStream(new FileInputStream(System.getProperty("user.home") + "//Documents//OpenSSL.pdf"));
				BufferedInputStream inS = new BufferedInputStream(
						new FileInputStream(System.getProperty("user.home") + "/Documents/OpenSSL.pdf.sign.cifrado.pdf"));) {
			Signature sign = Signature.getInstance("SHA512withRSA");

			KeyStore ks = KeyStore.getInstance("pkcs12");
			char[] contraseñas = "8675".toCharArray();
			ks.load(new FileInputStream("C:\\cygwin64\\home\\andre\\certs\\keystore.p12"), contraseñas);
			PublicKey pubKey = ks.getCertificate("andres").getPublicKey();
			sign.initVerify(pubKey);

			byte[] buffer = new byte[1024];
			int n; 
			while ((n = inF.read(buffer)) > 0) {
				sign.update(buffer, 0, n);
			}
			System.out.println(sign.verify(inS.readAllBytes()));

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}

	}
}
