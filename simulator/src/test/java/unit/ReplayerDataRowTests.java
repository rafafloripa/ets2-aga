package unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import simulator.filereplayer.ReplayerDataRow;
import simulator.filereplayer.FileReplayer;

public class ReplayerDataRowTests {

    @Test
    public void compareTo_greaterTimestamp_returns1() {

        // Arrange
        final ReplayerDataRow replayerDataRowWithGreaterTimestamp = new ReplayerDataRow(0, "", 200);
        final ReplayerDataRow replayerDataRowWithLesserTimestamp = new ReplayerDataRow(0, "", 100);

        // Act
        int result = replayerDataRowWithGreaterTimestamp.compareTo(replayerDataRowWithLesserTimestamp);

        // Assert
        assertEquals(1, result);
    }

    @Test
    public void compareTo_EqualTimestamp_returns0() {

        // Arrange
        final ReplayerDataRow replayerDataRowWithEqualTimestamp1 = new ReplayerDataRow(0, "", 200);
        final ReplayerDataRow replayerDataRowWithEqualTimestamp2 = new ReplayerDataRow(0, "", 200);

        // Act
        int result = replayerDataRowWithEqualTimestamp1.compareTo(replayerDataRowWithEqualTimestamp2);

        // Assert
        assertEquals(0, result);
    }

    @Test
    public void compareTo_LesserTimestamp_returnsMinus1() {

        // Arrange
        final ReplayerDataRow replayerDataRowWithGreaterTimestamp = new ReplayerDataRow(0, "", 200);
        final ReplayerDataRow replayerDataRowWithLesserTimestamp = new ReplayerDataRow(0, "", 100);

        // Act
        int result = replayerDataRowWithLesserTimestamp.compareTo(replayerDataRowWithGreaterTimestamp);

        // Assert
        assertEquals(-1, result);
    }

    String testExtractDataToExtract = "{\"timestamp\":431,\"name\":0001,\"value\":100}";

    @Test
    public void extractData_timestamp_return431() {
        String data[] = testExtractDataToExtract.split(",");
        long timestamp = Long.parseLong(FileReplayer.extractData(data[0]));
        assertEquals(431, timestamp);
    }

    @Test
    public void extractData_name_return0001() {
        String data[] = testExtractDataToExtract.split(",");
        String id = FileReplayer.extractData(data[1]);
        assertEquals("0001", id);
    }

    @Test
    public void extractData_value_return100() {
        String data[] = testExtractDataToExtract.split(",");
        int value = Integer.parseInt(FileReplayer.extractData(data[2]));
        assertEquals(100, value);
    }
}
