package combitech.sdp.dummy;

import android.swedspot.scs.data.*;

/**
 * Created by nine on 8/6/14.
 */
public class Parser {

    public final String START_SERVER = "startServer";
    public final String START_SEND_NODE = "startSendNode";
    public final String HELP = "help";
    public final String SUBSCRIBE = "subscribe";
    public final String UNSUBSCRIBE = "unsubscribe";
    public final String SEND_ON_NODE = "nodeSend";
    public final String PROVIDE_ON_NODE = "nodeProvide";
    public final String UNPROVIDE_ON_NODE = "nodeUnprovide";
    public final String SEND_ON_MANAGER = "send";
    public final String EXIT = "exit";

    private Server server;

    public Parser(Server server) {
        this.server = server;
    }

    public void parseCommand(String[] cmd) {
        if (cmd.length == 0) {
            System.out.println("Enter a command or type help ");
            return;
        }
        String command = cmd[0];
        String[] args = new String[cmd.length - 1];
        if (cmd.length > 1) {
            for (int i = 0; i < cmd.length - 1; i++) {
                args[i] = cmd[i + 1];
            }
        }
        boolean result = server.getManager() == null
                && !cmd[0].equals(START_SERVER);
        result = result
                || (server.getSendNode() == null && !cmd[0]
                .equals(START_SEND_NODE));
        if (!result) {
            System.out
                    .println("You have to start either the server or the send node before you can do anything else");
            return;
        }
        switch (command) {
            case START_SERVER:
                server.startServer();
                break;

            case START_SEND_NODE:
                server.startSendNode();
                break;

            case HELP:
                server.printHelp();
                break;

            case SUBSCRIBE:
                server.subscribe(args);
                break;

            case UNSUBSCRIBE:
                server.unsubscribe(args);
                break;

            case SEND_ON_MANAGER:
                if (args.length != 3) {
                    System.out.println("please give the correct format:");
                    System.out.println("id:int data:int type:string");
                    break;
                }
                SCSData managerRdyData = createSCSData(args[2], args[1]);
                server.sendFromManager(Integer.parseInt(args[0]), managerRdyData);
                break;

            case SEND_ON_NODE:
                SCSData nodeRdyData = createSCSData(args[2], args[1]);
                server.sendFromNode(Integer.parseInt(args[0]), nodeRdyData);
                break;

            case PROVIDE_ON_NODE:
                int[] provideIds = new int[args.length];
                for (int i = 0; i < args.length; i++) {
                    provideIds[i] = Integer.parseInt(args[i]);
                }
                server.provide(provideIds);
                break;

            case UNPROVIDE_ON_NODE:
                int[] unprovideIds = new int[args.length];
                for (int i = 0; i < args.length; i++) {
                    unprovideIds[i] = Integer.parseInt(args[i]);
                }
                server.unprovide(unprovideIds);
                break;

            case EXIT:
                System.out.println("Shutting down server...");
                server.shutdownServer();
                break;
            case "":
                break;
            default:
                System.out.println("command not recognized");
                break;
        }
    }

    private SCSData createSCSData(String type, String stringData) {

        SCSData rdyData = null;
        switch (type) {
            case "float":
                float floatData = Float.parseFloat(stringData);
                rdyData = new SCSFloat(floatData);
                break;
            case "double":
                double doubleData = Double.parseDouble(stringData);
                rdyData = new SCSDouble(doubleData);
                break;
            case "uint32":
                int uint32Data = Integer.parseInt(stringData);
                rdyData = new Uint32(uint32Data);
                break;
            case "uint16":
                int uint16Data = Integer.parseInt(stringData);
                rdyData = new Uint16(uint16Data);
                break;
            case "uint8":
                int uint8Data = Integer.parseInt(stringData);
                rdyData = new Uint8(uint8Data);
                break;
            case "short":
                short shortData = Short.parseShort(stringData);
                rdyData = new SCSShort(shortData);
                break;
            case "long":
                long longData = Long.parseLong(stringData);
                rdyData = new SCSLong(longData);
                break;
            case "integer":
                int integerData = Integer.parseInt(stringData);
                rdyData = new SCSInteger(integerData);
                break;
            default:
                System.out.println("type does not exist");
                return null;
        }
        System.out.println("created data of type: " + type);
        return rdyData;
    }
}
