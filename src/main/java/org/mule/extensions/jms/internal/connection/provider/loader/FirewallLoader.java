package org.mule.extensions.jms.internal.connection.provider.loader;

public class FirewallLoader extends ClassLoader {
    public FirewallLoader(ClassLoader parent) {
        super(parent);
    }
    public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.equals("org.apache.activemq.transport.tcp.SslTransport") || name.equals("org.apache.activemq.transport.tcp.TcpTransport")) {
            throw new ClassNotFoundException();
        }
        return super.loadClass(name, resolve);
    }
}