package unit;

import org.junit.Test;

import simulator.filereplayer.openxcreplayer.OpenXCReplayer;
import static org.junit.Assert.*;

public class OpenXCReplayerTest {
	@Test
	public void test__String__String(){
		OpenXCReplayer replayer = new OpenXCReplayer(null);
		String inputString1 = "{\"timestamp\": 1351181673.6880012, \"name\": \"vehicle_speed\", \"value\": 0.0}";
		String inputString2 = "{\"name\": \"vehicle_speed\",\"timestamp\": 1351181673.6880012, \"value\": 0.0}";
		
		
		String return1 = replayer.extractName(inputString1);
		assertEquals("vehicle_speed", return1);
		
		return1 = replayer.extractValue(inputString1);
		assertEquals("0.0", return1);
		
		return1 = replayer.extractTimestamp(inputString1);
		assertEquals("1351181673.6880012", return1);
		
		
		String return2 = replayer.extractName(inputString2);
		assertEquals("vehicle_speed", return2);
		
		return2 = replayer.extractValue(inputString2);
		assertEquals("0.0", return2);
		
		return2 = replayer.extractTimestamp(inputString2);
		assertEquals("1351181673.6880012", return2);
	}
}
