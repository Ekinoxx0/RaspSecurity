import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ManagersTests.class, NotificationTests.class, GPIOTests.class})
public class AllTests {}
