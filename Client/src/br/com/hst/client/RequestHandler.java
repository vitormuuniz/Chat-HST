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
import java.util.Scanner;

import br.com.hst.client.constants.MenuConstants;

public class RequestHandler {
	
	private Socket client;

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
							System.out.println("\n======= Escolha uma opera��o =======\n" 
									+ "  1 - Visualizar Usu�rios Online\r\n"
									+ "  2 - Enviar mensagem\r\n" 
									+ "  3 - Enviar arquivos\r\n" 
									+ "  4 - Sair\n"
									+ "====================================\n");
							break;
						case MenuConstants.LIST_USERS:
							receiveList(dis);
							break;
						case "2":
							break;
						case MenuConstants.RECIEVER_FILES:
							receiveFile(dis);
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

	protected synchronized void startInputThread(DataInputStream dis, DataOutputStream dos) {

		Thread t = new Thread(new Runnable() {

			@Override
			synchronized public void run() {
				while (true) {
					Scanner sc = new Scanner(System.in);
					int option = sc.nextInt();
					try {
						dos.writeInt(option);
						switch (option) {
						case 2:
							break;
						case 3:
							sendFile(dos);
							break;
						case 4:
							System.out.println("Encerrando conex�o...");
							client.close();
							System.exit(0);
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

	private void sendFile(DataOutputStream dos) {
		FileInputStream fis = null;
		try {

			Scanner sc = new Scanner(System.in);
			System.out.println("Digite o nome do destinat�rio: ");
			String target = sc.next();
			dos.writeUTF(target);

			System.out.println("Digite o nome do Arquivo: ");
			String filename = sc.next();

			fis = new FileInputStream(new File(filename));
			int fileSize = fis.available();

			dos.writeUTF(filename);
			dos.writeInt(fileSize);

			int bufferSize = fileSize < 4096 ? fileSize : 4096;
			byte[] buffer = new byte[bufferSize];

			int remaining = fileSize;
			while (remaining > 0) {
				fis.read(buffer);
				if (remaining < bufferSize) {
					byte[] bufferOld = buffer;
					buffer = new byte[remaining];
					System.arraycopy(bufferOld, 0, buffer, 0, buffer.length);
				}
				dos.write(buffer);
				remaining -= buffer.length;
			}
		} catch (IOException e) {
			System.out.println("Falha ao enviar arquivo");
		}
	}

	private void receiveFile(DataInputStream dis) {
		FileOutputStream fos = null;
		try {
			String filename = dis.readUTF();
			System.out.println("Arquivo recebido: " + filename);
			int fileSize = dis.readInt();
			System.out.println("Tamanho do arquivo: " + fileSize);

			Path path = Paths.get("out" + File.separator + filename);
			if (!Files.exists(path.getParent())) {
				Files.createDirectory(path.getParent());
				Files.createFile(path);
			}

			fos = new FileOutputStream(path.toFile());

			int bufferSize = fileSize < 4096 ? fileSize : 4096;
			byte[] buffer = new byte[bufferSize];
			int remaining = fileSize;
			while (remaining > 0) {
				dis.read(buffer);
				if (remaining < bufferSize) {
					byte[] bufferOld = buffer;
					buffer = new byte[remaining];
					System.arraycopy(bufferOld, 0, buffer, 0, buffer.length);
				}
				fos.write(buffer);
				remaining -= buffer.length;
			}
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

	protected void closeConnection(DataInputStream dis) throws IOException {
		String close = dis.readUTF();
		System.out.println(close);
	}

	protected void receiveList(DataInputStream dis) throws IOException {
		int num = dis.readInt();
		System.out.println("\nQuantidade de Usu�rios: " + num);
		for (int i = 0; i < num; i++) {
			String users = dis.readUTF();
			System.out.println(users);
		}
	}
}