# TCC

## Tema

**Análise Comparativa De Simuladores e Emuladores De Computação em Névoa para Internet Das Coisas**

</br>

Neste repositório estão todos os códigos utilizados no trabalho acima.

A Estrutura de pastas está dividida em:

- fogify: Contém todos os códigos utilizados na análise prática do Fogify e um readme.md com instruções de instalação e uso da ferramenta.
- ifogsim: Contém todos os códigos utilizados na análise prática do IFogSim e um readme.md com instruções de instalação e uso.
- notebooks: Contém todos os arquivos do Jupyter Notebook utilizados para geração dos dados utilizados na análise prática das ferramentas e os códigos que foram utilizados para geração dos gráficos.

## Cenário utilizados nas aplicações

Em 2019 o mundo sofreu com o inicio de uma grave pandemia causada pelo novo
coronavirus que é responsável por causar a doença COVID-19 que causa o comprometimento
pulmonar, levando assim os pacientes a óbito. Os sistemas de saúde mundial ficaram sobrecarre-
gados com o número de pacientes com baixa saturação de oxigênio devido à problemas causados
pelo vírus, de tal forma que o número de leitos não era suficiente para comportar a demanda de
pacientes. Pensando nesse cenário, podemos utilizar a computação em névoa para auxiliar no
monitoramento dos pacientes de COVID-19. A seguir iremos explicar as camadas da arquitetura
de névoa usadas no problema.

### Camada de IoT

Na primeira camada temos os sensores e atuadores. Os sensores são os oxímetros
que medem o nível de saturação de oxigênio no sangue. O nível de saturação de oxigênio
considerados normal é de 95% para a maioria das pessoas saudáveis e um nivel de 92% de
saturação [referência](https://www.tuasaude.com/oximetria/) já pode indicar alguma deficiência de oxigênio que atinge os
tecidos do corpo. Isso é mais grave em pacientes de covid-19, pois muitas vezes, eles não sentem
falta de ar, porém a saturação de oxigênio deles, segundo alguns médicos pode ficar entre 70% e
80% ou em casos mais graves ate 50%. Dessa forma, é crucial que o oxímetro que ficaria no leito
do paciente envie dados em tempo real com a menor latência possível, pois isso pode salvar a
vida de algum paciente. Os oxímetros ficariam conectados por meio de um RaspberryPy ou outro
dispositivo que tivesse um poder de processamento relativamente bom e alguma conexão de
rede. Os atuadores seriam responsáveis por regular o nível de oxigênio liberado para os paciente,

### Camada de Névoa

Na camada de névoa teríamos dispositivos um pouco mais potentes que seriam
responsáveis por pré-processar os dados vindos dos oxímetros, eliminando ruídos e artefatos que
possam atrapalhar no processamento e enviado esses dados para a camada acima ou realizando
alguma ação nos atuadores na camada inferior

### Camada de Nuvem

Na camada de nuvem teríamos o módulo responsável por receber os dados dos nós
de névoa e processar esses dados, determinando se o nível de oxigenação do paciente está acima
do adequado e mostrando em tela esses dados. Com base no processamento alguma ação pode
ser tomada, como aumentar ou diminuir o oxigênio que o paciente está recebendo. Essa camada
também fica responsável por guardar os dados de saturação de oxigênio para realizar futuras
métricas e treinar modelos de aprendizado de máquina que podem tomar decisões sem precisar
de um médico responsável.
