# praktikum-middle-java-myblog-v1.0

## Java
Приложение запускается на версии Java 21

## База данных
Для работы необходима база данных **PostgreSQL 16.0**

Перед стартом приложения нужно убедиться, что существует БД **myblog**

Для удобства можно воспользоваться docker-compose.yml файлом Docker/PostgreSQL16/docker-compose.yml для поднятия локальной БД

Данные подключения указываются в **resources/application.properties**

## Maven
При разработке приложения использовался Maven версии 3.9.9

## Запуск тестов
Для запуска тестов необходимо запустить команду
```
mvn clean test
```

## Сборка
Для сборки проекта необходимо запустить команду
```
mvn clean package
```

## Запуск веб-приложения в контейнере сервлетов
Скачать контейнер сервлетов Tomcat https://tomcat.apache.org/download-10.cgi

Разархивируйте файл и перейдите в директорию с Tomcat. В ней будет:

RUNNING.txt — файл, в котором лежит подробная инструкция о том, как запускать сервер;

webapps — директория, в которой расположены war-архивы;

bin — директория, в которой собраны основные команды, и т. д.

В дирректорию webapps нужно положить сформированный war файл на этапе сборки.

Затем из директории bin нужно запустить startup.sh (или startup.bat для Windows).

На момомент запуска дожлна быть доступна БД