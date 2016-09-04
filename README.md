# asyncPostgres
Postgresql driver written using java Asynchronous IO


Existing JDBC driver uses blocking Sockets, and this can't be changed.
I'd like to run a server where threads are never blocked on IO, as it is more efficient for a large number of users.
To do this, use async IO for database communication as well as user.

Interfaces are supposed to be simple and similar to JDBC, and easy to use with functional or reactive programming style 
(i.e. lambda expressions for callbacks when data is received)
