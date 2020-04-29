# Treinamento Chat

    Aplicação que permite comunicação entre vários clientes conectados à um mesmo servidor
    
## Cliente

    Ao ser inicializado mostra o seguinte menu:
    
    1 - Visualizar usuários online
    2 - Enviar mensagem
    3 - Enviar arquivo
    4 - Sair
   
    Opção 1 -> Mostra a lista de usuarios online; 
    Opção 2 -> Escolhe-se um usuario disponível e permite o envio de mensagem ao destinatário;  
    Opção 3 -> Escolhe-se um usuario disponível e permite o envio de arquivo ao destinatário; 
    Opção 4 -> Finaliza execução;
   
## Servidor

    Aplicativo que trata varios clientes simultaneos. Suas principais tarefas são:
    - Receber mensagem/arquivo de um cliente e enviar para o destinatario,
    - Controlar lista de usuarios conectados [nickname / IP] ( enviar brodcast para clientes ).  
    
## Arquitetura

![](https://www.caelum.com.br/apostila-java-orientacao-objetos/images/apendicesockets/cliente-servidor.png)
