#!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "ocpsoft/rewrite" ] && 
    [ "$TRAVIS_BRANCH" == "master" ] &&
    [ "$TRAVIS_PULL_REQUEST" == "false" ] &&
    [ "$TRAVIS_JDK_VERSION" == "openjdk7" ] &&
    [ "$CONTAINER" == "JBOSS_AS_MANAGED_7.X" ]; then

  echo "Starting snapshot deployment..."
  ./mvnw -s .travis-settings.xml -DperformRelease -DskipTests -Dgpg.skip=true deploy
  echo "Snapshots deployed!"

else
  echo "Skipping snapshot deployment..."
fi
