#PC 
host :172.168.20.168

##
nohup ipfs daemon --enable-pubsub-experiment >/var/log/ipfs/ipfs.log 2>&1 & 
