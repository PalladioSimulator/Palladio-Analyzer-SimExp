Setup PRISM for SimExp
-----------------------

1. Download latest Prism distribution for your OS from https://www.prismmodelchecker.org/download.php
2. Install Prism on your local machine
3. Create user environment variable 'PRISM=<prism installation dir>'
4. Copy all *.jar files from <prism installation dir>\lib into folder Palladio-Analyzer-SimExp\bundles\org.palladiosimulator.simexp.pcm.prism.wrapper\lib\
5. Configure Eclipse launch configuration: in the launcher under Environment add environment variable 'PATH' with '${env_var:PRISM}\lib'
   -> See https://github.com/prismmodelchecker/prism/wiki/Setting-up-Eclipse
