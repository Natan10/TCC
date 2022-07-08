# Fogify

pré-requisítos: É necessário possui o docker, docker compose e docker swarm instalados para utilizar a ferramenta. Abaixo seguem links de instalação e tutorias.

- <https://docs.docker.com/engine/install/ubuntu/>
- <https://docs.docker.com/desktop/windows/install/>
- <https://docs.docker.com/compose/install/>

</br>

### Como usar o Fogify

primeiro inicie o cluster do docker swarm

```
docker swarm init --advertise-addr <hostname>
```

depois instale o iproute, caso não tenha essa versão instale o iproute2

```
apt-get install iproute
```

Baixe o arquivo docker-compose do repositório e crie um arquivo .env no mesmo diretório com as configurações abaixo:

```
MANAGER_NAME=hostname
MANAGER_IP=host_ip
HOST_IP=host_ip
CPU_OVERPROVISIONING_PERCENTAGE=0
RAM_OVERPROVISIONING_PERCENTAGE=0
CPU_FREQ=30
```

Substituir o hostname e o host_ip pelas configuracoes da sua maquina, basta rodar no terminal

```
"hostname -I" e depois "hostname"
```

Após isso rode o comando no mesmo diretório do docker-compose.yaml para subir os componentes do Fogify, bem como Jupyter Notebook

```
sudo docker-compose -p fogemulator up
```

**Para acessar o Jypter Notebook do Fogify basta clicar no link que será gerado ao rodar o comando acima**

```
# exemplo do link
http://127.0.0.1:8888/?token=<tokenID>
```

### Erros

os possíveis erros que podem acontecer durante a instalação.

- falta do docker
- falta de inicializar o cluster swarm
- erro ao configurar o cluster swarm com o hostname errado
- erro na hora de subir o Jupyter Notebook

### Topologia

![Topologia utilizada](/imagens/fogify/fogify-topology.png)

### Uso Prático

Inicialmente, para utilizar o fogify é necessaŕio definir as imagens docker que representarão seus serviços. No exemplo usado, cada serviço utilizado tem seu próprio Dockerfile.

Para construir as imagems basta rodar o comando:

```
docker build -t <nomeDoServiço> .
```

Após rodar as imagens é necessário configurar os Blueprints em um arquivo docker-compose.yml. Inicialmente, temos a configuração dos serviços, onde cada serviço irá representar um nó da nossa arquitetura mostrada acima.

```
version: "3.7"
services:
  oxi-workload:
    image: iot_node
  cloud-server:
    image: cloud_node
  mec-svc-1:
    image: fog_node
  mec-svc-2:
    image: fog_node
```

Após a configuração dos serviços, iremos definir os Blueprints na seção **x-fogify**. Começamos definindo as redes.

```
x-fogify:
  networks:
    - downlink:
        bandwidth: 5Mbps
        latency:
          delay: 50ms
      name: internet
      uplink:
        bandwidth: 10Mbps
        drop: 0.1%
        latency:
          delay: 50ms
    - bidirectional:
        bandwidth: 100Mbps
        drop: 0.1%
        latency:
          delay: 5ms
      name: edge-net-1
    - bidirectional:
        bandwidth: 100Mbps
        drop: 0.1%
        latency:
          delay: 5ms
      name: edge-net-2
```

Agora iremos definir os recursos dos nós.

```
 nodes:
    - capabilities:
        memory: 4G
        processor:
          clock_speed: 1400
          cores: 4
      name: cloud-server-node
    - capabilities:
        memory: 2G
        processor:
          clock_speed: 1400
          cores: 2
      name: edge-node
    - capabilities:
        memory: 0.5G
        processor:
          clock_speed: 700
          cores: 1
      name: oxi-node
```

E por último definiremos os Blueprints que são um conjunto de um serviço, uma rede, um nó e um label.

```
  topology:
    - label: cloud-server
      networks:
        - internet
      node: cloud-server-node
      service: cloud-server
      replicas: 1

    - label: mec-svc-1
      networks:
        - edge-net-1
        - internet
      node: edge-node
      replicas: 1
      service: mec-svc-1

    - label: mec-svc-2
      networks:
        - edge-net-2
        - internet
      node: edge-node
      replicas: 1
      service: mec-svc-2

    - label: oxi-workload-1
      networks:
        - edge-net-1
        - internet
      node: oxi-node
      replicas: 1
      service: oxi-workload

    - label: oxi-workload-2
      networks:
        - edge-net-2
        - internet
      node: oxi-node
      replicas: 1
      service: oxi-workload
```

O arquivo docker-compose.yaml completo está na mesma pasta e contém toda essa definição. Com o arquivo yaml iremos realizar o deploy da arquitetura pelo Jupyter Notebook utilizando o SDK disponibilizado pelo Fogify.

Faça o upload do arquivo docker-compose.yaml para o Jupyter Notebook e siga os passos da imagem abaixo. Se tudo estiver configurado irão aparecer os serviços que foram deployados no cluster swarm.

![Deploy fogify](/imagens/fogify/fogify-deploy.png)

Para ver as métricas basta utilizar o nome do serviço.
![Métricas fogify](/imagens/fogify/fogify-metrics.png)

Também é possível realizar ações durante a execução do cenário. Para saber mais sobre os tipos de ações disponíveis acesse a [documentação oficial](https://ucy-linc-lab.github.io/fogify/actions-and-scenarios.html).

Abaixo demostramos o uso das ações de stress, horizontal_scaling_down e horizontal_scaling_up.

**Stress**: Utilizada para aumentar a carga de uso de cpu

```
fogify.stress('cloud-server.1',duration=100,cpu=50)
```

![stress](/imagens/fogify/fogigy-stress.png)

</br>

**Horizontal Scaling Down**: Diminuir o número de instâncias de determinado serviço

```
fogify.horizontal_scaling_down('oxi-workload-1')
```

![scaling_down](/imagens/fogify/horizontal_scaling_down_fogify.png)

</br>

**Horizontal Scaling Up**: Aumentar o número de instâncias de determinado serviço

```
fogify.horizontal_scaling_up('oxi-workload-1')
```

![scaling_down](/imagens/fogify/horizontal_scaling_up_fogify.png)

Para derrubar os serviços basta realizar o undeploy deles pelo próprio fogify.

```
fogify.undeploy()
```

Todo o código utilizado no docker e para configuração dos serviços está na pasta **health_care** e os códigos utilizados no Jupyter estão dentro da pasta **notbooks/TCC.ipynb**.
