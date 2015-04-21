package combitech.sdp.simulator;

import java.util.Map;

import android.swedspot.scs.SignalInfo;
import android.swedspot.scs.data.DataType;
import android.swedspot.sdp.configuration.Configuration;

import com.swedspot.vil.configuration.ConfigurationFactory;
import com.swedspot.vil.signal.ContinuosSignalInfo;

public class SimulatorConfig implements Configuration {

    private final Configuration standard = ConfigurationFactory.getConfiguration();

    @Override
    public Map<Integer, SignalInfo> getConfiguredSignals() {
        final Map<Integer, SignalInfo> map = standard.getConfiguredSignals();
        map.put(0x0096, new ContinuosSignalInfo("", "", 100, "UNITLESS", DataType.UINT32, 0, Integer.MAX_VALUE * 2, 1));
        map.put(0x0097, new ContinuosSignalInfo("", "", 100, "UNITLESS", DataType.UINT32, 0, Integer.MAX_VALUE * 2, 1));
        return map;
    }

    @Override
    public String getDataType(final int signalId) throws IllegalArgumentException {
        if (signalId == 0x0096 || signalId == 0x0097) {
            return DataType.UINT32.toString();
        }
        return standard.getDataType(signalId);
    }

    @Override
    public long getMaximumSignalPayloadSize() {
        return standard.getMaximumSignalPayloadSize();
    }

    @Override
    public SignalInfo getSignalInformation(final int signalId) {
        if (signalId == 0x0096) {
            return new ContinuosSignalInfo("", "", 100, "UNITLESS", DataType.UINT32, 0, Integer.MAX_VALUE * 2, 1);
        } else if (signalId == 0x0097) {
            return new ContinuosSignalInfo("", "", 100, "UNITLESS", DataType.UINT32, 0, Integer.MAX_VALUE * 2, 1);
        }
        return standard.getSignalInformation(signalId);
    }

    @Override
    public String getUnit(final int signalId) throws IllegalArgumentException {
        if (signalId == 0x0096 || signalId == 0x0097) {
            return "UNITLESS";
        }
        return standard.getUnit(signalId);
    }

    @Override
    public boolean isSignalConfigured(final int signalId) {
        return standard.isSignalConfigured(signalId) || signalId == 0x0096 || signalId == 0x0097;
    }

    @Override
    public void printConfig() {
        standard.printConfig();
    }

}
