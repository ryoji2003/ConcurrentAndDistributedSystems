import org.mozartspaces.core.*;
import org.mozartspaces.capi3.*;
import java.util.ArrayList;

public class ex07_mozartBank
{
    public static void main(String argv[]) throws Exception 
    {
        MzsCore core = DefaultMzsCore.newInstance();//(12345);    // create space (must be there)
        Capi capi = new Capi(core);
        ContainerReference accountContainer = capi.createContainer("Account", null, MzsConstants.Container.UNBOUNDED, null, null, null);
        capi.write(accountContainer, new Entry(0));

        // read two values from the "Signal" container
        ContainerReference signalContainer = capi.createContainer("Signal", null, MzsConstants.Container.UNBOUNDED, null, null, null);
        capi.read(signalContainer, AnyCoordinator.newSelector(2), MzsConstants.RequestTimeout.INFINITE, null);

        ArrayList<Integer> entries = capi.read(accountContainer);
        System.out.println("Account = " + entries.get(0));
        core.shutdown(true);
    }
}
