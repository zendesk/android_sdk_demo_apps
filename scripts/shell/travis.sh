#!/usr/bin/env bash

exitOnFailedBuild() {
    if [ $? -ne 0 ]; then
        exit 1
    fi
}

boxOut(){
    local s="$*"
    tput setaf 3
    echo -e " =${s//?/=}=\n| $(tput setaf 4)$s$(tput setaf 3) |\n =${s//?/=}=\n"
    tput sgr 0
}

buildAll() {
  ./gradlew assembleRelease
}

# Build types
pullRequestBuild() {
    boxOut "Build All Sample Apps"
    buildAll
}

# do the thing
pullRequestBuild
