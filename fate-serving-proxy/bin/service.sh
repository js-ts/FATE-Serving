#!/bin/bash

#
#  Copyright 2019 The FATE Authors. All Rights Reserved.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
set -e
source ./bin/common.sh
#export JAVA_HOME=/data/projects/fate/common/jdk/jdk-8u192
#export PATH=$PATH:$JAVA_HOME/bin

basepath=$(cd `dirname $0`;pwd)
configpath=$(cd $basepath/conf;pwd)
module=serving-proxy
main_class=com.webank.ai.fate.serving.proxy.bootstrap.Bootstrap
module_version=2.1.2


case "$1" in
    start)
        start  $module
        status $module
        ;;
    starting)
        start front
        ;;
    stop)
        stop $module
        ;;
    status)
        status $module
        ;;
    restart)
        stop $module
        sleep 0.5
        start  $module
        status $module
        ;;
    *)
        echo "usage: $0 {start|stop|status|restart}"
        exit 1
esac
