# Fogify

### Instalação do Docker

TODO

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

Após isso rode o comando no mesmo diretório do docker-compose.yaml para subir os componentes do Fogify, bem como Jypyter Notebook

```
sudo docker-compose -p fogemulator up
```

**Para acessar o Jypter Notebook do Fogify basta clicar no link que será gerado ao rodar o comando acima**

```
# exemplo do link
http://127.0.0.1:8888/?token=<tokenID>
```
