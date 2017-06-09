package org.codelibs.fess.crawler.client.http.conn;

import java.net.IDN;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.conn.DnsResolver;

public class IdnDnsResolver implements DnsResolver {

    protected int flag = 0;

    @Override
    public InetAddress[] resolve(final String host) throws UnknownHostException {
        return InetAddress.getAllByName(toAscii(host));
    }

    protected String toAscii(final String host) {
        return IDN.toASCII(host, flag);
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
