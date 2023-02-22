/*
Proyecto 3
Zepeta Rivera José Antonio
4CM14
*/ 
import networking.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Aggregator {
    private WebClient webClient; //Objeto webclient (Solo lo tenemos como dato)

    public Aggregator() { 
        this.webClient = new WebClient();//Instancia de nuestro webclient
    }

    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks) {//Metodo (unico) "sendTasksToWorkers", sirve para que se reciba la lista de los trabajadores y tareas
        CompletableFuture<String>[] futures = new CompletableFuture[workersAddresses.size()];//Empleamos la clase CompletableFuture. para el manejo de la comunicacion asincrona
	//Permitiendo continuar con la ejecucion de codigo bloqueante. En el arreglo se guardaran las respuestas futuras de los dos servidores
        for (int i = 0; i < workersAddresses.size(); i++) {//iteramos sobre todos los elementos
            String workerAddress = workersAddresses.get(i);//Obtencion de las direcciones de los trabajadores
            String task = tasks.get(i);//Tambien de las tareas

            byte[] requestPayload = task.getBytes();//Almacenamos las tareas
            futures[i] = webClient.sendTask(workerAddress, requestPayload);//Enviamos las tareas asincronas (usanod sendtask)
        }

        List<String> results = Stream.of(futures).map(CompletableFuture::join).collect(Collectors.toList());//Declaramos lista de resultados

        return results;
    }
}
