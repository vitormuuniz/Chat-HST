package br.com.hst.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
	private Socket client;
	private String name;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Scanner sc;

	public ClientHandler(Socket client, String name, DataInputStream dis, DataOutputStream dos) {
		this.client = client;
		this.name = name;
		this.dis = dis;
		this.dos = dos;
	}

	@Override
	public void run() {
		while (true) {
			try {
				int opcao = dis.readInt();
				String target = dis.readUTF();
				switch(opcao) {
				case 1:
					sendFile(target);
					break;
				case 2:
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendFile(String target) {
		try {
			DataOutputStream targetClient = Server.getClientList().get(target).getDataOutputStream();
			targetClient.writeUTF("arquivo");
			
			String filename = dis.readUTF();
			int fileSize = dis.readInt();
			int bufferSize = fileSize < 4096 ? fileSize : 4096;

			targetClient.writeUTF("received_" + System.currentTimeMillis() + "_" + filename);
			targetClient.writeInt(fileSize);

			byte[] buffer = new byte[bufferSize];
			int remaining = fileSize;
			while (remaining > 0) {
				dis.read(buffer);
				if (remaining < bufferSize) {
					byte[] bufferOld = buffer;
					buffer = new byte[remaining];
					System.arraycopy(bufferOld, 0, buffer, 0, buffer.length);
				}
				targetClient.write(buffer);
				remaining -= buffer.length;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Falha ao ler request");
		}
	}

	public DataOutputStream getDataOutputStream() {
		return dos;
	}

	protected void sendMessage(String message) {
		try {
			dos.writeUTF(message);
		} catch (IOException e) {
			System.out.println("Falha ao enviar mensagem");
		}
	}

}