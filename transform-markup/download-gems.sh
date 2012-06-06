#!/bin/bash

# create working directories
BASE=./target/gems
mkdir -p $BASE/install
mkdir -p $BASE/resources/ruby

# install gems into install directory
jruby -S gem install maruku sass -i $BASE/install --no-rdoc --no-ri

# copy required files to resource directory
rsync -a --include '/lib/***' --exclude '*' $BASE/install/gems/maruku-*/ $BASE/resources/ruby/maruku
rsync -a --include '/lib/***' --include '/VERSION*' --exclude '*' $BASE/install/gems/sass-*/ $BASE/resources/ruby/sass

echo "Done!"
echo "You can now copy the contents of $BASE/resources to src/main/resources"