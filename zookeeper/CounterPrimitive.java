import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;
import java.text.DecimalFormat;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class CounterPrimitive implements Watcher {

	static ZooKeeper zk = null;
	static Integer mutex;
	String root;

	CounterPrimitive(String address) {
		if(zk == null){
			try {
				zk = new ZooKeeper(address, 3000, this);
				mutex = new Integer(-1);
			} catch (IOException e) {
				System.out.println(e.toString());
				zk = null;
			}
		}
	}

    synchronized public void process(WatchedEvent event) {
        synchronized (mutex) {
            //System.out.println("Process: " + event.getType());
            mutex.notify();
        }
    }

	/**
	 *  Counter
	 */
	static public class Counter extends CounterPrimitive {

		String counter;
		String znode;

		/**
		 * Constructor of  Counter
		 */
		Counter(String address, String root_name, String counter_name) {
			super(address);
			this.root = root_name;
			counter = counter_name;
			znode = this.root + "/" + counter;
			// Create ZK node name
			if (zk != null) {
				try {
					Stat s = zk.exists(root, false);
					if (s == null) {
						zk.create(root, new byte[0], Ids.OPEN_ACL_UNSAFE,
								CreateMode.PERSISTENT);
					}
				} catch (KeeperException e) {
					System.out
							.println("Keeper exception when instantiating Counter: "
									+ e.toString());
				} catch (InterruptedException e) {
					System.out.println("Interrupted exception");
				}
			}
		}

		/**
		 * Create the counter.
		 */

		boolean create() throws KeeperException, InterruptedException{
			ByteBuffer b = ByteBuffer.allocate(4);
			byte[] value;

			// Add child with value i
			b.putInt(0);
			value = b.array();
			zk.create(znode, value, Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);

			return true;
		}

		/**
		 * Delete counter
		 */
		void delete() throws KeeperException, InterruptedException{
			zk.delete(znode, -1);
		}
		/**
		 * Incliment to the counter.
		 */

		int inclement() throws KeeperException, InterruptedException{
			int value = 0;
			int retry = 0;

			do {
				try {
					Stat stat = zk.exists(znode, false);
					byte[] b = zk.getData(znode,false, stat);
					ByteBuffer buffer = ByteBuffer.wrap(b);
		  			value = buffer.getInt();

					value++;

					ByteBuffer b1 = ByteBuffer.allocate(6);
					b1.putInt(value);
					b = b1.array();
					zk.setData(znode, b, stat.getVersion());
					retry = 0;
				} catch ( KeeperException e) {
					retry = 1;
				}
			}while(retry > 0);

			return value;
		}
	}

	public static void main(String args[]) {
		counterTest(args);
	}

	public static void counterTest(String args[]) {
		Counter q = new Counter(args[0], "/app1", "counter");

		System.out.println("Input: " + args[0]);

		if (args[1].equals("n")) {
			System.out.println("New Counter");
			try{
				q.create();
			} catch (KeeperException e){

			} catch (InterruptedException e){

			}
		} else if (args[1].equals("i")) {
			int i;
			int r = 0;
			Integer max = new Integer(args[2]);
			for( i = 0; i < max; i++ ){
				//System.out.println("Inclement");
				try{
					r = q.inclement();
				} catch (KeeperException e){

				} catch (InterruptedException e){

				}
			}
   			System.out.println("Item: " + r);
		} else {
			try{
				q.delete();
				System.out.println("Deleted");
			} catch (KeeperException e){

			} catch (InterruptedException e){

			}
		}
	}

}

