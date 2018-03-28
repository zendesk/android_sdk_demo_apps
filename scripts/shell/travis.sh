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

acceptLicenses() {
    mkdir -p ${ANDROID_HOME}licenses
    echo -e "\nd56f5187479451eabf01fb78af6dfcb131a6481e" > ${ANDROID_HOME}licenses/android-sdk-license
}

buildAll() {
  ./gradlew assembleRelease
}

# Build types
pullRequestBuild() {
    boxOut "Build All Sample Apps"
    acceptLicenses
    buildAll
}

# do the thing
pullRequestBuild
