# Front-end многопоточного чата
Проект является мини лабораторной работой по изучению многопоточности и взаимодействия с сетью
## Main.java
Класс содержит подключение к порту, прослушивание и обработку входящих сообщений в отдельных потоках с помощью Threads.  
Так же в этом классе реализовано чтение сообщений пользователя (в терминале) и отправка их на сервер. Для имитации не очень анонимного чата в сообщении надо указать имя пользователя  
Пример сообщения: `user1:test` 
* user1 - имя пользователя
* : - разделитель
* test - текст сообщения
   
## IncomingMessagesHandler.java
Класс нужен для реализации обработчика сообщений от других пользователей (их отправляет сервер) в отдельных потоках и выводе текста в консоль

Пример получаемого сообщения: `user2:prikol`
Полученное сообщение рассылается всем подключенным клиентам (даже отправителю)

## Серверная часть описана в проекте chat_server 
https://github.com/Egor-Pavlov/chat_server
