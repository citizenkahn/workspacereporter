# Goal
Implement Microservice as an exercise


# Assumptions
- workspace with built source within it (example: https://kernelnewbies.org/KernelBuild.)
- ubuntu OS with
-- cloc installed (apt-get install cloc)
-- java 1.8
-- gradle
-- groovy

# RestAPI
GET	  /api/linux[/{file|directory}[/{file|directory}[/...]]][?content]
* On Directory: list files and directories including type (symlink, dir, file)
- On File: list standard ls -l output and cloc tally for comment, blank, code, total lines
- On Content for Directory: send 404
- On Content for File: text files, provide contents.  binary files, send octal dump (od filename)

# Client
Client should walk the tree building of a tally for all contents.  It should match the sum if the command was run locally on the workspace
  
