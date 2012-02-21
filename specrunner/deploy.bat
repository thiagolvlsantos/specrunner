cls & mvn clean source:jar javadoc:jar deploy -Pgpg
@rem mvn clean package verify release:perform -Darguments=-Dgpg.passphrase=%1