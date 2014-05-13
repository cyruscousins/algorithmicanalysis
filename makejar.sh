#! /bin/bash

sh prepareapplet.sh

cp -r META-INF/ bin/META-INF
cd bin
jar cfM ../complexity.jar ./
