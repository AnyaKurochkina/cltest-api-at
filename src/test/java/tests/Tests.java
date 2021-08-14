package tests;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public interface Tests {

    @BeforeEach
    default void beforeScenarios()  {
    }

    @AfterEach
    default void afterScenarios(){
    }

}
