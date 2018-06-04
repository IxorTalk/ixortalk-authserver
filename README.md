# IxorTalk Authserver

For general documentation about the IxorTalk platform: https://github.com/ixortalk/ixortalk-gateway

IxorTalk Authserver is the microservice responsible for security within the IxorTalk stack.  It contains a JHipster application for user management and also acts as the OAuth 2 authorization server.

## Building

For building Maven, Java 1.8+ and Docker are required. 

```
$ mvn clean install
```

To build without a Docker image being built:
```
$ mvn clean install -DskipDocker
```

## Running

For easy bootstrapping an IxorTalk instance, see [ixortalk-config-docker](https://github.com/ixortalk/ixortalk-config-docker) which contains a Docker compose configuration for the complete stack.

## Contributing

Pull request are welcome.

## License

```
The MIT License (MIT)

Copyright (c) 2016-present IxorTalk CVBA

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
