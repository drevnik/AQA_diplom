# Дипломный проект

* SUT, gate-simulator находятся в папке [/artifacts](https://github.com/falkona/qa-diploma/tree/master/artifacts)


### Как запустить

#### MySQL:
1. Запустить контейнеры: MySQL, Node.js

docker-compose up
    
2. Запустить SUT

java -jar artifacts/aqa-shop.jar -P:jdbc.url=jdbc:mysql://192.168.99.100:3306/app -P:jdbc.user=app -P:jdbc.password=pass

3. Запустить тесты
gradlew test -Dtest.db.url=jdbc:mysql://192.168.99.100:3306/app
 
4. Остановить контейнеры
 
docker-compose down
   
#### Для работы с Postgres
1. Запустить контейнеры: Postgres, Node.js

docker-compose -f docker-compose-ps.yml up -d  

2. Запустить SUT

java -jar artifacts/aqa-shop.jar -P:jdbc.url=jdbc:postgresql://192.168.99.100:3306/app -P:jdbc.user=app -P:jdbc.password=pass

3. Запустить тесты

gradlew test -Dtest.db.url=jdbc:postgresql://192.168.99.100:5432/app

4. После прогона тестов остановить контейнеры

 docker-compose down

  
