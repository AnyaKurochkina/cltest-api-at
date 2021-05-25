package clp.core.iso.core.isorequests;

import org.jpos.iso.ClientChannel;
import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import clp.core.iso.core.requests.ProcessingRequest;
import clp.core.iso.core.requests.ProcessingResponse;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Map;

public class ISOConnection {
    String host;
    int port;
    ISOChannel channel;
    static boolean debug;
    private static final Logger log = LoggerFactory.getLogger(ISOConnection.class);

    public ISOConnection(final String host, final int port, final String packagerFilename, final boolean debug) throws ISOException {
        this(host, port, new ProfileTCPChannel(packagerFilename), debug);
    }

    public ISOConnection(final String host, final int port, final String packagerFilename) throws ISOException {
        this(host, port, new ProfileTCPChannel(packagerFilename), false);
    }

    public ISOConnection(final String host, final int port, final ISOChannel channel, final boolean debug) {
        this.host = host;
        this.port = port;
        this.channel = channel;
    }

    public void open() throws IOException {
        final ClientChannel clientChannel = (ClientChannel) this.channel;
        clientChannel.setHost(this.host, this.port);
        clientChannel.connect();
    }

    public ProcessingResponse send(final ProcessingRequest request) throws NumberFormatException, ISOException, IOException {
        final ISOMessageContent response = this.send(request.getISOMessageContent());
        return new ProcessingResponse(response);
    }

    public ISOMessageContent send(final ISOMessageContent message) throws NumberFormatException, ISOException, IOException {
        if (this.channel == null || !this.channel.isConnected()) {
            throw new RuntimeException("KafkaConsumer is not established.");
        }
        final ISOMsg isoMsg = this.createISOMsgFromISOMessageContent(message);

        this.channel.send(isoMsg);

        final ISOMsg response = this.channel.receive();
        return this.createISOMessageContentFromISOMsg(response);
    }


    public void close() throws IOException {
        this.channel.disconnect();
    }

    ISOMessageContent createISOMessageContentFromISOMsg(final ISOMsg message) throws ISOException {
        message.setPackager(this.channel.getPackager());
        String msg = new String(message.pack());
        log.debug("RESPONSE MESSAGE HEX DUMP: {}", msg);
        final ISOMessageContent response = new ISOMessageContent();
        response.setMTI(message.getMTI());
        for (int i = 1; i <= message.getMaxField(); i++) {
            if (message.hasField(i)) {
                if (message.getComponent(i) instanceof ISOMsg) {
                    final ISOMsg sField = (ISOMsg) message.getComponent(i);
                    for (int j = 0; j <= sField.getMaxField(); j++) {
                        if (sField.hasField(j)) {
                            response.getFields().put(i + "." + j, sField.getString(j));
                        }
                    }
                } else {
                    response.getFields().put(String.valueOf(i), ISOConnection.normalize(message.getString(i)));
                }
            }
        }
        response.setSourceMessage(message);
        return response;
    }

    ISOMsg createISOMsgFromISOMessageContent(final ISOMessageContent message) throws NumberFormatException, ISOException {
        final ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(this.channel.getPackager());
        isoMsg.setMTI(message.getMTI());
        if (message.getISOHeader() != null && message.getISOHeader().length() > 0) {
            isoMsg.setHeader(message.getISOHeader().getBytes());
        }
        for (final Map.Entry<String, String> e : message.getFields().entrySet()) {
            final String key = e.getKey();
            final String value = e.getValue();
            if (value.startsWith("#")) {
                isoMsg.set(Integer.parseInt(key), DatatypeConverter.parseHexBinary(value.substring(1)));
            } else if (!value.isEmpty() && value != "") {
                isoMsg.set(key, value);
            }
        }
        String msg = new String(isoMsg.pack());
        log.debug("REQUEST MESSAGE HEX DUMP: {}", msg);
        message.setSourceMessage(isoMsg);
        return isoMsg;
    }

    static String normalize(final String buf) {
        String new_buf = "";
        for (int i = 0; i < buf.length(); i++) {
            final char b = buf.charAt(i);
            if (b < ' ' || b > '~') {
                new_buf = new_buf + "\\x" + b;
            } else if (b == ' ') {
                new_buf = new_buf + "_";
            } else {
                new_buf = new_buf + b;
            }
        }
        return new_buf;
    }
}
