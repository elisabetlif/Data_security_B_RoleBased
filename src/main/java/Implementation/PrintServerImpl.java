package Implementation;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import Interface.PrintServer;
import io.jsonwebtoken.Claims;
import lib.SessionManager;


public class PrintServerImpl extends UnicastRemoteObject implements PrintServer {
    private Boolean isRunning;
    private SessionManager sManager;
    private Map<String, LinkedList<String>> printerQueues;
    private Map<String, String> configParameters;
   

    public PrintServerImpl(SessionManager sManager) throws RemoteException {
        super();
        isRunning = false;
        this.sManager = sManager;
        this.printerQueues = new HashMap<>();
        this.configParameters = new HashMap<>();
    }

    /**
     * Validates the access token 
     * @param token Access token
     * @return
     * @throws AuthenticationException
     */
    private Claims validateToken(String token) {
        System.out.println("Check token validation...");
        Claims claims = sManager.validateAccessToken(token);
        return claims;
    }

    public boolean hasRole(Claims claims, List<String> requiredRole){
        String role = claims.get("role", String.class);
        return requiredRole.contains(role);
    }

   

    /**
     * prints file filename on the specified printer
     * @throws Exception 
     */
    @Override
    public String print(String token, String filename, String printer){
        if(!isRunning){
            System.err.println("Server: Printer not running, unable to commit action");
            return "Printer is not running";
        }
        Claims claims = validateToken(token);
        List<String> requiredRole = new ArrayList<>(List.of("Admin", "Technician", "Power_user", "User"));
        
        if(claims != null){
            boolean role = hasRole(claims, requiredRole);

            if(role){
                System.out.println("Server: Printing document: " + filename);
                return "print";   
            } else {
                System.err.println("Server: Unauthorized, insufficient role or permission to print");
                return "Unauthorized: Insufficient role or permission to print";
            }
        } else {
            System.err.println("Server: Unauthorized - Access token validation failed. User not authorized.");
            return "User not is not authenticated";
        }
    }

    /**
     * Lists the print queue for a given printer on the user's display
     * in lines of the form !job number ? !filename?
     */
    @Override
    public String queue(String token, String printer){     
        if(!isRunning){
            System.err.println("Server: Printer not running, unable to commit action");
            return "Printer is not running";
        }
        Claims claims = validateToken(token);
        List<String> requiredRole = new ArrayList<>(List.of("Admin", "Power_user", "User"));

        if (claims != null) {
            boolean role = hasRole(claims, requiredRole);

            if(role){                
                System.out.println("Pritner gives current queue");
                return "queue";
            } else {
                System.err.println("Server: Unauthorized, insufficient role or permission to queue");
                return "Unauthorized: Insufficient role or permission to queue";
            }                

        } else {
            System.err.println("Server: Unauthorized - Access token validation failed. User not authorized.");
            return "User not is not authenticated";
        }
    }

    /**
     * moves job to the top of the queue
     */
    @Override
    public String topQueue(String token, String printer, int job){
        if(!isRunning){
            System.err.println("Server: Printer not running, unable to commit action");
            return "Printer is not running";
        }
        Claims claims = validateToken(token);
        List<String> requiredRole = new ArrayList<>(List.of("Admin", "Power_user"));

        if (claims != null) {
            boolean role = hasRole(claims, requiredRole);

            if(role){
                System.out.println("Printer moves job to the top of the queue");
                return "topqueue";
            } else {
                System.err.println("Server: Unauthorized, insufficient role or permission to topqueue");
                return "Unauthorized: Insufficient role or permission to topqueue";
            }  
        } else {
            System.err.println("Server: Unauthorized - Access token validation failed. User not authorized.");
            return "User not is not authenticated";
        }
    }

    /**
     * starts the print server
     * @throws RemoteException 
     */
    @Override
    public String start(String token) {
        if(isRunning){
            System.err.println("Server: Printer is alreadt running, unable to commit action");
            return "Printer is already running";
        }
        Claims claims = validateToken(token);
        List<String> requiredRole = new ArrayList<>(List.of("Admin", "Technician"));

        if (claims != null) {
            boolean role = hasRole(claims, requiredRole);
            

            if(role){
                isRunning = true;
                System.out.println("Printer has been started");
                return "start";
            } else {
                System.err.println("Server: Unauthorized, insufficient role or permission to start");
                return "Unauthorized: Insufficient role or permission to start";
            } 
        } else {
            System.err.println("Server: Unauthorized - Access token validation failed. User not authorized.");
            return "User not is not authenticated";
        }
    }

    /**
     * stops the print server
     */
    @Override
    public String stop(String token){
        if(!isRunning){
            System.err.println("Server: Printer is already not running, unable to commit action");
            return "Printer is already stopped";
        }
        Claims claims = validateToken(token);
        List<String> requiredRole = new ArrayList<>(List.of("Admin", "Technician"));

        if (claims != null) {
            boolean role = hasRole(claims, requiredRole);

            if(role){
                isRunning = false;
                System.out.println("Printer has been stopped");
                return "stop";   
            } else {
                System.err.println("Server: Unauthorized, insufficient role or permission to stop.");
                return "Unauthorized: Insufficient role or permission to stop";
            }
        } else {
            System.err.println("Server: Unauthorized - Access token validation failed. User not authorized.");
            return "User not is not authenticated";
        }
    }

    /**
     * stops the print server, clears the print queue and starts the print server again
     * @throws RemoteException 
     */
    @Override
    public String restart(String token){
        if(isRunning){
            System.err.println("Server: Printer not running, unable to commit action");
            return "Print has not been started to be able to restart";
        }
        Claims claims = validateToken(token);
        List<String> requiredRole = new ArrayList<>(List.of("Admin", "Technician", "Power_user"));

        if (claims != null) {
            boolean role = hasRole(claims, requiredRole);

            if(role){
                System.out.println("Printer has been restarted");
                return "restart"; 
            } else {
                System.err.println("Server: Unauthorized, insufficient role or permission to restart.");
                return "Unauthorized: Insufficient role or permission to restart.";
            } 
        } else {
            System.err.println("Server: Unauthorized - Access token validation failed. User not authorized.");
            return "User not is not authenticated";
        }
    }

    /**
     * prints status of printer on the user's display
     */
    @Override
    public String status(String token, String printer){
        if(!isRunning){
            System.err.println("Server: Printer not running, unable to commit action");
            return "Printer is not running";
        }
        Claims claims = validateToken(token);
        List<String> requiredRole = new ArrayList<>(List.of("Admin", "Technician"));

        if (claims != null) {
            boolean role = hasRole(claims, requiredRole);

            if(role){
                System.out.println("Printer prints status");
                return "status"; 
            } else {
                System.err.println("Server: Unauthorized, insufficient role or permission to status");
                return "Unauthorized: Insufficient role or permission to status";
            } 
        } else {
            System.err.println("Server: Unauthorized - Access token validation failed. User not authorized.");
            return "User not is not authenticated";
        }
    }

    /**
     * prints the value of the parameter on the print server to the user's display
     */
    @Override
    public String readConfig(String token, String parameter){
        if(!isRunning){
            System.err.println("Server: Printer not running, unable to commit action");
            return "Printer is not running";
        }
        Claims claims = validateToken(token);
        List<String> requiredRole = new ArrayList<>(List.of("Admin", "Technician"));

        if (claims != null) {
            boolean role = hasRole(claims, requiredRole);

            if(role){
                System.out.println("Printer reads value of parameter");
                return "readConfig"; 
            } else {
                System.err.println("Server: Unauthorized, insufficient role or permission to readConfig");
                return "Unauthorized: Insufficient role or permission to readConfig";
            } 
        } else {
            System.err.println("Server: Unauthorized - Access token validation failed. User not authorized.");
            return "User not is not authenticated";
        }
    }

    /**
     * sets the parameter on the print server to value
     */
    @Override
    public String setConfig(String token, String parameter, String value){
        if(!isRunning){
            System.err.println("Server: Printer not running, unable to commit action");
            return "Printer is not running";
        }
        Claims claims = validateToken(token);
        List<String> requiredRole = new ArrayList<>(List.of("Admin", "Technician"));

        if (claims != null) {
            boolean role = hasRole(claims, requiredRole);

            if(role){
                System.out.println("Printer sets value of parameter");
                return "setConfig";
            } else {
                System.err.println("Server: Unauthorized, insufficient role or permission to setConfig");
                return "Unauthorized: Insufficient role or permission to setConfig";
            }
        } else {
            System.err.println("Server: Unauthorized - Access token validation failed. User not authorized.");
            return "User not is not authenticated";
        }
    }
}
