package io.github.sebastiantoepfer.openvdi.payara;

import fish.payara.micro.BootstrapException;
import fish.payara.micro.PayaraMicro;
import java.io.IOException;
import java.net.URISyntaxException;

public class EmbeddedPayara {

    public static void main(final String[] args) throws BootstrapException, URISyntaxException, IOException {
        PayaraMicro.bootstrap();
    }
}
