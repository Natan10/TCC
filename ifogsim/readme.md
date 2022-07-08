# IFogSim

### Instalando o JDK no Ubuntu

```
sudo apt-get install openjdk-11jdk
```

### Verificar versao do java

```
java --version
```

### Configurar as variaveis de ambiente do java

```
sudo update-alternatives --config java
```

### Copie o caminho de instalacao do java e abra o arquivo .bashrc

```
sudo gedit ~/.bashrc
```

### Va ao final do arquivo e adicione as seguintes linhas

```
JAVA_HOME=<cole o caminho copiado no comando acima>
export JAVA_HOME
export PATH=$PATH:$JAVA_HOME
```

### Instalacao eclipse

Baixe o pacote no [link](https://www.eclipse.org/downloads/download.php?file=/oomph/epp/2022-06/R/eclipse-inst-jre-linux64.tar.gz). Descompacte o pacote e clique no instalador do eclipse.

### Feita a instalacao siga os passaos abaixo

Crie uma pasta e rode

```
git clone https://github.com/Cloudslab/iFogSim
```

Inicie o eclipse e ao criar um projeto aponte a localizacao da pasta para os arquivos baixados do IFogSim. Crie suas classes dentro da pasta src/org/fog/test/perfeval.

**OBS**: A instalação da JDK no windows e mais fácil. O resto da instalação é o mesmo, basta baixar o repositório e seguir a partir da instalação do eclipse.

### Módulos

![Topologia utilizada em Nuvem](/imagens/ifogsim/ifogsim-topology-modulos.png)

### Topologia

Topologia de Névoa
![Topologia utilizada em Névoa](/imagens/ifogsim/ifogsim-topology-cenario-fog.png)

Topologia de Nuvem
![Topologia utilizada em Nuvem](/imagens/ifogsim/ifogsim-topology-cloud.png)

### Códigos

Para utilizar os códigos, basta importar o arquivo **AppHealth.java** dentro da pasta src/org/fog/test/perfeval e rodar o arquivo.
