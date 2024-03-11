package dam.psp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dam.psp.Certificador;

public class Servidor {

	static KeyStore ks;


	public static void main(String[] args) {
		 try {
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
		} catch (KeyStoreException e) {e.printStackTrace();}
		 
		 ExecutorService es = Executors.newCachedThreadPool();
		try (ServerSocket ss = new ServerSocket(9000)) {
			while (true) {
				try {
					System.out.println("Esperando clientes...");
					Socket s = ss.accept();
					System.out.println("Cliente encontrado: " + s.getInetAddress().toString());
					es.submit(new Certificador(s));
				} catch (Exception e) {
					System.err.println(e);
				}
			}
		} catch (IOException e1) {
			System.err.println("Error IO: " + e1);
		}

	}

}
