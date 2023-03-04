# XlsxToMongoDb
## Приложение создаёт из xlsx базу данных MongoDb и предоставляет доступ через http и TelegrammBot.
В файл private.propertes необходимо записать:
[x] admin password  
[x] user password
[x] bot name
[x] bot key (выдает fatherBot в телеграмм)
Приложение использует https  и в директории приложения необходим сертификат p12.  (можно воспользоватсья командной для генерации 
keytool -import -alias springboot -file myCertificate.crt -keystore springboot.p12 -storepass password).  
Для создания базы необходимо отправить POST по адресу $//mongo/upload  с BasicAuth username amdmin password установленный в private.propertes.  
Затем Post запрос по адресу $//mongo/createBase.  
Информация  endpoint будет доступна по адрес $/mongo/swagger-ui/index.html  или [спецификация http](https://app.swaggerhub.com/apis/dkantserev/mongo/1.0.0) .
