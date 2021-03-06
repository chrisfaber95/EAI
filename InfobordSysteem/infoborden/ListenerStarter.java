package infoborden;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.fusesource.mqtt.cli.Listener;

public  class ListenerStarter implements Runnable, ExceptionListener {
	private String selector="";
	private Infobord infobord;
	private Berichten berichten;
	
	public ListenerStarter() {
	}
	
	public ListenerStarter(String selector, Infobord infobord, Berichten berichten) {
		this.selector=selector;
		this.infobord=infobord;
		this.berichten=berichten;
	}

	public void run() {
        try {
            ActiveMQConnectionFactory connectionFactory = 
            		new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
//			TODO maak de connection aan
			//Connection connection = connectionFactory.createConnection();
			TopicConnection connection = connectionFactory.createTopicConnection();
			connection.start();
			connection.setExceptionListener(this);
//			TODO maak de session aan
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			//TopicSession topicSession = connection.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
//			TODO maak de destination aan
			//Destination destination = session.createTopic(selector);
			Topic destination = session.createTopic("Infobord");
//			TODO maak de consumer aan
			//MessageConsumer consumer = session.createConsumer(destination);
			MessageConsumer consumer = session.createConsumer(destination, selector);
            System.out.println("Produce, wait, consume "+ selector);
           // System.out.println(consumer);
//			TODO maak de Listener aan
			QueueListener listener = new QueueListener(selector, infobord, berichten);
			//TopicRequestor listener = new TopicRequestor(topicSession,destination);
			//MessageListener myListener = new MessageListener();
			//TopicListener
			//consumer.setMessageListener(listener);

			//listener.request(consumer.receive());
			listener.onMessage(consumer.receive());
			//System.out.println(consumer.receive().getJMSMessageID());
			//consumer.setMessageListener(session.getMessageListener());
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }
}