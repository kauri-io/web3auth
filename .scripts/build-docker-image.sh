#!/bin/bash
GIT_COMMIT_DESC=$(git log --format=oneline -n 1 $CIRCLE_SHA1)
echo Git commit message: $GIT_COMMIT_DESC
echo Git PR Number: $CIRCLE_PR_NUMBER
if [[ "$GIT_COMMIT_DESC" != *"maven-release-plugin"* && "$CIRCLE_PR_NUMBER" == "" ]]; then
  RELEASE_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec | sed 's/-SNAPSHOT//g')
  echo building docker with version: ${RELEASE_VERSION}
  set -e

  docker login -u ${DOCKER_HUB_USER_ID} -p ${DOCKER_HUB_PWD}
  docker build -t kauriorg/web3auth:${RELEASE_VERSION} -f server/Dockerfile server/.
  docker build -t kauriorg/web3auth:latest -f server/Dockerfile server/.
  docker push kauriorg/web3auth:${RELEASE_VERSION}
  docker push kauriorg/web3auth:latest
fi
