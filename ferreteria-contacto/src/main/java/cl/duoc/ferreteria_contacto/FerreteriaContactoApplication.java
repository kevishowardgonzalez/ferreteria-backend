package cl.duoc.ferreteria_contacto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FerreteriaContactoApplication {

	public static void main(String[] args) {
        // ¡Aquí estaba el error! Debe llevar el mismo nombre de la clase de arriba
		SpringApplication.run(FerreteriaContactoApplication.class, args);
	}

}