package clp.core.iso.core.isorequests;

import org.jpos.iso.ISOException;
import org.jpos.iso.channel.BASE24TCPChannel;
import org.jpos.iso.packager.GenericPackager;

import java.io.IOException;

public class ProfileTCPChannel extends BASE24TCPChannel {
    GenericPackager gPackager;

    @Override
    protected void getMessage(final byte[] b, final int offset, final int len) throws IOException, ISOException {
        super.getMessage(b, offset, len);
    }

    public ProfileTCPChannel(final String innerFilename) throws ISOException {
        this.gPackager = new GenericPackager(innerFilename);
        this.setPackager(this.gPackager);
    }

    @Override
    protected int getHeaderLength() {
        return 8;
    }

    @Override
    protected int getMessageLength() throws IOException, ISOException {
        return super.getMessageLength() + 1;
    }

    @Override
    protected void getMessageTrailler() throws IOException {
        // TODO: реализовать?
    }
}
