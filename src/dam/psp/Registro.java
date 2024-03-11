package dam.psp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;

import dam.psp.*;

public class Registro {

	public static void main(String[] args) throws Exception {
		KeyStore ks = KeyStore.getInstance("pkcs12");
		char[] contraseñas = "8675".toCharArray();
		ks.load(new FileInputStream("C:\\cygwin64\\home\\andre\\certs\\keystore.p12"), contraseñas);
		PrivateKey privKey = (PrivateKey) ks.getKey("andres", contraseñas);
		
		try (BufferedInputStream in = new BufferedInputStream(Registro.class.getResourceAsStream("/OpenSSL.pdf"));
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(System.getProperty("user.home") + "//Desktop//OpenSSL.pdf.sign"))) {
			Signature sign = Signature.getInstance("SHA512withRSA");
			sign.initSign(privKey);

			byte[] buffer = new byte[1024];
			int n; 
			while((n = in.read(buffer))> 0) {
				sign.update(buffer, 0, n);
			}
			byte[] señal = sign.sign();
			out.write(señal);
			

		}
		

	}
}
