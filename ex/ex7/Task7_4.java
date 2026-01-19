import org.mozartspaces.core.*;
import org.mozartspaces.capi3.*;
import java.net.URI;
import java.util.ArrayList;

public class Task7_4 {
    public static void main(String argv[]) throws Exception {
        int N = 2000;
        
        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        Capi capi = new Capi(core);

        URI spaceURI = URI.create("xvsm://localhost:9876");
        
        ContainerReference accountContainer = capi.lookupContainer("Account", spaceURI, MzsConstants.RequestTimeout.DEFAULT, null);
        ContainerReference signalContainer = capi.lookupContainer("Signal", spaceURI, MzsConstants.RequestTimeout.DEFAULT, null);

        for (int i = 0; i < N; i++) {
            ArrayList<Integer> entries = capi.take(accountContainer, AnyCoordinator.newSelector(1), MzsConstants.RequestTimeout.INFINITE, null);
            
            if (entries.isEmpty()) {
                break;
            }

            int currentBalance = entries.get(0);
            int newBalance = currentBalance + 1;

            capi.write(accountContainer, new Entry(newBalance));
        }

        capi.write(signalContainer, new Entry("Done"));
        core.shutdown(true);
    }
}