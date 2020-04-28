package br.com.hst.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com.hst.client.constants.MenuConstants;

public class RequestHandler {

	private Socket client;
	private Scanner sc;
	private List<String> listUsers = new ArrayList<String>();

	public RequestHandler(Socket client) {
		this.client = client;
	}

	protected synchronized void startReceiveThread(DataInputStream dis, DataOutputStream dos) {
		Thread t = new Thread(new Runnable() {
			@Override
			synchronized public void run() {
				while (true) {
					try {
						String opcao = dis.readUTF();
						switch (opcao) {
						case MenuConstants.MENU:
							showMenu();
							break;
						case MenuConstants.LIST_USERS:
							receiveUsersList(dis);
							break;
						case MenuConstants.RECEIVE_MESSAGE:
							receiveMessage(dis);
							showMenu();
							break;
						case MenuConstants.RECEIVE_FILE:
							receiveFile(dis);
							showMenu();
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			private void showMenu() {
				System.out.println("\n==============================\n1 - Visualizar usuários online\r\n" + 
							"2 - Enviar mensagem\r\n" +
							"3 - Enviar arquivos\r\n" + 
							"4 - Sair\n");
				System.out.println("> Escolha uma operação: ");
			}
		});
		t.start();
	}

	protected synchronized void startInputThread(DataInputStream dis, DataOutputStream dos, String name) {

		Thread t = new Thread(new Runnable() {

			@Override
			synchronized public void run() {
				while (true) {
					try {
						sc = new Scanner(System.in);
						String option = sc.nextLine();
						dos.writeUTF(option);
						switch (option) {
						case MenuConstants.SEND_MESSAGE:
							sendMessage(dos, name);
							break;
						case MenuConstants.SEND_FILE:
							sendFile(dos, name);
							break;
						case MenuConstants.EXIT:
							closeConnection();
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		t.start();
	}

	private void sendMessage(DataOutputStream dos, String name) {
		try {
			Scanner sc = new Scanner(System.in);
			Thread.sleep(1000);
			String target = userTarget(name, sc);
			dos.writeUTF(target);

			String message = sc.nextLine();
			System.out.print("> Para " + target + ": ");
			
			message = sc.nextLine();
			
			if (!message.isBlank()) {
				dos.writeUTF(message);
			}
		} catch (IOException e) {
			System.out.println("Falha ao enviar mensagem.");
		} catch (InterruptedException e) {
			System.out.println("Falha ao listar usuários.");
		}
		listUsers.clear();
	}

	private void sendFile(DataOutputStream dos, String name) {
		FileInputStream fis = null;
		try {
			sc = new Scanner(System.in);
			Thread.sleep(1000);

			String target = userTarget(name, sc);
			dos.writeUTF(target);

			System.out.println("Digite o nome do Arquivo: ");
			String filename = sc.next();
			File archive = new File(filename);
			fis = new FileInputStream(archive);
			

			dos.writeUTF(archive.getName());
			dos.writeLong(archive.length());

			int lido = 0;
			byte[] buffer = new byte[4092];

			while ((lido = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, lido);
				dos.flush();
			}
			System.out.println("\nArquivo " + filename + " enviado para " + target);
		} catch (IOException e) {
			System.out.println("Falha ao enviar arquivo.");
		} catch (InterruptedException e) {
			System.out.println("Falha ao listar usuários.");
		}
		listUsers.clear();
	}

	private void receiveFile(DataInputStream dis) {
		FileOutputStream fos = null;
		try {
			String filename = dis.readUTF();
			long fileSize = dis.readLong();

			Path path = Paths.get("out" + File.separator + filename);
			if (!Files.exists(path.getParent())) {
				Files.createDirectory(path.getParent());
				Files.createFile(path);
			}

			fos = new FileOutputStream(path.toFile());

			long lido = 0;
			byte[] buffer = new byte[4092];

			while (fileSize > 0 && (lido = dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
				fos.write(buffer, 0, (int) lido);
				fileSize -= lido;
			}

			System.out.println("\n\nArquivo recebido: " + filename);
			System.out.println("Tamanho do arquivo: " + fileSize + " bytes\n");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Falha ao obter arquivo");
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	protected void closeConnection() throws IOException {
		System.out.println("Encerrando conexão...");
		client.close();
		System.exit(0);
	}

	protected void receiveMessage(DataInputStream dis) {
		try {
			String message = dis.readUTF();
			System.out.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void receiveUsersList(DataInputStream dis) throws IOException {
		int num = dis.readInt();
		System.out.println("\nNúmero de usuários online: " + num);
		for (int i = 0; i < num; i++) {
			String user = dis.readUTF();
			listUsers.add(user);
			System.out.println(user);
		}
	}

	private String userTarget(String name, Scanner sc) {
		System.out.println("\nInforme o Destinatário: ");
		String target = "           ";
		while (!listUsers.contains(target) || target.equals(name)) {
			target = sc.next();

			if (!listUsers.contains(target)) {
				System.err.println("Destinatário inválido, digite novamente:");
				continue;
			}
			if (target.equals(name)) {
				System.err.println("Você digitou o próprio nome, digite um destinatário válido:");
				continue;
			}
		}
		return target;
	}
}