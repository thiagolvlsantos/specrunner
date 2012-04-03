@rem mvn clean package verify release:perform -Darguments=-Dgpg.passphrase=%1
cls & mvn clean source:jar javadoc:jar deploy
