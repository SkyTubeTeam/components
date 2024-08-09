

release  VERSION:
  mvn versions:set -DnewVersion={{VERSION}}
  mvn clean install
  git add pom.xml android-utils/pom.xml okhttp-client/pom.xml
  git commit -m "New version - {{VERSION}}"
  git tag "{{VERSION}}"
