import org.mozartspaces.core.*;
import org.mozartspaces.capi3.*;
import java.util.ArrayList;

public class ex07_cbServer {
    public static void main(String argv[]) throws Exception {
        MzsCore core = DefaultMzsCore.newInstance();
        Capi capi = new Capi(core);

        capi.createContainer("JobKeys1", null, MzsConstants.Container.UNBOUNDED, null, null, null);
        capi.createContainer("JobKeys2", null, MzsConstants.Container.UNBOUNDED, null, null, null);
        capi.createContainer("Results", null, MzsConstants.Container.UNBOUNDED, null, new KeyCoordinator());

        capi.createContainer("Queue1", null, MzsConstants.Container.UNBOUNDED, null, new KeyCoordinator());
        capi.createContainer("Queue2", null, MzsConstants.Container.UNBOUNDED, null, new KeyCoordinator());

        System.out.println("Server started. Containers: JobKeys1, JobKeys2, Results, Queue1, Queue2 created.");
        // core.shutdown(true);
    }
}