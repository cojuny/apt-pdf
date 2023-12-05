FROM jenkins/jenkins:lts-alpine
USER root
RUN apk add --no-cache python3
RUN python3 -m ensurepip
RUN python3 -m pip install --upgrade pip
RUN if [ ! -e /usr/bin/pip ]; then ln -s pip3 /usr/bin/pip ; fi
RUN if [[ ! -e /usr/bin/python ]]; then ln -sf /usr/bin/python3 /usr/bin/python; fi
RUN rm -r /root/.cache
RUN apk add pkgconf
RUN apk add build-base
RUN apk add python3-dev