package acceptancetest.receivedata;

import static org.junit.Assert.*;

import acceptancetest.util.Util;
import android.swedspot.scs.SCSFactory;
import android.swedspot.scs.data.SCSData;
import android.swedspot.scs.data.SCSDouble;
import android.swedspot.scs.data.SCSFloat;
import android.swedspot.scs.data.SCSInteger;
import android.swedspot.scs.data.SCSShort;
import android.swedspot.scs.data.Uint16;
import android.swedspot.scs.data.Uint32;
import android.swedspot.scs.data.Uint8;
import cucumber.api.java.en.Given;

public class ReceiveDataSteps {
	@Given("^The dummy server sends the signal (\\d+) with the value (\\d+) as a (.*)$")
	public void dummyServerSendsData(int signalID, int value, String t) {
		String type = t.toLowerCase();
		SCSData data = null;

		switch (type) {
		case "integer":
			data = new SCSInteger(value);
			break;
		case "float":
			data = new SCSFloat(value);
			break;
		case "uint32":
			data = new Uint32(value);
			break;
		case "uint16":
			data = new Uint16(value);
			break;
		case "uint8":
			data = new Uint8(value);
			break;
		case "double":
			data = new SCSDouble(value);
			break;
		case "short":
			data = new SCSShort((short) value);
			break;
		}
		assertNotNull(data);
		Util.staticServer.sendFromManager(signalID, data);
	}
}
