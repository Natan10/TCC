import os
import logging
from threading import Thread
from time import sleep
from requests import post
from datetime import datetime

logging.basicConfig(filename="iot.log", level=logging.INFO)


services_network = {
  'mec-svc-1': 'edge-net-1', 
  'mec-svc-2': 'edge-net-2'
}

def fake_data_node_1():
  data = ''
  with open("dados_oximetro_sensor_1.csv","r") as f:
    data = f.readline()
    buffer = f.readlines()
  with open("dados_oximetro_sensor_1.csv","w+") as f:
    for line in buffer:
      f.write(line)
 
  return data

def fake_data_node_2():
  data = ''
  with open("dados_oximetro_sensor_2.csv","r") as f:
    data = f.readline()
    buffer = f.readlines()
  with open("dados_oximetro_sensor_2.csv","w+") as f:
    for line in buffer:
      f.write(line)
 
  return data

# def fake_data():
#   data = []
#   file = open("dados_oximetro_sensor.csv","r")
#   oximetro_data = file.readline()
#   data.append(oximetro_data)
#   file.close()  
#   return data

# def send_data_to_edge(data): 
#   logging.info(f"data: {data}")
#   for fog_node in ['mec-svc-1','mec-svc-2']:
#     try:
#       # enviando dados para os fog nodes
#       post(f"http://tasks.{fog_node}.{services_network[fog_node]}:7946/", data=str(data), timeout=2)
#       logging.info("data sending...")
#     except:
#       continue

def send_data_to_edge(data_node_1, data_node_2): 
  fog_nodes = ['mec-svc-1','mec-svc-2']
  sent_data = {'mec-svc-1': False, 'mec-svc-2': False}

  node_type = os.environ['NODE_TYPE']

  if(node_type == 'IOT_NODE_ONE'):
    try:
      # enviando dados para os fog nodes
      post(f"http://tasks.{fog_nodes[0]}.{services_network[fog_nodes[0]]}:7946/", data=str(data_node_1), timeout=1)
      sent_data[fog_nodes[0]] = True
      
      logging.info(f"{datetime.now()} - dado enviado ao fog_node 1")
    except:
      logging.info(f"{datetime.now()} - erro ao enviar dado para o fog_node 1!")

    if not sent_data['mec-svc-1']:
      try:
        post("http://tasks.cloud-server.internet:7946/", data=str(data_node_1))
        logging.info(f"{datetime.now()} - dado node_1 enviado a cloud!")
      except:
        logging.info(f"{datetime.now()} - erro ao enviar dado node_1 para cloud!")
  else:
    try:
      # enviando dados para os fog nodes
      post(f"http://tasks.{fog_nodes[1]}.{services_network[fog_nodes[1]]}:7946/", data=str(data_node_2), timeout=1)
      sent_data[fog_nodes[1]] = True
      logging.info(f"{datetime.now()} - dado enviado ao fog_node 2")
    except:
      logging.info(f"{datetime.now()} - erro ao enviar dado para o fog_node 2!")

    # if not sent_data['mec-svc-2']:
    #   try:
    #     post("http://tasks.cloud-server.internet:7946/", data=str(data_node_2))
    #     logging.info("dado node_2 enviado a cloud!")
    #   except:
    #     logging.info("erro ao enviar dado node_2 para cloud!")


def send_oximetro_data_to_edge():
  data_node_1 = fake_data_node_1()
  data_node_2 = fake_data_node_2()
  send_data_to_edge(data_node_1,data_node_2)

while True:
  sleep(1)
  thread = Thread(target=send_oximetro_data_to_edge)
  thread.start()


# build -> docker build -t iot .
# run container -> docker run --rm -d --name iot_node iot