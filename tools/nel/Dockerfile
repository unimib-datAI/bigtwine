FROM openjdk:8-stretch

RUN mkdir /tool \
  && cd /tool \
  && mkdir /data \
  && mkdir /kb
WORKDIR /tool

COPY kb/* /kb/

RUN apt-get update \
  && apt-get install -y python python-pip \
  && apt-get autoclean \
  && apt-get clean \
  && apt-get autoremove

RUN pip install watchdog

COPY tool/ /tool

CMD [ "python2", "-u", "/tool/main.py", "/data", "/kb" ]
