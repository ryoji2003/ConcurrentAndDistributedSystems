import org.mozartspaces.core.*;
import org.mozartspaces.capi3.*;
import java.util.ArrayList;

public class ex07_cbServer
{
    public static void main(String argv[]) throws Exception
    {
        MzsCore core = DefaultMzsCore.newInstance();    // create space
        Capi capi = new Capi(core);

        // Create two job containers for load balancing
        ContainerReference jobKeyContainer1 = capi.createContainer("JobKeys1", null, MzsConstants.Container.UNBOUNDED, null, null, null);
        ContainerReference jobKeyContainer2 = capi.createContainer("JobKeys2", null, MzsConstants.Container.UNBOUNDED, null, null, null);

        ContainerReference queueContainer = capi.createContainer("Queue", null, MzsConstants.Container.UNBOUNDED, null, new KeyCoordinator());
        ContainerReference resultContainer = capi.createContainer("Results", null, MzsConstants.Container.UNBOUNDED, null, new KeyCoordinator());

        System.out.println("Server started with two job containers for load balancing");
        // core.shutdown(true);
    }
}
