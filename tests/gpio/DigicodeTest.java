package gpio;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.SimulatedGpioProvider;

import me.security.hardware.Digicode;
import me.security.managers.NotificationManager;
import me.security.managers.SecuManager;
import utils.JUnitGPIO;
import utils.dummy.DummyDatabaseManager;

public class DigicodeTest {
	
	private static SimulatedGpioProvider gpio;
	
    private SecuManager secu;
    private Digicode digi;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        gpio = new SimulatedGpioProvider();
        GpioFactory.setDefaultProvider(gpio);
    }
    
    @AfterClass
    public static void tearDownClass() {
		JUnitGPIO.cleanOut(gpio);
    	gpio = null;
    }
	
	@Before
	public void setUp() throws Exception {
		this.secu = new SecuManager(new NotificationManager(), new DummyDatabaseManager());
		this.digi = this.secu.getDigicode();
	}

	@After
	public void tearDown() throws Exception {
		JUnitGPIO.cleanOut(gpio);
		this.digi = null;
		this.secu = null;
	}

	@Test
	public void testToggleOnAlarm() {
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_14);//1
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_03, RaspiPin.GPIO_10);//5
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_06);//7
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_10);//4
		
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_02, RaspiPin.GPIO_05);//#
		
		assertTrue(secu.isEnabled());
	}

	@Test
	public void testToggleOnWithErrorAlarm() {
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_14);//1
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_03, RaspiPin.GPIO_10);//5
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_02, RaspiPin.GPIO_10);//6

		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_05);//*

		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_14);//1
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_03, RaspiPin.GPIO_10);//5
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_06);//7
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_10);//4
		
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_02, RaspiPin.GPIO_05);//#
		
		assertTrue(secu.isEnabled());
	}

	@Test
	public void testAddPasscodeThenToggleOn() {
		this.digi.addPasscode("4751");
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_10);//4
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_06);//7
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_03, RaspiPin.GPIO_10);//5
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_14);//1
		
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_02, RaspiPin.GPIO_05);//#
		
		assertTrue(secu.isEnabled());
	}

	@Test
	public void testToggleOnThenOffAlarm() {
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_14);//1
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_03, RaspiPin.GPIO_10);//5
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_06);//7
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_10);//4
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_02, RaspiPin.GPIO_05);//#
		
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_14);//1
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_03, RaspiPin.GPIO_10);//5
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_06);//7
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_04, RaspiPin.GPIO_10);//4
		JUnitGPIO.pressDigicode(gpio, RaspiPin.GPIO_02, RaspiPin.GPIO_05);//#
		
		assertFalse(secu.isEnabled());
	}

}
