# Splunk_Grafana_Prometheus

1) Instalar o Splunk com docker-compose

````
$ cd observability

$ sudo docker-compose up -d
````

2) Configurar um token de acesso ao Splunk para o envio dos logs pela aplicação

* Acessar o Splunk
  URL: http://localhost:4000'
  username: admin
  password: changed!

2.1) Desabilitar SSL
````
Settings > Data inputs > HTTP Event Collector > Global Settings > Desmarque "Enable SSL"
````
2.2) Criar Token
````
Settings > Data inputs > HTTP Event Collector > New Token
````
Em "Select Source":
````
Name=Nome que identifique sua aplicação (Ex: my-api)
Source name override=pode ser o mesmo nome (Ex: my-api)
`````
Em "Input Settings":
````
Select > Select Source Type > Structured > _json
````
Click em "Create a new index" na parte inferior, dê um nome ao index (Ex: my-api-idx) e click em "Save"

Selecione o index recém criado para ele ir para os "Selected item(s)"

Avance para "Review"

Click em "Submit"

3) Guarde o nome do index, o nome do source e o valor do token necessários para configurar a aplicação 
no arquivo src/main/resources/log4j2-spring.xml

4) Acesse http://localhost:4000/en-US/app/search/search
e busque por:
````
index="my-api-idx"
````

# Passos para configurar um projeto para usar o Splunk:

#Em Maven

-Adicionar o "splunk-artifactory"

````
<repositories>
  <repository>
    <id>splunk-artifactory</id>
    <name>Splunk Releases</name>
    <url>http://splunk.jfrog.io/splunk/ext-releases-local</url>
  </repository>
</repositories>

-Adicionar as dependências

<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>

<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter</artifactId>
	<exclusions>
		<exclusion>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-logging</artifactId>
		</exclusion>
	</exclusions>
</dependency>

<dependency>
	<groupId>com.splunk.logging</groupId>
	<artifactId>splunk-library-javalogging</artifactId>
	<version>1.8.0</version>
</dependency>

````

#Em Gradle

-Adicionar o "splunk-artifactory"

````
repositories {
    mavenCentral()
    maven {'
    url "https://splunk.jfrog.io/splunk/ext-releases-local"
    }
}
````

-Adicionar as dependências

````
implementation 'org.springframework.boot:spring-boot-starter-log4j2'

configurations.implementation {
exclude group: 'org.springframework.boot', module: 'spring-boot-starter-logging'
}

runtimeOnly 'com.splunk.logging:splunk-library-javalogging:1.8.0'
````

-Criar o arquivo em src/main/resources/log4j2-spring.xml

````
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
    <Appenders>
        <Console name="console" target="SYSTEM_OUT">
            <PatternLayout
                    pattern="%style{%d{ISO8601}} %highlight{%-5level }[%style{%t}{bright,blue}] %style{%C{10}}{bright,yellow}: %msg%n%throwable" />
        </Console>
        <SplunkHttp
                name="splunkhttp"
                url="http://localhost:8088"
                token="TOKEN_CRIADO_NO_SPLUNK
                "
                host="localhost"
                index="INDEX_CRIADO_NO_SPLUNK"
                type="raw"
                source="SOURCE_CRIADO_NO_SPLUNK"
                sourcetype="_json"
                messageFormat="text"
                disableCertificateValidation="true"
        >
            <PatternLayout pattern="%m" />
        </SplunkHttp>
    </Appenders>
    <Loggers>
        <!-- LOG everything at INFO level -->
        <Root level="info">
            <AppenderRef ref="console" />
            <AppenderRef ref="splunkhttp" />
        </Root>
    </Loggers>
</Configuration>
````

Referências:
https://github.com/splunk/splunk-sdk-java