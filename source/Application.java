/*
Proyecto 3
Zepeta Rivera José Antonio
4CM14
*/ 

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Application {
    public static void validarArgs(String[] args) {
        if (args.length <= 3) {
            System.out.println("Número de argumentos incorrecto");
            System.exit(-1);
        }
    }
    public static void main(String[] args) {
        validarArgs(args);
        String WORKER_ADDRESS_1 = "http://"+args[0]+"/searchipn"; // End point de los 4 servidores, obtenidos como argumentos
        String WORKER_ADDRESS_2 = "http://"+args[1]+"/searchipn";
        String WORKER_ADDRESS_3 = "http://"+args[2]+"/searchipn";
        
        List<String> tasks = new ArrayList<String>(); 
        
        for (int i = 0; i < args.length-3; i++) {
            tasks.add(args[i+3]);
        }

        int k = 0;
        int repeticiones = (int) Math.ceil((double) tasks.size()/3);
        while (k<repeticiones) {
            tasks = repartirCadenas(tasks,WORKER_ADDRESS_1,WORKER_ADDRESS_2,WORKER_ADDRESS_3);
            k++;
        }
            
      
    }
    public static List<String> repartirCadenas(List<String> tasks, String WORKER_ADDRESS_1, String WORKER_ADDRESS_2, String WORKER_ADDRESS_3){
        Aggregator aggregator = new Aggregator();//Instanciar un objeto aggregator que definimos en otro archivo
        List<String> results = null;
        if(tasks.size()==1){
            results = aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1),tasks);// Metodo sendtaskstoworker que sirve para asignar todas las tareas a los trabajadores, usando en 2 arreglos diferentes, 1 para los tabajadores y 1 para las tareas
            tasks.remove(0);
        }else if(tasks.size()==2){
            results = aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1,WORKER_ADDRESS_2),tasks);// Metodo sendtaskstoworker que sirve para asignar todas las tareas a los trabajadores, usando en 2 arreglos diferentes, 1 para los tabajadores y 1 para las tareas
            tasks.remove(0);
            tasks.remove(0);
        }else if(tasks.size()>=3){
            results = aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1,WORKER_ADDRESS_2, WORKER_ADDRESS_3),tasks);// Metodo sendtaskstoworker que sirve para asignar todas las tareas a los trabajadores, usando en 2 arreglos diferentes, 1 para los tabajadores y 1 para las tareass
            tasks.remove(0);
            tasks.remove(0);
            tasks.remove(0);
            
        }
        for (String result : results) {//Recepcion de los resultados
            System.out.println(result);//Impresion los resultados
        }
        return tasks;
    }
}
