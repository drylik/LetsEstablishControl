# LetsEstablishControl
#####I want to control computers in LAN by sending comands to cmd.

I'm doing it in java.

now I have a terrible way of getting IPs list, and it's needed to be improved.

I need some improvements about receiver's class.

**How does it work?**

SenderThread sends messages using UDP, which contain comands, which have to be executed in another PC's cmd.

ReceivingThread receives that comands, executes them and sends back the result of execution.
