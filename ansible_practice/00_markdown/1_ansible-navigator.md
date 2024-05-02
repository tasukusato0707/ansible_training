
# ansible-navigatorのはなし

- 目次
  - Playbookを実行する方法は？
  - ansible-navigatorとは？
  - ansible-playbookからansible-navigatorへ移行した経緯
  - ansible-navigator構成図
  - 参考サイト

<br>
<br>
<br>

---

## Playbookを実行する方法は？
Playbookを実行する方法は何種類か方法はあるが、昔から定番だった方法は以下である。
```shell
ansible-playbook -i <インベントリファイル> <Playbook名>.yml 
```

但し、本トレーニングでは以下のコマンドでPlaybookを実行する。
```shell
ansible-navigator run <Playbook名>.yml -i <インベントリファイル>
```
ansible-playbookに替わって、ansible-navigatorが登場した経緯についても簡単に述べる。

## ansible-navigatorとは？
ansible-navigatorは、Execution Environment（EE）経由でPlaybookを実行するソフトウェア。

- Execution Environment（以下EE）とは
  - Ansibleを実行するために必要なものが入っているコンテナ化したイメージ。

## ansible-playbookからansible-navigatorへ移り変わった経緯

- ansible-playbookコマンド期1
  - ansibleを実行するサーバの環境によって動いたり動かなかったりなどの問題が発生していた。
  - ex) ansibleのバージョン差分、pythonパッケージの有無など。

- ansible-playbookコマンド期2
  - 上記の問題を解消するため、実行環境をコンテナイメージ(後にEEと呼ばれるようになる)にして、
    dockerなどのコンテナツールを経由してansible-playbookを実行していた。

- ansible-runnerの登場
  - 2018年にansible-runnerがリリースされる。ansible-runnerコマンドからEEを実行できるようになる。
  - podmanやdockerコマンドを直接打ってコンテナ内でPlaybookを実行していた時よりも利便性が向上
    - コンテナツールに渡すオプションを設定ファイル(env/settings)に記載することで、都度オプションを指定する必要がなくなった。
      - けど、ansible-playbookコマンドとオプションの指定の仕方が異なり、結構不便な部分もあった。

- ansible-builderの登場
  - 2020年にansible-builderがリリースされる。
  - ansible-builderは、ansible-runner(後述のansible-navigatorも)で利用するEEを作成するツール。

- ansible-navigatorの登場
  - 2021年6月、ansible-navigatorがリリースされる。
  - ansible-navigatorコマンドもansible-runner同様、EE経由でPlaybookを実行することが可能なツール
    - ansible-navigatorコマンドはansible-playbookコマンドと同じオプション指定が可能になり、より便利に。
    - 設定ファイルはansible-navigator.yamlに変更。env/settingsに書いていた内容よりも直感的になった。


## ansible-navigator構成図
今回のトレーニングの構成図は以下となる。<br>
![image](https://github.com/apc-nw-auto-cft/ansible_on_vyos/blob/main/others/navigator_image.png)

## 参考サイト
  - [これからはじめるAnsible - Ansible Night Tokyo 2024](https://www.slideshare.net/slideshow/ansible-ansible-night-tokyo-2024/266763151)
  - [2021年頃からコンテナ化が進んできたAnsible実行環境](https://logmi.jp/tech/articles/329608)
 
