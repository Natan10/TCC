from flask import Flask, request
import logging
from datetime import datetime

logging.basicConfig(filename="cloud.log", level=logging.INFO)
app = Flask(__name__)


@app.route("/", methods=['POST'])
def save():
  logging.info(f"{datetime.now()} - saving data from {request.headers['app_type']}")
  file = open('cloud', 'a+')
  file.write(str(request.data))
  file.close()

  return "{'success':'true'}"


if __name__ == '__main__':
  app.run(debug=True, host="0.0.0.0", port=7946)


# build -> docker build -t cloud .
# run container -> docker run --rm -d --name cloud_server -p 8000:8000 cloud