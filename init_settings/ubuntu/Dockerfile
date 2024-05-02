FROM ubuntu:22.04
RUN apt update -y && apt install -y \ 
    net-tools \
    openssh-server \
    traceroute \
 && apt clean all \
 && rm -rf /var/cache/apt/*

Run sed -i -e 's/#PermitRootLogin.*/PermitRootLogin yes/' /etc/ssh/sshd_config
EXPOSE 22
RUN echo "root:test_password" | chpasswd
