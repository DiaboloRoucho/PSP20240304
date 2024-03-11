package dam.psp;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import dam.psp.Servidor;

public class Certificador implements Runnable{

	Socket cliente;
	DataOutputStream out;

	public Certificador(Socket cliente) {
		this.cliente = cliente;
		try {
			this.out = new DataOutputStream(cliente.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			DataInputStream in = new DataInputStream(cliente.getInputStream());
			String c = in.readUTF();
			switch (c) {
			case "hash":
				getHash(in);
				break;
			case "cert":
				guardaCertificado(in);
				break;
			case "cifrar":
				cifrar(in);
				break;
			default:
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void getHash(DataInputStream in) {
		String algoritmo;
		try {
			algoritmo = in.readUTF();
			byte[] bytes = in.readAllBytes();
			MessageDigest md = MessageDigest.getInstance(algoritmo);
			if (bytes.length > 0) {
				envMensaje("OK:" + Base64.getEncoder().encodeToString(md.digest(bytes)));
			} else
				envMensaje("ERROR: No hay datos");
		} catch (NoSuchAlgorithmException e) {
			envMensaje("ERROR: Problema con el algoritmo");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void guardaCertificado(DataInputStream in) {
		try {
			String alias = in.readUTF();
			String certifBase64 = in.readUTF();
			CertificateFactory f = CertificateFactory.getInstance("X.509");
			byte[] certificadoCifrado = Base64.getDecoder().decode(certifBase64);
			Certificate certificado = f.generateCertificate(new ByteArrayInputStream(certificadoCifrado));
			Servidor.ks.setCertificateEntry(alias, certificado);
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(certifBase64.getBytes());
			envMensaje("OK:" + Base64.getEncoder().encode(md.digest()));
		} catch (CertificateException e) {
			envMensaje("ERROR: Certificado inexistente");
			e.printStackTrace();
		} catch (KeyStoreException e) {
			envMensaje("ERROR: Problema con el keystore");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			envMensaje("ERROR: Algoritmo invalido");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

	}

	private void cifrar(DataInputStream in) {
		try {
			String alias = in.readUTF();
			Certificate cert = Servidor.ks.getCertificate(alias);
			if (cert == null)
				envMensaje("ERROR: No hay certificado con el nombre:  " + alias);
			else {
				Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				c.init(Cipher.ENCRYPT_MODE, cert.getPublicKey());

				int n;
				byte[] buffer = new byte[256];
				while ((n = in.read(buffer)) != -1) {
					byte[] coded = c.doFinal(buffer, 0, n);
					envMensaje("OK:" + Base64.getEncoder().encodeToString(coded));
				}
				envMensaje("----Fin del cifrado----");
			}
		} catch (KeyStoreException e) {
			envMensaje("ERROR: Problema con el keystore");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			envMensaje("ERROR: Algoritmo invalido");
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			envMensaje("ERROR: Algoritmo invalido");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			envMensaje("ERROR: Key invalida");
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
	}

	private void envMensaje(String msg) {
		try {
			out.writeUTF(msg);
		} catch (IOException e) {
			System.err.println("Error en la comunicacion con el cliente");
			e.printStackTrace();
		}
	}
}
