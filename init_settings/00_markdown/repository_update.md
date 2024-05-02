
# ローカルリポジトリを最新化する手順書

※本手順は指示があった場合実施

### 1.構築したEC2インスタンスを起動

### 2.起動したインスタンスにSSH(VSCode推奨)

### 3.ターミナル画面で以下を実施
```yaml
cd ~/ansible_on_vyos/

git switch main

git pull

git switch <APCのユーザ名(メールアドレスの@前)>

git merge main
```
