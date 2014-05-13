#! /bin/bash

cp out/tex/* ./

for i in *.tex
  do
    pdflatex $i
  done
