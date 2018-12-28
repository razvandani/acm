#!/bin/bash

libraries=('restclient' 'jpaframework' 'common', 'email')
services=('registry' 'iam' 'customerservice')
export PATH=C:\\apache-maven-3.6.0-bin\\bin:${PATH}

for lib in ${libraries[@]} ; do
  (
  cd ./"${lib}" || exit 1
  pwd
  mvn -e clean install
  )
done

mkdir build

for svc in ${services[@]} ; do
  (
  cd ./"${svc}" || exit 1
  pwd
  mvn clean install
  cp ./target/"${svc}"-*.jar ../build/
  )
done

echo
