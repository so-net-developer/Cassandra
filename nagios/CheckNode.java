import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/*
  useage: java CheckNode [hostname or IP]
*/

public class CheckNode
{

    public static void main(String[] args) throws Exception
    {
        String user = "cassandra";
        String pass = "";
        String JMXURL = "service:jmx:rmi:///jndi/rmi://" + args[0] + ":8080/jmxrmi";
        Map hm = new HashMap();
        hm.put(JMXConnector.CREDENTIALS, new String[]{user, pass});

	try {
        	JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(JMXURL), hm);
        	MBeanServerConnection connection = connector.getMBeanServerConnection();

        	ObjectName on = new ObjectName("org.apache.cassandra.service:type=StorageService");
		String list = connection.getAttribute(on, "LiveNodes").toString();
       		connector.close();

		if (list.indexOf(args[0]) != -1 ){
			System.out.println(args[0] + " is alive.");
			System.exit(0);
		}else{
			System.out.println(args[0] + " is down.");
			System.exit(1);
		}
	} catch (IOException e) {
		System.exit(1);
	}
    }

}

