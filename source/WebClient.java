/*
Proyecto 3
Zepeta Rivera José Antonio
4CM14
*/ 

package networking;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class WebClient {
    private HttpClient client;//Objeto tipo httpclient (unico objeto) provisto por librerias de java

    public WebClient() {//Crear el constructor elobjeto httpclient
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)//Usa el protocolo http version 1
                .build();
    }

    public CompletableFuture<String> sendTask(String url, byte[] requestPayload) { //Metodo recibe la direccion de la conexion y los datos a enviar
        HttpRequest request = HttpRequest.newBuilder()//Crea una solicitud
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestPayload))//Metodo post
                .uri(URI.create(url))//Dirreccion de destino 
		.header("X-Debug", "true")// *** Cabecera de depurado, obtener tiempo de transmisión" ***
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())//Llamamos al metodo sendAsync para enviar la solicitud request (de forma asincrona)
                .thenApply(respuesta -> {return respuesta.body() + "\nCabeceras: " + respuesta.headers() + "\nVersión de HTML: " + respuesta.version() + "\nURI: " + respuesta.uri() + "\n";}); //  *** Recibe la respuesta del cuerpo, luego de los headers, versión html y uri. ***
    }
}
