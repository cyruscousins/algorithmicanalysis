#! /bin/bash

sh prepareapplet.sh

rm -r bin/META-INF/
cp -r META-INF/ bin/META-INF
cd bin
jar cfM ../complexity.jar ./
