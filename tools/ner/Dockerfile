FROM openjdk:8-stretch AS builder

RUN apt-get update \
  && apt-get install -y python python-pip

ENV TWITTER_NLP_COMMIT=947c9bc9e8060c350a37af7a5995101e9bca4cf9

RUN mkdir /tool \
  && cd /tool \
  && git clone https://github.com/aritter/twitter_nlp.git . \
  && git checkout $TWITTER_NLP_COMMIT \
  && chmod +x build.sh \
  && ./build.sh
 

FROM openjdk:8-stretch

RUN mkdir /tool \
  && mkdir /data \
  && cd /tool
WORKDIR /tool

COPY --from=builder /tool .
COPY ./python/ner/extractEntitiesLoop.py /tool/python/ner/

ENV TWITTER_NLP=./

CMD ["python2", "/tool/python/ner/extractEntitiesLoop.py", "/data", "--classify"]
