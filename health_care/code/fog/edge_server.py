from flask import Flask, request
from threading import Thread
from send_data_to_cloud import run_data_send

app = Flask(__name__)

@app.route("/", methods=['POST'])
def save():
  file = open('edge', 'a+')
  file.write(str(request.data))
  file.close()

  return "{'success':'true'}"


if __name__ == '__main__':
  Thread(target=run_data_send).start()
  app.run(debug=True, host="0.0.0.0", port=7946)


# build -> docker build -t fog .
# run container -> docker run --rm -d --name fog_node -p 7000:7000 fog