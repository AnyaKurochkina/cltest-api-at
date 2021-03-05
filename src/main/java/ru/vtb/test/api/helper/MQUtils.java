package ru.vtb.test.api.helper;

import com.ibm.jms.JMSBytesMessage;
import com.ibm.jms.JMSTextMessage;
import com.ibm.mq.jms.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;


@Getter
@Setter
@ToString
@Slf4j
public class MQUtils {


    private MQQueueConnection queueConnection = null;
    private MQQueueSession queueSession = null;
    private MQQueue queue;

    private MQQueueSender sender = null;
    private MQQueueReceiver receiver = null;

    private String hostName;
    private Integer port;
    private String queueManager;
    private String channel;
    private int transportType;
    private String queueName;
    private int sessionMode;
    private static ConfigVars var = ConfigFactory.create(ConfigVars.class);

    public MQUtils(String hostName, Integer port, String queueManager, String channel, String queueName) {
        this.hostName = hostName;
        this.queueManager = queueManager;
        this.channel = channel;
        this.port = port;
        this.queueName = queueName;
    }


    public void initSenderQueue(String queueName) throws JMSException {
            MQQueueConnectionFactory cf = new MQQueueConnectionFactory();

            // Config
            cf.setHostName(hostName);
            cf.setPort(port);
            cf.setTransportType(1);
            cf.setQueueManager(queueManager);
            cf.setChannel(channel);


            queueConnection = (MQQueueConnection) cf.createQueueConnection(var.mqUser(), var.mqPassword());
            queueSession = (MQQueueSession) queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            MQQueue queue = (MQQueue) queueSession.createQueue("queue:///" + queueName);
            sender = (MQQueueSender) queueSession.createSender(queue);

            // Start the connection
            queueConnection.start();
    }

    public void initReceiverQueue(String queueName) throws JMSException {

            MQQueueConnectionFactory cf = new MQQueueConnectionFactory();

            // Config
            cf.setHostName(hostName);
            cf.setPort(port);
            cf.setTransportType(1);
            cf.setQueueManager(queueManager);
            cf.setChannel(channel);

            queueConnection = (MQQueueConnection) cf.createQueueConnection(var.mqUser(), var.mqPassword());
            queueSession = (MQQueueSession) queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            MQQueue queue = (MQQueue) queueSession.createQueue("queue:///" + queueName);
            receiver = (MQQueueReceiver) queueSession.createReceiver(queue);

            // Start the connection
            queueConnection.start();

    }

    public void initReceiverQueue(String queueName, String selector) throws JMSException {

            MQQueueConnectionFactory cf = new MQQueueConnectionFactory();

            // Config
            cf.setHostName(hostName);
            cf.setPort(port);
            cf.setTransportType(1);
            cf.setQueueManager(queueManager);
            cf.setChannel(channel);

            queueConnection = (MQQueueConnection) cf.createQueueConnection(var.mqUser(), var.mqPassword());
            queueSession = (MQQueueSession) queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            MQQueue queue = (MQQueue) queueSession.createQueue("queue:///" + queueName);
            receiver = (MQQueueReceiver) queueSession.createReceiver(queue, selector);

            // Start the connection
            queueConnection.start();

    }


    public void close() throws JMSException {
            if (sender != null) sender.close();
            if (receiver != null) receiver.close();
            if (queueSession != null) queueSession.close();
            if (queueConnection != null) queueConnection.close();
    }

    public void send(String queueName, String mess) throws JMSException {
        try {
            initSenderQueue(queueName);
            JMSTextMessage message = (JMSTextMessage) queueSession.createTextMessage(mess);
            sender.send(message);
            log.info("Message sent into queue: " + queueName);
            log.debug(mess);
        } finally {
            close();
        }
    }

    public String receiveText(String queueName) throws JMSException {
        String res = "";
        try {
            initReceiverQueue(queueName);
            Message msg = receiver.receive(1000);
            TextMessage receivedMessage = (TextMessage) msg;
            if (receivedMessage != null) res = receivedMessage.getText();
        } finally {
            close();
        }

        return res;
    }

    public String receiveByteToText(String queueName) throws JMSException {
        String res = "";
        try {
            initReceiverQueue(queueName);
            Message msg = receiver.receive(1000);
            System.out.println(msg);
            JMSBytesMessage bytesMessage = (JMSBytesMessage) msg;
            byte[] byteData = null;
            byteData = new byte[(int) bytesMessage.getBodyLength()];
            bytesMessage.readBytes(byteData);
            bytesMessage.reset();
            String receivedMessage = new String(byteData);

            if (receivedMessage != null) res = receivedMessage;

        } finally {
            close();
        }
        return res;
    }

    public String receiveByteToText(String queueName, String selector) throws JMSException {
        String res = "";
        try {
            initReceiverQueue(queueName, selector);
            Message msg = receiver.receive(1000);
            System.out.println(msg);
            JMSBytesMessage bytesMessage = (JMSBytesMessage) msg;
            byte[] byteData = null;
            byteData = new byte[(int) bytesMessage.getBodyLength()];
            bytesMessage.readBytes(byteData);
            bytesMessage.reset();
            String receivedMessage = new String(byteData);

            if (receivedMessage != null) res = receivedMessage;

        } finally {
            close();
        }
        return res;
    }

    public String receiveNoWait() throws JMSException {
        String res = "";
        try {
            Message msg = receiver.receiveNoWait();
            TextMessage receivedMessage = (TextMessage) msg;
            if (receivedMessage != null) res = receivedMessage.getText();

        } finally {
            close();
        }
        return res;
    }


}
