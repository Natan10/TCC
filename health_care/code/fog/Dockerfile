# Pull base image of latest Python 3.7.x
FROM python:3.6

# Set environment variables
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

RUN git clone https://github.com/opsengine/cpulimit/
WORKDIR cpulimit
RUN make && cp src/cpulimit /usr/bin


RUN mkdir /app
WORKDIR /app
ADD requirements.txt /app/
RUN pip install -r requirements.txt

RUN apt-get update && apt-get install -y stress && apt-get -y install cron rsyslog

EXPOSE 7000
# porta para comunicação entre nós
EXPOSE 7946
# porta para tráfego de rede de sobreposição
EXPOSE 4789
# porta para comunicação de gerenciamento de cluster
EXPOSE 2377


ADD . /app/

# RUN chmod 0644 /app/cronjobs
# RUN service rsyslog start
# RUN chmod +x /app/entrypoint.sh
# RUN /bin/bash /app/entrypoint.sh

CMD ["python", "edge_server.py"]