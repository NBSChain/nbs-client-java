# nbs-client-test
[![Waffle.io - Columns and their card count](https://badge.waffle.io/NBSChain/NBS-QML.svg?columns=all)](https://waffle.io/NBSChain/NBS-QML) 
## compile to jar command
First make sure maven is properly installed
command :
mvn clean package appassembler:assemble -Dmaven.test.skip=true -X

## IPFS SERVER FAST 
ipfs daemon  --enable-pubsub-experiment
<br>
--routing=dhtclient
<br>
nohup ipfs daemon --enable-pubsub-experiment >/var/log/ipfs/ipfs.log 2>&1 &
<br>
ipfs shutdown
<br>
ipfs pubsub peers bmJzaW8ubmV0 
# Version History
## Version 2.0.1
  - 聊天功能
  - 联系人名片
  - 增加节点信息显示
## Version 2.0.2
  - 增加文件下载功能
  
# Dapp 界面截图
## 首页
![index](https://github.com/NBSChain/nbs-client-java/blob/master/wiki/dist/cli4j.home.png?raw=true)
## 群聊天界面
![im](https://github.com/NBSChain/nbs-client-java/blob/master/wiki/dist/cli4j.im.png?raw=true)
## 上传文件界面
![files](https://github.com/NBSChain/nbs-client-java/blob/master/wiki/dist/cli4j.files.png?raw=true)
## 在NBS网络搜索hash 界面
![search](https://github.com/NBSChain/nbs-client-java/blob/master/wiki/dist/cli4j.search.png?raw=true)  
## Dapp 动图
![git](https://github.com/NBSChain/nbs-client-java/blob/master/wiki/dist/cli-java-demo.gif?raw=true)
