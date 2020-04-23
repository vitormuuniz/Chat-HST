# Treinamento Chat

    Aplicação console que permite conversa entre vários clientes
    
## Cliente

    Ao ser inicializado mostra o seguinte menu:
    
    1 - Visualizar usuários online
    2 - Enviar mensagem
    3 - Enviar arquivo
    4 - Sair
   
    Opção 1 -> Mostra a lista de usuarios online,   
    Opção 2 -> Escolha do usuario e em seguida permite a entrada da mensagem a ser enviada,   
    Opção 3 -> Entrada do caminho de um arquivo a ser enviado,   
    Opção 4 -> Sai do programa.  
   
## Servidor

    Aplicativo que trata varios clientes simultaneos. Suas principais tarefas são:
    - Receber mensagem/arquivo de um cliente e enviar para o destinatario,
    - Controlar lista de usuarios conectados [nickname / IP] ( enviar brodcast para clientes ).  
    
## Arquitetura

![](https://www.caelum.com.br/apostila-java-orientacao-objetos/images/apendicesockets/cliente-servidor.png)
