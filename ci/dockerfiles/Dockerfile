# This value is overridden during Concourse jobs to use a local copy of the image.
# If you are changing the base image here, you should also change it in the Concourse pipeline configuration.
ARG base_image=mingc/android-build-box:1.24.0

FROM ${base_image}
LABEL maintainer="RoboJackets <hello@robojackets.org>"
LABEL description="Image for building and analyzing the MyRoboJackets Android app"

RUN gem install bundler:2.3.1

# Install GitVersion
RUN pwd
RUN wget https://github.com/GitTools/GitVersion/releases/download/5.8.1/gitversion-linux-x64-5.8.1.tar.gz