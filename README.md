## Системные требования ##
***Версия java:***
java 22.0.2\
***Версия Gradle:***
Gradle 8.13
### Зависимости ###
#### JUnit Jupiter(JUnit 5) #### 
  - https://junit.org/junit5
  - testImplementation(platform("org.junit:junit-bom:5.10.0"))\
    testImplementation("org.junit.jupiter:junit-jupiter")

## Инструкция по запуску: ##
1. ### Через gradle ###
  - gradle build
  - java -cp build/libs/CFT_Test_Task-1.0-SNAPSHOT.jar ru.chepenkov.Main [-o <arg>] [-p <arg>] [-f | -s] [-a] [input files]
2. ### Через ./gradlew ###
  - ./gradlew run --args="[-o <arg>] [-p <arg>] [-f | -s] [-a] [input files]"

## Особенности реализации ##
- Обработка файлов происходит в многопоточном режиме
- При указывании несуществующего файла, программа "не падаёт", а просто не учитывает его
- Целыми числами считаются те числа, которые являются целыми или в конечном счёте являются целыми в математике, в частности, 1e3 (1000), 1.2e2 (120), 50 и тд.
- Ограничений на размер чисел нет. Используются типы данных BigInteger и BigDecimal
- Присутствуют юнит тесты

## Сравнение многопоточного и однопоточного исполнения ##
<img width="1490" height="774" alt="image" src="https://github.com/user-attachments/assets/f63b8e1c-fbcd-4b64-a130-b102c89dcfff" />
