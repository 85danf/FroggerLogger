# FroggerLogger
A neat java agent that allows runtime code injection for the purpose of injecting log directives at runtime.

Purposefully written to only allow injection of log directives (although exploitable on their own account), 
This library can be easily forked and adapted to any code injection usage.   


### Usage
Simply include this dependency in your project `add ojo repo link HERE` and use:
```
FroggerLogger froggerLogger = new FroggerLogger();
froggerLogger.injectLog(className, classMethod, logContent, line);
```
