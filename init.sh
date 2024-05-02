#!/bin/bash

# install docker
sudo dnf install -y docker

# install poetry
curl -sSL https://install.python-poetry.org | python3 -

poetry config virtualenvs.in-project true

# install python pkgs by poetry
poetry install --no-root -C ~/ansible_on_vyos/

# https://qiita.com/DQNEO/items/da5df074c48b012152ee

# dockerグループがなければ作る
sudo groupadd docker

# 現行ユーザをdockerグループに所属させる
sudo gpasswd -a $USER docker

# dockerデーモンを再起動する
sudo systemctl restart docker

# ansible-navigator.ymlの環境変数を設定する
echo 'export ANSIBLE_NAVIGATOR_CONFIG="$HOME/ansible_on_vyos/ansible-navigator.yaml"' >> ~/.bashrc
