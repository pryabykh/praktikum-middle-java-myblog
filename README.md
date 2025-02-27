# praktikum-middle-java-myblog

## Java
Приложение запускается на версии Java 21

## База данных
Для работы необходима база данных **PostgreSQL 16.0**

Перед стартом приложения нужно убедиться, что существует БД **myblog**

Для удобства можно воспользоваться docker-compose.yml файлом Docker/PostgreSQL16/docker-compose.yml для поднятия локальной БД

Данные подключения указываются в **resources/application.properties**

## Maven
При разработке приложения использовался Maven версии 3.9.9

## Сборка
Для сборки проекта необходимо запустить команду
```
mvn clean package
```