package combitech.sdp.simulator.car.torcs;

import android.swedspot.automotiveapi.AutomotiveSignalId;
import android.swedspot.scs.data.SCSFloat;
import android.swedspot.scs.data.SCSLong;
import android.swedspot.scs.data.SCSShort;
import combitech.sdp.simulator.BasicModule;
import combitech.sdp.simulator.SimulationModuleState;
import combitech.sdp.simulator.SimulatorGateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Ets2 extends BasicModule {

    ServerSocket welcomeSocket;
    short currentGear;
    float fuelLevel;
    float fuelConsumption;
    float speed;
    long distance;
    private float engineSpeed;
    private float acceleratorPedalPosition;
    private Socket clientSocket;
    private final static Logger LOGGER = LoggerFactory.getLogger(Ets2.class);

    public Ets2(SimulatorGateway gateway) {
        super(gateway);
    }

    @Override
    public int[] getProvidingSignals() {
        return new int[]{
                AutomotiveSignalId.FMS_WHEEL_BASED_SPEED,
                AutomotiveSignalId.FMS_CURRENT_GEAR,
                AutomotiveSignalId.FMS_FUEL_LEVEL_1,
                AutomotiveSignalId.FMS_FUEL_RATE,
                AutomotiveSignalId.FMS_HIGH_RESOLUTION_TOTAL_VEHICLE_DISTANCE,
                AutomotiveSignalId.FMS_ENGINE_SPEED,
                AutomotiveSignalId.FMS_ACCELERATOR_PEDAL_POSITION_1};
    }

    @Override
    public void run() {
        getModuleThread().setName("ETS2");
        String signalUpdate;
        
        while (state == SimulationModuleState.RUNNING) {
        	 try {
                 if (welcomeSocket == null) {
                 	welcomeSocket = new ServerSocket(6000);
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             }

            try {
                //System.out.println("Awaiting connection");
                //clientSocket = welcomeSocket.accept();
            	clientSocket = new Socket("127.0.0.1", 25555);
                PrintWriter out = new PrintWriter(new BufferedWriter(
    					new OutputStreamWriter(clientSocket.getOutputStream())));
                out.println("GET /api/ets2/telemetry");
    			out.flush();
    			
                //System.out.println("Got connection");

                BufferedReader inFromETS2 = new BufferedReader(new InputStreamReader(
    					clientSocket.getInputStream()));
                
               
                

                while (state == SimulationModuleState.RUNNING && clientSocket.isConnected()) {
                	
                	signalUpdate = inFromETS2.readLine();
                    if (signalUpdate == null) {
                        break;
                    }
                    if (signalUpdate.length() > 30) {
                        extractValues(signalUpdate);
                        gateway.sendValue(
                                AutomotiveSignalId.FMS_WHEEL_BASED_SPEED,
                                new SCSFloat(speed));
//                        gateway.sendValue(AutomotiveSignalId.FMS_CURRENT_GEAR,
//                                new SCSShort(currentGear));
//                        gateway.sendValue(AutomotiveSignalId.FMS_FUEL_LEVEL_1,
//                                new SCSFloat(fuelLevel));
//                        gateway.sendValue(AutomotiveSignalId.FMS_FUEL_RATE,
//                                new SCSFloat(fuelConsumption));
//                        gateway.sendValue(AutomotiveSignalId.FMS_HIGH_RESOLUTION_TOTAL_VEHICLE_DISTANCE,
//                                new SCSLong(distance));
//                        gateway.sendValue(AutomotiveSignalId.FMS_ENGINE_SPEED,
//                                new SCSFloat(engineSpeed));
//                        gateway.sendValue(AutomotiveSignalId.FMS_ACCELERATOR_PEDAL_POSITION_1,
//                                new SCSFloat(acceleratorPedalPosition));
                    }
                    Thread.sleep(18);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private void extractValues(String signalUpdate) {
        String[] values = signalUpdate.split(",");
        
        try {
//            fuelLevel = Float.parseFloat(values[0]);
//            currentGear = Short.parseShort(values[1]);
              speed = Float.parseFloat(values[6].substring(13));
//            fuelConsumption = Float.parseFloat(values[3]);
//            distance = Long.parseLong(values[4]);
            //engineSpeed = Float.parseFloat(values[5]);
            //acceleratorPedalPosition = Float.parseFloat(values[6]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopModule() {
        state = SimulationModuleState.STOPPED;
        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (welcomeSocket != null) {
                welcomeSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.stopModule();
    }
}
