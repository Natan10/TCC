import os
import logging
from time import sleep
from requests import post
from datetime import datetime

logging.basicConfig(filename="fog.log", level=logging.INFO)

def send_data_to_cloud(data):
  try:
    # enviando dados para os fog nodes
    headers = { 'app_type': os.environ["NODE_TYPE"] }

    post("http://tasks.cloud-server.internet:7946/", data=str(data), headers=headers)
    logging.info(f"{datetime.now()} - data send to cloud...")
  except:
    logging.info(f"{datetime.now()} - data is lost...")
    
def send_edge_data_to_cloud():
  if os.path.exists("edge"):
    myFile = open("edge", 'r')
    send_data_to_cloud(data=myFile.readlines())
    myFile.close()
    os.remove("edge")

def run_data_send():
  while True:
    sleep(60)
    send_edge_data_to_cloud()