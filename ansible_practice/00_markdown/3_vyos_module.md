
# day3 vyosのモジュール

- vyosのモジュールを学習しながら、playbookの実行方法なども学習する

- 目次
  - 1.vyosのモジュールとは？
  - 2.vyosのモジュールの使用例
  - 3.vyosのモジュールの演習[ハンズオン]
  - vyosのモジュールについてのまとめ
  - 4.vyosのモジュールの演習

<br>
<br>
<br>

---

## 1.vyosのモジュールとは？

- 前章:vyosとは の後に読み進めていくことをお勧めする。
- Ansibleでvyosを操作するために使用する。vyosに関するモジュールは主に以下の2つがある

| モジュール名 | 説明 |
| :-----: | :------------------------------------------------------------------------------------------------------------ |
| vyos_command | vyosのオペレーションモードで操作したいときに使用。<br>主にshow系コマンドなどを実行する時に使用。|
| vyos_config | vyosのコンフィギューレーションモード移行後に操作したいときに使用。<br>設定変更などを行う時に使用。 |

### vyos_commandのパラメータ

| パラメータ | 説明 |
| :-----: | :------------------------------------------------------------------------------------------------------------ |
| commands | オペレーションモードで実行させるコマンドを記載。必須のパラメータ。<br>show interfaces やshow versionなど。 |

- commands以外もパラメータはある。
- すべてのパラメータや、必須のパラメータを確認するには、Ansible documentを使用する。
- vyos_commandのAnsible documentは[こちら](https://docs.ansible.com/ansible/latest/collections/vyos/vyos/vyos_command_module.html)

### vyos_configのパラメータ

| パラメータ | 説明 |
| :-----: | :------------------------------------------------------------------------------------------------------------ |
| lines | コンフィギュレーションモードで実行させるコマンドを記載。<br>set system XX やdelete system XX など。 |
| save | linesで実行した内容を保存する。commitして、saveと同義。<br>true または false を指定。デフォルトではfalseになる。|

- lines,save以外もパラメータはある。
- vyos_configのAnsible documentは[こちら](https://docs.ansible.com/ansible/latest/collections/vyos/vyos/vyos_config_module.html)

<br>
<br>
<br>

---

## 2.vyosのモジュールの使用例

### vyos_commandの使用例

- 以下は、vyosに対して「show version」コマンドを実行しているplaybookである。

```yaml
---
- hosts: vyos

  tasks:
    - name: Vyos_command example
      vyos.vyos.vyos_command:
        commands:
          - show version
```

### vyos_configの説明

- 以下は、vyosのeth1を有効化しているplaybookである。

```yaml
---
- hosts: vyos

  tasks:
    - name: Set interfaces
      vyos.vyos.vyos_config:
        lines:
          - set interfaces ethernet eth1 enable
        save: true
```

<br>
<br>
<br>

---

## 3.vyosのモジュールの演習[ハンズオン]

### 目的

- vyos01,vyos02に対して、インターフェースの設定内容を確認
- vyos01,vyos02に対して、eth1,eth2にdescriptionを設定<br>
    (eth1→vyos_config-test1 eth2→vyos_config-test2 と設定する)

### 1.ディレクトリ移動

- 使用するplaybook,inventoryファイルが存在するディレクトリに移動

```shell
cd /home/ec2-user/ansible_on_vyos/ansible_practice/03_vyos
```

### 2.仮想環境(poetry)に入る

```shell
$ poetry shell

# Spawning shell within /home/ec2-user/ansible_on_vyos/.venv
```

### 3.vyos01,vyos02の設定を事前確認

- vyos01,vyos02にログインして、現在のDescriptionを確認
- eth1に「vyos_config-test1」eth2に「vyos_config-test2」が設定されていないことを確認

#### vyos01

```shell
$ docker exec -it vyos01 su - vyos
vyos@vyos01:~$ show interfaces 
Codes: S - State, L - Link, u - Up, D - Down, A - Admin Down
Interface        IP Address                        S/L  Description
---------        ----------                        ---  -----------
eth0             10.0.0.2/24                       u/u  
eth1             192.168.1.252/24                  u/u  
                 192.168.1.254/24                       
eth2             192.168.2.252/24                  u/u  
                 192.168.2.254/24                       
lo               127.0.0.1/8                       u/u  
vyos@vyos01:~$ 
vyos@vyos01:~$ exit
logout
$ 
```

#### vyos02

```shell
$ docker exec -it vyos02 su - vyos
vyos@vyos02:~$ show interfaces 
Codes: S - State, L - Link, u - Up, D - Down, A - Admin Down
Interface        IP Address                        S/L  Description
---------        ----------                        ---  -----------
eth0             10.0.0.3/24                       u/u  
eth1             192.168.1.253/24                  u/u  
eth2             192.168.2.253/24                  u/u  
lo               127.0.0.1/8                       u/u  
vyos@vyos02:~$ 
vyos@vyos02:~$ exit
logout
$ 
```

### 4.inventory.iniの内容を確認

- vyos01,vyos02の接続に必要な情報が存在することを確認する。

```ini
$ cat inventory.ini
[vyos]
vyos01 ansible_host=10.0.0.2
vyos02 ansible_host=10.0.0.3

[vyos:vars]
ansible_network_os=vyos
ansible_connection=network_cli
ansible_user=vyos
ansible_password=vyos
```

### 5.playbookの内容を確認

```yaml
$ cat vyos_module_sample.yml 
---
- name: Sample
  hosts: vyos
  gather_facts: false

  tasks:
    - name: Check interfaces description
      vyos.vyos.vyos_command:
        commands:
          - show interfaces

    - name: Setting interfaces description
      vyos.vyos.vyos_config:
        lines:
          - set interfaces ethernet eth1 description vyos_config-test1
          - set interfaces ethernet eth2 description vyos_config-test2
```

### 6.playbookを実行

- TASK [Check interfaces description] でshow interfacesを実行している。ok: [vyos01] ok: [vyos02]であることを確認<br>
- TASK [Setting interfaces description] でdescriptionを設定している。changed: [vyos01] changed: [vyos02]であることを確認

```shell
$ ansible-navigator run vyos_module_sample.yml -i inventory.ini

PLAY [Sample] **********************************************************************************************

TASK [Check interfaces description] ************************************************************************
ok: [vyos01]
ok: [vyos02]

TASK [Setting interfaces description] **********************************************************************
[WARNING]: To ensure idempotency and correct diff the input configuration lines should be similar to how they appear if present in the running configuration
on device
changed: [vyos01]
changed: [vyos02]

PLAY RECAP *************************************************************************************************
vyos01                     : ok=2    changed=1    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
vyos02                     : ok=2    changed=1    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   

$ 
```

### 7.vyos01,vyos02の設定を事後確認

- vyos01,vyos02にログインして、現在のDescriptionを確認
- eth1に「vyos_config-test1」eth2に「vyos_config-test2」が設定されていることを確認

#### vyos01

```shell
$ docker exec -it vyos01 su - vyos
vyos@vyos01:~$ show interfaces
Codes: S - State, L - Link, u - Up, D - Down, A - Admin Down
Interface        IP Address                        S/L  Description
---------        ----------                        ---  -----------
eth0             10.0.0.2/24                       u/u  
eth1             192.168.1.252/24                  u/u  vyos_config-test1
                 192.168.1.254/24                       
eth2             192.168.2.252/24                  u/u  vyos_config-test2
                 192.168.2.254/24                       
lo               127.0.0.1/8                       u/u  
vyos@vyos01:~$  
vyos@vyos01:~$ exit
logout
$ 
```

#### vyos02

```shell
$ docker exec -it vyos02 su - vyos
vyos@vyos02:~$ show interfaces 
Codes: S - State, L - Link, u - Up, D - Down, A - Admin Down
Interface        IP Address                        S/L  Description
---------        ----------                        ---  -----------
eth0             10.0.0.3/24                       u/u  
eth1             192.168.1.253/24                  u/u  vyos_config-test1
eth2             192.168.2.253/24                  u/u  vyos_config-test2
lo               127.0.0.1/8                       u/u  
vyos@vyos02:~$ 
vyos@vyos02:~$ exit
logout
$ 
```

### 補足1.もう一度、playbookを実行

- TASK [Check interfaces description] でshow interfacesを実行している。ok: [vyos01] ok: [vyos02]であることを確認。<br>
- TASK [Setting interfaces description] でdescriptionを設定しているが、手順6ですでに設定しているので、今回は実施されない。<br>
  ok: [vyos01] ok: [vyos02]であることを確認。= べき等性

```shell
$ ansible-navigator run vyos_module_sample.yml -i inventory.ini

PLAY [Sample] **********************************************************************************************

TASK [Check interfaces description] ************************************************************************
ok: [vyos01]
ok: [vyos02]

TASK [Setting interfaces description] **********************************************************************
ok: [vyos01]
ok: [vyos02]

PLAY RECAP *************************************************************************************************
vyos01                     : ok=2    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
vyos02                     : ok=2    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   

$ 
```

<br>
<br>
<br>

---

## vyosのモジュールについてのまとめ

### よく使用するvyosのモジュールは2つある

#### vyos_command

- 主にshow系コマンドを実行するときに使用
- 必須のパラメータは commands

#### vyos_config

- 主に設定変更を実行するときに使用

<br>
<br>
<br>

---

## 4.vyosのモジュールの演習

---

### Q1 以下のplaybookの空欄に当てはまるものは何でしょう

```yaml
---
- name: Exam1
  hosts: vyos
  gather_facts: false

  tasks:
    - name: Check interface
      ■■■■■■:
        commands:
          - show interfaces
```

1. vyos.vyos.vyos_config
2. vyos.vyos.vyos_configure
3. vyos.vyos.vyos_commands
4. vyos.vyos.vyos_command

<br>
<br>
<br>

---

### Q2 以下のplaybookの空欄に当てはまるものは何でしょう

```yaml
- name: Exam2
  hosts: vyos
  gather_facts: false

  tasks:
    - name: Disable interface
      vyos.vyos.vyos_config:
        ■■■■■■:
          - delete interfaces ethernet eth0 disable
```

1. commands
2. interfaces
3. lines
4. config

<br>
<br>
<br>

---

### Q3 以下の条件のplaybookを作成して、実行してください

- 使用インベントリファイル：「/home/ec2-user/ansible_on_vyos/ansible_practice/03_vyos」配下のinventory.ini
- playbook作成先ディレクトリ：「/home/ec2-user/ansible_on_vyos/ansible_practice/03_vyos」配下
- playbook名：「vyos_module_exam_3.yml」で作成
- 実行対象ノード：vyos01,vyos02
- 処理内容：「show version」「show ip route」を実行

<br>
<br>
<br>

---

### Q4 以下の条件のplaybookを作成して、実行してください

- 使用インベントリファイル：「/home/ec2-user/ansible_on_vyos/ansible_practice/03_vyos」配下のinventory.ini
- playbook作成先ディレクトリ：「/home/ec2-user/ansible_on_vyos/ansible_practice/03_vyos」配下
- playbook名：「vyos_module_exam_4.yml」で作成
- 実行対象ノード：vyos01のみ
- 処理内容：
  - 1.「show interfaces」を実行
  - 2.現在設定されているeth1,eth2のdescriptionを削除を実行し、saveをする

<br>
<br>
<br>

---

### A1 正解：「4.vyos.vyos.vyos_command」

- 以下、正しいplaybook

```yaml
---
- name: Exam1
  hosts: vyos
  gather_facts: false

  tasks:
    - name: Check interface
      vyos.vyos.vyos_command:
        commands:
          - show interfaces

```

- 選択肢の解説

1. vyos.vyos.vyos_config　vyosに対して、主に設定変更を実行するときに使用するモジュール
2. vyos.vyos.vyos_configure　存在しないモジュール
3. vyos.vyos.vyos_commands　2と同じく、存在しないモジュール
4. vyos.vyos.vyos_command　vyosに対して、主にshow系コマンドを実行するときに使用するモジュール、正しい

<br>
<br>
<br>

---

### A2 正解：「3. lines」

- 以下、正しいplaybook

```yaml
- name: Exam2
  hosts: vyos
  gather_facts: false

  tasks:
    - name: Disable interface
      vyos.vyos.vyos_config:
        lines:
          - delete interfaces ethernet eth0 disable
```

- 選択肢の解説

1. commands vyos_commandのパラメータ
2. interfaces 存在しないパラメータ
3. lines コンフィギュレーションモードで実行させるコマンドを記載する時のパラメータ、正しい
4. config 2と同じく、存在しないパラメータ

<br>
<br>
<br>

---

### A3.以下、解答例

- playbook

```yaml
---
- name: Exam3
  hosts: vyos
  gather_facts: false

  tasks:
    - name: Check vyos info
      vyos.vyos.vyos_command:
        commands:
          - show version
          - show ip route

```

- playbookの実行結果

```shell
$ ansible-navigator run vyos_module_exam_3.yml -i inventory.ini

PLAY [Exam3] ***********************************************************************************************

TASK [Check vyos info] *************************************************************************************
ok: [vyos01]
ok: [vyos02]

PLAY RECAP *************************************************************************************************
vyos01                     : ok=1    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
vyos02                     : ok=1    changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0  

$ 
```

<br>
<br>
<br>

---

### A4.以下、解答例

- playbook

```yaml
---
- name: Exam4
  hosts: vyos01
  gather_facts: false

  tasks:
  - name: Check interfaces
    vyos.vyos.vyos_command:
      commands:
        - show interfaces

  - name: Delete interfaces description
    vyos.vyos.vyos_config:
      lines:
        - delete interfaces ethernet eth1 description
        - delete interfaces ethernet eth2 description
      save: true

```

- playbookの実行結果

```shell
$ ansible-navigator run vyos_module_exam_4.yml -i inventory.ini

PLAY [Exam4] **************************************************************************************************

TASK [Check interfaces] ***************************************************************************************
ok: [vyos01]

TASK [Delete interfaces description] **************************************************************************
[WARNING]: To ensure idempotency and correct diff the input configuration lines should be similar to how they appear if present in the running configuration on device
changed: [vyos01]

PLAY RECAP ****************************************************************************************************
vyos01                     : ok=2    changed=1    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   

$ 
```

#### (補足)事前と事後確認

- 事前確認(vyos01)

```shell
$ docker exec -it vyos01 su - vyos
vyos@vyos01:~$ show interfaces 
Codes: S - State, L - Link, u - Up, D - Down, A - Admin Down
Interface        IP Address                        S/L  Description
---------        ----------                        ---  -----------
eth0             10.0.0.2/24                       u/u  
eth1             192.168.1.252/24                  u/u  vyos_config-test1
                 192.168.1.254/24                       
eth2             192.168.2.252/24                  u/u  vyos_config-test2
                 192.168.2.254/24                       
lo               127.0.0.1/8                       u/u  
vyos@vyos01:~$ 
vyos@vyos01:~$ exit
logout
$ 
```

- 事前確認(vyos02)

```shell
$ docker exec -it vyos02 su - vyos
vyos@vyos02:~$ show interfaces 
Codes: S - State, L - Link, u - Up, D - Down, A - Admin Down
Interface        IP Address                        S/L  Description
---------        ----------                        ---  -----------
eth0             10.0.0.3/24                       u/u  
eth1             192.168.1.253/24                  u/u  vyos_config-test1
eth2             192.168.2.253/24                  u/u  vyos_config-test2
lo               127.0.0.1/8                       u/u  
vyos@vyos02:~$ exit
logout
$ 
```

- 事後確認(vyos01)

```shell
$ docker exec -it vyos01 su - vyos
vyos@vyos01:~$ show interfaces 
Codes: S - State, L - Link, u - Up, D - Down, A - Admin Down
Interface        IP Address                        S/L  Description
---------        ----------                        ---  -----------
eth0             10.0.0.2/24                       u/u  
eth1             192.168.1.252/24                  u/u  
                 192.168.1.254/24                       
eth2             192.168.2.252/24                  u/u  
                 192.168.2.254/24                       
lo               127.0.0.1/8                       u/u  
vyos@vyos01:~$ 
vyos@vyos01:~$ exit
logout
$ 
```

- 事後確認(vyos02)

```shell
$ docker exec -it vyos02 su - vyos
vyos@vyos02:~$ show interfaces 
Codes: S - State, L - Link, u - Up, D - Down, A - Admin Down
Interface        IP Address                        S/L  Description
---------        ----------                        ---  -----------
eth0             10.0.0.3/24                       u/u  
eth1             192.168.1.253/24                  u/u  vyos_config-test1
eth2             192.168.2.253/24                  u/u  vyos_config-test2
lo               127.0.0.1/8                       u/u  
vyos@vyos02:~$ exit
logout
$ 
```
