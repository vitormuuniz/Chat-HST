package br.com.hst.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.Scanner;

public class ClientHandler implements Runnable {
	private Map<String, ClientHandler> clientList;
	private Socket client;
	private String name;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Scanner sc;

	public ClientHandler(Socket client, Map<String, ClientHandler> clientList, String name, DataInputStream dis, DataOutputStream dos) {
		this.client = client;
		this.clientList = clientList;
		this.name = name;
		this.dis = dis;
		this.dos = dos;
	}

	@Override
	public void run() {	
		while (true) {
			try {
				dos.writeUTF("\n===== Digite a opção ======\n" 
							+ "1 - Usuários Online\n"
							+ "2 - Enviar Mensagens\n" 
							+ "3 - Enviar Arquivos\n" 
							+ "4 - Sair\n"
							+ "==========================");
				int opcao = dis.readInt();					
				switch(opcao) {
				case 1:
					listUser();
					break;
				case 2:
					break;
				case 3:
					String target = dis.readUTF();
					sendFile(target);
					break;
				case 4:
					closeConnection(client, dos);
					throw new SocketException("Erro");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void listUser() throws IOException {
		dos.writeUTF("lista_usuario");
		clientList =  Server.getClientList();
		dos.writeInt(clientList.size());
		clientList.forEach((key,value) -> {
			try {
				dos.writeUTF(key);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});		
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
	
	protected void closeConnection(Socket client, DataOutputStream dos) {
		try {			
			System.out.println("O cliente" + this.name + " está encerrando a conexão com servidor");
			dos.writeUTF("fecha_conexao");
			dos.writeUTF("Você está encerrando a conexão na porta " +  client.getPort() + ".");
			clientList.remove(name);
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}