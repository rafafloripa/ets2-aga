package acceptancetest.util;

import android.swedspot.scs.data.SCSData;
import combitech.sdp.dummy.Server;
import combitech.sdp.simulator.BasicModule;
import combitech.sdp.simulator.SimulatorGateway;

public class Util {
    public static DummyApplication staticDummyApp;
    public static SimulatorGateway staticSimulator;
    public static Server staticServer;
	public static BasicModule staticModule;
    
    public static boolean WaitFor(Predicate condition ,int timeoutInMiliSec, int retryIntervalInMilisec)
    {
    	int numberOfTries = timeoutInMiliSec/retryIntervalInMilisec;
    	for (int i=0; i<numberOfTries; i++)
    	{
    		if (condition.check()) return true;
    		try {
				Thread.sleep(retryIntervalInMilisec);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	return false;
    }
}
