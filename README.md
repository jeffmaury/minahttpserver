minahttpserver
==============

A simple file HTTP server based on Apache MINA.

It tries to leverage the asynchronous features of MINA

Connection management is performed as the MINA HTTP layer does not handle it.

All file based operations are performed outside the IO threads dealing with HTTP
traffic handling.

Testing is provided and can be activated through Maven: mvn clean test
