package clp.core.helpers;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import clp.core.exception.CustomException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class SftpClient {

    private static final Logger LOG = LoggerFactory.getLogger(SftpClient.class);
    private String user;
    private String password;
    private String host;
    private String port;
    private ChannelSftp sftpChannel;

    public SftpClient(String user, String password, String host, String port) throws CustomException {
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
        sftpChannelOpen();
    }

    private void sftpChannelOpen() throws CustomException {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(this.user, this.host, Integer.valueOf(this.port));
            session.setPassword(this.password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("UserAuth", "user.password");
            LOG.debug("Establishing KafkaConsumer to the SFTP...");
            LOG.debug("Connecting to {}:{} ", this.host, this.port);
            session.connect();
            LOG.debug("KafkaConsumer established.");
            LOG.debug("Creating SFTP Channel.");
            this.sftpChannel = (ChannelSftp) session.openChannel("sftp");
            this.sftpChannel.connect();
        } catch (JSchException e) {
            LOG.error("JschException - " + e.getMessage(), e);
            throw new CustomException(e);
        }
    }

    public boolean sendMessageToSFTP(String msg, String destFileName) throws CustomException {
        if (msg == null) {
            LOG.error("Error: SentXmlUrl = null. Can't get xml for message sending.");
            throw new CustomException("Error: SentXmlUrl = null. Can't get xml for message sending.");
        }
        try {
            InputStream msgByte = new ByteArrayInputStream(msg.getBytes(StandardCharsets.UTF_8));
            this.sftpChannel.put(msgByte, destFileName);
            LOG.debug("File successfully send to SFTP server. Filename: {}", destFileName);
            this.sftpChannel.disconnect();
            this.sftpChannel.getSession().disconnect();
            return true;
        } catch (JSchException | SftpException | NullPointerException e) {
            throw new CustomException(e);
        }

    }


    public String getSftpFile(String path, String destFileName, String timeout) throws CustomException {
        LOG.debug("File name  {}", destFileName);
        try {
            StringBuilder respStr = new StringBuilder();

            Boolean fileExists = false;
            Object[] files;
            long seconds = 0;
            while (seconds <= Integer.parseInt(timeout)) {
                try {
                    files = this.sftpChannel.ls(path).toArray();
                } catch (SftpException ex) {
                    LOG.error("Error: Path not exists {}", path);
                    throw new CustomException(ex);
                }
                for (Object f : files) {
                    if (f.toString().contains(destFileName)) {
                        fileExists = true;
                        break;
                    }
                }
                if (fileExists) {
                    break;
                }
                LOG.debug("Second iteration {}, timeout {}", seconds, Integer.parseInt(timeout));
                seconds++;
                try {
                    TimeUnit.SECONDS.sleep(Integer.parseInt(timeout) / 10);
                } catch (InterruptedException e) {
                    LOG.error("Error: Sleep error {}", seconds);
                    Thread.currentThread().interrupt();
                }
            }
            if (fileExists) {
                InputStream response;
                response = this.sftpChannel.get(path + "/" + destFileName);
                LOG.info("File successfully tacked from server");
                BufferedReader bufReads = new BufferedReader(new InputStreamReader(response));

                String line;
                try {
                    while ((line = bufReads.readLine()) != null) {
                        respStr.append(line);

                    }
                    bufReads.close();
                } catch (Exception ex) {
                    throw new CustomException(ex);
                }
            } else {
                LOG.info("File could not found: {}{}", path, destFileName);
                throw new CustomException("File could not found");
            }
            this.sftpChannel.disconnect();
            this.sftpChannel.getSession().disconnect();
            return respStr.toString();
        } catch (JSchException | SftpException e) {
            LOG.warn("Exception: {}{}", path, destFileName);
            throw new CustomException(e);
        }
    }

}
