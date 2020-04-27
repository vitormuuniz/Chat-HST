package br.com.hst.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server {

	private static Socket client;
	private static ServerSocket serverSocket;
	private static Map<String, ClientHandler> clientList;
	private static Scanner sc;

	public static void main(String[] args) {
		System.out.println("Digite a porta desejada do servidor: ");
		try {

			sc = new Scanner(System.in);
			int port = sc.nextInt();
			serverSocket = new ServerSocket(port);
			System.out.println("------------------ Servidor iniciado na porta " + port + " ------------------");
			clientList = new HashMap<>();
			while (true) {
				client = serverSocket.accept();
				System.out.println("Novo cliente conectado: " + client);
				DataInputStream dis = new DataInputStream(client.getInputStream());
				DataOutputStream dos = new DataOutputStream(client.getOutputStream());
				String name = dis.readUTF();
				ClientHandler clientHandler = new ClientHandler(client, clientList, name, dis, dos);
				clientList.put(name, clientHandler);
				Thread t = new Thread(clientHandler);
				t.start();
			}
		} catch (IOException ioe) {
			System.out.println("Erro ao iniciar servidor");
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					System.out.println("Falha ao encerrar o servidor");
				}
			}
		}
	}

	protected static Map<String, ClientHandler> getClientList() {
		return clientList;
	}
}