import org.mozartspaces.core.*;
import org.mozartspaces.capi3.*;
import java.util.ArrayList;

public class ex07_cbServer
{
    public static void main(String argv[]) throws Exception 
    {
        MzsCore core = DefaultMzsCore.newInstance();    // create space
        Capi capi = new Capi(core);
        ContainerReference jobKeyContainer = capi.createContainer("JobKeys", null, MzsConstants.Container.UNBOUNDED, null, null, null);
        ContainerReference queueContainer = capi.createContainer("Queue", null, MzsConstants.Container.UNBOUNDED, null, new KeyCoordinator());
        ContainerReference resultContainer = capi.createContainer("Results", null, MzsConstants.Container.UNBOUNDED, null, new KeyCoordinator());

        // core.shutdown(true);
    }
}
