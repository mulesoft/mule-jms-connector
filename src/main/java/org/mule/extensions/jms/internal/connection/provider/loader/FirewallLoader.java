package org.mule.extensions.jms.internal.connection.provider.loader;

public class FirewallLoader extends ClassLoader {
    public FirewallLoader(ClassLoader parent) {
        super(parent);
    }
    public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.equals("org.bouncycastle.jsse.provider.ProvSSLSocketDirect")) {
            throw new ClassNotFoundException();
        }
        return super.loadClass(name, resolve);
    }
}