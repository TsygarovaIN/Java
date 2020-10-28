package main;

import main.ObjectClasses.Buffer;
import main.ObjectClasses.Device;
import main.ObjectClasses.Manager;
import main.ObjectClasses.Source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

//Тут всё происходит
public class Main {
    public static double systemTime = 0.0;      //системное время моделирования
    public static double alpha = 1.0;                 //параметр alpha для времени генерации заявки
    public static double beta = 1.1;                  //параметр beta для времени генерации заявки
    public static double lambda = 0.275;                //параметр lambda для экспоненциального закона распределения обработки прибора
    public static int countSources;             //количество источников в системе
    public static int countBuffers;             //количество буферов в системе
    public static int countDevices;             //количество приборов в системе
    public static int countRequests;            //количество моделируемых заявок


    //В каждую единицу времени (проход цикла) в самом начале, проверяем каждый источник на готовность сгенерировать заявку, если
    //он готов, то генерирует от него, если нет, то ищем другой
    //источники сначала заносим в лист, а затем перемешиваем и генерируем заявки из них в уже рандомном (после перемешивания) порядке

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
//        System.out.print("Введите alpha: ");
//        alpha = scanner.nextDouble();
//        System.out.print("Введите beta: ");
//        beta = scanner.nextDouble();
//        System.out.print("Введите lambda: ");
//        lambda = scanner.nextDouble();
        System.out.print("Введите количсетво источников: ");
        countSources = scanner.nextInt();
        System.out.print("Введите количсетво буферов: ");
        countBuffers = scanner.nextInt();
        System.out.print("Введите количсетво приборов: ");
        countDevices = scanner.nextInt();
        System.out.print("Введите количсетво моделируемых заявок: ");
        countRequests = scanner.nextInt();

        ArrayList<Source> sources = new ArrayList<Source>(countSources);
        for (int i = 0; i < countSources; i++) {
            sources.add(new Source(i + 1));
        }
        Collections.shuffle(sources);

        ArrayList<Buffer> buffers = new ArrayList<>(countBuffers);
        for (int i = 0; i < countBuffers; i++) {
            buffers.add(new Buffer(i + 1));
        }

        ArrayList<Device> devices = new ArrayList<>(countDevices);
        for (int i = 0; i < countDevices; i++) {
            devices.add(new Device(i + 1));
        }

        //Главный цикл программы
        while(Device.getCountRequest() != countRequests) {
            Source.Request newRequest = null;  //Новая заявка, с которой работаем
            int numberSourse = 0;             // номер источника, у которого сгенерировалась заявка

            for(Source s : sources){          //находим источник, который готов сгенерировать заявку
                if(s.isReady()){
                    newRequest = s.generate();
                    numberSourse = s.getNumber();
                    break;
                }
            }

            //Если ни один источник не смог сгенерировать заявку,
            //то разбираемся только с теми заявками, которые лежат в буфере и приборах
            //Если кто-то сгенерировал, то работаем с ней
            if(newRequest != null) {
                for (Buffer b : buffers) {          //Находим пустой буфер
                    if (b.isEmpty()) {              //Если нашли, то добавляем туда новую заявку
                        b.add(newRequest, numberSourse);
                        break;
                    }
                }

                if (!newRequest.isInBuffer()) {    //Если заявка не была добавлена в буфер (все заняты)
                    Source.Request req = buffers.get(countBuffers - 1).getRequest(); //Достаём последнюю заявку
                    int numberSource = buffers.get(countBuffers - 1).getNumberSource();
                    req.setInRefusal(true, numberSource);                    //Ставим статус "В отказ"
                    Source.Request.setCountRefusal();                                //Увеличиваем счётчик заявок в отказе
                    buffers.get(countBuffers - 1).delete();                          //Удаляем последнюю заявку в буфере
                    buffers.get(countBuffers - 1).add(newRequest, numberSourse);     //Ставим на её место новую заявку
                }
            }

            Manager manager = new Manager();         //В менаджер передаём массив с буферами и приборами
            manager.toDevice(buffers, devices);      //внутри происходит постановка заявки на прибор из буфера, если есть свободный прибор
            systemTime += 0.1;
        }
        //Вывод результатов
    }
}
