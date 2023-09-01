# This value is overridden during Concourse jobs to use a local copy of the image.
# If you are changing the base image here, you should also change it in the Concourse pipeline configuration
# in https://github.com/RoboJackets/concourse-pipeline-library/blob/main/pipelines/information-technology/apiary-mobile-docker-build.yml
ARG base_image=mingc/android-build-box:1.25.0

FROM ${base_image}
LABEL maintainer="RoboJackets <hello@robojackets.org>"
LABEL description="Image for building and analyzing the MyRoboJackets Android app"

# Gradle setup
ENV GRADLE_USER_HOME /usr/local/.gradle

# Ruby env setup based on https://github.com/starkandwayne/dockerfiles/blob/master/concourse-ruby/2.6/Dockerfile
# Basically, install Ruby gems globally
ENV GEM_HOME /usr/local/bundle
ENV BUNDLE_PATH="$GEM_HOME" \
	BUNDLE_BIN="$GEM_HOME/bin" \
	BUNDLE_SILENCE_ROOT_WARNING=1 \
	BUNDLE_APP_CONFIG="$GEM_HOME"
ENV PATH $BUNDLE_BIN:$PATH
RUN mkdir -p "$GEM_HOME" "$BUNDLE_BIN" \
	&& chmod 777 "$GEM_HOME" "$BUNDLE_BIN"

COPY fastlane/ /tmp/build/fastlane/
COPY Gemfile Gemfile.lock /tmp/build/

RUN cd /tmp/build && \
	gem install bundler && \
    bundle install && \
    wget -q https://github.com/GitTools/GitVersion/releases/download/5.8.1/gitversion-linux-x64-5.8.1.tar.gz && \
    tar -xf gitversion-linux-x64-5.8.1.tar.gz && \
	mv gitversion /usr/bin