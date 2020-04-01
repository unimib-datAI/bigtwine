FROM nginx

RUN mkdir -p /var/www/html \
    && rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY www /var/www/html/
