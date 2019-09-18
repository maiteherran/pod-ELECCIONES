package ar.edu.itba.pod.util;

public enum Party {

    GORILLA ("GORILLA"),
    LEOPARD ("LEOPARD"),
    TURTLE ("TURTLE"),
    OWL ("OWL"),
    TIGER ("TIGER"),
    TARSIER ("TARSIER"),
    MONKEY ("MONKEY"),
    LYNX ("LYNX"),
    WHITE_TIGER ("WHITE_TIGER"),
    WHITE_GORILLA ("WHITE_GORILLA"),
    SNAKE ("SNAKE"),
    JACKALOPE ("JACKALOPE"),
    BUFFALO ("BUFFALO"),
    BLANK ("-");/* VOTO VACÍO **/

    final String name;

    Party (String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }

}
