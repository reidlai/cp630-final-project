package cp630oc.paymentsolution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;

@ExtendWith(MockitoExtension.class)
public class ApplicationTest {

    @Test
    public void testMain() {
        try (MockedStatic<SpringApplication> mockedStatic = Mockito.mockStatic(SpringApplication.class)) {

            // Arguments
            String[] args = new String[]{};
            
            // Act
            Application.main(args);
            
            // Assert
            mockedStatic.verify(() -> 
                SpringApplication.run(Application.class, args)
            );
        }
    }
}