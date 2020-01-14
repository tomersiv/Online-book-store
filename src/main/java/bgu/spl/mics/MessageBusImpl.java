package bgu.spl.mics;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import sun.misc.Queue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	HashMap<MicroService, Queue<Message>> queueHashMap=new HashMap<>();
	HashMap<Class<? extends Event>,LinkedBlockingQueue<MicroService> > eventHashMap=new HashMap<>();
	HashMap<MicroService,Class<? extends Broadcast>>broadcastHashMap =new HashMap<>();
	HashMap<Class<? extends Event>,Future> futureHashMap =new HashMap<>();
	private static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private MessageBusImpl() {

	}
	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		LinkedBlockingQueue<MicroService> linkedBlockingQueue=new LinkedBlockingQueue<>();
	    if(eventHashMap.containsKey(type)) {
            eventHashMap.get(type).add(m);
        }
		else
		   linkedBlockingQueue.add(m);
            eventHashMap.put(type,linkedBlockingQueue);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastHashMap.put(m,type);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
	futureHashMap.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for (Map.Entry<MicroService, Class<? extends Broadcast>> entry : broadcastHashMap.entrySet()) {
			//find the broadcast type in the map
			if(entry.getValue()==b.getClass()){
				//find the microservice inerested in this broadcat and add it to its queue.
				MicroService microService=entry.getKey();
				queueHashMap.get(microService).enqueue(b);
			}
		}

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {

			    if(!eventHashMap.containsKey(e))
			        return null;
			    LinkedBlockingQueue<MicroService> services=eventHashMap.get(e);
				MicroService microService=services.poll();
				//push the event to the correct microservice queue.
				queueHashMap.get(microService).enqueue(e);
				//put the event in the last place for round robin.
				services.add(microService);

				return microService.sendEvent(e);
	}

	@Override
	public void register(MicroService m) {
		Queue<Message> messageQueue=new Queue<>();
		queueHashMap.put(m,messageQueue);
	}

	@Override
	public void unregister(MicroService m) {
		if(queueHashMap.containsKey(m))
			queueHashMap.remove(m);
            for (Map.Entry<Class<? extends Event>,LinkedBlockingQueue<MicroService>> entry : eventHashMap.entrySet()){
                if(entry.getValue().contains(m))
                    entry.getValue().remove(m);
            }
			if(broadcastHashMap.containsKey(m))
                 broadcastHashMap.remove(m);

	}

	@Override
	public synchronized Message awaitMessage(MicroService m) throws InterruptedException {
		if(!queueHashMap.containsKey(m))
			throw new IllegalStateException("non registered service");
		Queue<Message> messageQueue=queueHashMap.get(m);
		while(messageQueue.isEmpty()){
			try{
				m.wait();
			}
			catch (InterruptedException e){
				throw e;
			}
		}
		Message message=messageQueue.dequeue();
		m.notifyAll();
		return message;
	}

	

}
