#!/bin/bash
set -e

CURRENT_VERSION=$(grep version= gradle.properties | cut -d "=" -f2)

echo "Current version is: ${CURRENT_VERSION}"
if [ -z ${1} ] ; then
  read -p "Enter new version: " NEW_VERSION
else
  NEW_VERSION="${1}"
  echo "Bumping version to: ${NEW_VERSION}"
fi

set -u

sed -i '' "s|${CURRENT_VERSION}|${NEW_VERSION}|" $(find . -name "README.md" -o -name "gradle.properties" | xargs -n 1)
git add $(find . -name "README.md" -o -name "gradle.properties" | xargs -n 1)
git commit -m "Release ${NEW_VERSION}"

echo -e "Version bumped\nPush your changes, send a PR and after merging create a release in github"
