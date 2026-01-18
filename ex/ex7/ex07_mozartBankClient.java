import org.mozartspaces.core.*;
import org.mozartspaces.capi3.*;
import java.net.URI;
import java.util.ArrayList;

public class ex07_mozartBankClient {
    public static void main(String argv[]) throws Exception {
        int N = 2000;  // Number of money units to add

        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        Capi capi = new Capi(core);

        // Connect to the bank's containers
        ContainerReference accountContainer = capi.lookupContainer("Account", URI.create("xvsm://localhost:9876"), MzsConstants.RequestTimeout.DEFAULT, null);
        ContainerReference signalContainer = capi.lookupContainer("Signal", URI.create("xvsm://localhost:9876"), MzsConstants.RequestTimeout.DEFAULT, null);

        System.out.println("Client connected. Adding " + N + " money units...");

        // Add N money units one at a time
        for (int i = 0; i < N; i++) {
            // Use a transaction to ensure atomicity
            TransactionReference tx = capi.createTransaction(MzsConstants.RequestTimeout.DEFAULT, null);

            try {
                // Take (remove) the current account value
                ArrayList<Integer> entries = capi.take(accountContainer, AnyCoordinator.newSelector(1), MzsConstants.RequestTimeout.DEFAULT, tx);
                int currentValue = entries.get(0);

                // Increment the value
                int newValue = currentValue + 1;

                // Write the new value back
                capi.write(accountContainer, new Entry(newValue), MzsConstants.RequestTimeout.DEFAULT, tx);

                // Commit the transaction
                capi.commitTransaction(tx);
            } catch (Exception e) {
                // If something goes wrong, rollback and retry
                capi.rollbackTransaction(tx);
                i--;  // Retry this iteration
            }
        }

        // Signal completion by adding an entry to the Signal container
        capi.write(signalContainer, new Entry("done"));

        System.out.println("Client finished adding " + N + " money units.");
        core.shutdown(true);
    }
}
