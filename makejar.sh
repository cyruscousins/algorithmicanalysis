#! /bin/bash

sh prepareapplet.sh

cp -rf META-INF/ bin/META-INF
cd bin
jar cfM ../complexity.jar ./
