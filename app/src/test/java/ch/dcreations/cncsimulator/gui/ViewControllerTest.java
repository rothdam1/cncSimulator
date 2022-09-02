package ch.dcreations.cncsimulator.gui;

import ch.dcreations.cncsimulator.TestConfig;
import ch.dcreations.cncsimulator.cncControl.CNCControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * <p>
 * <p>
 *  TEST FOR THE ViewController.class
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
/* TESTS
TESTCASES 1
    TEST Initialisation
       1_1 Verify initialisation mock works fine


 */
class ViewControllerTest {

    private final TestConfig testConfig = new TestConfig();
    @Mock
    private ViewController viewController  = new ViewController();;

    @Spy
    private CNCControl cncControl = new CNCControl(TestConfig.GET_CNC_CANALS());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        viewController.initialize(testConfig,cncControl);
    }

    @Test
    void initialize() {
        //1_1 Verify initialisation mock works fine
        verify(viewController,times(1)).initialize(testConfig,cncControl);
    }

    @Test
    void closeController() {
    }

    @Test
    void loadSampleProgram() {
    }

    @Test
    void setCNCControl() {
    }

    @Test
    void runCNCButtonClicked() {
    }

    @Test
    void stopCNCButtonClicked() {
    }

    @Test
    void resetCNCButtonClicked() {
    }

    @Test
    void goToNextStepCNCButtonClicked() {
    }
}