#!/bin/bash
echo "stoping WecomBotRepeater ..."
for sid in `jps -lm | grep WecomBotRepeater | awk '{print $1}'`
do
    kill -15 ${sid}
done
sleep 3
for sid in `jps -lm | grep WecomBotRepeater | awk '{print $1}'`
do
    # 防止SIGTERM没完全关闭进程
    kill -9 ${sid}
done
echo "stop WecomBotRepeater done"
