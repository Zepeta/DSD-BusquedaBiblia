/*
Proyecto 3
Zepeta Rivera José Antonio
4CM14
*/ 
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
public class WebServer {
    //Declaración de endpoints
    private static final String STATUS_ENDPOINT = "/status";
    private static final String SEARCHIPN_ENDPOINT = "/searchipn";
    //Puerto para realizar conexiones con clientesjava
    private final int port;
    //Implementando un servidor con la clase HttpServer
    private HttpServer server;
    public static void main(String[] args) {
    //Se coloca puerto por default en caso de que no se envíe por línea de comandos
        int serverPort = 8080;
        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]);
        }
    //Instancia de WebServer
        WebServer webServer = new WebServer(serverPort);
    //Ejecutando método startServer para iniciar configuración del servidor
        webServer.startServer();
        System.out.println("Servidor escuchando en el puerto " + serverPort);
    }
    //Constructor
    public WebServer(int port) {
        this.port = port;
    }
    public void startServer() {
        try {
        //Creando instancia de la clase HttpServer
        //Recibe un socket y el tamaño de la lista de solicitudes pendientes para el servidor
        //Al colocar un cero se deja la decisión al servidor
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    //Creando objetos HttpContext
        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext searchContext = server.createContext(SEARCHIPN_ENDPOINT);
    //Asigna un método manejador a los endpoints
        statusContext.setHandler(this::handleStatusCheckRequest);
        searchContext.setHandler(this::handleSearchRequest);
    //Se proveen 8 hilos para que el servidor trabaje
        server.setExecutor(Executors.newFixedThreadPool(8));
    //Se inicia el servidor como hilo en segundo plano
        server.start();
    }
    //Manejador del endpoint task
    //HttpExchange contiene todo lo relacionado con la transacción HTTP actual

    //Se analiza si la petición es GET para devolver que el servidor está vivo
    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }
        String responseMessage = "El servidor está vivo\n";
        sendResponse(responseMessage.getBytes(), exchange);
    }
    
    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
    //Agrega estatus code 200 de éxito y longitud de la respuesta
        exchange.sendResponseHeaders(200, responseBytes.length);
    //Se escribe en el cuerpo del mensaje
        OutputStream outputStream = exchange.getResponseBody();
    //Se envía al cliente por medio del Stream
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }

    //Manejador del endpoint searchipn
    private void handleSearchRequest(HttpExchange exchange) throws IOException {
    //Se verifica si se solicitó un método POST
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }
    //Se busca clave X-Debug para verificar estado del servidor
        Headers headers = exchange.getRequestHeaders();
        boolean isDebugMode = false;
        if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")) {
            isDebugMode = true;
        }
    //Calculando tiempo que tardó el proceso completo
        long startTime = System.nanoTime();
    //Se recupera el cuerpo del mensaje de la transacción HTTP
    //Este va a contener la cantidad de tokens aleatorios
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
    //Se envían los números recibidos para calcular una operación con calculateResponse
        byte[] responseBytes = calculateSearchResponse(requestBytes);
    //Se termina el cálculo y se toma el tiempo final
        long finishTime = System.nanoTime();
    //Si se activó el modo Debug se imprime el tiempo que tardó
        if (isDebugMode) {
            String debugMessage = String.format("La operacion tomo %d nanosegundos", finishTime - startTime);
            //Se coloca el tiempo en el header X-Debug-Info
        exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMessage));
            // Concatenando responseBytes y el tiempo que tardó en finalizar la petición
            byte[] timeBytes = String.format("La operacion tomo %d nanosegundos", finishTime - startTime).getBytes();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(responseBytes);
            outputStream.write(timeBytes);
            responseBytes = outputStream.toByteArray();
        }
    //Se envía respuesta al cliente
        sendResponse(responseBytes, exchange);
    }

    //Método para buscar cadena en tokens de cadenota
    private byte[] calculateSearchResponse(byte[] requestBytes) {
        //Separando parámetros recibidos
        String cadenaOriginal = new String(requestBytes);
        
        //Convirtiendo parámetros al formato deseado
        //int n = Integer.parseInt(stringNumbers[0]);
        String cadena = cadenaOriginal;
        
        //Declaración de cadenota
        StringBuilder cadenota = new StringBuilder();   
        try {
            
            BufferedReader lector = new BufferedReader(new InputStreamReader(new FileInputStream("biblia.txt"), "ISO-8859-1"));

            String line = null;
            while ((line = lector.readLine()) != null) {
                cadenota.append(line);
            }
            lector.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        
        //Buscando la cadena deseada y contando cantidad de apariciones
        int apariciones = 0, buscar = 0;
        while(buscar != -1){
            buscar = cadenota.indexOf(cadena, buscar);
            if(buscar != -1){
                apariciones++;
                buscar += 4;
            }
        }
        buscar = 0;
        cadena = cadena.toUpperCase();
        if(!cadena.equals(cadenaOriginal)){
            while(buscar != -1){
                buscar = cadenota.indexOf(cadena, buscar);
                if(buscar != -1){
                    apariciones++;
                    buscar += 4;
                }
            }
        }
        buscar = 0;
        cadena = cadena.toLowerCase();
        if(!cadena.equals(cadenaOriginal)){
            while(buscar != -1){
                buscar = cadenota.indexOf(cadena, buscar);
                if(buscar != -1){
                    apariciones++;
                    buscar += 4;
                }
            }
        }
        buscar = 0;
        cadena = cadena.substring(0, 1).toUpperCase() + cadena.substring(1);
        if(!cadena.equals(cadenaOriginal)){
            while(buscar != -1){
                buscar = cadenota.indexOf(cadena, buscar);
                if(buscar != -1){
                    apariciones++;
                    buscar += 4;
                }
            }
        }
        System.out.println("Cadena recibida: "+cadenaOriginal+" con "+apariciones+" apariciones");
        return String.format("\nLa cantidad de veces que se encontró la cadena '" + cadenaOriginal + "' fue: %d\n\n", apariciones).getBytes();
    }
}