package ar.edu.unsam.cardiovigia.bdd;

import ar.edu.unsam.cardiovigia.CardioVigiaApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = CardioVigiaApplication.class)
public class CucumberSpringConfiguration {
}
